# ============================================================================
# GUÍA DE PRUEBAS PARA EL JURADO
# FlightOnTime - Sistema de Predicción de Puntualidad de Vuelos
# ============================================================================

## 🎯 OBJETIVO DE ESTA GUÍA

Esta guía proporciona instrucciones paso a paso para que el jurado de Oracle Enterprise Partner pueda evaluar todas las funcionalidades del sistema FlightOnTime de manera rápida y efectiva.

---

## ⚡ INICIO RÁPIDO (5 minutos)

### Opción A: Windows (PowerShell)

```powershell
# 1. Abrir PowerShell como Administrador
# 2. Navegar al directorio del proyecto
cd C:\Users\julio\Documents\FlightOnTime

# 3. Ejecutar script de inicio
.\start.ps1
```

### Opción B: Linux/Mac (Bash)

```bash
# 1. Abrir terminal
# 2. Navegar al directorio del proyecto
cd /path/to/FlightOnTime

# 3. Dar permisos de ejecución
chmod +x start.sh

# 4. Ejecutar script de inicio
./start.sh
```

### Opción C: Manual (Docker Compose)

```bash
# En el directorio raíz del proyecto
docker-compose up --build
```

**Tiempo de inicio**: 60-90 segundos

---

## ✅ VERIFICACIÓN DE SERVICIOS

### 1. Verificar que todos los contenedores estén corriendo

```bash
docker-compose ps
```

**Salida esperada**:
```
NAME                    STATUS
flightontime-frontend   Up (healthy)
flightontime-backend    Up (healthy)
flightontime-ml         Up (healthy)
```

### 2. Verificar Health Checks

**Backend**:
```bash
curl http://localhost:8080/api/health
```

**ML Service**:
```bash
curl http://localhost:8001/health
```

**Frontend**:
```bash
curl http://localhost/
```

---

## 🧪 PRUEBAS FUNCIONALES

### PRUEBA 1: Predicción en Modo Mock (Demo Rápido)

**Objetivo**: Verificar conectividad básica sin dependencias externas

**Pasos**:

1. Abrir navegador en: http://localhost

2. Llenar formulario:
   - **Aerolínea**: LATAM
   - **Origen**: GRU (São Paulo)
   - **Destino**: JFK (New York)
   - **Fecha**: (dejar por defecto)

3. Clic en botón **"Modo Demo (Mock)"**

**Resultado esperado**:
- ✅ Respuesta instantánea (< 100ms)
- ✅ Predicción: "Puntual"
- ✅ Probabilidad de retraso: 15%
- ✅ Confianza: 85%
- ✅ Distancia: 850 km
- ✅ Clima mock visible
- ✅ Metadata con indicador "🔧 Demo (Mock)"

**Criterio de éxito**: Respuesta en menos de 1 segundo con todos los datos visibles

---

### PRUEBA 2: Predicción Real con ML y Clima

**Objetivo**: Verificar integración completa con modelo ML y API meteorológica

**Pasos**:

1. En el mismo formulario, cambiar:
   - **Aerolínea**: GOL
   - **Origen**: GIG (Rio de Janeiro)
   - **Destino**: BSB (Brasília)

2. Clic en botón **"Obtener Predicción"**

3. Observar loading overlay

**Resultado esperado**:
- ✅ Loading overlay visible durante 1-2 segundos
- ✅ Predicción basada en modelo real
- ✅ Clima en tiempo real de Rio de Janeiro
- ✅ Distancia calculada automáticamente (~920 km)
- ✅ Metadata con indicador "🚀 Predicción Real"
- ✅ Temperatura, humedad, viento actuales

**Criterio de éxito**: Datos meteorológicos reales y predicción del modelo ML

---

### PRUEBA 3: Vuelo Internacional de Larga Distancia

**Objetivo**: Verificar cálculo de distancia con fórmula de Haversine

**Pasos**:

1. Configurar:
   - **Aerolínea**: LATAM
   - **Origen**: GRU (São Paulo)
   - **Destino**: LHR (London)

2. Obtener predicción (modo real)

**Resultado esperado**:
- ✅ Distancia: ~9,400 km
- ✅ Clima de São Paulo
- ✅ Predicción ajustada por distancia larga

**Criterio de éxito**: Distancia correcta (±100 km)

---

### PRUEBA 4: Validación de Errores

**Objetivo**: Verificar manejo de errores y validaciones

**Caso 4.1: Origen y Destino Iguales**

1. Configurar:
   - **Origen**: GRU
   - **Destino**: GRU

2. Intentar predicción

**Resultado esperado**:
- ✅ Alerta: "El aeropuerto de origen y destino deben ser diferentes"

**Caso 4.2: Código IATA Inválido** (requiere modificar código temporalmente)

