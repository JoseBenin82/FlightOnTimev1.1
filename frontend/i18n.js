// ============================================================================
// FLIGHTONTIME - INTERNACIONALIZACIÓN (i18n)
// ============================================================================
// Sistema de traducción para soporte multiidioma (Español/Inglés)
// ============================================================================

const translations = {
    es: {
        // Header
        'header.title': 'FlightOnTime',
        'header.status.operational': 'Sistema Operativo',
        'header.status.limited': 'Modo Limitado',

        // Form Section
        'form.title': 'Predicción de Puntualidad',
        'form.description': 'Ingrese los datos del vuelo para obtener una predicción basada en ML y datos meteorológicos',
        'form.airline': 'Aerolínea',
        'form.airline.select': 'Seleccione una aerolínea',
        'form.origin': 'Aeropuerto de Origen',
        'form.origin.select': 'Seleccione origen',
        'form.destination': 'Aeropuerto de Destino',
        'form.destination.select': 'Seleccione destino',
        'form.departure': 'Fecha y Hora de Partida',
        'form.submit': 'Obtener Predicción',
        'form.mock': 'Modo Demo (Mock)',
        'form.processing': 'Procesando...',

        // Results
        'results.title': 'Resultado de la Predicción',
        'results.ontime': 'Puntual',
        'results.delayed': 'Retrasado',
        'results.ontime.subtitle': 'El vuelo tiene alta probabilidad de despegar a tiempo',
        'results.delayed.subtitle': 'El vuelo podría experimentar retrasos',

        // Metrics
        'metrics.probability': 'Probabilidad de Retraso',
        'metrics.confidence': 'Confianza del Modelo',
        'metrics.distance': 'Distancia del Vuelo',

        // Weather
        'weather.title': 'Clima Detectado en Origen',
        'weather.title.origin': 'Clima en Origen',
        'weather.title.dest': 'Clima en Destino',
        'weather.condition': 'Condición',
        'weather.temperature': 'Temperatura',
        'weather.humidity': 'Humedad',
        'weather.wind': 'Viento',
        'weather.visibility': 'Visibilidad',

        // Metadata
        'metadata.title': 'Información del Vuelo',
        'metadata.airline': 'Aerolínea',
        'metadata.route': 'Ruta de Vuelo',
        'metadata.distance': 'Distancia',
        'metadata.origin': 'Origen',
        'metadata.destination': 'Destino',
        'metadata.departure': 'Salida Programada',
        'metadata.calculated': 'Cálculo Realizado',
        'metadata.mode.mock': '🔧 Demo (Datos simulados)',
        'metadata.mode.real': '🚀 Predicción Real (Producción)',
        'metadata.mode.ml': '🚀 Demo con Modelo ML Real',
        'metadata.mode.fallback': '🔧 Demo (Fallback activo)',
        'metadata.mode.label': 'Modo del Sistema',
        'metadata.note': 'Nota del Sistema',

        // Loading
        'loading.text': 'Analizando datos de vuelo y clima...',

        // Footer
        'footer.text': '© 2025 FlightOnTime - Oracle Enterprise Partner | Sistema de Misión Crítica',

        // Errors
        'error.same.airport': '⚠️ El aeropuerto de origen y destino deben ser diferentes',
        'error.not.found': '⚠️ No se hallan esos datos en la base de datos.',
        'error.verify': 'Por favor, verifique que:',
        'error.airline.valid': '• La aerolínea seleccionada sea válida',
        'error.airports.exist': '• Los aeropuertos de origen y destino existan en el sistema',
        'error.airlines.valid': 'Aerolíneas válidas: LATAM, GOL, AZUL, AVIANCA, COPA, AMERICAN, UNITED, DELTA',
        'error.connection': '🔌 No se puede conectar con el servidor.',
        'error.backend': 'Verifique que el backend esté ejecutándose en',
        'error.timeout': '⏱️ La solicitud tardó demasiado tiempo.',
        'error.server.busy': 'El servidor puede estar sobrecargado. Intente nuevamente.',
        'error.prediction': 'Error al obtener predicción:',

        // Settings
        'settings.language': 'Idioma',
        'settings.units': 'Unidades de Distancia',
        'settings.units.km': 'Kilómetros (km)',
        'settings.units.miles': 'Millas (mi)',

        // Countries
        'country.brazil': 'Brasil',
        'country.usa': 'Estados Unidos',
        'country.mexico': 'México',
        'country.europe': 'Europa'
    },
    en: {
        // Header
        'header.title': 'FlightOnTime',
        'header.status.operational': 'System Operational',
        'header.status.limited': 'Limited Mode',

        // Form Section
        'form.title': 'Flight Punctuality Prediction',
        'form.description': 'Enter flight details to get a prediction based on ML and real-time weather data',
        'form.airline': 'Airline',
        'form.airline.select': 'Select an airline',
        'form.origin': 'Origin Airport',
        'form.origin.select': 'Select origin',
        'form.destination': 'Destination Airport',
        'form.destination.select': 'Select destination',
        'form.departure': 'Departure Date and Time',
        'form.submit': 'Get Prediction',
        'form.mock': 'Demo Mode (Mock)',
        'form.processing': 'Processing...',

        // Results
        'results.title': 'Prediction Result',
        'results.ontime': 'On Time',
        'results.delayed': 'Delayed',
        'results.ontime.subtitle': 'The flight has a high probability of departing on time',
        'results.delayed.subtitle': 'The flight may experience delays',

        // Metrics
        'metrics.probability': 'Delay Probability',
        'metrics.confidence': 'Model Confidence',
        'metrics.distance': 'Flight Distance',

        // Weather
        'weather.title': 'Detected Weather at Origin',
        'weather.title.origin': 'Weather at Origin',
        'weather.title.dest': 'Weather at Destination',
        'weather.condition': 'Condition',
        'weather.temperature': 'Temperature',
        'weather.humidity': 'Humidity',
        'weather.wind': 'Wind',
        'weather.visibility': 'Visibility',

        // Metadata
        'metadata.title': 'Flight Information',
        'metadata.airline': 'Airline',
        'metadata.route': 'Flight Route',
        'metadata.distance': 'Distance',
        'metadata.origin': 'Origin',
        'metadata.destination': 'Destination',
        'metadata.departure': 'Scheduled Departure',
        'metadata.calculated': 'Calculated At',
        'metadata.mode.mock': '🔧 Demo (Simulated Data)',
        'metadata.mode.real': '🚀 Real Prediction (Production)',
        'metadata.mode.ml': '🚀 Demo with Real ML Model',
        'metadata.mode.fallback': '🔧 Demo (Fallback Active)',
        'metadata.mode.label': 'System Mode',
        'metadata.note': 'System Note',

        // Loading
        'loading.text': 'Analyzing flight and weather data...',

        // Footer
        'footer.text': '© 2025 FlightOnTime - Oracle Enterprise Partner | Mission Critical System',

        // Errors
        'error.same.airport': '⚠️ Origin and destination airports must be different',
        'error.not.found': '⚠️ Data not found in database.',
        'error.verify': 'Please verify that:',
        'error.airline.valid': '• The selected airline is valid',
        'error.airports.exist': '• Origin and destination airports exist in the system',
        'error.airlines.valid': 'Valid airlines: LATAM, GOL, AZUL, AVIANCA, COPA, AMERICAN, UNITED, DELTA',
        'error.connection': '🔌 Cannot connect to server.',
        'error.backend': 'Verify that the backend is running at',
        'error.timeout': '⏱️ Request took too long.',
        'error.server.busy': 'Server may be overloaded. Please try again.',
        'error.prediction': 'Error getting prediction:',

        // Settings
        'settings.language': 'Language',
        'settings.units': 'Distance Units',
        'settings.units.km': 'Kilometers (km)',
        'settings.units.miles': 'Miles (mi)',

        // Countries
        'country.brazil': 'Brazil',
        'country.usa': 'United States',
        'country.mexico': 'Mexico',
        'country.europe': 'Europe'
    }
};

