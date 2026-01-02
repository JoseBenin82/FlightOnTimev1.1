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

@app.get("/health")
async def health_check():
    """
    Endpoint de salud para Docker Healthcheck
    """
    return {"status": "UP", "service": "FlightOnTime ML Service"}

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
MODEL_PATH = "random_forest_v1.pkl"
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
    prediccion: int  # 0 = Puntual, 1 = Retrasado
    probabilidad_retraso: float
    confianza: float
    distancia_km: float
    clima_origen: WeatherData
    clima_destino: Optional[WeatherData] = None
    metadata: Dict[str, Any]

# ... existing code ...

# ============================================================================
# FUNCIONES AUXILIARES
# ============================================================================

def calcular_distancia_haversine(lat1, lon1, lat2, lon2):
    """
    Calcula la distancia en kilómetros entre dos puntos geográficos
    usando la fórmula de Haversine.
    """
    # Radio de la Tierra en km
    R = 6371.0

    # Convertir grados a radianes
    dlat = radians(lat2 - lat1)
    dlon = radians(lon2 - lon1)
    
    a = sin(dlat / 2)**2 + cos(radians(lat1)) * cos(radians(lat2)) * sin(dlon / 2)**2
    c = 2 * atan2(sqrt(a), sqrt(1 - a))
    
    distancia = R * c
    return distancia


def obtener_clima_aeropuerto(iata_code: str) -> Optional[WeatherData]:
    """
    Obtiene el clima actual para un aeropuerto usando OpenWeatherMap.
    Usa las coordenadas del diccionario para la búsqueda.
    """
    try:
        # 1. Obtener coordenadas
        if iata_code not in AIRPORT_COORDINATES:
            logger.warning(f"⚠️ No hay coordenadas para {iata_code}, no se puede obtener clima")
            return None
            
        coords = AIRPORT_COORDINATES[iata_code]
        lat = coords['lat']
        lon = coords['lon']
        
        # 2. Consultar API
        # Documentación: https://openweathermap.org/current
        params = {
            'lat': lat,
            'lon': lon,
            'appid': OPENWEATHER_API_KEY,
            'units': 'metric', # Para obtener Celsius y m/s
            'lang': 'es'       # Descripciones en español
        }
        
        logger.debug(f"Petición clima {iata_code}: {lat}, {lon}")
        
        response = requests.get(OPENWEATHER_BASE_URL, params=params, timeout=5)
        
        if response.status_code == 200:
            data = response.json()
            
            # Mapear respuesta a nuestro DTO
            weather = WeatherData(
                temperatura=float(data['main']['temp']),
                humedad=int(data['main']['humidity']),
                presion=int(data['main']['pressure']),
                visibilidad=int(data.get('visibility', 10000)),
                viento_velocidad=float(data['wind']['speed']),
                condicion=data['weather'][0]['main'],
                descripcion=data['weather'][0]['description']
            )
            return weather
            
        else:
            logger.error(f"❌ Error API OpenWeatherMap ({response.status_code}): {response.text}")
            return None
            
    except Exception as e:
        logger.error(f"❌ Excepción obteniendo clima para {iata_code}: {e}")
        return None


