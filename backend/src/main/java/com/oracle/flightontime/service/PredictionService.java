package com.oracle.flightontime.service;

import com.oracle.flightontime.config.AirlineConfig;
import com.oracle.flightontime.dto.PredictionRequestDTO;
import com.oracle.flightontime.dto.PredictionResponseDTO;
import com.oracle.flightontime.dto.WeatherDataDTO;
import com.oracle.flightontime.util.GeoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * ============================================================================
 * SERVICIO DE PREDICCIÓN - ORQUESTADOR EMPRESARIAL
 * ============================================================================
 * Este servicio actúa como orquestador entre el frontend y el servicio ML.
 * Implementa dos modos:
 * 1. Modo Mock: Respuesta estática para pruebas rápidas
 * 2. Modo Real: Integración con el servicio ML Python
 * ============================================================================
 */
@Service
public class PredictionService {

    private static final Logger logger = LoggerFactory.getLogger(PredictionService.class);

    private final WebClient webClient;

    @Value("${ml.service.url}")
    private String mlServiceUrl;

    @Value("${ml.service.timeout:10}")
    private int mlServiceTimeout;

    public PredictionService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * ========================================================================
     * VALIDACIÓN DE DATOS DE ENTRADA
     * ========================================================================
     * Valida que la aerolínea, origen y destino existan en la base de datos.
     * Lanza excepción con mensaje específico si algún dato no es válido.
     * ========================================================================
     */
    private void validarDatosEntrada(PredictionRequestDTO request) {
        String aerolinea = request.getAerolinea();
        String origen = request.getOrigen();
        String destino = request.getDestino();

        // Validar que origen y destino no sean iguales
        if (origen != null && origen.equals(destino)) {
            logger.warn("⚠️ Origen y destino son iguales: {}", origen);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El aeropuerto de origen y destino no pueden ser el mismo.");
        }

        // Validar que la aerolínea exista
        if (!AirlineConfig.esAerolineaValida(aerolinea)) {
            logger.warn("⚠️ Aerolínea no válida: {}", aerolinea);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Aerolínea no válida. Use: 1 (Delta Air Lines) o 2 (Southwest Airlines)");
        }

        // Validar aeropuerto de origen
        if (!AirlineConfig.esAeropuertoValido(aerolinea, origen)) {
            logger.warn("⚠️ Aeropuerto de origen {} no disponible para aerolínea {}", 
                       origen, AirlineConfig.getNombreAerolinea(aerolinea));
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    String.format("El aeropuerto de origen %s no está disponible para %s",
                                origen, AirlineConfig.getNombreAerolinea(aerolinea)));
        }

