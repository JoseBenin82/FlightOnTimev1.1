// ============================================================================
// FLIGHTONTIME - APLICACIÓN JAVASCRIPT
// ============================================================================
// Lógica de frontend para integración con backend y visualización de resultados
// ============================================================================

// Configuración de la API
const API_CONFIG = {
    baseUrl: 'http://localhost:8080/api',
    endpoints: {
        predict: '/predict',
        health: '/health'
    }
};

// ============================================================================
// ELEMENTOS DEL DOM
// ============================================================================
const elements = {
    form: document.getElementById('predictionForm'),
    submitBtn: document.getElementById('submitBtn'),
    mockBtn: document.getElementById('mockBtn'),
    btnText: document.getElementById('btnText'),
    loadingOverlay: document.getElementById('loadingOverlay'),
    resultsSection: document.getElementById('resultsSection'),
    statusIndicator: document.getElementById('statusIndicator'),

    // Inputs
    aerolinea: document.getElementById('aerolinea'),
    origen: document.getElementById('origen'),
    destino: document.getElementById('destino'),
    fechaPartida: document.getElementById('fechaPartida'),

    // Resultados
    predictionIcon: document.getElementById('predictionIcon'),
    predictionStatus: document.getElementById('predictionStatus'),
    predictionSubtitle: document.getElementById('predictionSubtitle'),

    // Métricas
    metricProbability: document.getElementById('metricProbability'),
    metricConfidence: document.getElementById('metricConfidence'),
    metricDistance: document.getElementById('metricDistance'),
    probabilityBar: document.getElementById('probabilityBar'),
    confidenceBar: document.getElementById('confidenceBar'),

    // Clima
    weatherCondition: document.getElementById('weatherCondition'),
    weatherTemp: document.getElementById('weatherTemp'),
    weatherHumidity: document.getElementById('weatherHumidity'),
    weatherWind: document.getElementById('weatherWind'),
    weatherVisibility: document.getElementById('weatherVisibility'),

    // Metadata
    metadataGrid: document.getElementById('metadataGrid')
};

// ============================================================================
// INICIALIZACIÓN
// ============================================================================
document.addEventListener('DOMContentLoaded', () => {
    console.log('🚀 FlightOnTime Frontend iniciado');

    // Establecer fecha por defecto (mañana a las 10:00)
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    tomorrow.setHours(10, 0, 0, 0);
    elements.fechaPartida.value = tomorrow.toISOString().slice(0, 16);

    // Event Listeners
    elements.form.addEventListener('submit', handleSubmit);
    elements.mockBtn.addEventListener('click', handleMockSubmit);

    // Verificar estado del backend
    checkBackendHealth();
});

// ============================================================================
// VERIFICACIÓN DE SALUD DEL BACKEND
// ============================================================================
async function checkBackendHealth() {
    try {
        const response = await fetch(`${API_CONFIG.baseUrl}${API_CONFIG.endpoints.health}`);
        if (response.ok) {
            console.log('✅ Backend conectado');
            updateStatusIndicator(true);
        } else {
            console.warn('⚠️ Backend responde pero con errores');
            updateStatusIndicator(false);
        }
    } catch (error) {
        console.error('❌ Backend no disponible:', error);
        updateStatusIndicator(false);
    }
}

function updateStatusIndicator(isHealthy) {
    const statusDot = elements.statusIndicator.querySelector('.status-dot');
    const statusText = elements.statusIndicator.querySelector('.status-text');

    if (isHealthy) {
        statusDot.style.background = 'hsl(142, 71%, 45%)';
        statusText.textContent = 'Sistema Operativo';
    } else {
        statusDot.style.background = 'hsl(45, 100%, 51%)';
        statusText.textContent = 'Modo Limitado';
    }
}

// ============================================================================
// MANEJO DE ENVÍO DE FORMULARIO (MODO REAL)
// ============================================================================
async function handleSubmit(event) {
    event.preventDefault();

    // Validar que origen y destino sean diferentes
    if (elements.origen.value === elements.destino.value) {
        alert('⚠️ El aeropuerto de origen y destino deben ser diferentes');
        return;
    }

    const requestData = {
        aerolinea: elements.aerolinea.value,
        origen: elements.origen.value,
        destino: elements.destino.value,
        fecha_partida: elements.fechaPartida.value ?
            new Date(elements.fechaPartida.value).toISOString() : null
    };

    console.log('📤 Enviando solicitud (modo real):', requestData);
    await sendPredictionRequest(requestData, false);
}

