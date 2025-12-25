# ============================================================================
# SERVICIO ML - FLIGHTONTIME
# Motor de Predicción de Puntualidad de Vuelos con Integración Meteorológica
# ============================================================================
# Este servicio FastAPI carga el modelo ML entrenado (model.pkl) y proporciona
# predicciones enriquecidas con datos meteorológicos en tiempo real.
# ============================================================================

from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field
from typing import Optional, Dict, Any
import joblib
import pandas as pd
import numpy as np
from math import radians, sin, cos, sqrt, atan2
import requests
from datetime import datetime
import logging
import os

from airport_coords import AIRPORT_COORDINATES

# ============================================================================
# CONFIGURACIÓN DE LOGGING
# ============================================================================
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# ============================================================================
# INICIALIZACIÓN DE FASTAPI
# ============================================================================
app = FastAPI(
    title="FlightOnTime ML Service",
    description="Servicio de predicción de puntualidad de vuelos con integración meteorológica",
    version="1.0.0"
)

# Configuración CORS para permitir llamadas desde el backend
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# ============================================================================
# CARGA DEL MODELO ML
# ============================================================================
MODEL_PATH = "model.pkl"
try:
    model = joblib.load(MODEL_PATH)
    logger.info(f" Modelo cargado exitosamente desde {MODEL_PATH}")
except Exception as e:
    logger.error(f" Error al cargar el modelo: {e}")
    model = None

# ============================================================================
# CONFIGURACIÓN DE API METEOROLÓGICA
# ============================================================================
OPENWEATHER_API_KEY = "d4ce4d4589c7a7ac4343085c00c39f9b"
OPENWEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/weather"

# ============================================================================
# MODELOS DE DATOS (DTOs)
# ============================================================================

class PredictionRequest(BaseModel):
    """
    Modelo de entrada para solicitud de predicción.
    El usuario NO envía la distancia, se calcula automáticamente.
    """
    aerolinea: str = Field(..., description="Código de aerolínea (ej: LATAM, GOL, AZUL)")
    origen: str = Field(..., description="Código IATA del aeropuerto de origen (ej: GRU, JFK)")
    destino: str = Field(..., description="Código IATA del aeropuerto de destino (ej: GIG, MEX)")
    fecha_partida: Optional[str] = Field(None, description="Fecha de partida en formato ISO-8601")
    
    class Config:
        json_schema_extra = {
            "example": {
                "aerolinea": "LATAM",
                "origen": "GRU",
                "destino": "JFK",
                "fecha_partida": "2025-12-25T14:30:00"
            }
        }


class WeatherData(BaseModel):
    """Datos meteorológicos del aeropuerto de origen"""
    temperatura: float
    humedad: int
    presion: int
    visibilidad: int
    viento_velocidad: float
    condicion: str
    descripcion: str


class PredictionResponse(BaseModel):
    """Respuesta completa de la predicción"""
    prediccion: str  # "Puntual" o "Retrasado"
    probabilidad_retraso: float
    confianza: float
    distancia_km: float
    clima_origen: WeatherData
    metadata: Dict[str, Any]


# ============================================================================
# FUNCIONES AUXILIARES
# ============================================================================

def calcular_distancia_haversine(lat1: float, lon1: float, lat2: float, lon2: float) -> float:
    """
    Calcula la distancia entre dos puntos geográficos usando la fórmula de Haversine.
    
    Args:
        lat1, lon1: Coordenadas del punto de origen
        lat2, lon2: Coordenadas del punto de destino
    
    Returns:
        Distancia en kilómetros
    """
    # Radio de la Tierra en kilómetros
    R = 6371.0
    
    # Convertir grados a radianes
    lat1_rad = radians(lat1)
    lon1_rad = radians(lon1)
    lat2_rad = radians(lat2)
    lon2_rad = radians(lon2)
    
    # Diferencias
    dlat = lat2_rad - lat1_rad
    dlon = lon2_rad - lon1_rad
    
    # Fórmula de Haversine
    a = sin(dlat / 2)**2 + cos(lat1_rad) * cos(lat2_rad) * sin(dlon / 2)**2
    c = 2 * atan2(sqrt(a), sqrt(1 - a))
    
    distancia = R * c
    
    logger.info(f"📏 Distancia calculada: {distancia:.2f} km")
    return round(distancia, 2)