Si se envía un código inválido vía API:
```bash
curl -X POST http://localhost:8080/api/predict \
  -H "Content-Type: application/json" \
  -d '{"aerolinea":"TEST","origen":"ABC","destino":"XYZ"}'
```

**Resultado esperado**:
- ✅ Error 400: Aeropuerto no encontrado

---

### PRUEBA 5: Diferentes Condiciones Climáticas

**Objetivo**: Observar cómo el clima afecta la predicción

**Casos de prueba**:

| Origen | Destino | Clima Esperado        | Impacto en Predicción |
|--------|---------|----------------------|----------------------|
| GRU    | GIG     | Tropical/Húmedo      | Moderado             |
| JFK    | ORD     | Continental/Variable | Alto (invierno)      |
| MEX    | CUN     | Seco/Cálido          | Bajo                 |
| LHR    | CDG     | Templado/Lluvioso    | Moderado             |

**Criterio de éxito**: Clima real reflejado en la respuesta

---

## 📊 PRUEBAS DE API (cURL)

### Test 1: Endpoint de Predicción

```bash
curl -X POST http://localhost:8080/api/predict \
  -H "Content-Type: application/json" \
  -d '{
    "aerolinea": "AZUL",
    "origen": "CNF",
    "destino": "SSA",
    "fecha_partida": "2025-12-26T10:00:00"
  }'
```

**Validar**:
- ✅ Status code: 200
- ✅ JSON válido
- ✅ Campos requeridos presentes

### Test 2: Modo Mock vía API

```bash
curl -X POST "http://localhost:8080/api/predict?mock=true" \
  -H "Content-Type: application/json" \
  -d '{
    "aerolinea": "COPA",
    "origen": "MEX",
    "destino": "CUN"
  }'
```

**Validar**:
- ✅ `modo_mock: true` en respuesta
- ✅ Respuesta rápida (< 200ms)

### Test 3: Listar Aeropuertos

```bash
curl http://localhost:8001/airports
```

**Validar**:
- ✅ Lista de 40+ aeropuertos
- ✅ Cada aeropuerto tiene: codigo, nombre, lat, lon

### Test 4: Health Checks

```bash
# Backend
curl http://localhost:8080/api/health

# ML Service
curl http://localhost:8001/health
```

**Validar**:
- ✅ Ambos retornan status 200
- ✅ Información de servicio presente

---

## 🎨 PRUEBAS DE UX/UI

### Evaluación de Diseño

**Criterios de evaluación**:

1. **Primera Impresión** (0-5 segundos)
   - ✅ Diseño profesional y moderno
   - ✅ Branding claro (FlightOnTime + Oracle)
   - ✅ Paleta de colores armoniosa

2. **Usabilidad** (interacción)
   - ✅ Formulario intuitivo
   - ✅ Dropdowns bien organizados (por región)
   - ✅ Botones claramente diferenciados
   - ✅ Loading states visibles

3. **Visualización de Resultados**
   - ✅ Predicción destacada con color semántico
   - ✅ Métricas fáciles de entender
   - ✅ Barras de progreso animadas
   - ✅ Clima presentado de forma clara

4. **Responsive Design**
   - ✅ Probar en diferentes tamaños de ventana
   - ✅ Mobile-friendly (< 768px)

### Animaciones y Micro-interacciones

**Verificar**:
- ✅ Hover effects en botones
- ✅ Transiciones suaves
- ✅ Loading spinner
- ✅ Slide-in de resultados
- ✅ Scale-in del icono de predicción

---

## 🔍 PRUEBAS DE RENDIMIENTO

### Test de Carga Básico

**Herramienta**: Apache Bench (ab) o similar

```bash
# 100 requests, 10 concurrentes (modo mock)
ab -n 100 -c 10 -p payload.json -T application/json \
   http://localhost:8080/api/predict?mock=true
```

**Archivo payload.json**:
```json
{"aerolinea":"LATAM","origen":"GRU","destino":"GIG"}
```

**Métricas esperadas**:
- ✅ Requests/sec: > 50
- ✅ Time per request (mean): < 200ms
- ✅ Failed requests: 0

### Test de Latencia

**Medir tiempos de respuesta**:

```bash
# Modo Mock
time curl -X POST "http://localhost:8080/api/predict?mock=true" \
  -H "Content-Type: application/json" \
  -d '{"aerolinea":"GOL","origen":"GRU","destino":"GIG"}'

# Modo Real
time curl -X POST http://localhost:8080/api/predict \
  -H "Content-Type: application/json" \
  -d '{"aerolinea":"GOL","origen":"GRU","destino":"GIG"}'
```

**Tiempos esperados**:
- Mock: < 100ms
- Real: < 2000ms

---

## 🐳 PRUEBAS DE DOCKER

### Verificar Imágenes

```bash
docker images | grep flightontime
```

**Esperado**: 3 imágenes (frontend, backend, ml-service)

### Verificar Redes

