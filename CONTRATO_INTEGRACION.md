# ============================================================================
# CONTRATO DE INTEGRACIÓN - FLIGHTONTIME API
# ============================================================================
# Documento técnico que define el contrato JSON entre servicios
# ============================================================================

## 1. INFORMACIÓN GENERAL

**Versión del Contrato**: 1.0.0  
**Fecha**: 2025-12-25  
**Propietario**: Oracle Enterprise Partner  
**Protocolo**: HTTP/HTTPS  
**Formato**: JSON  
**Encoding**: UTF-8  

---

## 2. ENDPOINTS

### 2.1 Backend API (Puerto 8080)

**Base URL**: `http://localhost:8080/api`

#### 2.1.1 POST /predict

**Descripción**: Realiza una predicción de puntualidad de vuelo

**Headers Requeridos**:
```
Content-Type: application/json
```

**Query Parameters**:
| Parámetro | Tipo    | Requerido | Default | Descripción                    |
|-----------|---------|-----------|---------|--------------------------------|
| mock      | boolean | No        | false   | Si true, usa respuesta mock    |

**Request Body**:
```json
{
  "aerolinea": "string",      // REQUERIDO: Código de aerolínea
  "origen": "string",         // REQUERIDO: Código IATA (3 letras mayúsculas)
  "destino": "string",        // REQUERIDO: Código IATA (3 letras mayúsculas)
  "fecha_partida": "string"   // OPCIONAL: ISO-8601 (ej: "2025-12-25T14:30:00")
}
```

**Validaciones**:
- `aerolinea`: No puede estar vacío
- `origen`: Debe ser exactamente 3 letras mayúsculas (regex: `^[A-Z]{3}$`)
- `destino`: Debe ser exactamente 3 letras mayúsculas (regex: `^[A-Z]{3}$`)
- `origen` ≠ `destino`: Deben ser diferentes
- `fecha_partida`: Debe ser formato ISO-8601 válido (si se proporciona)

**Response Body (200 OK)**:
```json
{
  "prediccion": integer,               // 0 = Puntual, 1 = Retrasado
  "probabilidad_retraso": number,      // 0.0 - 1.0
  "confianza": number,                 // 0.0 - 1.0
  "distancia_km": number,              // Distancia calculada en km
  "clima_origen": {
    "temperatura": number,             // Grados Celsius
    "humedad": integer,                // Porcentaje (0-100)
    "presion": integer,                // hPa
    "visibilidad": integer,            // Metros
    "viento_velocidad": number,        // m/s
    "condicion": "string",             // Ej: "Clear", "Clouds", "Rain"
    "descripcion": "string"            // Descripción en español
  },
  "metadata": {
    "aerolinea": "string",
    "ruta": "string",                  // Formato: "XXX → YYY"
    "origen_nombre": "string",
    "destino_nombre": "string",
    "fecha_partida": "string",         // ISO-8601
    "timestamp_prediccion": "string"   // ISO-8601
  },
  "modo_mock": boolean                 // true si se usó modo mock
}
```

**Response Codes**:
- `200 OK`: Predicción exitosa
- `400 Bad Request`: Validación fallida
- `500 Internal Server Error`: Error interno del servidor
- `503 Service Unavailable`: Servicio ML no disponible

**Ejemplo de Error (400)**:
```json
{
  "error": "Validación fallida",
  "campos": {
    "origen": "El código de origen debe ser 3 letras mayúsculas (ej: GRU)",
    "destino": "El aeropuerto de destino es obligatorio"
  }
}
```

#### 2.1.2 GET /health

**Descripción**: Verifica el estado del backend

**Response Body (200 OK)**:
```json
{
  "status": "string",      // "UP" o "DOWN"
  "service": "string",     // "FlightOnTime Backend"
  "version": "string",     // "1.0.0"
  "timestamp": number      // Unix timestamp en milisegundos
}
```

#### 2.1.3 GET /docs

**Descripción**: Documentación automática de endpoints

**Response Body (200 OK)**:
```json
{
  "servicio": "string",
  "version": "string",
  "endpoints": {
    "predict": {
      "metodo": "string",
      "url": "string",
      "descripcion": "string",
      "parametros": {},
      "body_ejemplo": {}
    },
    "health": {
      "metodo": "string",
      "url": "string",
      "descripcion": "string"
    }
  }
}
```

---

### 2.2 ML Service API (Puerto 8001)

**Base URL**: `http://localhost:8001`

#### 2.2.1 POST /predict_internal

**Descripción**: Endpoint interno para predicción (llamado por backend)

**Headers Requeridos**:
```
Content-Type: application/json
```