// ============================================================================
// CLASE DE INTERNACIONALIZACIÓN
// ============================================================================
class I18n {
    constructor() {
        // Detectar idioma del navegador o usar español por defecto
        const browserLang = navigator.language.split('-')[0];
        this.currentLanguage = ['es', 'en'].includes(browserLang) ? browserLang : 'es';

        // Cargar desde localStorage si existe
        const savedLang = localStorage.getItem('flightontime_language');
        if (savedLang && ['es', 'en'].includes(savedLang)) {
            this.currentLanguage = savedLang;
        }
    }

    /**
     * Obtiene una traducción por su clave
     * @param {string} key - Clave de traducción (ej: 'form.title')
     * @param {object} params - Parámetros opcionales para interpolación
     * @returns {string} Texto traducido
     */
    t(key, params = {}) {
        let text = translations[this.currentLanguage][key] || key;

        // Interpolación de parámetros
        Object.keys(params).forEach(param => {
            text = text.replace(`{${param}}`, params[param]);
        });

        return text;
    }

    /**
     * Cambia el idioma actual
     * @param {string} lang - Código de idioma ('es' o 'en')
     */
    setLanguage(lang) {
        if (!['es', 'en'].includes(lang)) {
            console.error(`Idioma no soportado: ${lang}`);
            return;
        }

        this.currentLanguage = lang;
        localStorage.setItem('flightontime_language', lang);

        // Actualizar atributo lang del HTML
        document.documentElement.lang = lang;

        // Emitir evento personalizado para que otros componentes se actualicen
        window.dispatchEvent(new CustomEvent('languageChanged', { detail: { language: lang } }));
    }