        // Validar aeropuerto de destino
        if (!AirlineConfig.esAeropuertoValido(aerolinea, destino)) {
            logger.warn("⚠️ Aeropuerto de destino {} no disponible para aerolínea {}", 
                       destino, AirlineConfig.getNombreAerolinea(aerolinea));
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    String.format("El aeropuerto de destino %s no está disponible para %s",
                                destino, AirlineConfig.getNombreAerolinea(aerolinea)));
        }

        logger.info("✅ Validación exitosa: {} {} → {}",
                AirlineConfig.getNombreAerolinea(aerolinea), origen, destino);
    }

    /**
     * ========================================================================
     * MODO MOCK - PREDICCIÓN SIMULADA LOCAL
     * ========================================================================
     * Genera una respuesta simulada sin llamar al servicio ML.
     * Utiliza heurísticas básicas para demostración si el servicio real no está disponible.
     * ========================================================================
     */
    public PredictionResponseDTO predictMock(PredictionRequestDTO request) {
        logger.info("🔧 Generando predicción simulada (MOCK LOCAL)");
        logger.info("📋 Request: {} {} → {}", request.getAerolinea(), request.getOrigen(), request.getDestino());

        // Validar datos de entrada
        validarDatosEntrada(request);

        long startTime = System.currentTimeMillis();

        // Calcular distancia real usando GeoUtils
        Double distanciaKm = GeoUtils.calcularDistancia(request.getOrigen(), request.getDestino());
        if (distanciaKm == null) {
            logger.warn("⚠️ No se pudo calcular distancia para {} → {}, usando valor por defecto",
                    request.getOrigen(), request.getDestino());
            distanciaKm = 1000.0;
        }

        // Obtener nombres de aeropuertos
        String origenNombre = GeoUtils.getAirportName(request.getOrigen());
        String destinoNombre = GeoUtils.getAirportName(request.getDestino());

        // Generar clima simulado
        Random random = new Random();
        WeatherDataDTO climaOrigen = generarClimaSimulado(random);
        WeatherDataDTO climaDestino = generarClimaSimulado(random);

        // CÁLCULO DINÁMICO DE PROBABILIDAD (SIMULADO)
        double probabilidadRetraso = 0.15; // Probabilidad base

        // Factor: Distancia (vuelos largos tienen más probabilidad de retraso)
        if (distanciaKm > 5000) {
            probabilidadRetraso += 0.15;
        } else if (distanciaKm > 2000) {
            probabilidadRetraso += 0.05;
        }

        // Variación aleatoria
        probabilidadRetraso += (random.nextDouble() * 0.10 - 0.05); // ±5%

        // Limitar entre 0.0 y 1.0
        probabilidadRetraso = Math.max(0.0, Math.min(1.0, probabilidadRetraso));

        // Determinar predicción binaria: 0 = Puntual, 1 = Retrasado
        Integer prediccion = probabilidadRetraso > 0.5 ? 1 : 0;

        // Calcular confianza
        double confianza = Math.max(probabilidadRetraso, 1.0 - probabilidadRetraso);
        confianza = Math.round(confianza * 10000.0) / 10000.0;
        probabilidadRetraso = Math.round(probabilidadRetraso * 10000.0) / 10000.0;

        // Metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("aerolinea", request.getAerolinea());
        metadata.put("ruta", request.getOrigen() + " → " + request.getDestino());
        metadata.put("origen_nombre", origenNombre != null ? origenNombre : request.getOrigen());
        metadata.put("destino_nombre", destinoNombre != null ? destinoNombre : request.getDestino());
        metadata.put("fecha_partida", request.getFechaPartida());
        metadata.put("timestamp_prediccion", LocalDateTime.now().toString());
        metadata.put("modo", "MOCK_LOCAL");
        metadata.put("mensaje", "Predicción simulada (sin servicio ML)");
        metadata.put("tiempo_respuesta_ms", System.currentTimeMillis() - startTime);

        // Construir respuesta usando Setters (evitar Builder)
        PredictionResponseDTO response = new PredictionResponseDTO();
        response.setPrediccion(prediccion);
        response.setProbabilidadRetraso(probabilidadRetraso);
        response.setConfianza(confianza);
        response.setDistanciaKm(distanciaKm);
        response.setClimaOrigen(climaOrigen);
        response.setClimaDestino(climaDestino);
        response.setMetadata(metadata);
        response.setModoMock(true);

        logger.info("✅ Mock generado: {} (Prob: {}%, Conf: {}%)", prediccion, probabilidadRetraso*100, confianza*100);
        return response;
    }

    private WeatherDataDTO generarClimaSimulado(Random random) {
        double temperatura = 15.0 + random.nextDouble() * 20.0; // 15-35°C
        int humedad = 40 + random.nextInt(50); // 40-90%
        double vientoVelocidad = 2.0 + random.nextDouble() * 15.0; // 2-17 m/s
        int visibilidad = 5000 + random.nextInt(5000); // 5-10 km

        WeatherDataDTO clima = new WeatherDataDTO();
        clima.setTemperatura(Math.round(temperatura * 10.0) / 10.0);
        clima.setHumedad(humedad);
        clima.setPresion(1013);
        clima.setVisibilidad(visibilidad);
        clima.setVientoVelocidad(Math.round(vientoVelocidad * 10.0) / 10.0);
        clima.setCondicion(humedad > 70 ? "Clouds" : "Clear");
        clima.setDescripcion(humedad > 70 ? "nublado" : "cielo claro");
        return clima;
    }

    /**
     * ========================================================================
     * MODO REAL - INTEGRACIÓN CON SERVICIO ML
     * ========================================================================
     * Realiza una llamada HTTP al servicio ML Python para obtener la predicción
     * real basada en el modelo entrenado y datos meteorológicos actuales.
     * ========================================================================
     */
    public PredictionResponseDTO predictReal(PredictionRequestDTO request) {
        logger.info("🚀 Ejecutando predicción en MODO REAL");
        logger.info("📋 Request: {} {} → {}", request.getAerolinea(), request.getOrigen(), request.getDestino());

        // Validar datos de entrada
        validarDatosEntrada(request);

        long startTime = System.currentTimeMillis();

        try {
            // Construir URL del endpoint ML
            String mlEndpoint = mlServiceUrl + "/predict_internal";
            logger.info("🔗 Llamando a ML Service: {}", mlEndpoint);

            // Realizar llamada HTTP POST al servicio ML
            PredictionResponseDTO response = webClient.post()
                    .uri(mlEndpoint)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(PredictionResponseDTO.class)
                    .timeout(Duration.ofSeconds(mlServiceTimeout))
                    .doOnError(error -> logger.error("❌ Error al llamar al servicio ML: {}", error.getMessage()))
                    .onErrorResume(error -> {
                        logger.warn("⚠️ Fallback a modo mock debido a error: {}", error.getMessage());
                        return Mono.just(predictMock(request));
                    })
                    .block();

            long duration = System.currentTimeMillis() - startTime;

            if (response != null) {
                response.setModoMock(false);
                
                // Agregar tiempo de respuesta a metadata
                if (response.getMetadata() == null) {
                    response.setMetadata(new HashMap<>());
                }
                response.getMetadata().put("tiempo_respuesta_ms", duration);

                logger.info("✅ Predicción Real: {} (Probabilidad retraso: {}%) - Tiempo: {}ms",
                        response.getPrediccion(),
                        response.getProbabilidadRetraso() * 100,
                        duration);
            }

            return response;

        } catch (Exception e) {
            logger.error("❌ Error inesperado en predicción real: {}", e.getMessage(), e);
            logger.warn("⚠️ Fallback a modo mock");
            return predictMock(request);
        }
    }

    /**
     * ========================================================================
     * MODO HÍBRIDO - SELECCIÓN AUTOMÁTICA
     * ========================================================================
     * Intenta usar el modo real, pero hace fallback a mock si hay problemas.
     * Este es el método recomendado para producción.
     * ========================================================================
     */
    public PredictionResponseDTO predict(PredictionRequestDTO request, boolean useMock) {
        if (useMock) {
            return predictMock(request);
        } else {
            return predictReal(request);
        }
    }
}
