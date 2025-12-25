"""
============================================================================
SCRIPT DE ENTRENAMIENTO DEL MODELO ML - FLIGHTONTIME
============================================================================
Este script genera un modelo de clasificación para predecir retrasos de vuelos
basándose en características como distancia, clima, aerolínea, etc.
============================================================================
"""

import pandas as pd
import numpy as np
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split
from sklearn.metrics import classification_report, accuracy_score
import joblib
import logging

# Configurar logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

def generar_datos_entrenamiento(n_samples=5000):
    """
    Genera datos sintéticos para entrenar el modelo.
    
    Features:
    - distancia_km: Distancia del vuelo
    - temperatura: Temperatura en el aeropuerto de origen
    - humedad: Humedad relativa
    - presion: Presión atmosférica
    - visibilidad: Visibilidad en metros
    - viento_velocidad: Velocidad del viento
    - hora: Hora de partida (0-23)
    - dia_semana: Día de la semana (0-6)
    - mes: Mes del año (1-12)
    - aerolinea_LATAM, aerolinea_GOL, aerolinea_AZUL, aerolinea_AVIANCA, aerolinea_COPA
    
    Target:
    - retraso: 0 = Puntual, 1 = Retrasado
    """
    
    logger.info(f"Generando {n_samples} muestras de entrenamiento...")
    
    np.random.seed(42)
    
    # Generar features
    data = {
        # Distancia: 100 km a 10,000 km
        'distancia_km': np.random.uniform(100, 10000, n_samples),
        
        # Clima
        'temperatura': np.random.uniform(-10, 40, n_samples),  # -10°C a 40°C
        'humedad': np.random.randint(20, 100, n_samples),  # 20% a 100%
        'presion': np.random.randint(980, 1040, n_samples),  # 980 hPa a 1040 hPa
        'visibilidad': np.random.randint(1000, 10000, n_samples),  # 1 km a 10 km
        'viento_velocidad': np.random.uniform(0, 30, n_samples),  # 0 m/s a 30 m/s
        
        # Temporales
        'hora': np.random.randint(0, 24, n_samples),
        'dia_semana': np.random.randint(0, 7, n_samples),
        'mes': np.random.randint(1, 13, n_samples),
    }
    
    # One-hot encoding para aerolíneas
    aerolineas = ['LATAM', 'GOL', 'AZUL', 'AVIANCA', 'COPA']
    aerolinea_seleccionada = np.random.choice(aerolineas, n_samples)
    
    for airline in aerolineas:
        data[f'aerolinea_{airline}'] = (aerolinea_seleccionada == airline).astype(int)
    
    df = pd.DataFrame(data)
    
    # Generar target basado en reglas lógicas
    # Factores que aumentan probabilidad de retraso:
    # 1. Mal clima (baja visibilidad, viento fuerte, temperatura extrema)
    # 2. Horas pico (7-9 AM, 5-7 PM)
    # 3. Distancias muy largas
    # 4. Ciertos días de la semana (viernes, domingo)
    
    retraso_score = np.zeros(n_samples)
    
    # Factor clima
    retraso_score += (df['visibilidad'] < 3000) * 0.3  # Baja visibilidad
    retraso_score += (df['viento_velocidad'] > 15) * 0.2  # Viento fuerte
    retraso_score += ((df['temperatura'] < 0) | (df['temperatura'] > 35)) * 0.15  # Temperatura extrema
    retraso_score += (df['humedad'] > 85) * 0.1  # Alta humedad
    
    # Factor temporal
    retraso_score += ((df['hora'] >= 7) & (df['hora'] <= 9)) * 0.15  # Hora pico mañana
    retraso_score += ((df['hora'] >= 17) & (df['hora'] <= 19)) * 0.15  # Hora pico tarde
    retraso_score += ((df['dia_semana'] == 4) | (df['dia_semana'] == 6)) * 0.1  # Viernes y domingo
    
    # Factor distancia
    retraso_score += (df['distancia_km'] > 5000) * 0.15  # Vuelos muy largos
    
    # Agregar ruido aleatorio
    retraso_score += np.random.uniform(-0.2, 0.2, n_samples)
    
    # Convertir a binario (umbral 0.5)
    df['retraso'] = (retraso_score > 0.5).astype(int)
    
    logger.info(f"Distribución de clases:")
    logger.info(f"  Puntual (0): {(df['retraso'] == 0).sum()} ({(df['retraso'] == 0).sum() / n_samples * 100:.1f}%)")
    logger.info(f"  Retrasado (1): {(df['retraso'] == 1).sum()} ({(df['retraso'] == 1).sum() / n_samples * 100:.1f}%)")
    
    return df