**Request Body**:
```json
{
  "aerolinea": "string",
  "origen": "string",
  "destino": "string",
  "fecha_partida": "string"
}
```

**Response Body (200 OK)**:
```json
{
  "prediccion": integer,               // 0 = Puntual, 1 = Retrasado
  "probabilidad_retraso": number,
  "confianza": number,
  "distancia_km": number,
  "clima_origen": {
    "temperatura": number,
    "humedad": integer,
    "presion": integer,
    "visibilidad": integer,
    "viento_velocidad": number,
    "condicion": "string",
    "descripcion": "string"
  },
  "metadata": {
    "aerolinea": "string",
    "ruta": "string",
    "origen_nombre": "string",
    "destino_nombre": "string",
    "fecha_partida": "string",
    "timestamp_prediccion": "string"
  }
}
```

**Response Codes**:
- `200 OK`: Predicción exitosa
- `400 Bad Request`: Aeropuerto no encontrado
- `500 Internal Server Error`: Error en predicción
- `503 Service Unavailable`: Modelo no disponible

#### 2.2.2 GET /airports

**Descripción**: Lista todos los aeropuertos disponibles

**Response Body (200 OK)**:
```json
{
  "total": integer,
  "aeropuertos": [
    {
      "codigo": "string",
      "nombre": "string",
      "lat": number,
      "lon": number
    }
  ]
}
```

#### 2.2.3 GET /health

**Descripción**: Health check del servicio ML

**Response Body (200 OK)**:
```json
{
  "status": "string",      // "healthy"
  "modelo": "string",      // "cargado"
  "timestamp": "string"    // ISO-8601
}
```

**Response Codes**:
- `200 OK`: Servicio saludable
- `503 Service Unavailable`: Modelo no disponible

---

## 3. TIPOS DE DATOS

### 3.1 Códigos IATA

**Formato**: 3 letras mayúsculas  
**Regex**: `^[A-Z]{3}$`  
**Ejemplos válidos**: `GRU`, `JFK`, `MEX`, `LHR`  
**Ejemplos inválidos**: `gru`, `JF`, `JFKK`, `123`

### 3.2 Códigos de Aerolínea

**Formato**: String (sin restricciones estrictas)  
**Ejemplos**: `LATAM`, `GOL`, `AZUL`, `AVIANCA`, `COPA`, `AMERICAN`, `UNITED`, `DELTA`

### 3.3 Fecha/Hora (ISO-8601)

**Formato**: `YYYY-MM-DDTHH:mm:ss` o `YYYY-MM-DDTHH:mm:ssZ`  
**Ejemplos válidos**:
- `2025-12-25T14:30:00`
- `2025-12-25T14:30:00Z`
- `2025-12-25T14:30:00-06:00`

### 3.4 Predicción

**Valores permitidos**: `0` (Puntual) o `1` (Retrasado)  
**Tipo**: Integer  
**Descripción**: Según alcance sugerido del proyecto

### 3.5 Probabilidad/Confianza

**Rango**: 0.0 - 1.0 (float)  
**Formato**: Hasta 4 decimales  
**Ejemplos**: `0.15`, `0.8523`, `1.0`

### 3.6 Distancia

**Unidad**: Kilómetros  
**Tipo**: Float  
**Rango**: 0.0 - 20000.0 (aproximadamente)  
**Formato**: Hasta 2 decimales

---

## 4. CÓDIGOS DE ESTADO HTTP

| Código | Significado              | Cuándo se usa                                    |
|--------|--------------------------|--------------------------------------------------|
| 200    | OK                       | Operación exitosa                                |
| 400    | Bad Request              | Validación fallida o datos inválidos             |
| 404    | Not Found                | Endpoint no existe                               |
| 500    | Internal Server Error    | Error inesperado en el servidor                  |
| 503    | Service Unavailable      | Servicio dependiente no disponible               |

---

## 5. REGLAS DE NEGOCIO

### 5.1 Validaciones Obligatorias

1. **Aeropuerto de origen y destino deben ser diferentes**
   - Si `origen == destino` → Error 400

2. **Códigos IATA deben existir en el diccionario**
   - Si código no existe → Error 400 con mensaje descriptivo

3. **Fecha de partida debe ser futura** (opcional, no implementado actualmente)
   - Si `fecha_partida < now()` → Warning (no error)

### 5.2 Cálculo de Distancia

- **Método**: Fórmula de Haversine
- **Entrada**: Coordenadas lat/lon de origen y destino
- **Salida**: Distancia en kilómetros (redondeada a 2 decimales)
- **Responsable**: ML Service

### 5.3 Integración Meteorológica

