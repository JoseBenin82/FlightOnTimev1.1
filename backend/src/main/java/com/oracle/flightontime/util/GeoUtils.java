package com.oracle.flightontime.util;

import java.util.HashMap;
import java.util.Map;

/**
 * ============================================================================
 * UTILIDADES GEOGRÁFICAS - CÁLCULO DE DISTANCIAS
 * ============================================================================
 * Proporciona funciones para calcular distancias entre aeropuertos usando
 * la fórmula de Haversine.
 * 
 * ACTUALIZADO: Incluye todos los aeropuertos de Delta Air Lines y Southwest
 * Airlines para el correcto cálculo de distancias.
 * ============================================================================
 */
public class GeoUtils {

    /**
     * Coordenadas de aeropuertos principales (IATA -> {lat, lon, name})
     * Generado automáticamente desde la base de datos airportsdata.
     */
    private static final Map<String, AirportCoordinates> AIRPORT_COORDINATES = new HashMap<>();

    static {
        // ====================================================================
        // AEROPUERTOS DE ESTADOS UNIDOS - DELTA & SOUTHWEST
        // ====================================================================
        // Generado automáticamente desde python airportsdata
        AIRPORT_COORDINATES.put("ABQ", new AirportCoordinates(35.040222, -106.609194, "Albuquerque International Sunport"));
        AIRPORT_COORDINATES.put("AGS", new AirportCoordinates(33.369944, -81.9645, "Augusta Regional at Bush Field"));
        AIRPORT_COORDINATES.put("ALB", new AirportCoordinates(42.748267, -73.801692, "Albany International Airport"));
        AIRPORT_COORDINATES.put("AMA", new AirportCoordinates(35.219369, -101.705931, "Rick Husband Amarillo International Airport"));
        AIRPORT_COORDINATES.put("ANC", new AirportCoordinates(61.174361, -149.996361, "Ted Stevens Anchorage International Airport"));
        AIRPORT_COORDINATES.put("ATL", new AirportCoordinates(33.636719, -84.428067, "Hartsfield-Jackson Atlanta International Airport"));
        AIRPORT_COORDINATES.put("ATW", new AirportCoordinates(44.257408, -88.507576, "Appleton International Airport"));
        AIRPORT_COORDINATES.put("AUS", new AirportCoordinates(30.197535, -97.662015, "Austin-Bergstrom International Airport"));
        AIRPORT_COORDINATES.put("AVL", new AirportCoordinates(35.436194, -82.541806, "Asheville Regional Airport"));
        AIRPORT_COORDINATES.put("BDL", new AirportCoordinates(41.938889, -72.683222, "Bradley International Airport"));
        AIRPORT_COORDINATES.put("BGR", new AirportCoordinates(44.807444, -68.828139, "Bangor International Airport"));
        AIRPORT_COORDINATES.put("BHM", new AirportCoordinates(33.562942, -86.75355, "Birmingham-Shuttlesworth International Airport"));
        AIRPORT_COORDINATES.put("BIL", new AirportCoordinates(45.80921, -108.537654, "Billings Logan International Airport"));
        AIRPORT_COORDINATES.put("BIS", new AirportCoordinates(46.772842, -100.757931, "Bismarck Municipal Airport"));
        AIRPORT_COORDINATES.put("BNA", new AirportCoordinates(36.126083, -86.681722, "Nashville International Airport"));
        AIRPORT_COORDINATES.put("BOI", new AirportCoordinates(43.564361, -116.222861, "Boise Air Terminal/Gowen Field"));
        AIRPORT_COORDINATES.put("BOS", new AirportCoordinates(42.364347, -71.005181, "Boston Logan International Airport"));
        AIRPORT_COORDINATES.put("BTR", new AirportCoordinates(30.533167, -91.149639, "Baton Rouge Metropolitan Airport"));
        AIRPORT_COORDINATES.put("BTV", new AirportCoordinates(44.471861, -73.153278, "Burlington International Airport"));
        AIRPORT_COORDINATES.put("BUF", new AirportCoordinates(42.940525, -78.732167, "Buffalo Niagara International Airport"));
        AIRPORT_COORDINATES.put("BUR", new AirportCoordinates(34.200667, -118.358667, "Hollywood Burbank Airport"));
        AIRPORT_COORDINATES.put("BWI", new AirportCoordinates(39.175361, -76.668333, "Baltimore/Washington International Airport"));
        AIRPORT_COORDINATES.put("BZN", new AirportCoordinates(45.777643, -111.160151, "Bozeman Yellowstone International Airport"));
        AIRPORT_COORDINATES.put("CAE", new AirportCoordinates(33.938833, -81.119528, "Columbia Metropolitan Airport"));
        AIRPORT_COORDINATES.put("CHA", new AirportCoordinates(35.035278, -85.203808, "Lovell Field"));
        AIRPORT_COORDINATES.put("CHO", new AirportCoordinates(38.138639, -78.452861, "Charlottesville Albemarle Airport"));
        AIRPORT_COORDINATES.put("CHS", new AirportCoordinates(32.898647, -80.040528, "Charleston International Airport"));
        AIRPORT_COORDINATES.put("CID", new AirportCoordinates(41.884694, -91.710806, "The Eastern Iowa Airport"));
        AIRPORT_COORDINATES.put("CLE", new AirportCoordinates(41.411689, -81.849794, "Cleveland Hopkins International Airport"));
        AIRPORT_COORDINATES.put("CLT", new AirportCoordinates(35.214, -80.943139, "Charlotte Douglas International Airport"));
        AIRPORT_COORDINATES.put("CMH", new AirportCoordinates(39.998056, -82.891889, "John Glenn Columbus International Airport"));
        AIRPORT_COORDINATES.put("COS", new AirportCoordinates(38.805805, -104.700778, "City of Colorado Springs Municipal Airport"));
        AIRPORT_COORDINATES.put("CRP", new AirportCoordinates(27.770361, -97.501222, "Corpus Christi International Airport"));
        AIRPORT_COORDINATES.put("CVG", new AirportCoordinates(39.048836, -84.667822, "Cincinnati/Northern Kentucky International Airport"));
        AIRPORT_COORDINATES.put("DAB", new AirportCoordinates(29.179917, -81.058056, "Daytona Beach International Airport"));
        AIRPORT_COORDINATES.put("DAL", new AirportCoordinates(32.847111, -96.851778, "Dallas Love Field"));
        AIRPORT_COORDINATES.put("DAY", new AirportCoordinates(39.902375, -84.219375, "James M Cox Dayton International Airport"));
        AIRPORT_COORDINATES.put("DCA", new AirportCoordinates(38.852083, -77.037722, "Ronald Reagan Washington National Airport"));
        AIRPORT_COORDINATES.put("DEN", new AirportCoordinates(39.861656, -104.673178, "Denver International Airport"));
        AIRPORT_COORDINATES.put("DFW", new AirportCoordinates(32.896828, -97.037997, "Dallas/Fort Worth International Airport"));
        AIRPORT_COORDINATES.put("DLH", new AirportCoordinates(46.842091, -92.193649, "Duluth International Airport"));
        AIRPORT_COORDINATES.put("DSM", new AirportCoordinates(41.533972, -93.663083, "Des Moines International Airport"));
        AIRPORT_COORDINATES.put("DTW", new AirportCoordinates(42.212444, -83.353389, "Detroit Metropolitan Wayne County Airport"));
        AIRPORT_COORDINATES.put("ECP", new AirportCoordinates(30.357106, -85.795414, "Northwest Florida Beaches International Airport"));
        AIRPORT_COORDINATES.put("EGE", new AirportCoordinates(39.642556, -106.917694, "Eagle County Regional Airport"));
        AIRPORT_COORDINATES.put("ELP", new AirportCoordinates(31.80725, -106.377583, "El Paso International Airport"));
        AIRPORT_COORDINATES.put("EUG", new AirportCoordinates(44.124583, -123.211972, "Eugene Airport"));
        AIRPORT_COORDINATES.put("EWR", new AirportCoordinates(40.6925, -74.168667, "Newark Liberty International Airport"));
        AIRPORT_COORDINATES.put("EYW", new AirportCoordinates(24.556111, -81.759556, "Key West International Airport"));
        AIRPORT_COORDINATES.put("FAI", new AirportCoordinates(64.815114, -147.856267, "Fairbanks International Airport"));
        AIRPORT_COORDINATES.put("FAR", new AirportCoordinates(46.918944, -96.815972, "Hector International Airport"));
        AIRPORT_COORDINATES.put("FAT", new AirportCoordinates(36.776194, -119.718389, "Fresno Yosemite International Airport"));
        AIRPORT_COORDINATES.put("FAY", new AirportCoordinates(34.991222, -78.880028, "Fayetteville Regional Airport"));
        AIRPORT_COORDINATES.put("FCA", new AirportCoordinates(48.310472, -114.256, "Glacier Park International Airport"));
        AIRPORT_COORDINATES.put("FLL", new AirportCoordinates(26.072583, -80.152750, "Fort Lauderdale-Hollywood International Airport"));
        AIRPORT_COORDINATES.put("FSD", new AirportCoordinates(43.582014, -96.741914, "Joe Foss Field"));
        AIRPORT_COORDINATES.put("GEG", new AirportCoordinates(47.619861, -117.533833, "Spokane International Airport"));
        AIRPORT_COORDINATES.put("GNV", new AirportCoordinates(29.690056, -82.271778, "Gainesville Regional Airport"));
        AIRPORT_COORDINATES.put("GPT", new AirportCoordinates(30.407278, -89.070111, "Gulfport-Biloxi International Airport"));
        AIRPORT_COORDINATES.put("GRB", new AirportCoordinates(44.485072, -88.129589, "Green Bay Austin Straubel International Airport"));
        AIRPORT_COORDINATES.put("GRR", new AirportCoordinates(42.880833, -85.522806, "Gerald R. Ford International Airport"));
        AIRPORT_COORDINATES.put("GSO", new AirportCoordinates(36.09775, -79.937306, "Piedmont Triad International Airport"));
        AIRPORT_COORDINATES.put("GSP", new AirportCoordinates(34.895556, -82.218889, "Greenville-Spartanburg International Airport"));
        AIRPORT_COORDINATES.put("HDN", new AirportCoordinates(40.481181, -107.21766, "Yampa Valley Airport"));
        AIRPORT_COORDINATES.put("HNL", new AirportCoordinates(21.318681, -157.922428, "Daniel K. Inouye International Airport"));
        AIRPORT_COORDINATES.put("HOU", new AirportCoordinates(29.645419, -95.278889, "William P. Hobby Airport"));
        AIRPORT_COORDINATES.put("HPN", new AirportCoordinates(41.066959, -73.707575, "Westchester County Airport"));
        AIRPORT_COORDINATES.put("HRL", new AirportCoordinates(26.228500, -97.654389, "Valley International Airport"));
        AIRPORT_COORDINATES.put("HSV", new AirportCoordinates(34.637194, -86.775056, "Huntsville International Airport"));
        AIRPORT_COORDINATES.put("IAD", new AirportCoordinates(38.944533, -77.455811, "Washington Dulles International Airport"));
        AIRPORT_COORDINATES.put("IAH", new AirportCoordinates(29.984433, -95.341442, "George Bush Intercontinental Airport"));
        AIRPORT_COORDINATES.put("ICT", new AirportCoordinates(37.649944, -97.433056, "Wichita Dwight D. Eisenhower National Airport"));
        AIRPORT_COORDINATES.put("IDA", new AirportCoordinates(43.514556, -112.07075, "Idaho Falls Regional Airport"));
        AIRPORT_COORDINATES.put("ILM", new AirportCoordinates(34.270615, -77.902569, "Wilmington International Airport"));
        AIRPORT_COORDINATES.put("IND", new AirportCoordinates(39.717331, -86.294383, "Indianapolis International Airport"));
        AIRPORT_COORDINATES.put("ISP", new AirportCoordinates(40.79525, -73.100222, "Long Island MacArthur Airport"));
        AIRPORT_COORDINATES.put("ITO", new AirportCoordinates(19.721375, -155.048469, "Hilo International Airport"));
        AIRPORT_COORDINATES.put("JAC", new AirportCoordinates(43.607333, -110.737722, "Jackson Hole Airport"));
        AIRPORT_COORDINATES.put("JAN", new AirportCoordinates(32.311167, -90.075889, "Jackson-Medgar Wiley Evers International Airport"));
        AIRPORT_COORDINATES.put("JAX", new AirportCoordinates(30.494056, -81.687861, "Jacksonville International Airport"));
        AIRPORT_COORDINATES.put("JFK", new AirportCoordinates(40.639751, -73.778925, "John F. Kennedy International Airport"));
        AIRPORT_COORDINATES.put("JNU", new AirportCoordinates(58.354972, -134.576278, "Juneau International Airport"));
        AIRPORT_COORDINATES.put("KOA", new AirportCoordinates(19.738767, -156.045631, "Ellison Onizuka Kona International Airport"));
        AIRPORT_COORDINATES.put("LAS", new AirportCoordinates(36.080056, -115.15225, "Harry Reid International Airport"));
        AIRPORT_COORDINATES.put("LAX", new AirportCoordinates(33.942536, -118.408075, "Los Angeles International Airport"));
        AIRPORT_COORDINATES.put("LBB", new AirportCoordinates(33.663639, -101.822778, "Lubbock Preston Smith International Airport"));
        AIRPORT_COORDINATES.put("LEX", new AirportCoordinates(38.0365, -84.605889, "Blue Grass Airport"));
        AIRPORT_COORDINATES.put("LGA", new AirportCoordinates(40.777245, -73.872608, "LaGuardia Airport"));
        AIRPORT_COORDINATES.put("LGB", new AirportCoordinates(33.817722, -118.151611, "Long Beach Airport"));
        AIRPORT_COORDINATES.put("LIH", new AirportCoordinates(21.975983, -159.338958, "Lihue Airport"));
        AIRPORT_COORDINATES.put("LIT", new AirportCoordinates(34.729444, -92.224306, "Bill and Hillary Clinton National Airport"));
        AIRPORT_COORDINATES.put("MAF", new AirportCoordinates(31.942528, -102.201914, "Midland International Air and Space Port"));
        AIRPORT_COORDINATES.put("MCI", new AirportCoordinates(39.297606, -94.713905, "Kansas City International Airport"));
        AIRPORT_COORDINATES.put("MCO", new AirportCoordinates(28.429394, -81.308994, "Orlando International Airport"));
        AIRPORT_COORDINATES.put("MDT", new AirportCoordinates(40.193494, -76.763403, "Harrisburg International Airport"));
        AIRPORT_COORDINATES.put("MDW", new AirportCoordinates(41.785972, -87.752417, "Chicago Midway International Airport"));
        AIRPORT_COORDINATES.put("MEM", new AirportCoordinates(35.042417, -89.976667, "Memphis International Airport"));
        AIRPORT_COORDINATES.put("MHT", new AirportCoordinates(42.932556, -71.435667, "Manchester-Boston Regional Airport"));
        AIRPORT_COORDINATES.put("MIA", new AirportCoordinates(25.79325, -80.290556, "Miami International Airport"));
        AIRPORT_COORDINATES.put("MKE", new AirportCoordinates(42.947222, -87.896583, "General Mitchell International Airport"));
        AIRPORT_COORDINATES.put("MLB", new AirportCoordinates(28.102753, -80.645258, "Melbourne Orlando International Airport"));
        AIRPORT_COORDINATES.put("MOB", new AirportCoordinates(30.691231, -88.242814, "Mobile Regional Airport"));
        AIRPORT_COORDINATES.put("MSN", new AirportCoordinates(43.139858, -89.337514, "Dane County Regional Airport"));
        AIRPORT_COORDINATES.put("MSO", new AirportCoordinates(46.916306, -114.090556, "Missoula Montana Airport"));
        AIRPORT_COORDINATES.put("MSP", new AirportCoordinates(44.881956, -93.221767, "Minneapolis-Saint Paul International Airport"));
        AIRPORT_COORDINATES.put("MSY", new AirportCoordinates(29.993389, -90.258028, "Louis Armstrong New Orleans International Airport"));
        AIRPORT_COORDINATES.put("MTJ", new AirportCoordinates(38.509794, -107.894242, "Montrose Regional Airport"));
        AIRPORT_COORDINATES.put("MYR", new AirportCoordinates(33.679694, -78.928333, "Myrtle Beach International Airport"));
        AIRPORT_COORDINATES.put("OAK", new AirportCoordinates(37.721278, -122.220722, "Oakland International Airport"));
        AIRPORT_COORDINATES.put("OGG", new AirportCoordinates(20.89865, -156.430458, "Kahului Airport"));
        AIRPORT_COORDINATES.put("OKC", new AirportCoordinates(35.393089, -97.600733, "Will Rogers World Airport"));
        AIRPORT_COORDINATES.put("OMA", new AirportCoordinates(41.303167, -95.894069, "Eppley Airfield"));
        AIRPORT_COORDINATES.put("ONT", new AirportCoordinates(34.056, -117.601194, "Ontario International Airport"));
        AIRPORT_COORDINATES.put("ORD", new AirportCoordinates(41.978603, -87.904842, "O'Hare International Airport"));
        AIRPORT_COORDINATES.put("ORF", new AirportCoordinates(36.894611, -76.201222, "Norfolk International Airport"));
        AIRPORT_COORDINATES.put("PBI", new AirportCoordinates(26.683161, -80.095589, "Palm Beach International Airport"));
        AIRPORT_COORDINATES.put("PDX", new AirportCoordinates(45.588722, -122.5975, "Portland International Airport"));
        AIRPORT_COORDINATES.put("PHL", new AirportCoordinates(39.871944, -75.241139, "Philadelphia International Airport"));
        AIRPORT_COORDINATES.put("PHX", new AirportCoordinates(33.437269, -112.007788, "Phoenix Sky Harbor International Airport"));
        AIRPORT_COORDINATES.put("PIT", new AirportCoordinates(40.491467, -80.232872, "Pittsburgh International Airport"));
        AIRPORT_COORDINATES.put("PNS", new AirportCoordinates(30.473425, -87.186611, "Pensacola International Airport"));
        AIRPORT_COORDINATES.put("PSC", new AirportCoordinates(46.264722, -119.119056, "Tri-Cities Airport"));
        AIRPORT_COORDINATES.put("PSP", new AirportCoordinates(33.829667, -116.506694, "Palm Springs International Airport"));
        AIRPORT_COORDINATES.put("PVD", new AirportCoordinates(41.732581, -71.420383, "Rhode Island T. F. Green International Airport"));
        AIRPORT_COORDINATES.put("PWM", new AirportCoordinates(43.646161, -70.309281, "Portland International Jetport"));
        AIRPORT_COORDINATES.put("RAP", new AirportCoordinates(44.045278, -103.057222, "Rapid City Regional Airport"));
        AIRPORT_COORDINATES.put("RDU", new AirportCoordinates(35.877639, -78.787472, "Raleigh-Durham International Airport"));
        AIRPORT_COORDINATES.put("RIC", new AirportCoordinates(37.505167, -77.319667, "Richmond International Airport"));
        AIRPORT_COORDINATES.put("RNO", new AirportCoordinates(39.499108, -119.768108, "Reno-Tahoe International Airport"));
        AIRPORT_COORDINATES.put("ROA", new AirportCoordinates(37.325472, -79.975417, "Roanoke-Blacksburg Regional Airport"));
        AIRPORT_COORDINATES.put("ROC", new AirportCoordinates(43.118866, -77.672389, "Greater Rochester International Airport"));
        AIRPORT_COORDINATES.put("RSW", new AirportCoordinates(26.536167, -81.755167, "Southwest Florida International Airport"));
        AIRPORT_COORDINATES.put("SAN", new AirportCoordinates(32.733556, -117.189667, "San Diego International Airport"));
        AIRPORT_COORDINATES.put("SAT", new AirportCoordinates(29.533694, -98.469778, "San Antonio International Airport"));
        AIRPORT_COORDINATES.put("SAV", new AirportCoordinates(32.127583, -81.202139, "Savannah/Hilton Head International Airport"));
        AIRPORT_COORDINATES.put("SBA", new AirportCoordinates(34.426211, -119.840372, "Santa Barbara Municipal Airport"));
        AIRPORT_COORDINATES.put("SBN", new AirportCoordinates(41.708661, -86.31725, "South Bend International Airport"));
        AIRPORT_COORDINATES.put("SDF", new AirportCoordinates(38.174111, -85.736, "Louisville Muhammad Ali International Airport"));
        AIRPORT_COORDINATES.put("SEA", new AirportCoordinates(47.449, -122.309306, "Seattle-Tacoma International Airport"));
        AIRPORT_COORDINATES.put("SFO", new AirportCoordinates(37.618972, -122.374889, "San Francisco International Airport"));
        AIRPORT_COORDINATES.put("SGF", new AirportCoordinates(37.245667, -93.388639, "Springfield-Branson National Airport"));
        AIRPORT_COORDINATES.put("SHV", new AirportCoordinates(32.446629, -93.8256, "Shreveport Regional Airport"));
        AIRPORT_COORDINATES.put("SJC", new AirportCoordinates(37.362228, -121.929006, "Norman Y. Mineta San Jose International Airport"));
        AIRPORT_COORDINATES.put("SJU", new AirportCoordinates(18.439417, -66.001833, "Luis Muñoz Marín International Airport"));
        AIRPORT_COORDINATES.put("SLC", new AirportCoordinates(40.788389, -111.977772, "Salt Lake City International Airport"));
        AIRPORT_COORDINATES.put("SMF", new AirportCoordinates(38.695417, -121.590778, "Sacramento International Airport"));
        AIRPORT_COORDINATES.put("SNA", new AirportCoordinates(33.675667, -117.868222, "John Wayne Airport"));
        AIRPORT_COORDINATES.put("SRQ", new AirportCoordinates(27.395444, -82.554389, "Sarasota Bradenton International Airport"));
        AIRPORT_COORDINATES.put("STL", new AirportCoordinates(38.748697, -90.370028, "St. Louis Lambert International Airport"));
        AIRPORT_COORDINATES.put("STT", new AirportCoordinates(18.337306, -64.973361, "Cyril E. King Airport"));
        AIRPORT_COORDINATES.put("STX", new AirportCoordinates(17.701889, -64.798556, "Henry E. Rohlsen Airport"));
        AIRPORT_COORDINATES.put("SYR", new AirportCoordinates(43.111187, -76.106311, "Syracuse Hancock International Airport"));
        AIRPORT_COORDINATES.put("TLH", new AirportCoordinates(30.396528, -84.350333, "Tallahassee International Airport"));
        AIRPORT_COORDINATES.put("TPA", new AirportCoordinates(27.975472, -82.53325, "Tampa International Airport"));
        AIRPORT_COORDINATES.put("TRI", new AirportCoordinates(36.475222, -82.407417, "Tri-Cities Regional Airport"));
        AIRPORT_COORDINATES.put("TUL", new AirportCoordinates(36.198389, -95.888111, "Tulsa International Airport"));
        AIRPORT_COORDINATES.put("TUS", new AirportCoordinates(32.116083, -110.941028, "Tucson International Airport"));
        AIRPORT_COORDINATES.put("TVC", new AirportCoordinates(44.741445, -85.582235, "Cherry Capital Airport"));
        AIRPORT_COORDINATES.put("TYS", new AirportCoordinates(35.810972, -83.994028, "McGhee Tyson Airport"));
        AIRPORT_COORDINATES.put("VPS", new AirportCoordinates(30.48325, -86.525417, "Destin-Fort Walton Beach Airport"));
        AIRPORT_COORDINATES.put("XNA", new AirportCoordinates(36.281869, -94.306811, "Northwest Arkansas National Airport"));
        
        // ====================================================================
        // AEROPUERTOS INTERNACIONALES ADICIONALES (para rutas internacionales)
        // ====================================================================
        // México
        AIRPORT_COORDINATES.put("MEX", new AirportCoordinates(19.4363, -99.0721, "Mexico City International Airport"));
        AIRPORT_COORDINATES.put("CUN", new AirportCoordinates(21.0365, -86.8770, "Cancún International Airport"));
        AIRPORT_COORDINATES.put("GDL", new AirportCoordinates(20.5218, -103.3106, "Guadalajara International Airport"));
        AIRPORT_COORDINATES.put("MTY", new AirportCoordinates(25.7785, -100.1076, "Monterrey International Airport"));
        
        // Europa
        AIRPORT_COORDINATES.put("LHR", new AirportCoordinates(51.4700, -0.4543, "London Heathrow Airport"));
        AIRPORT_COORDINATES.put("CDG", new AirportCoordinates(49.0097, 2.5479, "Paris Charles de Gaulle Airport"));
        AIRPORT_COORDINATES.put("FRA", new AirportCoordinates(50.0379, 8.5622, "Frankfurt Airport"));
        AIRPORT_COORDINATES.put("MAD", new AirportCoordinates(40.4983, -3.5676, "Madrid Barajas Airport"));
        AIRPORT_COORDINATES.put("BCN", new AirportCoordinates(41.2974, 2.0833, "Barcelona El Prat Airport"));
        AIRPORT_COORDINATES.put("AMS", new AirportCoordinates(52.3086, 4.7639, "Amsterdam Schiphol Airport"));
        AIRPORT_COORDINATES.put("FCO", new AirportCoordinates(41.8003, 12.2389, "Rome Fiumicino Airport"));
        AIRPORT_COORDINATES.put("MUC", new AirportCoordinates(48.3538, 11.7861, "Munich Airport"));
        AIRPORT_COORDINATES.put("ZRH", new AirportCoordinates(47.4647, 8.5492, "Zurich Airport"));
        
        // Canadá
        AIRPORT_COORDINATES.put("YYZ", new AirportCoordinates(43.6772, -79.6306, "Toronto Pearson International Airport"));
        AIRPORT_COORDINATES.put("YVR", new AirportCoordinates(49.1947, -123.1839, "Vancouver International Airport"));
        AIRPORT_COORDINATES.put("YUL", new AirportCoordinates(45.4706, -73.7408, "Montreal-Trudeau International Airport"));
        
        // Sudamérica
        AIRPORT_COORDINATES.put("GRU", new AirportCoordinates(-23.4356, -46.4731, "São Paulo-Guarulhos International Airport"));
        AIRPORT_COORDINATES.put("GIG", new AirportCoordinates(-22.8099, -43.2505, "Rio de Janeiro-Galeão International Airport"));
        AIRPORT_COORDINATES.put("EZE", new AirportCoordinates(-34.8222, -58.5358, "Buenos Aires Ezeiza Airport"));
        AIRPORT_COORDINATES.put("SCL", new AirportCoordinates(-33.3928, -70.7858, "Santiago International Airport"));
        AIRPORT_COORDINATES.put("BOG", new AirportCoordinates(4.7016, -74.1469, "Bogotá El Dorado International Airport"));
        AIRPORT_COORDINATES.put("LIM", new AirportCoordinates(-12.0219, -77.1143, "Lima Jorge Chávez International Airport"));
        
        // Asia
        AIRPORT_COORDINATES.put("NRT", new AirportCoordinates(35.7647, 140.3864, "Tokyo Narita International Airport"));
        AIRPORT_COORDINATES.put("HND", new AirportCoordinates(35.5522, 139.7797, "Tokyo Haneda Airport"));
        AIRPORT_COORDINATES.put("ICN", new AirportCoordinates(37.4602, 126.4407, "Seoul Incheon International Airport"));
        AIRPORT_COORDINATES.put("PEK", new AirportCoordinates(40.0799, 116.6031, "Beijing Capital International Airport"));
        AIRPORT_COORDINATES.put("PVG", new AirportCoordinates(31.1434, 121.8052, "Shanghai Pudong International Airport"));
        AIRPORT_COORDINATES.put("HKG", new AirportCoordinates(22.3080, 113.9185, "Hong Kong International Airport"));
        AIRPORT_COORDINATES.put("SIN", new AirportCoordinates(1.3644, 103.9915, "Singapore Changi Airport"));
        AIRPORT_COORDINATES.put("BKK", new AirportCoordinates(13.6900, 100.7501, "Bangkok Suvarnabhumi Airport"));
    }

    /**
     * Calcula la distancia entre dos aeropuertos usando la fórmula de Haversine.
     *
     * @param origenIATA  Código IATA del aeropuerto de origen
     * @param destinoIATA Código IATA del aeropuerto de destino
     * @return Distancia en kilómetros, o null si algún aeropuerto no se encuentra
     */
    public static Double calcularDistancia(String origenIATA, String destinoIATA) {
        if (origenIATA == null || destinoIATA == null) {
            return null;
        }
        
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
        if (iataCode == null) {
            return null;
        }
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
        if (iataCode == null) {
            return false;
        }
        return AIRPORT_COORDINATES.containsKey(iataCode.toUpperCase());
    }

    /**
     * Obtiene las coordenadas de un aeropuerto.
     *
     * @param iataCode Código IATA del aeropuerto
     * @return Array con [latitud, longitud] o null si no existe
     */
    public static double[] getCoordinates(String iataCode) {
        if (iataCode == null) {
            return null;
        }
        AirportCoordinates coords = AIRPORT_COORDINATES.get(iataCode.toUpperCase());
        if (coords == null) {
            return null;
        }
        return new double[] { coords.getLatitude(), coords.getLongitude() };
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
