# ============================================================================
# FLIGHTONTIME - SISTEMA DE PREDICCIÓN DE PUNTUALIDAD DE VUELOS
# ============================================================================
# Oracle Enterprise Partner | Sistema de Misión Crítica
# ============================================================================

![FlightOnTime](https://img.shields.io/badge/FlightOnTime-v1.0.0-blue)
![Java](https://img.shields.io/badge/Java-17-orange)
![Python](https://img.shields.io/badge/Python-3.11-green)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-brightgreen)
![FastAPI](https://img.shields.io/badge/FastAPI-0.109-teal)

## 📋 Descripción

**FlightOnTime** es un sistema empresarial de predicción de puntualidad de vuelos que combina:

- 🤖 **Machine Learning** con modelo entrenado (model.pkl)
- 🌤️ **Datos meteorológicos en tiempo real** vía OpenWeatherMap API
- 📏 **Cálculo automático de distancias** usando la fórmula de Haversine
- 🏢 **Arquitectura empresarial** con Java Spring Boot y Python FastAPI
- 🎨 **Frontend moderno** estilo Oracle Redwood

---

## 🏗️ Arquitectura del Sistema

```
┌─────────────────────────────────────────────────────────────────┐
│                         USUARIO FINAL                           │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                    FRONTEND (Nginx)                             │
│  • HTML5 + CSS3 + JavaScript                                    │
│  • Diseño Oracle Redwood                                        │
│  • Puerto: 80                                                   │
└────────────────────────────┬────────────────────────────────────┘
                             │ HTTP
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│              BACKEND (Java 17 + Spring Boot)                    │
│  • Orquestador empresarial                                      │
│  • Validación de negocio                                        │
│  • Modo Mock + Modo Real                                        │
│  • Puerto: 8080                                                 │
└────────────┬───────────────────────────────┬────────────────────┘
             │                               │
             │ HTTP                          │ HTTP
             ▼                               ▼
┌──────────────────────────┐   ┌────────────────────────────────┐
│  ML SERVICE (FastAPI)    │   │  OpenWeatherMap API            │
│  • Carga model.pkl       │   │  • Clima en tiempo real        │
│  • Cálculo Haversine     │   │  • Key: d4ce4d4...             │
│  • Predicción ML         │   └────────────────────────────────┘
│  • Puerto: 8001          │
└──────────────────────────┘
```

### Flujo de Datos

1. **Usuario** ingresa datos del vuelo (aerolínea, origen, destino, fecha)
2. **Frontend** envía solicitud HTTP POST al Backend
3. **Backend** valida datos y reenvía al ML Service
4. **ML Service**:
   - Calcula distancia automáticamente (Haversine)
   - Consulta clima en tiempo real (OpenWeatherMap)
   - Prepara features para el modelo
   - Ejecuta predicción con model.pkl
5. **Respuesta** fluye de vuelta: ML → Backend → Frontend
6. **Usuario** visualiza predicción, probabilidades, clima y metadata

---

## 🚀 Inicio Rápido

### Prerrequisitos

- **Docker** y **Docker Compose** instalados
- **Java 17** (para ejecución local sin Docker)
- **Python 3.11** (para ejecución local sin Docker)
- **Maven** (para compilación del backend)

### Opción 1: Ejecución con Docker (Recomendado)

```bash
# 1. Clonar o navegar al directorio del proyecto
cd FlightOnTime

# 2. Construir y levantar todos los servicios
docker-compose up --build

# 3. Acceder a la aplicación
# Frontend: http://localhost
# Backend API: http://localhost:8080/api/docs
# ML Service: http://localhost:8001
```

**Tiempos de inicio aproximados:**
- ML Service: ~30 segundos
- Backend: ~60 segundos (incluye compilación Maven)
- Frontend: ~10 segundos

### Opción 2: Ejecución Local (Desarrollo)

#### ML Service

```bash
cd ml-service

# Instalar dependencias
pip install -r requirements.txt

# Ejecutar servicio
python main.py

# Servicio disponible en: http://localhost:8001
```

#### Backend

```bash
cd backend

# Compilar con Maven
mvn clean package

# Ejecutar JAR
java -jar target/backend-1.0.0.jar

# API disponible en: http://localhost:8080
```

#### Frontend

```bash
cd frontend

# Opción A: Servidor Python simple
python -m http.server 80

# Opción B: Abrir index.html directamente en navegador
# (Nota: Puede haber problemas de CORS)
```

---

## 📡 Endpoints de la API

### Backend (Puerto 8080)

#### `POST /api/predict`

Realiza una predicción de puntualidad de vuelo.

**Parámetros de Query:**
- `mock` (boolean, opcional): Si es `true`, usa modo mock. Por defecto: `false`

**Body (JSON):**
```json
{
  "aerolinea": "LATAM",
  "origen": "GRU",
  "destino": "JFK",
  "fecha_partida": "2025-12-25T14:30:00"
}
```

**Respuesta (JSON):**
```json
{
  "prediccion": "Puntual",
  "probabilidad_retraso": 0.15,
  "confianza": 0.85,
  "distancia_km": 7680.5,
  "clima_origen": {
    "temperatura": 22.5,
    "humedad": 65,
    "presion": 1013,
    "visibilidad": 10000,
    "viento_velocidad": 5.2,
    "condicion": "Clear",
    "descripcion": "cielo claro"
  },
  "metadata": {
    "aerolinea": "LATAM",
    "ruta": "GRU → JFK",
    "origen_nombre": "São Paulo-Guarulhos",
    "destino_nombre": "New York-JFK",
    "fecha_partida": "2025-12-25T14:30:00",
    "timestamp_prediccion": "2025-12-25T10:56:19"
  },
  "modo_mock": false
}
```

#### `GET /api/health`

Verifica el estado del backend.

**Respuesta:**
```json
{
  "status": "UP",
  "service": "FlightOnTime Backend",
  "version": "1.0.0",
  "timestamp": 1735143379000
}
```

#### `GET /api/docs`

Documentación automática de endpoints.

### ML Service (Puerto 8001)

#### `POST /predict_internal`

Endpoint interno para predicción (llamado por el backend).

#### `GET /airports`

Lista todos los aeropuertos disponibles en el sistema.

**Respuesta:**
```json
{
  "total": 40,
  "aeropuertos": [
    {
      "codigo": "GRU",
      "nombre": "São Paulo-Guarulhos",
      "lat": -23.4356,
      "lon": -46.4731
    },
    ...
  ]
}
```

#### `GET /health`

Health check del servicio ML.

---

## 🧪 Ejemplos de Prueba

### Ejemplo 1: Vuelo Doméstico Brasil (Modo Real)

```bash
curl -X POST http://localhost:8080/api/predict \
  -H "Content-Type: application/json" \
  -d '{
    "aerolinea": "GOL",
    "origen": "GRU",
    "destino": "GIG",
    "fecha_partida": "2025-12-26T08:00:00"
  }'
```

### Ejemplo 2: Vuelo Internacional (Modo Mock)

```bash
curl -X POST "http://localhost:8080/api/predict?mock=true" \
  -H "Content-Type: application/json" \
  -d '{
    "aerolinea": "LATAM",
    "origen": "GRU",
    "destino": "JFK",
    "fecha_partida": "2025-12-27T14:30:00"
  }'
```

### Ejemplo 3: Listar Aeropuertos Disponibles

```bash
curl http://localhost:8001/airports
```

---

## 🎯 Características Principales

### 1. Predicción Inteligente

- ✅ Modelo ML entrenado (model.pkl) cargado en memoria
- ✅ Predicción binaria: **Puntual** vs **Retrasado**
- ✅ Probabilidades de retraso (0.0 - 1.0)
- ✅ Nivel de confianza del modelo

### 2. Integración Meteorológica

- ✅ Consulta en tiempo real a OpenWeatherMap API
- ✅ Datos: temperatura, humedad, presión, viento, visibilidad
- ✅ Enriquecimiento de features para el modelo

### 3. Cálculo Automático de Distancia

- ✅ Diccionario de 40+ aeropuertos internacionales
- ✅ Fórmula de Haversine para distancia geodésica
- ✅ **UX mejorada**: Usuario NO envía distancia manualmente

### 4. Modo Híbrido

- ✅ **Modo Real**: Integración completa con ML y clima
- ✅ **Modo Mock**: Respuesta estática para demos y pruebas
- ✅ Fallback automático si el ML Service falla

### 5. Frontend Empresarial

- ✅ Diseño Oracle Redwood con paleta curada
- ✅ Animaciones suaves y micro-interacciones
- ✅ Loading states y manejo de errores visual
- ✅ Responsive design (mobile-first)
- ✅ Tarjetas de resultados con colores semánticos

---

## 📂 Estructura del Proyecto

```
FlightOnTime/
│
├── backend/                          # Backend Java Spring Boot
│   ├── src/
│   │   └── main/
│   │       ├── java/com/oracle/flightontime/
│   │       │   ├── FlightOnTimeApplication.java
│   │       │   ├── controller/
│   │       │   │   └── PredictionController.java
│   │       │   ├── service/
│   │       │   │   └── PredictionService.java
│   │       │   ├── dto/
│   │       │   │   ├── PredictionRequestDTO.java
│   │       │   │   ├── PredictionResponseDTO.java
│   │       │   │   └── WeatherDataDTO.java
│   │       │   └── config/
│   │       │       └── WebClientConfig.java
│   │       └── resources/
│   │           └── application.properties
│   ├── pom.xml
│   └── Dockerfile
│
├── ml-service/                       # Servicio ML Python FastAPI
│   ├── main.py                       # Aplicación principal
│   ├── airport_coords.py             # Diccionario de coordenadas IATA
│   ├── model.pkl                     # Modelo ML entrenado
│   ├── requirements.txt
│   └── Dockerfile
│
├── frontend/                         # Frontend HTML/CSS/JS
│   ├── index.html                    # Página principal
│   ├── styles.css                    # Estilos Oracle Redwood
│   ├── app.js                        # Lógica de aplicación
│   ├── nginx.conf                    # Configuración Nginx
│   └── Dockerfile
│
├── docker-compose.yml                # Orquestación de servicios
└── README.md                         # Este archivo
```

---

## 🔧 Configuración

### Variables de Entorno

#### Backend (`backend/src/main/resources/application.properties`)

```properties
# Puerto del servidor
server.port=8080

# URL del servicio ML
ml.service.url=http://ml-service:8001

# Timeout para llamadas al servicio ML (segundos)
ml.service.timeout=10
```

#### ML Service

```python
# API Key de OpenWeatherMap (en main.py)
OPENWEATHER_API_KEY = "d4ce4d4589c7a7ac4343085c00c39f9b"
```

**Nota**: Para producción, se recomienda usar variables de entorno en lugar de hardcodear la API key.

### Aeropuertos Disponibles

El sistema incluye 40+ aeropuertos internacionales:

**Brasil**: GRU, GIG, BSB, CGH, SSA, CNF, REC, FOR, POA, CWB  
**Estados Unidos**: JFK, LAX, ORD, MIA, ATL, DFW, SFO, IAH, LAS, BOS  
**México**: MEX, CUN, GDL, MTY, TIJ  
**Europa**: LHR, CDG, FRA, MAD, BCN, AMS, FCO, LIS  
**América del Sur**: EZE, BOG, LIM, SCL  
**Asia**: NRT, HND, PEK, PVG, HKG, SIN, ICN, DXB  

Para agregar más aeropuertos, editar `ml-service/airport_coords.py`.

---

## 🐳 Docker

### Comandos Útiles

```bash
# Construir y levantar servicios
docker-compose up --build

# Levantar en background
docker-compose up -d

# Ver logs
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f backend

# Detener servicios
docker-compose down

# Detener y eliminar volúmenes
docker-compose down -v

# Reconstruir un servicio específico
docker-compose up --build backend
```

### Health Checks

Todos los servicios incluyen health checks:

```bash
# Verificar estado de contenedores
docker-compose ps

# Salida esperada:
# NAME                    STATUS
# flightontime-frontend   Up (healthy)
# flightontime-backend    Up (healthy)
# flightontime-ml         Up (healthy)
```

---

## 🧪 Testing

### Pruebas Manuales

1. **Verificar servicios activos**:
   - Frontend: http://localhost
   - Backend: http://localhost:8080/api/health
   - ML Service: http://localhost:8001/health

2. **Probar predicción en modo mock**:
   - Abrir http://localhost
   - Llenar formulario
   - Clic en "Modo Demo (Mock)"
   - Verificar respuesta instantánea

3. **Probar predicción real**:
   - Llenar formulario
   - Clic en "Obtener Predicción"
   - Verificar clima en tiempo real

### Pruebas Automatizadas

**Script de Integración Completo:**

```powershell
# Ejecutar todas las pruebas de integración
.\test-integration.ps1
```

Este script realiza:
- ✅ Verificación de health checks de todos los servicios
- ✅ Listado de aeropuertos disponibles
- ✅ Pruebas de predicción con múltiples rutas internacionales
- ✅ Validación del modo mock
- ✅ Reporte detallado de resultados

**Salida esperada:**
```
============================================================================
PRUEBA DE INTEGRACIÓN - FLIGHTONTIME
============================================================================

PASO 1: Verificando servicios...
🔍 Probando ML Service Health... ✅ OK
🔍 Probando Backend Health... ✅ OK

PASO 2: Obteniendo aeropuertos disponibles...
✅ Total de aeropuertos: 40

PASO 3: Probando predicciones con diferentes rutas...
📋 Prueba: Vuelo internacional Brasil → USA
   Ruta: LATAM GRU → JFK
   ✅ Predicción: Puntual
   📊 Probabilidad retraso: 15.0%
   🎯 Confianza: 85.0%
   📏 Distancia: 7681 km
   🌤️  Clima: cielo claro, 22.5°C

...

RESUMEN DE PRUEBAS
✅ Pruebas exitosas: 5 / 5
🎉 ¡TODAS LAS PRUEBAS PASARON!
```

### Pruebas con cURL

Ver sección "Ejemplos de Prueba" arriba.

---

## 🚨 Solución de Problemas

Para problemas comunes y soluciones detalladas, consulte:

📖 **[TROUBLESHOOTING.md](TROUBLESHOOTING.md)** - Guía completa de solución de problemas

### Problemas Frecuentes

#### 1. Error "Aeropuerto no encontrado"

**Causa**: El código IATA no está en la base de datos.

**Solución rápida**:
```bash
# Ver aeropuertos disponibles
curl http://localhost:8001/airports
```

#### 2. Backend no se conecta al ML Service

**Síntoma**: Error "Connection refused" en logs del backend

**Solución**:
1. Verificar que el ML Service esté corriendo: `docker-compose ps`
2. Verificar logs del ML Service: `docker-compose logs ml-service`
3. Esperar a que el health check pase (puede tardar 30-40s)

#### 3. El modelo no se carga

**Síntoma**: Error "Modelo ML no disponible" en `/health`

**Solución**:
1. Verificar que `model.pkl` existe en `ml-service/`
2. Verificar permisos del archivo
3. Revisar logs: `docker-compose logs ml-service`

#### 4. CORS errors en el frontend

**Síntoma**: Error "CORS policy" en consola del navegador

**Solución**:
1. Verificar que el backend tenga CORS habilitado (ya configurado)
2. Si ejecuta frontend localmente, usar servidor HTTP (no abrir archivo directamente)
3. Verificar `nginx.conf` si usa Docker

#### 5. Clima no se obtiene

**Síntoma**: Datos de clima por defecto en resultados

**Solución**:
1. Verificar API key de OpenWeatherMap
2. Verificar conectividad a internet del contenedor
3. Revisar logs del ML Service para errores de API

**Nota**: El sistema funciona con datos de clima por defecto si la API falla.

### Comandos de Diagnóstico

```powershell
# Ver estado de todos los servicios
docker-compose ps

# Ver logs en tiempo real
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f ml-service

# Reiniciar un servicio
docker-compose restart ml-service

# Reconstruir y reiniciar todo
docker-compose down
docker-compose up --build
```

Para más detalles, consulte **[TROUBLESHOOTING.md](TROUBLESHOOTING.md)**.

---

## 📊 Tecnologías Utilizadas

### Backend
- **Java 17**: Lenguaje de programación
- **Spring Boot 3.2.1**: Framework empresarial
- **Maven**: Gestión de dependencias
- **Lombok**: Reducción de boilerplate
- **WebFlux**: Cliente HTTP reactivo
- **Validation**: Validación de DTOs

### ML Service
- **Python 3.11**: Lenguaje de programación
- **FastAPI**: Framework web moderno
- **scikit-learn**: Machine Learning
- **pandas/numpy**: Manipulación de datos
- **requests**: Cliente HTTP
- **joblib**: Serialización del modelo

### Frontend
- **HTML5**: Estructura semántica
- **CSS3**: Estilos modernos (Flexbox, Grid, Variables CSS)
- **JavaScript ES6+**: Lógica de aplicación
- **Fetch API**: Llamadas HTTP
- **Google Fonts (Inter)**: Tipografía profesional

### DevOps
- **Docker**: Containerización
- **Docker Compose**: Orquestación
- **Nginx**: Servidor web para frontend

---

## 🎨 Diseño UI/UX

### Paleta de Colores

- **Primario**: `hsl(210, 100%, 56%)` - Azul profesional
- **Éxito**: `hsl(142, 71%, 45%)` - Verde para vuelos puntuales
- **Peligro**: `hsl(0, 84%, 60%)` - Rojo para retrasos
- **Advertencia**: `hsl(45, 100%, 51%)` - Amarillo para alertas

### Características de Diseño

- ✅ **Glassmorphism** en header
- ✅ **Gradientes suaves** en botones y cards
- ✅ **Animaciones CSS** (pulse, blink, slideIn, scaleIn)
- ✅ **Micro-interacciones** en hover
- ✅ **Loading states** con spinner
- ✅ **Responsive design** mobile-first

---

## 📈 Roadmap Futuro

- [ ] Persistencia de predicciones en base de datos
- [ ] Dashboard de estadísticas históricas
- [ ] Predicción por lotes (batch)
- [ ] Autenticación y autorización
- [ ] Reentrenamiento automático del modelo
- [ ] Integración con más APIs meteorológicas
- [ ] Notificaciones push
- [ ] Exportación de reportes (PDF/Excel)

---

## 👥 Contribuciones

Este es un proyecto empresarial de Oracle Enterprise Partner. Para contribuciones:

1. Fork del repositorio
2. Crear branch de feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit de cambios (`git commit -m 'Agregar nueva funcionalidad'`)
4. Push al branch (`git push origin feature/nueva-funcionalidad`)
5. Abrir Pull Request

---

## 📄 Licencia

Copyright © 2025 FlightOnTime - Oracle Enterprise Partner

---

## 📞 Soporte

Para soporte técnico o consultas:

- **Email**: soporte@flightontime.com
- **Documentación**: http://localhost:8080/api/docs
- **Health Checks**: 
  - Backend: http://localhost:8080/api/health
  - ML Service: http://localhost:8001/health

---

## ✨ Créditos

Desarrollado como sistema de misión crítica para Oracle Enterprise Partner.

**Tecnologías**: Java 17, Spring Boot, Python, FastAPI, Docker, Nginx  
**APIs**: OpenWeatherMap  
**Diseño**: Oracle Redwood Design System  

---

**¡Gracias por usar FlightOnTime!** ✈️