// ============================================================================
// MANEJO DE ENVÍO EN MODO MOCK
// ============================================================================
async function handleMockSubmit(event) {
    event.preventDefault();

    // Validar que origen y destino sean diferentes
    if (elements.origen.value === elements.destino.value) {
        alert('⚠️ El aeropuerto de origen y destino deben ser diferentes');
        return;
    }

    const requestData = {
        aerolinea: elements.aerolinea.value,
        origen: elements.origen.value,
        destino: elements.destino.value,
        fecha_partida: elements.fechaPartida.value ?
            new Date(elements.fechaPartida.value).toISOString() : null
    };

    console.log('📤 Enviando solicitud (modo mock):', requestData);
    await sendPredictionRequest(requestData, true);
}

// ============================================================================
// ENVÍO DE SOLICITUD AL BACKEND
// ============================================================================
async function sendPredictionRequest(data, useMock) {
    // Mostrar loading
    showLoading(true);
    disableButtons(true);

    try {
        const url = `${API_CONFIG.baseUrl}${API_CONFIG.endpoints.predict}${useMock ? '?mock=true' : ''}`;

        console.log('🔄 Enviando solicitud a:', url);
        console.log('📦 Datos:', data);

        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        console.log('📡 Respuesta HTTP:', response.status, response.statusText);

        if (!response.ok) {
            // Intentar obtener el mensaje de error del servidor
            let errorMessage = `Error HTTP ${response.status}: ${response.statusText}`;
            try {
                const errorData = await response.json();
                if (errorData.detail) {
                    errorMessage = errorData.detail;
                } else if (errorData.error) {
                    errorMessage = errorData.error;
                } else if (errorData.message) {
                    errorMessage = errorData.message;
                }
            } catch (e) {
                // Si no se puede parsear el JSON, usar el mensaje por defecto
            }
            throw new Error(errorMessage);
        }

        const result = await response.json();
        console.log('📥 Respuesta recibida:', result);

        // Verificar si hay error de validación
        if (result.prediccion === 'Error' && result.metadata && result.metadata.error) {
            throw new Error(result.metadata.error);
        }

        // Mostrar resultados
        displayResults(result);

    } catch (error) {
        console.error('❌ Error en la solicitud:', error);

        let userMessage = 'Error al obtener predicción:\n\n';

        // Detectar mensaje específico de validación
        if (error.message === 'No se hallan esos datos en la base de datos.') {
            userMessage = '⚠️ No se hallan esos datos en la base de datos.\n\n';
            userMessage += 'Por favor, verifique que:\n';
            userMessage += '• La aerolínea seleccionada sea válida\n';
            userMessage += '• Los aeropuertos de origen y destino existan en el sistema\n\n';
            userMessage += 'Aerolíneas válidas: LATAM, GOL, AZUL, AVIANCA, COPA, AMERICAN, UNITED, DELTA';
        } else if (error.message.includes('Failed to fetch') || error.message.includes('NetworkError')) {
            userMessage += '🔌 No se puede conectar con el servidor.\n';
            userMessage += `Verifique que el backend esté ejecutándose en ${API_CONFIG.baseUrl}`;
        } else if (error.message.includes('Aeropuerto')) {
            userMessage += `✈️ ${error.message}\n\n`;
            userMessage += 'Por favor, seleccione aeropuertos válidos de la lista.';
        } else if (error.message.includes('timeout')) {
            userMessage += '⏱️ La solicitud tardó demasiado tiempo.\n';
            userMessage += 'El servidor puede estar sobrecargado. Intente nuevamente.';
        } else {
            userMessage += `⚠️ ${error.message}`;
        }

        alert(userMessage);
    } finally {
        showLoading(false);
        disableButtons(false);
    }
}

