package com.oracle.flightontime.service;

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

    /**
     * Lista de aerolíneas válidas en el sistema
     */
    private static final Set<String> AEROLINEAS_VALIDAS = new HashSet<>(Arrays.asList(
            "LATAM", "GOL", "AZUL", "AVIANCA", "COPA",
            "AMERICAN", "UNITED", "DELTA"));

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
        // Validar aerolínea
        if (!AEROLINEAS_VALIDAS.contains(request.getAerolinea().toUpperCase())) {
            logger.warn("⚠️ Aerolínea no válida: {}", request.getAerolinea());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se hallan esos datos en la base de datos.");
        }

        // Validar aeropuerto de origen
        if (!GeoUtils.existeAeropuerto(request.getOrigen())) {
            logger.warn("⚠️ Aeropuerto de origen no válido: {}", request.getOrigen());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se hallan esos datos en la base de datos.");
        }

        // Validar aeropuerto de destino
        if (!GeoUtils.existeAeropuerto(request.getDestino())) {
            logger.warn("⚠️ Aeropuerto de destino no válido: {}", request.getDestino());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se hallan esos datos en la base de datos.");
        }

        logger.info("✅ Validación exitosa: {} {} → {}",
                request.getAerolinea(), request.getOrigen(), request.getDestino());
    }

    /**
     * ========================================================================
     * MODO MOCK - PREDICCIÓN DINÁMICA CON MODELO ML
     * ========================================================================
     * Intenta usar el modelo ML real con timeout corto.
     * Si falla, usa valores por defecto como fallback.
     * Útil para:
     * - Pruebas rápidas con predicciones reales
     * - Demos con datos dinámicos
     * - Fallback automático si ML Service no está disponible
     * ========================================================================
     */
    public PredictionResponseDTO predictMock(PredictionRequestDTO request) {
        logger.info("🔧 Ejecutando predicción en MODO MOCK (con modelo ML)");
        logger.info("📋 Request: {} {} → {}", request.getAerolinea(), request.getOrigen(), request.getDestino());

        // Validar datos de entrada
        validarDatosEntrada(request);

        try {
            // Intentar obtener predicción real del ML Service
            String mlEndpoint = mlServiceUrl + "/predict_internal";
            logger.info("🔗 Intentando llamar a ML Service: {}", mlEndpoint);
            logger.info("⏱️ Timeout configurado: 10 segundos");

            PredictionResponseDTO mlResponse = webClient.post()
                    .uri(mlEndpoint)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(PredictionResponseDTO.class)
                    .timeout(Duration.ofSeconds(10)) // Timeout aumentado a 10 segundos
                    .doOnError(error -> logger.error("❌ Error detallado: {}", error.getMessage()))
                    .block();

            if (mlResponse != null) {
                // Marcar como modo mock aunque use predicción real
                mlResponse.setModoMock(true);

                // Actualizar metadata para indicar que es modo mock con ML
                if (mlResponse.getMetadata() != null) {
                    mlResponse.getMetadata().put("modo", "MOCK_CON_ML");
                }

                logger.info("✅ Predicción Mock con ML EXITOSA: {} (Probabilidad retraso: {}%, Confianza: {}%)",
                        mlResponse.getPrediccion(),
                        mlResponse.getProbabilidadRetraso() * 100,
                        mlResponse.getConfianza() * 100);
                logger.info("📊 Distancia: {} km, Clima: {}°C",
                        mlResponse.getDistanciaKm(),
                        mlResponse.getClimaOrigen() != null ? mlResponse.getClimaOrigen().getTemperatura() : "N/A");

                return mlResponse;
            } else {
                logger.warn("⚠️ ML Service retornó respuesta nula");
            }

        } catch (Exception e) {
            logger.error("❌ ML Service no disponible en modo mock: {}", e.getMessage());
            logger.error("🔍 Tipo de error: {}", e.getClass().getSimpleName());
            if (e.getCause() != null) {
                logger.error("🔍 Causa raíz: {}", e.getCause().getMessage());
            }
        }

        // FALLBACK: Si el ML Service falla, generar predicción dinámica basada en
        // heurísticas
        logger.info("📊 Generando respuesta mock con predicción dinámica (fallback)");

        // Calcular distancia real usando GeoUtils
        Double distanciaKm = GeoUtils.calcularDistancia(request.getOrigen(), request.getDestino());
        if (distanciaKm == null) {
            logger.warn("⚠️ No se pudo calcular distancia para {} → {}, usando valor por defecto",
                    request.getOrigen(), request.getDestino());
            distanciaKm = 1000.0;
        }
        logger.info("📏 Distancia calculada (Mock Fallback): {} km", distanciaKm);

        // Obtener nombres de aeropuertos
        String origenNombre = GeoUtils.getAirportName(request.getOrigen());
        String destinoNombre = GeoUtils.getAirportName(request.getDestino());

        // Generar clima simulado con variación
        Random random = new Random();
        double temperatura = 15.0 + random.nextDouble() * 20.0; // 15-35°C
        int humedad = 40 + random.nextInt(50); // 40-90%
        double vientoVelocidad = 2.0 + random.nextDouble() * 15.0; // 2-17 m/s
        int visibilidad = 5000 + random.nextInt(5000); // 5-10 km

        WeatherDataDTO climaSimulado = WeatherDataDTO.builder()
                .temperatura(Math.round(temperatura * 10.0) / 10.0)
                .humedad(humedad)
                .presion(1013)
                .visibilidad(visibilidad)
                .vientoVelocidad(Math.round(vientoVelocidad * 10.0) / 10.0)
                .condicion(humedad > 70 ? "Clouds" : "Clear")
                .descripcion(humedad > 70 ? "nublado" : "cielo claro")
                .build();

        // CÁLCULO DINÁMICO DE PROBABILIDAD basado en heurísticas
        double probabilidadRetraso = 0.0;

        // Factor 1: Distancia (vuelos largos tienen más probabilidad de retraso)
        if (distanciaKm > 5000) {
            probabilidadRetraso += 0.20;
        } else if (distanciaKm > 2000) {
            probabilidadRetraso += 0.10;
        } else {
            probabilidadRetraso += 0.05;
        }

        // Factor 2: Clima (mal clima aumenta probabilidad)
        if (visibilidad < 7000) {
            probabilidadRetraso += 0.15;
        }
        if (vientoVelocidad > 12.0) {
            probabilidadRetraso += 0.15;
        }
        if (temperatura < 5.0 || temperatura > 35.0) {
            probabilidadRetraso += 0.10;
        }
        if (humedad > 80) {
            probabilidadRetraso += 0.10;
        }

        // Factor 3: Agregar variación aleatoria pequeña para simular otros factores
        probabilidadRetraso += (random.nextDouble() * 0.15 - 0.075); // ±7.5%

        // Limitar entre 0.0 y 1.0
        probabilidadRetraso = Math.max(0.0, Math.min(1.0, probabilidadRetraso));

        // Determinar predicción binaria
        String prediccion = probabilidadRetraso > 0.5 ? "Retrasado" : "Puntual";

        // Calcular confianza (máxima probabilidad entre las dos clases)
        double confianza = Math.max(probabilidadRetraso, 1.0 - probabilidadRetraso);

        // Redondear a 4 decimales
        probabilidadRetraso = Math.round(probabilidadRetraso * 10000.0) / 10000.0;
        confianza = Math.round(confianza * 10000.0) / 10000.0;

        // Metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("aerolinea", request.getAerolinea());
        metadata.put("ruta", request.getOrigen() + " → " + request.getDestino());
        metadata.put("origen_nombre", origenNombre != null ? origenNombre : "Aeropuerto " + request.getOrigen());
        metadata.put("destino_nombre", destinoNombre != null ? destinoNombre : "Aeropuerto " + request.getDestino());
        metadata.put("fecha_partida", request.getFechaPartida());
        metadata.put("timestamp_prediccion", LocalDateTime.now().toString());
        metadata.put("modo", "MOCK_FALLBACK");
        metadata.put("nota", "ML Service no disponible, usando predicción heurística dinámica");

        // Respuesta fallback con valores DINÁMICOS
        PredictionResponseDTO response = PredictionResponseDTO.builder()
                .prediccion(prediccion)
                .probabilidadRetraso(probabilidadRetraso)
                .confianza(confianza)
                .distanciaKm(distanciaKm)
                .climaOrigen(climaSimulado)
                .metadata(metadata)
                .modoMock(true)
                .build();

        logger.info(
                "✅ Predicción Mock Fallback DINÁMICA: {} (Probabilidad retraso: {:.2f}%, Confianza: {:.2f}%, Distancia: {} km)",
                response.getPrediccion(),
                response.getProbabilidadRetraso() * 100,
                response.getConfianza() * 100,
                distanciaKm);

        return response;
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

            if (response != null) {
                response.setModoMock(false);
                logger.info("✅ Predicción Real: {} (Probabilidad retraso: {}%)",
                        response.getPrediccion(),
                        response.getProbabilidadRetraso() * 100);
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
