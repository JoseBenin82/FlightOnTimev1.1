package com.oracle.flightontime.config;

import java.util.*;

/**
 * ===========================================================================
 * CONFIGURACIÓN DE AEROLÍNEAS Y AEROPUERTOS
 * ===========================================================================
 * Define las aerolíneas soportadas y sus aeropuertos disponibles.
 * Basado en los requerimientos del proyecto FlightOnTime v1.1
 * ===========================================================================
 */
public class AirlineConfig {

    /**
     * Mapa de aerolíneas y sus aeropuertos disponibles
     * Key: Código de aerolínea ("1" = Delta, "2" = Southwest)
     * Value: Set de códigos IATA de aeropuertos
     */
    public static final Map<String, Set<String>> AEROLINEAS_AEROPUERTOS;

    /**
     * Nombres de las aerolíneas
     */
    public static final Map<String, String> NOMBRES_AEROLINEAS;

    static {
        AEROLINEAS_AEROPUERTOS = new HashMap<>();
        NOMBRES_AEROLINEAS = new HashMap<>();

        // ====================================================================
        // DELTA AIR LINES (DL) - Código: "1"
        // ====================================================================
        NOMBRES_AEROLINEAS.put("1", "Delta Air Lines (DL)");
        
        Set<String> deltaAirports = new HashSet<>(Arrays.asList(
            "ABQ","AGS","ALB","ANC","ATL","ATW","AUS","AVL","BDL","BGR","BHM","BIL","BIS",
            "BNA","BOI","BOS","BTR","BTV","BUF","BUR","BWI","BZN","CAE","CHA","CHO","CHS",
            "CID","CLE","CLT","CMH","COS","CVG","DAB","DAL","DAY","DCA","DEN","DFW","DLH",
            "DSM","DTW","ECP","EGE","ELP","EWR","EYW","FAI","FAR","FAT","FAY","FCA","FLL",
            "FSD","GEG","GNV","GPT","GRB","GRR","GSO","GSP","HDN","HNL","HOU","HPN","HRL",
            "HSV","IAD","IAH","ICT","IDA","ILM","IND","JAC","JAN","JAX","JFK","JNU","KOA",
            "LAS","LAX","LEX","LGA","LGB","LIH","LIT","MCI","MCO","MDT","MDW","MEM","MIA",
            "MKE","MLB","MOB","MSN","MSO","MSP","MSY","MTJ","MYR","OAK","OGG","OKC","OMA",
            "ONT","ORD","ORF","PBI","PDX","PHL","PHX","PIT","PNS","PSC","PSP","PVD","PWM",
            "RAP","RDU","RIC","RNO","ROA","ROC","RSW","SAN","SAT","SAV","SBA","SBN","SDF",
            "SEA","SFO","SGF","SHV","SJC","SJU","SLC","SMF","SNA","SRQ","STL","STT","STX",
            "SYR","TLH","TPA","TRI","TUL","TUS","TVC","TYS","VPS","XNA"
        ));
        AEROLINEAS_AEROPUERTOS.put("1", deltaAirports);

        // ====================================================================
        // SOUTHWEST AIRLINES (WN) - Código: "2"
        // ====================================================================
        NOMBRES_AEROLINEAS.put("2", "Southwest Airlines (WN)");
        
        Set<String> southwestAirports = new HashSet<>(Arrays.asList(
            "ABQ","ALB","AMA","ATL","AUS","BDL","BHM","BNA","BOI","BOS","BUF","BUR","BWI",
            "BZN","CHS","CLE","CLT","CMH","COS","CRP","CVG","DAL","DCA","DEN","DSM","DTW",
            "ECP","ELP","EUG","FAT","FLL","GEG","GRR","GSP","HDN","HNL","HOU","HRL","IAD",
            "ICT","IND","ISP","ITO","JAN","JAX","KOA","LAS","LAX","LBB","LGA","LGB","LIH",
            "LIT","MAF","MCI","MCO","MDW","MEM","MHT","MIA","MKE","MSP","MSY","MTJ","MYR",
            "OAK","OGG","OKC","OMA","ONT","ORD","ORF","PBI","PDX","PHL","PHX","PIT","PNS",
            "PSP","PVD","PWM","RDU","RIC","RNO","ROC","RSW","SAN","SAT","SAV","SBA","SDF",
            "SEA","SFO","SJC","SJU","SLC","SMF","SNA","SRQ","STL","TPA","TUL","TUS","VPS"
        ));
        AEROLINEAS_AEROPUERTOS.put("2", southwestAirports);
    }

    /**
     * Verifica si una aerolínea es válida
     */
    public static boolean esAerolineaValida(String codigo) {
        return AEROLINEAS_AEROPUERTOS.containsKey(codigo);
    }

    /**
     * Verifica si un aeropuerto está disponible para una aerolínea
     */
    public static boolean esAeropuertoValido(String aerolinea, String aeropuerto) {
        Set<String> aeropuertos = AEROLINEAS_AEROPUERTOS.get(aerolinea);
        return aeropuertos != null && aeropuertos.contains(aeropuerto.toUpperCase());
    }

    /**
     * Obtiene el nombre de una aerolínea
     */
    public static String getNombreAerolinea(String codigo) {
        return NOMBRES_AEROLINEAS.getOrDefault(codigo, "Aerolínea Desconocida");
    }

    /**
     * Obtiene todos los aeropuertos de una aerolínea
     */
    public static Set<String> getAeropuertos(String aerolinea) {
        return AEROLINEAS_AEROPUERTOS.getOrDefault(aerolinea, Collections.emptySet());
    }

    /**
     * Obtiene todas las aerolíneas disponibles
     */
    public static Set<String> getAerolineasDisponibles() {
        return AEROLINEAS_AEROPUERTOS.keySet();
    }
}