def obtener_clima_aeropuerto(codigo_iata: str) -> Optional[WeatherData]:
    """
    Consulta el clima actual en el aeropuerto de origen usando OpenWeatherMap API.
    
    Args:
        codigo_iata: Código IATA del aeropuerto
    
    Returns:
        WeatherData con información meteorológica o None si falla
    """
    try:
        # Obtener coordenadas del aeropuerto
        if codigo_iata not in AIRPORT_COORDINATES:
            logger.warning(f"⚠️ Aeropuerto {codigo_iata} no encontrado en base de datos")
            return None
        
        coords = AIRPORT_COORDINATES[codigo_iata]
        
        # Realizar petición a OpenWeatherMap
        params = {
            "lat": coords["lat"],
            "lon": coords["lon"],
            "appid": OPENWEATHER_API_KEY,
            "units": "metric",  # Celsius
            "lang": "es"
        }
        
        response = requests.get(OPENWEATHER_BASE_URL, params=params, timeout=5)
        response.raise_for_status()
        
        data = response.json()
        
        # Extraer datos relevantes
        weather_data = WeatherData(
            temperatura=data["main"]["temp"],
            humedad=data["main"]["humidity"],
            presion=data["main"]["pressure"],
            visibilidad=data.get("visibility", 10000),
            viento_velocidad=data["wind"]["speed"],
            condicion=data["weather"][0]["main"],
            descripcion=data["weather"][0]["description"]
        )
        
        logger.info(f"🌤️ Clima obtenido para {codigo_iata}: {weather_data.descripcion}, {weather_data.temperatura}°C")
        return weather_data
        
    except requests.exceptions.RequestException as e:
        logger.error(f"❌ Error al consultar clima: {e}")
        return None
    except Exception as e:
        logger.error(f"❌ Error inesperado al obtener clima: {e}")
        return None


def preparar_features_modelo(
    aerolinea: str,
    distancia_km: float,
    clima: Optional[WeatherData],
    fecha_partida: Optional[str]
) -> pd.DataFrame:
    """
    Prepara las features necesarias para el modelo de predicción.
    
    Args:
        aerolinea: Código de aerolínea
        distancia_km: Distancia calculada del vuelo
        clima: Datos meteorológicos
        fecha_partida: Fecha de partida del vuelo
    
    Returns:
        DataFrame con features preparadas para el modelo
    """
    # Features base
    features = {
        "distancia_km": distancia_km,
    }
    
    # Agregar features de clima si están disponibles
    if clima:
        features.update({
            "temperatura": clima.temperatura,
            "humedad": clima.humedad,
            "presion": clima.presion,
            "visibilidad": clima.visibilidad,
            "viento_velocidad": clima.viento_velocidad,
        })
    else:
        # Valores por defecto si no hay clima disponible
        features.update({
            "temperatura": 20.0,
            "humedad": 60,
            "presion": 1013,
            "visibilidad": 10000,
            "viento_velocidad": 5.0,
        })
    
    # Agregar features temporales si hay fecha
    if fecha_partida:
        try:
            dt = datetime.fromisoformat(fecha_partida.replace('Z', '+00:00'))
            features.update({
                "hora": dt.hour,
                "dia_semana": dt.weekday(),
                "mes": dt.month,
            })
        except:
            # Valores por defecto
            features.update({
                "hora": 12,
                "dia_semana": 0,
                "mes": 1,
            })
    else:
        features.update({
            "hora": 12,
            "dia_semana": 0,
            "mes": 1,
        })
    
    # Codificar aerolínea (one-hot encoding simplificado)
    # En producción, esto debería coincidir con el encoding del entrenamiento
    aerolineas_conocidas = ["LATAM", "GOL", "AZUL", "AVIANCA", "COPA"]
    for airline in aerolineas_conocidas:
        features[f"aerolinea_{airline}"] = 1 if aerolinea.upper() == airline else 0
    
    df = pd.DataFrame([features])
    logger.info(f"📊 Features preparadas: {list(df.columns)}")
    
    return df


