package com.oracle.flightontime.util;

import java.util.HashMap;
import java.util.Map;

/**
 * ============================================================================
 * UTILIDADES GEOGRÁFICAS - CÁLCULO DE DISTANCIAS
 * ============================================================================
 * Proporciona funciones para calcular distancias entre aeropuertos usando
 * la fórmula de Haversine.
 * ============================================================================
 */
public class GeoUtils {

    /**
     * Coordenadas de aeropuertos principales (IATA -> {lat, lon, name})
     */
    private static final Map<String, AirportCoordinates> AIRPORT_COORDINATES = new HashMap<>();

    static {
        // Brasil
        AIRPORT_COORDINATES.put("GRU", new AirportCoordinates(-23.4356, -46.4731, "São Paulo-Guarulhos"));
        AIRPORT_COORDINATES.put("GIG", new AirportCoordinates(-22.8099, -43.2505, "Rio de Janeiro-Galeão"));
        AIRPORT_COORDINATES.put("BSB", new AirportCoordinates(-15.8697, -47.9208, "Brasília"));
        AIRPORT_COORDINATES.put("CGH", new AirportCoordinates(-23.6261, -46.6564, "São Paulo-Congonhas"));
        AIRPORT_COORDINATES.put("SSA", new AirportCoordinates(-12.9086, -38.3225, "Salvador"));
        AIRPORT_COORDINATES.put("CNF", new AirportCoordinates(-19.6244, -43.9719, "Belo Horizonte-Confins"));
        AIRPORT_COORDINATES.put("REC", new AirportCoordinates(-8.1264, -34.9236, "Recife"));
        AIRPORT_COORDINATES.put("FOR", new AirportCoordinates(-3.7763, -38.5326, "Fortaleza"));
        AIRPORT_COORDINATES.put("POA", new AirportCoordinates(-29.9944, -51.1714, "Porto Alegre"));
        AIRPORT_COORDINATES.put("CWB", new AirportCoordinates(-25.5284, -49.1758, "Curitiba"));

        // Estados Unidos
        AIRPORT_COORDINATES.put("JFK", new AirportCoordinates(40.6413, -73.7781, "New York-JFK"));
        AIRPORT_COORDINATES.put("LAX", new AirportCoordinates(33.9416, -118.4085, "Los Angeles"));
        AIRPORT_COORDINATES.put("ORD", new AirportCoordinates(41.9742, -87.9073, "Chicago-O'Hare"));
        AIRPORT_COORDINATES.put("MIA", new AirportCoordinates(25.7959, -80.2870, "Miami"));
        AIRPORT_COORDINATES.put("ATL", new AirportCoordinates(33.6407, -84.4277, "Atlanta"));
        AIRPORT_COORDINATES.put("DFW", new AirportCoordinates(32.8998, -97.0403, "Dallas-Fort Worth"));
        AIRPORT_COORDINATES.put("SFO", new AirportCoordinates(37.6213, -122.3790, "San Francisco"));

        // México
        AIRPORT_COORDINATES.put("MEX", new AirportCoordinates(19.4363, -99.0721, "Ciudad de México"));
        AIRPORT_COORDINATES.put("CUN", new AirportCoordinates(21.0365, -86.8770, "Cancún"));
        AIRPORT_COORDINATES.put("GDL", new AirportCoordinates(20.5218, -103.3106, "Guadalajara"));
        AIRPORT_COORDINATES.put("MTY", new AirportCoordinates(25.7785, -100.1076, "Monterrey"));

        // Europa
        AIRPORT_COORDINATES.put("LHR", new AirportCoordinates(51.4700, -0.4543, "London-Heathrow"));
        AIRPORT_COORDINATES.put("CDG", new AirportCoordinates(49.0097, 2.5479, "Paris-Charles de Gaulle"));
        AIRPORT_COORDINATES.put("FRA", new AirportCoordinates(50.0379, 8.5622, "Frankfurt"));
        AIRPORT_COORDINATES.put("MAD", new AirportCoordinates(40.4983, -3.5676, "Madrid"));
        AIRPORT_COORDINATES.put("BCN", new AirportCoordinates(41.2974, 2.0833, "Barcelona"));
    }

    /**
     * Calcula la distancia entre dos aeropuertos usando la fórmula de Haversine.
     *
     * @param origenIATA  Código IATA del aeropuerto de origen
     * @param destinoIATA Código IATA del aeropuerto de destino
     * @return Distancia en kilómetros, o null si algún aeropuerto no se encuentra
     */
    public static Double calcularDistancia(String origenIATA, String destinoIATA) {
        AirportCoordinates origen = AIRPORT_COORDINATES.get(origenIATA.toUpperCase());
        AirportCoordinates destino = AIRPORT_COORDINATES.get(destinoIATA.toUpperCase());

        if (origen == null || destino == null) {
            return null;
        }

        return calcularDistanciaHaversine(
                origen.getLatitude(),
                origen.getLongitude(),
                destino.getLatitude(),
                destino.getLongitude());
    }

    /**
     * Calcula la distancia entre dos puntos geográficos usando la fórmula de
     * Haversine.
     *
     * @param lat1 Latitud del punto 1
     * @param lon1 Longitud del punto 1
     * @param lat2 Latitud del punto 2
     * @param lon2 Longitud del punto 2
     * @return Distancia en kilómetros
     */
    public static double calcularDistanciaHaversine(double lat1, double lon1, double lat2, double lon2) {
        // Radio de la Tierra en kilómetros
        final double R = 6371.0;

        // Convertir grados a radianes
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Diferencias
        double dlat = lat2Rad - lat1Rad;
        double dlon = lon2Rad - lon1Rad;

        // Fórmula de Haversine
        double a = Math.sin(dlat / 2) * Math.sin(dlat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(dlon / 2) * Math.sin(dlon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distancia = R * c;

        return Math.round(distancia * 100.0) / 100.0; // Redondear a 2 decimales
    }

    /**
     * Obtiene el nombre completo de un aeropuerto por su código IATA.
     *
     * @param iataCode Código IATA del aeropuerto
     * @return Nombre del aeropuerto, o null si no se encuentra
     */
    public static String getAirportName(String iataCode) {
        AirportCoordinates coords = AIRPORT_COORDINATES.get(iataCode.toUpperCase());
        return coords != null ? coords.getName() : null;
    }

    /**
     * Verifica si un aeropuerto existe en la base de datos.
     *
     * @param iataCode Código IATA del aeropuerto
     * @return true si el aeropuerto existe, false en caso contrario
     */
    public static boolean existeAeropuerto(String iataCode) {
        return AIRPORT_COORDINATES.containsKey(iataCode.toUpperCase());
    }

    /**
     * Clase interna para almacenar coordenadas de aeropuertos
     */
    private static class AirportCoordinates {
        private final double latitude;
        private final double longitude;
        private final String name;

        public AirportCoordinates(double latitude, double longitude, String name) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.name = name;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public String getName() {
            return name;
        }
    }
}
