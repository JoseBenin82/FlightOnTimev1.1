# ============================================================================
# DICCIONARIO DE COORDENADAS IATA - AEROPUERTOS PRINCIPALES
# ============================================================================
# Se utiliza la librería 'airportsdata' para obtener coordenadas
# de miles de aeropuertos globales.
# ============================================================================

import airportsdata
import logging

logger = logging.getLogger(__name__)

# Cargar base de datos de aeropuertos (formato IATA)
# Esto carga un diccionario donde la clave es el código IATA (ej: 'JFK')
try:
    _airports = airportsdata.load('IATA')
    logger.info(f"📚 Base de datos de aeropuertos cargada: {len(_airports)} aeropuertos")
except Exception as e:
    logger.error(f"❌ Error al cargar airportsdata: {e}")
    _airports = {}

class AirportCoordinatesDict(dict):
    """
    Wrapper para parecerse al diccionario original pero buscando en airportsdata.
    Retorna {'lat': float, 'lon': float, 'name': str}
    """
    def __getitem__(self, key):
        key = key.upper()
        if key in _airports:
            data = _airports[key]
            return {
                "lat": data['lat'],
                "lon": data['lon'],
                "name": data['name']
            }
        raise KeyError(key)

    def __contains__(self, key):
        return key.upper() in _airports

    def get(self, key, default=None):
        try:
            return self[key]
        except KeyError:
            return default
            
    def keys(self):
        return _airports.keys()
        
    def items(self):
        # Esto es costoso si se itera todo, pero mantenemos compatibilidad
        for k, v in _airports.items():
            yield k, {"lat": v['lat'], "lon": v['lon'], "name": v['name']}

    def __len__(self):
        return len(_airports)

# Instancia global compatible con el código existente
AIRPORT_COORDINATES = AirportCoordinatesDict()