    /**
     * Obtiene el idioma actual
     * @returns {string} Código de idioma actual
     */
    getLanguage() {
        return this.currentLanguage;
    }
}

// ============================================================================
// CLASE DE CONVERSIÓN DE UNIDADES
// ============================================================================
class UnitConverter {
    constructor() {
        // Cargar unidad preferida desde localStorage o usar km por defecto
        this.currentUnit = localStorage.getItem('flightontime_distance_unit') || 'km';
    }

    /**
     * Convierte kilómetros a la unidad actual
     * @param {number} km - Distancia en kilómetros
     * @param {boolean} includeUnit - Si debe incluir la unidad en el texto
     * @returns {string|number} Distancia convertida
     */
    convertDistance(km, includeUnit = true) {
        if (this.currentUnit === 'miles') {
            const miles = km * 0.621371;
            return includeUnit ? `${miles.toFixed(0)} mi` : miles;
        }
        return includeUnit ? `${km.toFixed(0)} km` : km;
    }

    /**
     * Convierte temperatura
     * @param {number} celsius - Temperatura en Celsius
     * @returns {string} Temperatura formateada
     */
    convertTemperature(celsius) {
        if (this.currentUnit === 'miles') {
            const fahrenheit = (celsius * 9 / 5) + 32;
            return `${fahrenheit.toFixed(1)}°F`;
        }
        return `${celsius.toFixed(1)}°C`;
    }

    /**
     * Convierte velocidad del viento
     * @param {number} ms - Velocidad en m/s
     * @returns {string} Velocidad formateada
     */
    convertWindSpeed(ms) {
        if (this.currentUnit === 'miles') {
            const mph = ms * 2.23694;
            return `${mph.toFixed(1)} mph`;
        }
        return `${ms.toFixed(1)} m/s`;
    }

    /**
     * Convierte visibilidad
     * @param {number} meters - Visibilidad en metros
     * @returns {string} Visibilidad formateada
     */
    convertVisibility(meters) {
        if (this.currentUnit === 'miles') {
            const miles = (meters / 1000) * 0.621371;
            return `${miles.toFixed(1)} mi`;
        }
        return `${(meters / 1000).toFixed(1)} km`;
    }

    /**
     * Cambia la unidad de distancia
     * @param {string} unit - 'km' o 'miles'
     */
    setUnit(unit) {
        if (!['km', 'miles'].includes(unit)) {
            console.error(`Unidad no soportada: ${unit}`);
            return;
        }

        this.currentUnit = unit;
        localStorage.setItem('flightontime_distance_unit', unit);

        // Emitir evento personalizado
        window.dispatchEvent(new CustomEvent('unitChanged', { detail: { unit } }));
    }

    /**
     * Obtiene la unidad actual
     * @returns {string} Unidad actual ('km' o 'miles')
     */
    getUnit() {
        return this.currentUnit;
    }
}

// ============================================================================
// EXPORTAR INSTANCIAS GLOBALES
// ============================================================================
const i18n = new I18n();
const unitConverter = new UnitConverter();

// Hacer disponibles globalmente
window.i18n = i18n;
window.unitConverter = unitConverter;

console.log('✅ i18n.js cargado correctamente');
console.log(`📍 Idioma actual: ${i18n.getLanguage()}`);
console.log(`📏 Unidad de distancia: ${unitConverter.getUnit()}`);
