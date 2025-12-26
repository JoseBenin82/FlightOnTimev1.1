import sys
import os
import joblib
import pandas as pd
from pydantic import ValidationError
from main import PredictionResponse, WeatherData, preparar_features_modelo

# Mock helpers
def test_ml_flow():
    print("--- 1. Testing Model Loading ---")
    model_path = "model.pkl"
    if not os.path.exists(model_path):
        print(f"❌ Model file not found at {model_path}")
        sys.exit(1)
    
    try:
        model = joblib.load(model_path)
        print("✅ Model loaded successfully")
    except Exception as e:
        print(f"❌ Failed to load model: {e}")
        sys.exit(1)

    print("\n--- 2. Testing Feature Preparation & Prediction ---")
    # Simulate data
    weather = WeatherData(
        temperatura=25.0,
        humedad=80,
        presion=1012,
        visibilidad=10000,
        viento_velocidad=10.0,
        condicion="Rain",
        descripcion="light rain"
    )
    
    features_df = preparar_features_modelo(
        aerolinea="LATAM",
        distancia_km=5000.0,
        clima=weather,
        fecha_partida="2025-12-25T14:00:00"
    )
    
    print(f"Features created: {features_df.shape}")
    
    # Predict
    try:
        probs = model.predict_proba(features_df)[0]
        binary = model.predict(features_df)[0]
        
        prob_delay = float(probs[1])
        confidence = float(max(probs))
        prediction = "Retrasado" if binary == 1 else "Puntual"
        
        print(f"✅ Prediction Result: {prediction}")
        print(f"✅ Probability (Delay): {prob_delay:.4f}")
        print(f"✅ Confidence: {confidence:.4f}")
        
        # Verify strict binary 0/1 logic requested
        if binary not in [0, 1]:
            print(f"❌ Binary prediction returned unexpected value: {binary}")
        else:
            print("✅ Binary classification is valid (0 or 1)")
            
    except Exception as e:
        print(f"❌ Prediction failed: {e}")
        sys.exit(1)

    print("\n--- 3. Testing Response Format (Schema Validation) ---")
    try:
        # Construct response to verify schema match
        resp = PredictionResponse(
            prevision=prediction,
            probabilidad=prob_delay,
            confianza=confidence,
            distancia_km=5000.0,
            clima_origen=weather,
            metadata={"test": "true"}
        )
        # Export to JSON
        json_out = resp.model_dump_json() if hasattr(resp, 'model_dump_json') else resp.json()
        print(f"✅ JSON Output looks correct:\n{json_out}")
        
    except ValidationError as e:
        print(f"❌ JSON Schema Validation failed: {e}")
        sys.exit(1)
        
    print("\n✅ VALIDATION SUCCESSFUL: System meets all ML and Data requirements.")

if __name__ == "__main__":
    test_ml_flow()