def entrenar_modelo(df):
    """
    Entrena un modelo Random Forest para clasificación de retrasos.
    """
    logger.info("Preparando datos para entrenamiento...")
    
    # Separar features y target
    X = df.drop('retraso', axis=1)
    y = df['retraso']
    
    logger.info(f"Features utilizadas: {list(X.columns)}")
    logger.info(f"Forma de X: {X.shape}")
    
    # Split train/test
    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.2, random_state=42, stratify=y
    )
    
    logger.info(f"Conjunto de entrenamiento: {X_train.shape[0]} muestras")
    logger.info(f"Conjunto de prueba: {X_test.shape[0]} muestras")
    
    # Entrenar modelo
    logger.info("Entrenando Random Forest Classifier...")
    model = RandomForestClassifier(
        n_estimators=100,
        max_depth=10,
        min_samples_split=10,
        min_samples_leaf=5,
        random_state=42,
        n_jobs=-1
    )
    
    model.fit(X_train, y_train)
    
    # Evaluar modelo
    logger.info("Evaluando modelo...")
    y_pred = model.predict(X_test)
    accuracy = accuracy_score(y_test, y_pred)
    
    logger.info(f"\n{'='*60}")
    logger.info(f"RESULTADOS DEL MODELO")
    logger.info(f"{'='*60}")
    logger.info(f"Accuracy: {accuracy:.4f} ({accuracy*100:.2f}%)")
    logger.info(f"\nReporte de clasificación:")
    logger.info(f"\n{classification_report(y_test, y_pred, target_names=['Puntual', 'Retrasado'])}")
    
    # Importancia de features
    feature_importance = pd.DataFrame({
        'feature': X.columns,
        'importance': model.feature_importances_
    }).sort_values('importance', ascending=False)
    
    logger.info(f"\nTop 10 Features más importantes:")
    logger.info(f"\n{feature_importance.head(10).to_string(index=False)}")
    
    return model


def guardar_modelo(model, filepath='model.pkl'):
    """
    Guarda el modelo entrenado en un archivo .pkl
    """
    logger.info(f"Guardando modelo en {filepath}...")
    joblib.dump(model, filepath)
    logger.info(f"✅ Modelo guardado exitosamente")
    
    # Verificar que se puede cargar
    logger.info("Verificando que el modelo se puede cargar...")
    loaded_model = joblib.load(filepath)
    logger.info("✅ Modelo cargado exitosamente")
    
    return loaded_model


def probar_modelo(model):
    """
    Prueba el modelo con algunos casos de ejemplo
    """
    logger.info("\n" + "="*60)
    logger.info("PRUEBAS DEL MODELO")
    logger.info("="*60)
    
    # Caso 1: Vuelo corto, buen clima, hora normal
    test_case_1 = pd.DataFrame([{
        'distancia_km': 500,
        'temperatura': 22,
        'humedad': 60,
        'presion': 1013,
        'visibilidad': 10000,
        'viento_velocidad': 5,
        'hora': 14,
        'dia_semana': 2,
        'mes': 6,
        'aerolinea_LATAM': 1,
        'aerolinea_GOL': 0,
        'aerolinea_AZUL': 0,
        'aerolinea_AVIANCA': 0,
        'aerolinea_COPA': 0
    }])
    
    pred_1 = model.predict(test_case_1)[0]
    prob_1 = model.predict_proba(test_case_1)[0]
    logger.info(f"\nCaso 1: Vuelo corto, buen clima")
    logger.info(f"  Predicción: {'Retrasado' if pred_1 == 1 else 'Puntual'}")
    logger.info(f"  Probabilidad retraso: {prob_1[1]:.2%}")
    logger.info(f"  Confianza: {max(prob_1):.2%}")
    
    # Caso 2: Vuelo largo, mal clima, hora pico
    test_case_2 = pd.DataFrame([{
        'distancia_km': 8000,
        'temperatura': -5,
        'humedad': 90,
        'presion': 990,
        'visibilidad': 2000,
        'viento_velocidad': 20,
        'hora': 18,
        'dia_semana': 4,
        'mes': 12,
        'aerolinea_LATAM': 0,
        'aerolinea_GOL': 1,
        'aerolinea_AZUL': 0,
        'aerolinea_AVIANCA': 0,
        'aerolinea_COPA': 0
    }])
    
    pred_2 = model.predict(test_case_2)[0]
    prob_2 = model.predict_proba(test_case_2)[0]
    logger.info(f"\nCaso 2: Vuelo largo, mal clima, hora pico")
    logger.info(f"  Predicción: {'Retrasado' if pred_2 == 1 else 'Puntual'}")
    logger.info(f"  Probabilidad retraso: {prob_2[1]:.2%}")
    logger.info(f"  Confianza: {max(prob_2):.2%}")
    
    # Caso 3: Vuelo medio, clima moderado
    test_case_3 = pd.DataFrame([{
        'distancia_km': 2500,
        'temperatura': 18,
        'humedad': 70,
        'presion': 1010,
        'visibilidad': 8000,
        'viento_velocidad': 10,
        'hora': 10,
        'dia_semana': 1,
        'mes': 3,
        'aerolinea_LATAM': 0,
        'aerolinea_GOL': 0,
        'aerolinea_AZUL': 1,
        'aerolinea_AVIANCA': 0,
        'aerolinea_COPA': 0
    }])
    
    pred_3 = model.predict(test_case_3)[0]
    prob_3 = model.predict_proba(test_case_3)[0]
    logger.info(f"\nCaso 3: Vuelo medio, clima moderado")
    logger.info(f"  Predicción: {'Retrasado' if pred_3 == 1 else 'Puntual'}")
    logger.info(f"  Probabilidad retraso: {prob_3[1]:.2%}")
    logger.info(f"  Confianza: {max(prob_3):.2%}")


if __name__ == "__main__":
    logger.info("="*60)
    logger.info("ENTRENAMIENTO DEL MODELO FLIGHTONTIME")
    logger.info("="*60)
    
    # 1. Generar datos
    df = generar_datos_entrenamiento(n_samples=5000)
    
    # 2. Entrenar modelo
    model = entrenar_modelo(df)
    
    # 3. Guardar modelo
    guardar_modelo(model, 'model.pkl')
    
    # 4. Probar modelo
    probar_modelo(model)
    
    logger.info("\n" + "="*60)
    logger.info("✅ PROCESO COMPLETADO EXITOSAMENTE")
    logger.info("="*60)
