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

    // Inicializar i18n
    initializeI18n();

    // Inicializar panel de configuración
    initializeSettingsPanel();

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
        statusText.textContent = window.i18n.t('header.status.operational');
    } else {
        statusDot.style.background = 'hsl(45, 100%, 51%)';
        statusText.textContent = window.i18n.t('header.status.limited');
    }
}

// ============================================================================
// MANEJO DE ENVÍO DE FORMULARIO (MODO REAL)
// ============================================================================
async function handleSubmit(event) {
    event.preventDefault();

    // Validar que origen y destino sean diferentes
    if (elements.origen.value === elements.destino.value) {
        alert(window.i18n.t('error.same.airport'));
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
        alert(window.i18n.t('error.same.airport'));
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
    // Guardar datos para re-renderizar cuando cambien las unidades
    window.lastPredictionData = data;

    // Determinar si es puntual o retrasado
    // API ahora devuelve: 0 = Puntual, 1 = Retrasado (según CONTRATO_INTEGRACION.md)
    const isPuntual = data.prediccion === 0;
    const predictionText = isPuntual ? window.i18n.t('results.ontime') : window.i18n.t('results.delayed');

    // Actualizar icono y estado
    elements.predictionIcon.innerHTML = isPuntual ? '✈️' : '⏰';
    elements.predictionIcon.className = `prediction-icon ${isPuntual ? 'success' : 'danger'}`;

    elements.predictionStatus.textContent = predictionText;
    elements.predictionStatus.className = `prediction-status ${isPuntual ? 'success' : 'danger'}`;

    elements.predictionSubtitle.textContent = isPuntual
        ? window.i18n.t('results.ontime.subtitle')
        : window.i18n.t('results.delayed.subtitle');

    // Actualizar métricas (usando nombres del contrato)
    const probabilityPercent = (data.probabilidad_retraso * 100).toFixed(1);
    const confidencePercent = (data.confianza * 100).toFixed(1);

    elements.metricProbability.textContent = `${probabilityPercent}%`;
    elements.metricConfidence.textContent = `${confidencePercent}%`;

    // Usar convertidor de unidades para la distancia
    elements.metricDistance.textContent = window.unitConverter.convertDistance(data.distancia_km, true);

    // Animar barras
    setTimeout(() => {
        elements.probabilityBar.style.width = `${probabilityPercent}%`;
        elements.confidenceBar.style.width = `${confidencePercent}%`;
    }, 100);

    // Actualizar clima
    if (data.clima_origen) {
        const clima = data.clima_origen;
        elements.weatherCondition.textContent = clima.descripcion || clima.condicion;
        elements.weatherTemp.textContent = window.unitConverter.convertTemperature(clima.temperatura);
        elements.weatherHumidity.textContent = `${clima.humedad}%`;
        elements.weatherWind.textContent = window.unitConverter.convertWindSpeed(clima.viento_velocidad);
        elements.weatherVisibility.textContent = window.unitConverter.convertVisibility(clima.visibilidad);
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

    // 1. MODO DE OPERACIÓN (Full Width)
    if (isMock !== undefined) {
        const modo = metadata.modo || 'MOCK';
        let modoTexto = '';
        let type = 'info';

        if (modo === 'MOCK_CON_ML') {
            modoTexto = '🚀 Demo con Modelo ML Real';
            type = 'success';
        } else if (modo === 'MOCK_FALLBACK') {
            modoTexto = '🔧 Demo (Fallback activo)';
            type = 'warning';
        } else if (isMock) {
            modoTexto = '🔧 Demo (Datos simulados)';
            type = 'info';
        } else {
            modoTexto = '🚀 Predicción Real (Producción)';
            type = 'success';
        }

        addMetadataItem('Modo del Sistema', modoTexto, type, true);
    }

    // 2. NOTAS IMPORTANTES (Full Width)
    if (metadata.nota) {
        addMetadataItem('Nota del Sistema', metadata.nota, 'warning', true);
    }

    // 3. RESTO DE METADATA (Grid normal)
    const metadataMap = {
        'aerolinea': 'Aerolínea',
        'ruta': 'Ruta de Vuelo',
        'distancia_km': 'Distancia',
        'origen_nombre': 'Origen',
        'destino_nombre': 'Destino',
        'fecha_partida': 'Salida Programada',
        'timestamp_prediccion': 'Cálculo Realizado'
    };

    for (const [key, label] of Object.entries(metadataMap)) {
        if (metadata[key]) {
            let value = metadata[key];

            // Formatear timestamp y fechas
            if (key.includes('timestamp') || key.includes('fecha')) {
                try {
                    const date = new Date(value);
                    value = date.toLocaleString('es-ES', {
                        day: '2-digit',
                        month: 'short',
                        year: 'numeric',
                        hour: '2-digit',
                        minute: '2-digit'
                    });
                } catch (e) { /* ignore */ }
            }

            // Append unit to distance if missing (though usually comes as number in displayResults, here it's string in metadata?)
            // Actually metadata might not have units, but let's just display as is.

            addMetadataItem(label, value, 'default', false);
        }
    }
}

function addMetadataItem(label, value, type = 'default', fullWidth = false) {
    const item = document.createElement('div');

    // Construir clases
    let className = 'metadata-item';
    if (fullWidth) className += ' full-width';
    if (type !== 'default') className += ` ${type}`;

    item.className = className;

    // Icono opcional según tipo (solo para full width para darle más estilo)
    let icon = '';
    if (fullWidth) {
        if (type === 'warning') icon = '⚠️ ';
        if (type === 'success') icon = '✅ ';
        if (type === 'info') icon = 'ℹ️ ';
    }

    item.innerHTML = `
        <span class="metadata-label">${label}</span>
        <span class="metadata-value">${icon}${value}</span>
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
        elements.btnText.textContent = window.i18n.t('form.processing');
    } else {
        elements.btnText.textContent = window.i18n.t('form.submit');
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

// ============================================================================
// INTERNACIONALIZACIÓN (i18n)
// ============================================================================

/**
 * Inicializa el sistema de internacionalización
 */
function initializeI18n() {
    // Aplicar traducciones iniciales
    updateAllTranslations();

    // Escuchar cambios de idioma
    window.addEventListener('languageChanged', () => {
        updateAllTranslations();

        // Re-renderizar resultados si están visibles
        if (elements.resultsSection.style.display !== 'none') {
            // Los resultados se actualizarán automáticamente con los atributos data-i18n
        }
    });

    // Escuchar cambios de unidad
    window.addEventListener('unitChanged', () => {
        // Re-renderizar resultados si están visibles
        if (elements.resultsSection.style.display !== 'none' && window.lastPredictionData) {
            displayResults(window.lastPredictionData);
        }
    });

    console.log('✅ Sistema i18n inicializado');
}

/**
 * Actualiza todas las traducciones en la página
 */
function updateAllTranslations() {
    const elementsWithI18n = document.querySelectorAll('[data-i18n]');

    elementsWithI18n.forEach(element => {
        const key = element.getAttribute('data-i18n');
        const translation = window.i18n.t(key);

        // Actualizar el contenido del elemento
        if (element.tagName === 'INPUT' || element.tagName === 'TEXTAREA') {
            element.placeholder = translation;
        } else if (element.tagName === 'OPTION' || element.tagName === 'OPTGROUP') {
            element.textContent = translation;
        } else {
            // Para otros elementos, preservar el HTML interno si tiene hijos
            if (element.children.length === 0) {
                element.textContent = translation;
            } else {
                // Si tiene hijos, solo actualizar el texto directo
                const textNodes = Array.from(element.childNodes).filter(node => node.nodeType === Node.TEXT_NODE);
                if (textNodes.length > 0) {
                    textNodes[0].textContent = translation;
                }
            }
        }
    });
}

// ============================================================================
// PANEL DE CONFIGURACIÓN
// ============================================================================

/**
 * Inicializa el panel de configuración
 */
function initializeSettingsPanel() {
    const settingsBtn = document.getElementById('settingsBtn');
    const settingsPanel = document.getElementById('settingsPanel');
    const settingsClose = document.getElementById('settingsClose');

    const langEs = document.getElementById('langEs');
    const langEn = document.getElementById('langEn');
    const unitKm = document.getElementById('unitKm');
    const unitMiles = document.getElementById('unitMiles');

    // Abrir panel
    settingsBtn.addEventListener('click', () => {
        settingsPanel.classList.add('active');
    });

    // Cerrar panel
    settingsClose.addEventListener('click', () => {
        settingsPanel.classList.remove('active');
    });

    // Cerrar al hacer clic fuera del panel
    settingsPanel.addEventListener('click', (e) => {
        if (e.target === settingsPanel) {
            settingsPanel.classList.remove('active');
        }
    });

    // Cambiar idioma
    langEs.addEventListener('click', () => {
        window.i18n.setLanguage('es');
        langEs.classList.add('active');
        langEn.classList.remove('active');
    });

    langEn.addEventListener('click', () => {
        window.i18n.setLanguage('en');
        langEn.classList.add('active');
        langEs.classList.remove('active');
    });

    // Cambiar unidad
    unitKm.addEventListener('click', () => {
        window.unitConverter.setUnit('km');
        unitKm.classList.add('active');
        unitMiles.classList.remove('active');
    });

    unitMiles.addEventListener('click', () => {
        window.unitConverter.setUnit('miles');
        unitMiles.classList.add('active');
        unitKm.classList.remove('active');
    });

    // Establecer estado inicial de los botones
    const currentLang = window.i18n.getLanguage();
    if (currentLang === 'es') {
        langEs.classList.add('active');
        langEn.classList.remove('active');
    } else {
        langEn.classList.add('active');
        langEs.classList.remove('active');
    }

    const currentUnit = window.unitConverter.getUnit();
    if (currentUnit === 'km') {
        unitKm.classList.add('active');
        unitMiles.classList.remove('active');
    } else {
        unitMiles.classList.add('active');
        unitKm.classList.remove('active');
    }

    console.log('✅ Panel de configuración inicializado');
}

console.log('✅ App.js cargado correctamente');