// ============================================================================
// VISUALIZACIÓN DE RESULTADOS
// ============================================================================
function displayResults(data) {
    // Determinar si es puntual o retrasado
    const isPuntual = data.prediccion === 'Puntual';

    // Actualizar icono y estado
    elements.predictionIcon.innerHTML = isPuntual ? '✈️' : '⏰';
    elements.predictionIcon.className = `prediction-icon ${isPuntual ? 'success' : 'danger'}`;

    elements.predictionStatus.textContent = data.prediccion;
    elements.predictionStatus.className = `prediction-status ${isPuntual ? 'success' : 'danger'}`;

    elements.predictionSubtitle.textContent = isPuntual
        ? 'El vuelo tiene alta probabilidad de despegar a tiempo'
        : 'El vuelo podría experimentar retrasos';

    // Actualizar métricas
    const probabilityPercent = (data.probabilidad_retraso * 100).toFixed(1);
    const confidencePercent = (data.confianza * 100).toFixed(1);

    elements.metricProbability.textContent = `${probabilityPercent}%`;
    elements.metricConfidence.textContent = `${confidencePercent}%`;
    elements.metricDistance.textContent = `${data.distancia_km.toFixed(0)} km`;

    // Animar barras
    setTimeout(() => {
        elements.probabilityBar.style.width = `${probabilityPercent}%`;
        elements.confidenceBar.style.width = `${confidencePercent}%`;
    }, 100);

    // Actualizar clima
    if (data.clima_origen) {
        const clima = data.clima_origen;
        elements.weatherCondition.textContent = clima.descripcion || clima.condicion;
        elements.weatherTemp.textContent = `${clima.temperatura.toFixed(1)}°C`;
        elements.weatherHumidity.textContent = `${clima.humedad}%`;
        elements.weatherWind.textContent = `${clima.viento_velocidad.toFixed(1)} m/s`;
        elements.weatherVisibility.textContent = `${(clima.visibilidad / 1000).toFixed(1)} km`;
    }

    // Actualizar metadata
    if (data.metadata) {
        displayMetadata(data.metadata, data.modo_mock);
    }

    // Mostrar sección de resultados con animación
    elements.resultsSection.style.display = 'block';
    elements.resultsSection.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
}

// ============================================================================
// VISUALIZACIÓN DE METADATA
// ============================================================================
function displayMetadata(metadata, isMock) {
    elements.metadataGrid.innerHTML = '';

    // Agregar indicador de modo con información detallada
    if (isMock !== undefined) {
        const modo = metadata.modo || 'MOCK';
        let modoTexto = '';
        let modoColor = '';

        if (modo === 'MOCK_CON_ML') {
            modoTexto = '🚀 Demo con Modelo ML Real';
            modoColor = 'color: hsl(142, 71%, 45%);'; // Verde
        } else if (modo === 'MOCK_FALLBACK') {
            modoTexto = '🔧 Demo (Fallback - ML no disponible)';
            modoColor = 'color: hsl(45, 100%, 51%);'; // Amarillo
        } else if (isMock) {
            modoTexto = '🔧 Demo (Mock)';
            modoColor = 'color: hsl(210, 100%, 56%);'; // Azul
        } else {
            modoTexto = '🚀 Predicción Real con ML';
            modoColor = 'color: hsl(142, 71%, 45%);'; // Verde
        }

        addMetadataItem('Modo', modoTexto, modoColor);
    }

    // Agregar nota si existe
    if (metadata.nota) {
        addMetadataItem('Nota', metadata.nota, 'color: hsl(45, 100%, 51%);');
    }

    // Agregar items de metadata
    const metadataMap = {
        'aerolinea': 'Aerolínea',
        'ruta': 'Ruta',
        'origen_nombre': 'Origen',
        'destino_nombre': 'Destino',
        'fecha_partida': 'Fecha de Partida',
        'timestamp_prediccion': 'Timestamp'
    };

    for (const [key, label] of Object.entries(metadataMap)) {
        if (metadata[key]) {
            let value = metadata[key];

            // Formatear timestamp si es necesario
            if (key.includes('timestamp') || key.includes('fecha')) {
                try {
                    const date = new Date(value);
                    value = date.toLocaleString('es-ES', {
                        year: 'numeric',
                        month: 'long',
                        day: 'numeric',
                        hour: '2-digit',
                        minute: '2-digit'
                    });
                } catch (e) {
                    // Mantener valor original si falla el parseo
                }
            }

            addMetadataItem(label, value);
        }
    }
}

function addMetadataItem(label, value, customStyle = '') {
    const item = document.createElement('div');
    item.className = 'metadata-item';
    item.innerHTML = `
        <span class="metadata-label">${label}</span>
        <span class="metadata-value" style="${customStyle}">${value}</span>
    `;
    elements.metadataGrid.appendChild(item);
}

// ============================================================================
// UTILIDADES UI
// ============================================================================
function showLoading(show) {
    if (show) {
        elements.loadingOverlay.classList.add('active');
    } else {
        elements.loadingOverlay.classList.remove('active');
    }
}

function disableButtons(disable) {
    elements.submitBtn.disabled = disable;
    elements.mockBtn.disabled = disable;

    if (disable) {
        elements.btnText.textContent = 'Procesando...';
    } else {
        elements.btnText.textContent = 'Obtener Predicción';
    }
}

// ============================================================================
// MANEJO DE ERRORES GLOBAL
// ============================================================================
window.addEventListener('error', (event) => {
    console.error('❌ Error global:', event.error);
});

window.addEventListener('unhandledrejection', (event) => {
    console.error('❌ Promise rechazada:', event.reason);
});

console.log('✅ App.js cargado correctamente');