- **API**: OpenWeatherMap
- **Endpoint**: `/data/2.5/weather`
- **Parámetros**: lat, lon, appid, units=metric, lang=es
- **Fallback**: Si la API falla, usar valores por defecto:
  ```json
  {
    "temperatura": 20.0,
    "humedad": 60,
    "presion": 1013,
    "visibilidad": 10000,
    "viento_velocidad": 5.0,
    "condicion": "Clear",
    "descripcion": "cielo claro"
  }
  ```

### 5.4 Modo Mock

- **Activación**: Query parameter `?mock=true`
- **Comportamiento**: 
  - No llama al ML Service
  - Retorna respuesta estática predefinida
  - Siempre predice "Puntual" con probabilidad 0.15
  - Usa clima mock
- **Uso**: Pruebas, demos, QA

---

## 6. EJEMPLOS DE INTEGRACIÓN

### 6.1 JavaScript (Fetch API)

```javascript
const requestData = {
  aerolinea: "LATAM",
  origen: "GRU",
  destino: "JFK",
  fecha_partida: "2025-12-25T14:30:00"
};

fetch('http://localhost:8080/api/predict', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify(requestData)
})
  .then(response => response.json())
  .then(data => console.log(data))
  .catch(error => console.error('Error:', error));
```

### 6.2 Python (requests)

```python
import requests

url = "http://localhost:8080/api/predict"
payload = {
    "aerolinea": "GOL",
    "origen": "GRU",
    "destino": "GIG",
    "fecha_partida": "2025-12-26T08:00:00"
}

response = requests.post(url, json=payload)
print(response.json())
```

### 6.3 cURL

```bash
curl -X POST http://localhost:8080/api/predict \
  -H "Content-Type: application/json" \
  -d '{
    "aerolinea": "AZUL",
    "origen": "BSB",
    "destino": "CNF",
    "fecha_partida": "2025-12-27T10:00:00"
  }'
```

### 6.4 Java (Spring WebClient)

```java
WebClient webClient = WebClient.create("http://localhost:8080");

PredictionRequestDTO request = PredictionRequestDTO.builder()
    .aerolinea("AVIANCA")
    .origen("BOG")
    .destino("MEX")
    .fechaPartida("2025-12-28T12:00:00")
    .build();

PredictionResponseDTO response = webClient.post()
    .uri("/api/predict")
    .bodyValue(request)
    .retrieve()
    .bodyToMono(PredictionResponseDTO.class)
    .block();

System.out.println(response);
```

---

## 7. VERSIONADO

**Estrategia de versionado**: Semantic Versioning (SemVer)

- **MAJOR**: Cambios incompatibles en la API
- **MINOR**: Nuevas funcionalidades compatibles
- **PATCH**: Correcciones de bugs

**Versión actual**: 1.0.0

**Cambios futuros**:
- Si se agrega un campo opcional → MINOR
- Si se cambia el tipo de un campo → MAJOR
- Si se corrige un bug → PATCH

---

## 8. SEGURIDAD

### 8.1 Actual (Desarrollo)

- ✅ CORS habilitado para todos los orígenes (`*`)
- ✅ Validación de entrada en DTOs
- ✅ Sanitización de códigos IATA (regex)

### 8.2 Recomendaciones para Producción

- [ ] Implementar autenticación (JWT, OAuth2)
- [ ] Restringir CORS a dominios específicos
- [ ] Rate limiting para prevenir abuso
- [ ] HTTPS obligatorio
- [ ] Validación de API keys
- [ ] Logging de auditoría

---

## 9. PERFORMANCE

### 9.1 Tiempos de Respuesta Esperados

| Endpoint           | Tiempo Promedio | Timeout |
|--------------------|-----------------|---------|
| POST /predict      | 500-2000ms      | 10s     |
| POST /predict?mock | 50-100ms        | 5s      |
| GET /health        | 10-50ms         | 3s      |
| GET /airports      | 10-30ms         | 3s      |

### 9.2 Límites

- **Tamaño máximo de request**: 1 MB
- **Tamaño máximo de response**: 5 MB
- **Requests concurrentes**: Sin límite (depende del hardware)

---

## 10. SOPORTE Y CONTACTO

**Documentación**: http://localhost:8080/api/docs  
**Health Checks**: 
- Backend: http://localhost:8080/api/health
- ML Service: http://localhost:8001/health

**Equipo de Desarrollo**: Oracle Enterprise Partner  
**Versión del Documento**: 1.0.0  
**Última Actualización**: 2025-12-25

---

**FIN DEL CONTRATO DE INTEGRACIÓN**