```bash
docker network ls | grep flightontime
```

**Esperado**: Red `flightontime-network`

### Verificar Logs

```bash
# Todos los servicios
docker-compose logs

# Solo backend
docker-compose logs backend

# Solo ML service
docker-compose logs ml-service

# Seguir logs en tiempo real
docker-compose logs -f
```

**Validar**:
- ✅ No hay errores críticos
- ✅ Servicios iniciaron correctamente
- ✅ Health checks pasaron

---

## 📋 CHECKLIST DE EVALUACIÓN

### Funcionalidad Core

- [ ] Predicción en modo mock funciona
- [ ] Predicción en modo real funciona
- [ ] Clima en tiempo real se obtiene correctamente
- [ ] Distancia se calcula automáticamente
- [ ] Validaciones de entrada funcionan
- [ ] Manejo de errores es robusto

### Arquitectura

- [ ] 3 servicios independientes (frontend, backend, ml)
- [ ] Comunicación HTTP entre servicios
- [ ] Health checks implementados
- [ ] Docker Compose orquesta correctamente
- [ ] Logs estructurados y útiles

### Código

- [ ] Código comentado en español
- [ ] Estructura modular y organizada
- [ ] DTOs con validaciones
- [ ] Manejo de excepciones
- [ ] Configuración externalizada

### UX/UI

- [ ] Diseño profesional (Oracle Redwood)
- [ ] Animaciones suaves
- [ ] Loading states
- [ ] Responsive design
- [ ] Colores semánticos (verde/rojo)

### Documentación

- [ ] README completo
- [ ] Contrato de integración definido
- [ ] Resumen ejecutivo
- [ ] Guía de pruebas (este documento)
- [ ] Comentarios en código

---

## 🎯 ESCENARIOS DE DEMOSTRACIÓN

### Demo 1: Flujo Completo (3 minutos)

1. Mostrar frontend
2. Explicar formulario
3. Ejecutar predicción en modo mock
4. Explicar resultados
5. Ejecutar predicción real
6. Comparar diferencias
7. Mostrar clima en tiempo real

### Demo 2: Arquitectura (5 minutos)

1. Mostrar `docker-compose.yml`
2. Explicar servicios
3. Mostrar logs en tiempo real
4. Ejecutar health checks
5. Mostrar código del backend
6. Mostrar código del ML service
7. Explicar flujo de datos

### Demo 3: API (2 minutos)

1. Mostrar documentación en `/api/docs`
2. Ejecutar request con cURL
3. Mostrar respuesta JSON
4. Explicar contrato de integración

---

## 🚨 TROUBLESHOOTING

### Problema: Contenedores no inician

**Solución**:
```bash
docker-compose down
docker-compose up --build --force-recreate
```

### Problema: Backend no se conecta a ML Service

**Verificar**:
```bash
docker-compose logs ml-service
docker exec -it flightontime-backend ping ml-service
```

### Problema: Clima no se obtiene

**Verificar**:
- API key de OpenWeatherMap válida
- Conectividad a internet del contenedor
- Logs del ML service

### Problema: Frontend muestra error CORS

**Verificar**:
- Backend tiene CORS habilitado
- Frontend accede a `http://localhost:8080` (no IP)

---

## 📞 SOPORTE DURANTE LA EVALUACIÓN

**Documentación**:
- README.md
- CONTRATO_INTEGRACION.md
- RESUMEN_EJECUTIVO.md

**Endpoints de ayuda**:
- http://localhost:8080/api/docs
- http://localhost:8001/airports

**Logs en tiempo real**:
```bash
docker-compose logs -f
```

---

## ✅ CRITERIOS DE APROBACIÓN

### Mínimo Aceptable

- ✅ Sistema inicia correctamente con Docker Compose
- ✅ Frontend accesible y funcional
- ✅ Predicción en modo mock funciona
- ✅ Predicción en modo real funciona
- ✅ Clima en tiempo real se obtiene
- ✅ Documentación completa

### Excelencia

- ✅ Todo lo anterior +
- ✅ Diseño UI excepcional
- ✅ Código limpio y bien documentado
- ✅ Arquitectura escalable
- ✅ Manejo robusto de errores
- ✅ Performance óptimo (< 2s)

---

## 🏆 PUNTOS DESTACABLES PARA EL JURADO

1. **UX Mejorada**: Usuario NO envía distancia manualmente
2. **Datos Reales**: Integración con OpenWeatherMap
3. **Modo Híbrido**: Mock para demos + Real para producción
4. **Arquitectura Empresarial**: Microservicios con Java y Python
5. **Documentación Completa**: En español, detallada
6. **Listo para Producción**: Docker, health checks, logging
7. **Diseño Premium**: Oracle Redwood style

---

**¡Buena suerte con la evaluación!** ✈️

**Tiempo estimado de evaluación completa**: 30-45 minutos