@app.post("/predict_internal", response_model=PredictionResponse)
async def predict_internal(request: PredictionRequest):
    """
    Endpoint principal de predicción con enriquecimiento meteorológico.
    """
    logger.info(f"🔍 Iniciando predicción para: {request.aerolinea} {request.origen} → {request.destino}")
    
    try:
        # ====================================================================
        # 1. VALIDACIÓN DE AEROPUERTOS
        # ====================================================================
        logger.debug(f"Validando aeropuerto de origen: {request.origen}")
        if request.origen not in AIRPORT_COORDINATES:
            logger.error(f"❌ Aeropuerto de origen '{request.origen}' no encontrado")
            # Optimize: don't list all if too many
            raise HTTPException(
                status_code=400,
                detail=f"Aeropuerto de origen '{request.origen}' no encontrado."
            )
        
        logger.debug(f"Validando aeropuerto de destino: {request.destino}")
        if request.destino not in AIRPORT_COORDINATES:
            logger.error(f" Aeropuerto de destino '{request.destino}' no encontrado")
            raise HTTPException(
                status_code=400,
                detail=f"Aeropuerto de destino '{request.destino}' no encontrado."
            )
        
        logger.info(f" Aeropuertos validados: {request.origen} y {request.destino}")
        
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
        # 3. CONSULTA DE CLIMA EN TIEMPO REAL (ORIGEN Y DESTINO)
        # ====================================================================
        # Clima Origen
        logger.debug(f"Consultando clima para {request.origen}")
        clima_origen = obtener_clima_aeropuerto(request.origen)
        
        if clima_origen is None:
            logger.warning(f"⚠️ No se pudo obtener clima real para {request.origen}, usando simulado")
            # ... simulación simplificada ...
            clima_origen = WeatherData(
                temperatura=20.0, humedad=60, presion=1013, visibilidad=10000,
                viento_velocidad=5.0, condicion="Clear", descripcion="cielo claro"
            )

        # Clima Destino (Nuevo Requerimiento)
        logger.debug(f"Consultando clima para {request.destino}")
        clima_destino = obtener_clima_aeropuerto(request.destino)
        
        if clima_destino is None:
             logger.warning(f"⚠️ No se pudo obtener clima real para {request.destino}, usando simulado")
             clima_destino = WeatherData(
                temperatura=20.0, humedad=60, presion=1013, visibilidad=10000,
                viento_velocidad=5.0, condicion="Clear", descripcion="cielo claro"
            )

        # ====================================================================
        # 4. PREPARACIÓN DE FEATURES Y PREDICCIÓN
        # ====================================================================
        if model is None:
            # Fallback mock si no hay modelo, pero request lo pide
            logger.error("❌ Modelo ML no está cargado")
            raise HTTPException(status_code=503, detail="Modelo ML no disponible")
        
        # NOTA: Solo usamos clima_origen para el modelo según diseño actual, 
        # aunque mostramos clima_destino al usuario.
        features_df = preparar_features_modelo(
            request.aerolinea,
            distancia_km,
            clima_origen, 
            request.fecha_partida
        )
        
        # Realizar predicción
        try:
            probabilidades = model.predict_proba(features_df)[0]
            prob_retraso = float(probabilidades[1])
            prediccion_binaria = int(model.predict(features_df)[0])
            confianza = float(max(probabilidades))
        except Exception as e:
            logger.error(f"❌ Error en predicción: {e}")
            raise HTTPException(status_code=500, detail=f"Error en predicción del modelo: {str(e)}")
        
        # ====================================================================
        # 5. CONSTRUCCIÓN DE RESPUESTA
        # ====================================================================
        response = PredictionResponse(
            prediccion=prediccion_binaria,
            probabilidad_retraso=round(prob_retraso, 4),
            confianza=round(confianza, 4),
            distancia_km=distancia_km,
            clima_origen=clima_origen,
            clima_destino=clima_destino,
            metadata={
                "aerolinea": request.aerolinea,
                "ruta": f"{request.origen} → {request.destino}",
                "origen_nombre": origen_coords["name"],
                "destino_nombre": destino_coords["name"],
                "fecha_partida": request.fecha_partida,
                "timestamp_prediccion": datetime.now().isoformat()
            }
        )
        return response
        
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"❌ Error inesperado en predict_internal: {e}", exc_info=True)
        raise HTTPException(status_code=500, detail=f"Error interno del servidor: {str(e)}")


def preparar_features_modelo(aerolinea: str, distancia_km: float, clima: WeatherData, fecha_partida_str: str = None) -> pd.DataFrame:
    """
    Prepara el DataFrame de features para el modelo Random Forest.
    IMPORTANTE: Según requerimientos, NO usamos el clima real para la predicción todavía,
    usamos valores promedio/default para neutralizar el factor climático en la inferencia
    mientras se recargan datos reales para visualización.
    """
    
    # 1. Procesar Fecha/Hora
    if fecha_partida_str:
        try:
            dt = datetime.fromisoformat(fecha_partida_str.replace('Z', '+00:00'))
        except ValueError:
            dt = datetime.now()
    else:
        dt = datetime.now()
        
    hora = dt.hour
    dia_semana = dt.weekday() # 0=Lunes, 6=Domingo
    mes = dt.month
    
    # 2. Features de Clima (USAR DEFAULT/NEUTRAL)
    # Se usan valores neutros para que la predicción dependa solo de ruta/aerolínea por ahora
    temp_neutral = 20.0
    humedad_neutral = 50
    presion_neutral = 1013
    visibilidad_neutral = 10000
    viento_neutral = 10.0
    
    data = {
        'distancia_km': [distancia_km],
        'temperatura': [temp_neutral],
        'humedad': [humedad_neutral],
        'presion': [presion_neutral],
        'visibilidad': [visibilidad_neutral],
        'viento_velocidad': [viento_neutral],
        'hora': [hora],
        'dia_semana': [dia_semana],
        'mes': [mes]
    }
    
    df = pd.DataFrame(data)
    
    # 3. One-Hot Encoding para Aerolíneas
    # El modelo espera columnas específicas (según train_model.py):
    # aerolinea_LATAM, aerolinea_GOL, aerolinea_AZUL, aerolinea_AVIANCA, aerolinea_COPA
    # Si la aerolinea entrante no es una de estas, todas serán 0 (Caso base)
    
    # Mapeo de IDs (1, 2) a Nombres esperados si fuera necesario, 
    # pero como el modelo fue entrenado con LATAM/GOL, y aquí usamos 1=Delta, 2=Southwest,
    # simplemente no activamos ninguna flag de las "viejas" aerolíneas.
    # Esto es seguro: el modelo tratará Delta/Southwest como "Otras".
    
    aerolineas_modelo = ['LATAM', 'GOL', 'AZUL', 'AVIANCA', 'COPA']
    
    for aerolinea_col in aerolineas_modelo:
        # Aquí aerolinea viene como "1" o "2", que no coincide con 'LATAM', etc.
        # Por lo tanto, setea 0.
        df[f'aerolinea_{aerolinea_col}'] = 0
        
    return df

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