# ============================================================================
# ENDPOINTS DE LA API
# ============================================================================

@app.get("/")
async def root():
    """Endpoint de verificación de salud del servicio"""
    return {
        "servicio": "FlightOnTime ML Service",
        "estado": "operativo",
        "version": "1.0.0",
        "modelo_cargado": model is not None,
        "aeropuertos_disponibles": len(AIRPORT_COORDINATES)
    }


@app.get("/health")
async def health_check():
    """Health check para Docker y orquestación"""
    if model is None:
        raise HTTPException(status_code=503, detail="Modelo no disponible")
    
    return {
        "status": "healthy",
        "modelo": "cargado",
        "timestamp": datetime.now().isoformat()
    }


@app.get("/airports")
async def list_airports():
    """Lista todos los aeropuertos disponibles en el sistema"""
    return {
        "total": len(AIRPORT_COORDINATES),
        "aeropuertos": [
            {
                "codigo": code,
                "nombre": data["name"],
                "lat": data["lat"],
                "lon": data["lon"]
            }
            for code, data in AIRPORT_COORDINATES.items()
        ]
    }


@app.post("/predict_internal", response_model=PredictionResponse)
async def predict_internal(request: PredictionRequest):
    """
    Endpoint principal de predicción con enriquecimiento meteorológico.
    
    Este endpoint:
    1. Valida los códigos IATA de origen y destino
    2. Calcula automáticamente la distancia usando Haversine
    3. Consulta el clima en tiempo real del aeropuerto de origen
    4. Realiza la predicción usando el modelo ML
    5. Retorna predicción binaria, probabilidad y datos contextuales
    """
    logger.info(f"🔍 Iniciando predicción para: {request.aerolinea} {request.origen} → {request.destino}")
    
    try:
        # ====================================================================
        # 1. VALIDACIÓN DE AEROPUERTOS
        # ====================================================================
        logger.debug(f"Validando aeropuerto de origen: {request.origen}")
        if request.origen not in AIRPORT_COORDINATES:
            logger.error(f"❌ Aeropuerto de origen '{request.origen}' no encontrado")
            available_airports = list(AIRPORT_COORDINATES.keys())
            raise HTTPException(
                status_code=400,
                detail=f"Aeropuerto de origen '{request.origen}' no encontrado. Aeropuertos disponibles: {', '.join(available_airports[:10])}... Use /airports para ver todos."
            )
        
        logger.debug(f"Validando aeropuerto de destino: {request.destino}")
        if request.destino not in AIRPORT_COORDINATES:
            logger.error(f"❌ Aeropuerto de destino '{request.destino}' no encontrado")
            available_airports = list(AIRPORT_COORDINATES.keys())
            raise HTTPException(
                status_code=400,
                detail=f"Aeropuerto de destino '{request.destino}' no encontrado. Aeropuertos disponibles: {', '.join(available_airports[:10])}... Use /airports para ver todos."
            )
        
        logger.info(f"✅ Aeropuertos validados: {request.origen} y {request.destino}")
        
        # ====================================================================
        # 2. CÁLCULO AUTOMÁTICO DE DISTANCIA
        # ====================================================================
        origen_coords = AIRPORT_COORDINATES[request.origen]
        destino_coords = AIRPORT_COORDINATES[request.destino]
        
        logger.debug(f"Calculando distancia entre {origen_coords['name']} y {destino_coords['name']}")
        
        distancia_km = calcular_distancia_haversine(
            origen_coords["lat"], origen_coords["lon"],
            destino_coords["lat"], destino_coords["lon"]
        )
        
        logger.info(f"📏 Distancia calculada: {distancia_km:.2f} km")
        
        # ====================================================================
        # 3. CONSULTA DE CLIMA EN TIEMPO REAL
        # ====================================================================
        logger.debug(f"Consultando clima para {request.origen}")
        clima_origen = obtener_clima_aeropuerto(request.origen)
        
        if clima_origen is None:
            # Clima por defecto si la API falla
            logger.warning(f"⚠️ No se pudo obtener clima real para {request.origen}, usando datos por defecto")
            # Generar clima aleatorio realista para evitar valores estáticos
            # Esto mejora la experiencia en desarrollo/offline
            rnd_temp = np.random.uniform(15.0, 30.0)
            rnd_hum = np.random.randint(40, 80)
            rnd_wind = np.random.uniform(2.0, 15.0)
            
            logger.warning(f"⚠️ No se pudo obtener clima real para {request.origen}, simulando clima: {rnd_temp:.1f}°C")
            
            clima_origen = WeatherData(
                temperatura=float(round(rnd_temp, 1)),
                humedad=int(rnd_hum),
                presion=1013,
                visibilidad=10000,
                viento_velocidad=float(round(rnd_wind, 1)),
                condicion="Clouds" if rnd_hum > 60 else "Clear",
                descripcion="nublado" if rnd_hum > 60 else "cielo claro"
            )
        else:
            logger.info(f"🌤️ Clima obtenido: {clima_origen.descripcion}, {clima_origen.temperatura}°C")
        
        # ====================================================================
        # 4. PREPARACIÓN DE FEATURES Y PREDICCIÓN
        # ====================================================================
        if model is None:
            logger.error("❌ Modelo ML no está cargado")
            raise HTTPException(status_code=503, detail="Modelo ML no disponible")
        
        logger.debug("Preparando features para el modelo")
        features_df = preparar_features_modelo(
            request.aerolinea,
            distancia_km,
            clima_origen,
            request.fecha_partida
        )
        
        logger.debug(f"Features preparadas: {features_df.shape[1]} columnas")
        
        # Realizar predicción
        try:
            logger.debug("Ejecutando predicción del modelo")
            # Obtener probabilidades [prob_puntual, prob_retrasado]
            probabilidades = model.predict_proba(features_df)[0]
            prob_retraso = float(probabilidades[1])
            
            # Predicción binaria
            prediccion_binaria = model.predict(features_df)[0]
            prediccion_texto = "Retrasado" if prediccion_binaria == 1 else "Puntual"
            
            # Confianza (máxima probabilidad)
            confianza = float(max(probabilidades))
            
            logger.info(f"✅ Predicción completada: {prediccion_texto} (prob_retraso: {prob_retraso:.2%}, confianza: {confianza:.2%})")
            
        except Exception as e:
            logger.error(f"❌ Error en predicción del modelo: {e}", exc_info=True)
            raise HTTPException(status_code=500, detail=f"Error en predicción del modelo: {str(e)}")
        
        # ====================================================================
        # 5. CONSTRUCCIÓN DE RESPUESTA
        # ====================================================================
        response = PredictionResponse(
            prediccion=prediccion_texto,
            probabilidad_retraso=round(prob_retraso, 4),
            confianza=round(confianza, 4),
            distancia_km=distancia_km,
            clima_origen=clima_origen,
            metadata={
                "aerolinea": request.aerolinea,
                "ruta": f"{request.origen} → {request.destino}",
                "origen_nombre": origen_coords["name"],
                "destino_nombre": destino_coords["name"],
                "fecha_partida": request.fecha_partida,
                "timestamp_prediccion": datetime.now().isoformat()
            }
        )
        
        logger.info(f"📤 Respuesta preparada exitosamente para {request.origen} → {request.destino}")
        return response
        
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"❌ Error inesperado en predict_internal: {e}", exc_info=True)
        raise HTTPException(status_code=500, detail=f"Error interno del servidor: {str(e)}")


# ============================================================================
# PUNTO DE ENTRADA
# ============================================================================
if __name__ == "__main__":
    import uvicorn
    uvicorn.run(
        "main:app",
        host="0.0.0.0",
        port=8001,
        reload=True,
        log_level="info"
    )
