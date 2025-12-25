# ============================================================================
# ARQUITECTURA DE SOFTWARE - FLIGHTONTIME
# ============================================================================
# Documento Técnico de Arquitectura del Sistema
# Oracle Enterprise Partner | Sistema de Misión Crítica
# ============================================================================

![Versión](https://img.shields.io/badge/Versión-1.0.0-blue)
![Arquitectura](https://img.shields.io/badge/Arquitectura-Microservicios-green)
![Java](https://img.shields.io/badge/Java-17-orange)
![Python](https://img.shields.io/badge/Python-3.11-blue)

## 📋 Tabla de Contenidos

1. [Visión General](#-visión-general)
2. [Patrón Arquitectónico](#-patrón-arquitectónico)
3. [Componentes del Sistema](#-componentes-del-sistema)
4. [Flujo de Datos](#-flujo-de-datos)
5. [Decisiones Arquitectónicas](#-decisiones-arquitectónicas)
6. [Patrones de Diseño](#-patrones-de-diseño)
7. [Seguridad](#-seguridad)
8. [Escalabilidad y Resiliencia](#-escalabilidad-y-resiliencia)
9. [Integración y Comunicación](#-integración-y-comunicación)
10. [Infraestructura](#-infraestructura)

---

## 🎯 Visión General

**FlightOnTime** es un sistema empresarial de predicción de puntualidad de vuelos que implementa una **arquitectura de microservicios híbrida** con las siguientes características:

- **Desacoplamiento de servicios** por responsabilidad y tecnología
- **Orquestador central** para lógica de negocio empresarial
- **Servicio especializado de ML** para predicciones
- **Frontend desacoplado** con diseño moderno
- **Comunicación síncrona** vía HTTP/REST
- **Contrato de integración formal** en JSON

### Objetivos Arquitectónicos

✅ **Separación de Responsabilidades**: Cada servicio tiene un propósito único y bien definido  
✅ **Escalabilidad Independiente**: Los servicios pueden escalar de forma autónoma  
✅ **Tecnologías Especializadas**: Java para orquestación, Python para ML  
✅ **Mantenibilidad**: Código modular, documentado y siguiendo principios SOLID  
✅ **Resiliencia**: Fallbacks, health checks y manejo robusto de errores  
✅ **Observabilidad**: Logging estructurado y monitoreo de salud  

---

## 🏗️ Patrón Arquitectónico

### Arquitectura de Microservicios con Orquestador

```
┌─────────────────────────────────────────────────────────────────────┐
│                         USUARIO FINAL                               │
│                    (Navegador Web / Cliente)                        │
└────────────────────────────┬────────────────────────────────────────┘
                             │
                             │ HTTP/HTTPS
                             ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    CAPA DE PRESENTACIÓN                             │
│                                                                     │
│  ┌───────────────────────────────────────────────────────────┐    │
│  │  FRONTEND (Nginx + HTML/CSS/JavaScript)                   │    │
│  │  • Interfaz de usuario Oracle Redwood                     │    │
│  │  • Validación de formularios                              │    │
│  │  • Renderizado de resultados                              │    │
│  │  • Puerto: 80                                             │    │
│  └───────────────────────────────────────────────────────────┘    │
└────────────────────────────┬────────────────────────────────────────┘
                             │
                             │ HTTP REST API
                             ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    CAPA DE ORQUESTACIÓN                             │
│                                                                     │
│  ┌───────────────────────────────────────────────────────────┐    │
│  │  BACKEND (Java 17 + Spring Boot 3.2.1)                    │    │
│  │  • Orquestador empresarial                                │    │
│  │  • Validación de negocio                                  │    │
│  │  • Modo Mock/Real                                         │    │
│  │  • Contrato de integración JSON                           │    │
│  │  • Manejo de errores y fallbacks                          │    │
│  │  • Puerto: 8080                                           │    │
│  └───────────────────────────────────────────────────────────┘    │
└──────────────┬──────────────────────────────┬─────────────────────┘
               │                              │
               │ HTTP                         │ HTTP
               ▼                              ▼
┌──────────────────────────┐    ┌────────────────────────────────────┐
│   CAPA DE PREDICCIÓN     │    │   SERVICIOS EXTERNOS               │
│                          │    │                                    │
│  ┌────────────────────┐  │    │  ┌──────────────────────────────┐ │
│  │  ML SERVICE        │  │    │  │  OpenWeatherMap API          │ │
│  │  (FastAPI)         │  │    │  │  • Clima en tiempo real      │ │
│  │  • model.pkl       │  │    │  │  • Temperatura, humedad      │ │
│  │  • Haversine       │  │    │  │  • Presión, viento           │ │
│  │  • 40+ aeropuertos │  │    │  │  • Visibilidad               │ │
│  │  • Puerto: 8001    │  │    │  └──────────────────────────────┘ │
│  └────────────────────┘  │    │                                    │
└──────────────────────────┘    └────────────────────────────────────┘
```

### Características del Patrón

| Característica | Descripción |
|----------------|-------------|
| **Tipo** | Microservicios con Orquestador Central |
| **Comunicación** | Síncrona (HTTP/REST) |
| **Acoplamiento** | Bajo (servicios independientes) |
| **Cohesión** | Alta (responsabilidades bien definidas) |
| **Escalabilidad** | Horizontal (por servicio) |
| **Despliegue** | Independiente (Docker containers) |

---

## 🔧 Componentes del Sistema

### 1️⃣ Frontend (Capa de Presentación)

**Responsabilidad**: Interfaz de usuario y experiencia del cliente

```
frontend/
├── index.html          # Página principal
├── styles.css          # Estilos Oracle Redwood
├── app.js              # Lógica de aplicación
├── nginx.conf          # Configuración del servidor
└── Dockerfile          # Imagen Docker
```

**Tecnologías**:
- HTML5 (estructura semántica)
- CSS3 (Flexbox, Grid, Variables CSS, Animaciones)
- JavaScript ES6+ (Fetch API, Async/Await)
- Nginx (servidor web)

**Características**:
- ✅ Diseño responsive (mobile-first)
- ✅ Validación de formularios en cliente
- ✅ Loading states y manejo de errores
- ✅ Animaciones y micro-interacciones
- ✅ Paleta de colores Oracle Redwood

**Endpoints Consumidos**:
- `POST /api/predict` - Solicitud de predicción
- `POST /api/predict?mock=true` - Modo demo

---

### 2️⃣ Backend (Capa de Orquestación)

**Responsabilidad**: Orquestador empresarial y validación de negocio

```
backend/
├── src/main/java/com/oracle/flightontime/
│   ├── FlightOnTimeApplication.java      # Aplicación principal
│   ├── controller/
│   │   └── PredictionController.java     # Endpoints REST
│   ├── service/
│   │   └── PredictionService.java        # Lógica de negocio
│   ├── dto/
│   │   ├── PredictionRequestDTO.java     # Request DTO
│   │   ├── PredictionResponseDTO.java    # Response DTO
│   │   └── WeatherDataDTO.java           # Weather DTO
│   └── config/
│       └── WebClientConfig.java          # Cliente HTTP reactivo
├── src/main/resources/
│   └── application.properties            # Configuración
├── pom.xml                               # Dependencias Maven
└── Dockerfile                            # Imagen Docker
```

**Tecnologías**:
- Java 17 (LTS)
- Spring Boot 3.2.1
- Spring WebFlux (cliente HTTP reactivo)
- Lombok (reducción de boilerplate)
- Bean Validation (validación de DTOs)
- Maven (gestión de dependencias)

**Responsabilidades Principales**:

1. **Validación de Entrada**:
   ```java
   @NotBlank(message = "La aerolínea es obligatoria")
   private String aerolinea;
   
   @Pattern(regexp = "^[A-Z]{3}$", message = "Código IATA inválido")
   private String origen;
   ```

2. **Orquestación de Servicios**:
   - Llamada al ML Service
   - Enriquecimiento de respuesta
   - Manejo de timeouts (10s)

3. **Modo Híbrido**:
   - **Modo Real**: Integración completa con ML
   - **Modo Mock**: Respuesta estática para demos

4. **Manejo de Errores**:
   - Validación de negocio
   - Fallback si ML Service falla
   - Respuestas HTTP semánticas

**Endpoints Expuestos**:

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/predict` | Predicción de vuelo |
| POST | `/api/predict?mock=true` | Predicción en modo mock |
| GET | `/api/health` | Health check |
| GET | `/api/docs` | Documentación automática |

**Configuración** (`application.properties`):
```properties
server.port=8080
ml.service.url=http://ml-service:8001
ml.service.timeout=10
```

---

### 3️⃣ ML Service (Capa de Predicción)

**Responsabilidad**: Motor de predicción con Machine Learning

```
ml-service/
├── main.py                # Aplicación FastAPI
├── airport_coords.py      # Diccionario de aeropuertos (40+)
├── model.pkl              # Modelo ML entrenado (scikit-learn)
├── requirements.txt       # Dependencias Python
└── Dockerfile             # Imagen Docker
```

**Tecnologías**:
- Python 3.11
- FastAPI (framework web moderno)
- scikit-learn (modelo ML)
- pandas/numpy (manipulación de datos)
- requests (cliente HTTP)
- joblib (serialización del modelo)
- uvicorn (servidor ASGI)

**Componentes Internos**:

1. **Modelo ML** (`model.pkl`):
   - Tipo: Clasificador binario (Puntual/Retrasado)
   - Features: distancia, clima, aerolínea, fecha
   - Output: Predicción + probabilidad

2. **Diccionario de Aeropuertos** (`airport_coords.py`):
   ```python
   AIRPORT_COORDS = {
       "GRU": {"name": "São Paulo-Guarulhos", "lat": -23.4356, "lon": -46.4731},
       "JFK": {"name": "New York-JFK", "lat": 40.6413, "lon": -73.7781},
       # ... 40+ aeropuertos
   }
   ```

3. **Cálculo de Distancia** (Fórmula de Haversine):
   ```python
   def calcular_distancia_haversine(lat1, lon1, lat2, lon2):
       R = 6371  # Radio de la Tierra en km
       # ... cálculo geodésico
       return distancia_km
   ```

4. **Integración Meteorológica**:
   ```python
   def obtener_clima_aeropuerto(codigo_iata):
       # Consulta a OpenWeatherMap API
       # Retorna: temperatura, humedad, presión, viento, visibilidad
   ```

**Endpoints Expuestos**:

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/predict_internal` | Predicción interna (llamado por backend) |
| GET | `/airports` | Lista de aeropuertos disponibles |
| GET | `/health` | Health check del servicio |
| GET | `/` | Información del servicio |

**Flujo de Predicción**:

```python
1. Recibir solicitud → PredictionRequest
2. Validar códigos IATA → airport_coords.py
3. Calcular distancia → Haversine
4. Obtener clima → OpenWeatherMap API
5. Preparar features → DataFrame
6. Ejecutar predicción → model.pkl.predict()
7. Calcular probabilidad → model.pkl.predict_proba()
8. Construir respuesta → PredictionResponse
```

---

## 🔄 Flujo de Datos

### Flujo Completo de Predicción

```
┌──────────────────────────────────────────────────────────────────┐
│ PASO 1: Usuario ingresa datos en el formulario                  │
│ ┌──────────────────────────────────────────────────────────┐    │
│ │ Aerolínea: LATAM                                         │    │
│ │ Origen: GRU (São Paulo)                                  │    │
│ │ Destino: JFK (New York)                                  │    │
│ │ Fecha: 2025-12-25T14:30:00                               │    │
│ └──────────────────────────────────────────────────────────┘    │
└────────────────────────────┬─────────────────────────────────────┘
                             │
                             ▼
┌──────────────────────────────────────────────────────────────────┐
│ PASO 2: Frontend envía solicitud HTTP POST                      │
│ POST http://localhost:8080/api/predict                           │
│ Content-Type: application/json                                   │
│                                                                  │
│ {                                                                │
│   "aerolinea": "LATAM",                                          │
│   "origen": "GRU",                                               │
│   "destino": "JFK",                                              │
│   "fecha_partida": "2025-12-25T14:30:00"                         │
│ }                                                                │
└────────────────────────────┬─────────────────────────────────────┘
                             │
                             ▼
┌──────────────────────────────────────────────────────────────────┐
│ PASO 3: Backend valida la solicitud                             │
│ ✓ Aerolínea no vacía                                            │
│ ✓ Origen = "GRU" (regex: ^[A-Z]{3}$)                            │
│ ✓ Destino = "JFK" (regex: ^[A-Z]{3}$)                           │
│ ✓ Origen ≠ Destino                                              │
│ ✓ Fecha formato ISO-8601 válido                                 │
└────────────────────────────┬─────────────────────────────────────┘
                             │
                             ▼
┌──────────────────────────────────────────────────────────────────┐
│ PASO 4: Backend reenvía al ML Service                           │
│ POST http://ml-service:8001/predict_internal                    │
│ Timeout: 10 segundos                                             │
│                                                                  │
│ (Mismo payload JSON)                                             │
└────────────────────────────┬─────────────────────────────────────┘
                             │
                             ▼
┌──────────────────────────────────────────────────────────────────┐
│ PASO 5: ML Service procesa la solicitud                         │
│                                                                  │
│ 5.1 Buscar coordenadas:                                          │
│     GRU → lat: -23.4356, lon: -46.4731                           │
│     JFK → lat: 40.6413, lon: -73.7781                            │
│                                                                  │
│ 5.2 Calcular distancia (Haversine):                             │
│     distancia_km = 7680.5 km                                     │
│                                                                  │
│ 5.3 Consultar clima de GRU (OpenWeatherMap):                    │
│     temperatura: 22.5°C                                          │
│     humedad: 65%                                                 │
│     presión: 1013 hPa                                            │
│     viento: 5.2 m/s                                              │
│     visibilidad: 10000 m                                         │
│     condición: "Clear"                                           │
│                                                                  │
│ 5.4 Preparar features para el modelo:                           │
│     [distancia_km, temperatura, humedad, presión,                │
│      viento, visibilidad, aerolinea_encoded, ...]                │
│                                                                  │
│ 5.5 Ejecutar predicción (model.pkl):                            │
│     prediccion = "Puntual"                                       │
│     probabilidad_retraso = 0.15 (15%)                            │
│     confianza = 0.85 (85%)                                       │
└────────────────────────────┬─────────────────────────────────────┘
                             │
                             ▼
┌──────────────────────────────────────────────────────────────────┐
│ PASO 6: ML Service retorna respuesta al Backend                 │
│                                                                  │
│ {                                                                │
│   "prediccion": "Puntual",                                       │
│   "probabilidad_retraso": 0.15,                                  │
│   "confianza": 0.85,                                             │
│   "distancia_km": 7680.5,                                        │
│   "clima_origen": {                                              │
│     "temperatura": 22.5,                                         │
│     "humedad": 65,                                               │
│     "presion": 1013,                                             │
│     "visibilidad": 10000,                                        │
│     "viento_velocidad": 5.2,                                     │
│     "condicion": "Clear",                                        │
│     "descripcion": "cielo claro"                                 │
│   },                                                             │
│   "metadata": {                                                  │
│     "aerolinea": "LATAM",                                        │
│     "ruta": "GRU → JFK",                                         │
│     "origen_nombre": "São Paulo-Guarulhos",                      │
│     "destino_nombre": "New York-JFK",                            │
│     "fecha_partida": "2025-12-25T14:30:00",                      │
│     "timestamp_prediccion": "2025-12-25T18:57:24Z"               │
│   }                                                              │
│ }                                                                │
└────────────────────────────┬─────────────────────────────────────┘
                             │
                             ▼
┌──────────────────────────────────────────────────────────────────┐
│ PASO 7: Backend enriquece y retorna al Frontend                 │
│                                                                  │
│ {                                                                │
│   ... (misma respuesta del ML Service)                          │
│   "modo_mock": false                                             │
│ }                                                                │
└────────────────────────────┬─────────────────────────────────────┘
                             │
                             ▼
┌──────────────────────────────────────────────────────────────────┐
│ PASO 8: Frontend renderiza el resultado                         │
│                                                                  │
│ ┌────────────────────────────────────────────────────────┐      │
│ │ ✅ PREDICCIÓN: PUNTUAL                                 │      │
│ │                                                        │      │
│ │ 📊 Probabilidad de Retraso: 15%                        │      │
│ │ 🎯 Confianza: 85%                                      │      │
│ │ 📏 Distancia: 7,681 km                                 │      │
│ │                                                        │      │
│ │ 🌤️ Clima en São Paulo-Guarulhos:                      │      │
│ │    • Condición: cielo claro                            │      │
│ │    • Temperatura: 22.5°C                               │      │
│ │    • Humedad: 65%                                      │      │
│ │    • Viento: 5.2 m/s                                   │      │
│ │                                                        │      │
│ │ ✈️ Ruta: GRU → JFK                                     │      │
│ │ 🏢 Aerolínea: LATAM                                    │      │
│ │ 📅 Fecha: 2025-12-25T14:30:00                          │      │
│ └────────────────────────────────────────────────────────┘      │
└──────────────────────────────────────────────────────────────────┘
```

### Tiempos de Respuesta

| Etapa | Tiempo Estimado |
|-------|-----------------|
| Frontend → Backend | ~10-50ms |
| Backend validación | ~5-10ms |
| Backend → ML Service | ~20-50ms |
| ML Service procesamiento | ~300-1500ms |
| ML Service → Backend | ~20-50ms |
| Backend → Frontend | ~10-50ms |
| **TOTAL** | **~500-2000ms** |

---

## 🎨 Decisiones Arquitectónicas

### ADR 001: Arquitectura de Microservicios

**Contexto**: Necesidad de separar responsabilidades entre orquestación de negocio y predicción ML.

**Decisión**: Implementar arquitectura de microservicios con:
- Backend Java (orquestación)
- ML Service Python (predicción)
- Frontend desacoplado

**Consecuencias**:
- ✅ **Positivas**: Escalabilidad independiente, tecnologías especializadas, mantenibilidad
- ⚠️ **Negativas**: Mayor complejidad operacional, latencia de red

---

### ADR 002: Comunicación Síncrona HTTP/REST

**Contexto**: Necesidad de comunicación entre servicios.

**Decisión**: Usar HTTP/REST síncrono en lugar de mensajería asíncrona.

**Razones**:
- Simplicidad de implementación
- Menor latencia para casos de uso en tiempo real
- Facilidad de debugging y monitoreo
- No requiere infraestructura adicional (message broker)

**Consecuencias**:
- ✅ **Positivas**: Simplicidad, bajo acoplamiento temporal
- ⚠️ **Negativas**: Acoplamiento espacial, requiere disponibilidad simultánea

---

### ADR 003: Java para Backend, Python para ML

**Contexto**: Elección de tecnologías por capa.

**Decisión**:
- **Backend**: Java 17 + Spring Boot (orquestación empresarial)
- **ML Service**: Python 3.11 + FastAPI (predicción ML)

**Razones**:
- Java: Ecosistema empresarial robusto, Spring Boot maduro
- Python: Ecosistema ML líder (scikit-learn, pandas, numpy)
- Separación de responsabilidades por tecnología

**Consecuencias**:
- ✅ **Positivas**: Mejor herramienta para cada trabajo
- ⚠️ **Negativas**: Múltiples stacks tecnológicos

---

### ADR 004: Modo Híbrido (Mock/Real)

**Contexto**: Necesidad de demos y pruebas sin dependencias externas.

**Decisión**: Implementar modo híbrido con query parameter `?mock=true`.

**Razones**:
- Facilita demos y presentaciones
- Permite pruebas sin ML Service
- Útil para QA y desarrollo frontend

**Consecuencias**:
- ✅ **Positivas**: Flexibilidad, facilita testing
- ⚠️ **Negativas**: Código adicional de mantenimiento

---

### ADR 005: Cálculo Automático de Distancia

**Contexto**: UX mejorada vs precisión de datos.

**Decisión**: Calcular distancia automáticamente en ML Service usando Haversine.

**Razones**:
- Mejora UX (usuario no envía distancia)
- Consistencia de datos
- Reducción de errores de entrada

**Consecuencias**:
- ✅ **Positivas**: Mejor UX, datos consistentes
- ⚠️ **Negativas**: Requiere diccionario de aeropuertos

---

### ADR 006: Contrato de Integración JSON

**Contexto**: Necesidad de comunicación formal entre servicios.

**Decisión**: Definir contrato JSON estricto documentado en `CONTRATO_INTEGRACION.md`.

**Razones**:
- Claridad en la comunicación
- Facilita integración con terceros
- Versionado de API

**Consecuencias**:
- ✅ **Positivas**: Documentación clara, facilita integraciones
- ⚠️ **Negativas**: Requiere mantenimiento de documentación

---

## 🎯 Patrones de Diseño

### 1. Facade Pattern (Fachada)

**Ubicación**: Backend como fachada del ML Service

**Propósito**: Simplificar la interfaz del sistema para el cliente.

```java
// Backend actúa como fachada
@Service
public class PredictionService {
    
    public PredictionResponseDTO predict(PredictionRequestDTO request) {
        // Orquesta múltiples operaciones:
        // 1. Validación
        // 2. Llamada al ML Service
        // 3. Enriquecimiento de respuesta
        // 4. Manejo de errores
    }
}
```

**Beneficios**:
- Cliente solo conoce el Backend
- Complejidad interna oculta
- Punto único de entrada

---

### 2. DTO Pattern (Data Transfer Object)

**Ubicación**: Todos los servicios

**Propósito**: Transferir datos entre capas sin exponer entidades internas.

```java
// Backend DTOs
@Data
@Builder
public class PredictionRequestDTO {
    @NotBlank
    private String aerolinea;
    
    @Pattern(regexp = "^[A-Z]{3}$")
    private String origen;
    
    @Pattern(regexp = "^[A-Z]{3}$")
    private String destino;
    
    private String fechaPartida;
}
```

```python
# ML Service DTOs
class PredictionRequest(BaseModel):
    aerolinea: str
    origen: str
    destino: str
    fecha_partida: Optional[str]
```

**Beneficios**:
- Desacoplamiento de capas
- Validación centralizada
- Versionado de contratos

---

### 3. Service Layer Pattern

**Ubicación**: Backend

**Propósito**: Separar lógica de negocio de controladores.

```java
@RestController
public class PredictionController {
    private final PredictionService service;
    
    @PostMapping("/predict")
    public ResponseEntity<PredictionResponseDTO> predict(@RequestBody PredictionRequestDTO request) {
        return ResponseEntity.ok(service.predict(request));
    }
}

@Service
public class PredictionService {
    // Lógica de negocio aquí
}
```

**Beneficios**:
- Separación de responsabilidades
- Testabilidad
- Reutilización de lógica

---

### 4. Strategy Pattern (Estrategia)

**Ubicación**: Backend (Modo Mock vs Real)

**Propósito**: Seleccionar algoritmo en tiempo de ejecución.

```java
public PredictionResponseDTO predict(PredictionRequestDTO request, boolean mockMode) {
    if (mockMode) {
        return getMockResponse(request);  // Estrategia Mock
    } else {
        return getRealPrediction(request); // Estrategia Real
    }
}
```

**Beneficios**:
- Flexibilidad
- Facilita testing
- Extensibilidad

---

### 5. Dependency Injection

**Ubicación**: Backend (Spring Boot)

**Propósito**: Inversión de control y desacoplamiento.

```java
@Service
public class PredictionService {
    
    private final WebClient webClient;
    
    @Autowired
    public PredictionService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }
}
```

**Beneficios**:
- Testabilidad (mocks)
- Desacoplamiento
- Configuración centralizada

---

### 6. Builder Pattern

**Ubicación**: Backend DTOs (Lombok)

**Propósito**: Construcción fluida de objetos complejos.

```java
PredictionResponseDTO response = PredictionResponseDTO.builder()
    .prediccion("Puntual")
    .probabilidadRetraso(0.15)
    .confianza(0.85)
    .distanciaKm(7680.5)
    .climaOrigen(weatherData)
    .metadata(metadata)
    .modoMock(false)
    .build();
```

**Beneficios**:
- Código legible
- Inmutabilidad
- Validación en construcción

---

## 🔐 Seguridad

### Seguridad Actual (Desarrollo)

#### ✅ Implementado

1. **Validación de Entrada**:
   ```java
   @Pattern(regexp = "^[A-Z]{3}$", message = "Código IATA inválido")
   private String origen;
   ```

2. **Sanitización de Datos**:
   - Regex para códigos IATA
   - Validación de formato de fechas
   - Validación de campos obligatorios

3. **CORS Habilitado**:
   ```java
   @CrossOrigin(origins = "*")
   ```

4. **Health Checks**:
   - Monitoreo de disponibilidad
   - Detección temprana de fallos

#### ⚠️ Limitaciones de Seguridad

- No hay autenticación
- No hay autorización
- CORS abierto a todos los orígenes
- API Key de OpenWeatherMap hardcodeada
- Sin rate limiting
- Sin HTTPS obligatorio

---

### Recomendaciones para Producción

#### 🔒 Autenticación y Autorización

```java
// Implementar Spring Security con JWT
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/health").permitAll()
                .requestMatchers("/api/**").authenticated()
            )
            .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
            .build();
    }
}
```

#### 🛡️ CORS Restrictivo

```java
@CrossOrigin(
    origins = {"https://flightontime.com", "https://app.flightontime.com"},
    methods = {RequestMethod.GET, RequestMethod.POST},
    allowedHeaders = {"Authorization", "Content-Type"}
)
```

#### 🔑 Gestión de Secretos

```yaml
# Usar variables de entorno
OPENWEATHER_API_KEY=${OPENWEATHER_API_KEY}
ML_SERVICE_URL=${ML_SERVICE_URL}
JWT_SECRET=${JWT_SECRET}
```

#### 🚦 Rate Limiting

```java
// Implementar rate limiting con Bucket4j
@RateLimiter(name = "predictApi", fallbackMethod = "rateLimitFallback")
public PredictionResponseDTO predict(PredictionRequestDTO request) {
    // ...
}
```

#### 🔐 HTTPS Obligatorio

```properties
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=${SSL_PASSWORD}
server.ssl.key-store-type=PKCS12
```

#### 📝 Logging de Auditoría

```java
@Aspect
@Component
public class AuditAspect {
    
    @AfterReturning("@annotation(Auditable)")
    public void logAudit(JoinPoint joinPoint) {
        // Log: usuario, timestamp, acción, resultado
    }
}
```

---

## 🚀 Escalabilidad y Resiliencia

### Escalabilidad Horizontal

#### Servicios Stateless

Todos los servicios son **stateless** (sin estado compartido), lo que permite:

```yaml
# Escalar ML Service a 3 instancias
docker-compose up --scale ml-service=3
```

#### Load Balancing

```
┌─────────────┐
│   Backend   │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ Load        │
│ Balancer    │
└──────┬──────┘
       │
       ├─────────────┬─────────────┬─────────────┐
       ▼             ▼             ▼             ▼
┌────────────┐ ┌────────────┐ ┌────────────┐ ┌────────────┐
│ ML Service │ │ ML Service │ │ ML Service │ │ ML Service │
│ Instance 1 │ │ Instance 2 │ │ Instance 3 │ │ Instance 4 │
└────────────┘ └────────────┘ └────────────┘ └────────────┘
```

---

### Resiliencia

#### 1. Health Checks

Todos los servicios implementan health checks:

```yaml
# docker-compose.yml
healthcheck:
  test: ["CMD", "wget", "--spider", "http://localhost:8080/api/health"]
  interval: 30s
  timeout: 10s
  retries: 3
  start_period: 60s
```

#### 2. Timeouts

```java
// Backend: Timeout de 10s para ML Service
WebClient webClient = WebClient.builder()
    .baseUrl(mlServiceUrl)
    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
    .clientConnector(new ReactorClientHttpConnector(
        HttpClient.create()
            .responseTimeout(Duration.ofSeconds(10))
    ))
    .build();
```

#### 3. Fallback (Modo Mock)

```java
public PredictionResponseDTO predict(PredictionRequestDTO request, boolean mockMode) {
    try {
        if (!mockMode) {
            return callMLService(request);
        }
    } catch (Exception e) {
        logger.error("ML Service falló, usando modo mock", e);
        return getMockResponse(request);
    }
    return getMockResponse(request);
}
```

#### 4. Circuit Breaker (Recomendado)

```java
// Implementar con Resilience4j
@CircuitBreaker(name = "mlService", fallbackMethod = "mlServiceFallback")
public PredictionResponseDTO predict(PredictionRequestDTO request) {
    return callMLService(request);
}

public PredictionResponseDTO mlServiceFallback(PredictionRequestDTO request, Exception e) {
    logger.warn("Circuit breaker activado, usando fallback", e);
    return getMockResponse(request);
}
```

#### 5. Retry Logic

```java
// Retry con backoff exponencial
@Retry(
    name = "mlService",
    maxAttempts = 3,
    waitDuration = 1000,
    exponentialBackoffMultiplier = 2
)
public PredictionResponseDTO predict(PredictionRequestDTO request) {
    return callMLService(request);
}
```

---

### Monitoreo y Observabilidad

#### Logging Estructurado

```java
logger.info("Predicción solicitada: aerolinea={}, ruta={}->{}", 
    request.getAerolinea(), 
    request.getOrigen(), 
    request.getDestino()
);
```

```python
logger.info(
    "Predicción completada",
    extra={
        "ruta": f"{request.origen}->{request.destino}",
        "prediccion": prediccion,
        "probabilidad": probabilidad_retraso,
        "distancia_km": distancia_km
    }
)
```

#### Métricas (Recomendado)

```java
// Implementar con Micrometer
@Timed(value = "prediction.time", description = "Tiempo de predicción")
public PredictionResponseDTO predict(PredictionRequestDTO request) {
    // ...
}
```

---

## 🔗 Integración y Comunicación

### Contrato de Integración JSON

Documentado en `CONTRATO_INTEGRACION.md`:

#### Request Format

```json
{
  "aerolinea": "string",
  "origen": "string",      // Regex: ^[A-Z]{3}$
  "destino": "string",     // Regex: ^[A-Z]{3}$
  "fecha_partida": "string" // ISO-8601
}
```

#### Response Format

```json
{
  "prediccion": "string",           // "Puntual" | "Retrasado"
  "probabilidad_retraso": number,   // 0.0 - 1.0
  "confianza": number,              // 0.0 - 1.0
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
  },
  "modo_mock": boolean
}
```

---

### Códigos de Estado HTTP

| Código | Significado | Uso |
|--------|-------------|-----|
| 200 | OK | Predicción exitosa |
| 400 | Bad Request | Validación fallida |
| 404 | Not Found | Endpoint no existe |
| 500 | Internal Server Error | Error inesperado |
| 503 | Service Unavailable | ML Service no disponible |

---

### Versionado de API

**Estrategia**: Semantic Versioning (SemVer)

- **MAJOR** (1.x.x): Cambios incompatibles en la API
- **MINOR** (x.1.x): Nuevas funcionalidades compatibles
- **PATCH** (x.x.1): Correcciones de bugs

**Versión actual**: 1.0.0

**Ejemplo de versionado futuro**:

```java
@RequestMapping("/api/v2/predict")
public class PredictionControllerV2 {
    // Nueva versión con cambios incompatibles
}
```

---

## 🐳 Infraestructura

### Docker Compose

```yaml
services:
  ml-service:
    build: ./ml-service
    ports: ["8001:8001"]
    healthcheck:
      test: ["CMD", "python", "-c", "import urllib.request; urllib.request.urlopen('http://localhost:8001/health')"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
    networks:
      - flightontime-network
    restart: unless-stopped

  backend:
    build: ./backend
    ports: ["8080:8080"]
    depends_on:
      ml-service:
        condition: service_healthy
    environment:
      - ML_SERVICE_URL=http://ml-service:8001
    healthcheck:
      test: ["CMD", "wget", "--spider", "http://localhost:8080/api/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s
    networks:
      - flightontime-network
    restart: unless-stopped

  frontend:
    build: ./frontend
    ports: ["80:80"]
    depends_on:
      backend:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "wget", "--spider", "http://localhost/"]
      interval: 30s
      timeout: 5s
      retries: 3
      start_period: 10s
    networks:
      - flightontime-network
    restart: unless-stopped

networks:
  flightontime-network:
    driver: bridge
```

---

### Orden de Inicio

```
1. ml-service (40s start period)
   └─ Health check: /health
   
2. backend (60s start period)
   └─ Espera: ml-service healthy
   └─ Health check: /api/health
   
3. frontend (10s start period)
   └─ Espera: backend healthy
   └─ Health check: /
```

---

### Networking

```
┌─────────────────────────────────────────────────┐
│   flightontime-network (bridge)                 │
│                                                 │
│   ┌──────────────┐  ┌──────────────┐           │
│   │  ml-service  │  │   backend    │           │
│   │  :8001       │◄─┤  :8080       │           │
│   └──────────────┘  └──────┬───────┘           │
│                             │                   │
│                     ┌───────▼──────┐            │
│                     │   frontend   │            │
│                     │   :80        │            │
│                     └──────────────┘            │
│                                                 │
└─────────────────────────────────────────────────┘
         │
         │ Port Mapping
         ▼
┌─────────────────────────────────────────────────┐
│   Host Machine                                  │
│   localhost:80    → frontend                    │
│   localhost:8080  → backend                     │
│   localhost:8001  → ml-service                  │
└─────────────────────────────────────────────────┘
```

---

## 📊 Métricas de Calidad

### Principios SOLID

| Principio | Implementación |
|-----------|----------------|
| **S**ingle Responsibility | Cada servicio tiene una responsabilidad única |
| **O**pen/Closed | DTOs extensibles sin modificación |
| **L**iskov Substitution | Interfaces consistentes |
| **I**nterface Segregation | DTOs específicos por caso de uso |
| **D**ependency Inversion | Inyección de dependencias (Spring) |

---

### Características de Calidad

| Característica | Nivel | Evidencia |
|----------------|-------|-----------|
| **Mantenibilidad** | Alta | Código modular, documentado, SOLID |
| **Escalabilidad** | Alta | Servicios stateless, horizontal scaling |
| **Resiliencia** | Media | Health checks, timeouts, fallbacks |
| **Seguridad** | Baja | Sin autenticación (desarrollo) |
| **Observabilidad** | Media | Logging estructurado, health checks |
| **Testabilidad** | Alta | Inyección de dependencias, DTOs |

---

## 🎓 Conclusiones

### Fortalezas de la Arquitectura

✅ **Separación de Responsabilidades**: Cada servicio tiene un propósito claro  
✅ **Tecnologías Especializadas**: Java para orquestación, Python para ML  
✅ **Escalabilidad**: Servicios stateless, escalado horizontal  
✅ **Mantenibilidad**: Código modular, documentado, principios SOLID  
✅ **Flexibilidad**: Modo híbrido, contrato de integración formal  
✅ **Resiliencia**: Health checks, timeouts, fallbacks  

---

### Áreas de Mejora

⚠️ **Seguridad**: Implementar autenticación, autorización, HTTPS  
⚠️ **Observabilidad**: Agregar métricas, tracing distribuido  
⚠️ **Persistencia**: Agregar base de datos para histórico  
⚠️ **Circuit Breaker**: Implementar Resilience4j  
⚠️ **Caché**: Agregar Redis para respuestas frecuentes  
⚠️ **API Gateway**: Centralizar routing y autenticación  

---

### Roadmap Arquitectónico

#### Fase 1: Seguridad (Corto Plazo)
- [ ] Implementar autenticación JWT
- [ ] Configurar HTTPS
- [ ] Restringir CORS
- [ ] Externalizar secretos

#### Fase 2: Observabilidad (Medio Plazo)
- [ ] Agregar Prometheus + Grafana
- [ ] Implementar tracing distribuido (Jaeger)
- [ ] Centralizar logs (ELK Stack)
- [ ] Dashboards de métricas

#### Fase 3: Persistencia (Medio Plazo)
- [ ] Agregar PostgreSQL
- [ ] Implementar Repository Pattern
- [ ] Histórico de predicciones
- [ ] Dashboard de estadísticas

#### Fase 4: Resiliencia Avanzada (Largo Plazo)
- [ ] Implementar Circuit Breaker
- [ ] Agregar Redis para caché
- [ ] Rate limiting avanzado
- [ ] API Gateway (Kong/Ambassador)

---

## 📚 Referencias

### Documentación del Proyecto

- [README.md](README.md) - Guía de inicio rápido
- [CONTRATO_INTEGRACION.md](CONTRATO_INTEGRACION.md) - Contrato de API
- [TROUBLESHOOTING.md](TROUBLESHOOTING.md) - Solución de problemas
- [GUIA_PRUEBAS.md](GUIA_PRUEBAS.md) - Guía de pruebas

### Tecnologías

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [FastAPI Documentation](https://fastapi.tiangolo.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [scikit-learn Documentation](https://scikit-learn.org/)

### Patrones y Principios

- [Microservices Patterns](https://microservices.io/patterns/)
- [SOLID Principles](https://en.wikipedia.org/wiki/SOLID)
- [12-Factor App](https://12factor.net/)
- [RESTful API Design](https://restfulapi.net/)

---

## 📞 Contacto

**Equipo de Arquitectura**: Oracle Enterprise Partner  
**Versión del Documento**: 1.0.0  
**Última Actualización**: 2025-12-25  

---

**© 2025 FlightOnTime - Oracle Enterprise Partner**
