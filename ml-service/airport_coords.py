# ============================================================================
# DICCIONARIO DE COORDENADAS IATA - AEROPUERTOS PRINCIPALES
# ============================================================================
# Este diccionario contiene las coordenadas geográficas (latitud, longitud)
# de los principales aeropuertos internacionales para cálculo automático
# de distancias usando la fórmula de Haversine.
# ============================================================================

AIRPORT_COORDINATES = {
    # Brasil
    "GRU": {"lat": -23.4356, "lon": -46.4731, "name": "São Paulo-Guarulhos"},
    "GIG": {"lat": -22.8099, "lon": -43.2505, "name": "Rio de Janeiro-Galeão"},
    "BSB": {"lat": -15.8697, "lon": -47.9208, "name": "Brasília"},
    "CGH": {"lat": -23.6261, "lon": -46.6564, "name": "São Paulo-Congonhas"},
    "SSA": {"lat": -12.9086, "lon": -38.3225, "name": "Salvador"},
    "CNF": {"lat": -19.6244, "lon": -43.9719, "name": "Belo Horizonte-Confins"},
    "REC": {"lat": -8.1264, "lon": -34.9236, "name": "Recife"},
    "FOR": {"lat": -3.7763, "lon": -38.5326, "name": "Fortaleza"},
    "POA": {"lat": -29.9944, "lon": -51.1714, "name": "Porto Alegre"},
    "CWB": {"lat": -25.5284, "lon": -49.1758, "name": "Curitiba"},
    
    # Estados Unidos
    "JFK": {"lat": 40.6413, "lon": -73.7781, "name": "New York-JFK"},
    "LAX": {"lat": 33.9416, "lon": -118.4085, "name": "Los Angeles"},
    "ORD": {"lat": 41.9742, "lon": -87.9073, "name": "Chicago-O'Hare"},
    "MIA": {"lat": 25.7959, "lon": -80.2870, "name": "Miami"},
    "ATL": {"lat": 33.6407, "lon": -84.4277, "name": "Atlanta"},
    "DFW": {"lat": 32.8998, "lon": -97.0403, "name": "Dallas-Fort Worth"},
    "SFO": {"lat": 37.6213, "lon": -122.3790, "name": "San Francisco"},
    "IAH": {"lat": 29.9902, "lon": -95.3368, "name": "Houston"},
    "LAS": {"lat": 36.0840, "lon": -115.1537, "name": "Las Vegas"},
    "BOS": {"lat": 42.3656, "lon": -71.0096, "name": "Boston"},
    
    # México
    "MEX": {"lat": 19.4363, "lon": -99.0721, "name": "Ciudad de México"},
    "CUN": {"lat": 21.0365, "lon": -86.8770, "name": "Cancún"},
    "GDL": {"lat": 20.5218, "lon": -103.3106, "name": "Guadalajara"},
    "MTY": {"lat": 25.7785, "lon": -100.1076, "name": "Monterrey"},
    "TIJ": {"lat": 32.5411, "lon": -116.9700, "name": "Tijuana"},
    
    # Europa
    "LHR": {"lat": 51.4700, "lon": -0.4543, "name": "London-Heathrow"},
    "CDG": {"lat": 49.0097, "lon": 2.5479, "name": "Paris-Charles de Gaulle"},
    "FRA": {"lat": 50.0379, "lon": 8.5622, "name": "Frankfurt"},
    "MAD": {"lat": 40.4983, "lon": -3.5676, "name": "Madrid"},
    "BCN": {"lat": 41.2974, "lon": 2.0833, "name": "Barcelona"},
    "AMS": {"lat": 52.3105, "lon": 4.7683, "name": "Amsterdam"},
    "FCO": {"lat": 41.8003, "lon": 12.2389, "name": "Rome-Fiumicino"},
    "LIS": {"lat": 38.7742, "lon": -9.1342, "name": "Lisbon"},
    
    # América del Sur (otros)
    "EZE": {"lat": -34.8222, "lon": -58.5358, "name": "Buenos Aires-Ezeiza"},
    "BOG": {"lat": 4.7016, "lon": -74.1469, "name": "Bogotá"},
    "LIM": {"lat": -12.0219, "lon": -77.1143, "name": "Lima"},
    "SCL": {"lat": -33.3930, "lon": -70.7858, "name": "Santiago de Chile"},
    
    # Asia
    "NRT": {"lat": 35.7720, "lon": 140.3929, "name": "Tokyo-Narita"},
    "HND": {"lat": 35.5494, "lon": 139.7798, "name": "Tokyo-Haneda"},
    "PEK": {"lat": 40.0799, "lon": 116.6031, "name": "Beijing"},
    "PVG": {"lat": 31.1443, "lon": 121.8083, "name": "Shanghai-Pudong"},
    "HKG": {"lat": 22.3080, "lon": 113.9185, "name": "Hong Kong"},
    "SIN": {"lat": 1.3644, "lon": 103.9915, "name": "Singapore"},
    "ICN": {"lat": 37.4602, "lon": 126.4407, "name": "Seoul-Incheon"},
    "DXB": {"lat": 25.2532, "lon": 55.3657, "name": "Dubai"},
}
