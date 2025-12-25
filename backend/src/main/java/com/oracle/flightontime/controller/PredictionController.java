package com.oracle.flightontime.controller;

import com.oracle.flightontime.dto.PredictionRequestDTO;
import com.oracle.flightontime.dto.PredictionResponseDTO;
import com.oracle.flightontime.service.PredictionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * ============================================================================
 * CONTROLADOR REST - PREDICCIÓN DE VUELOS
 * ============================================================================
 * Expone los endpoints HTTP para el sistema FlightOnTime.
 * Implementa validación de negocio y manejo de errores.
 * ============================================================================
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PredictionController {

    private static final Logger logger = LoggerFactory.getLogger(PredictionController.class);

    private final PredictionService predictionService;

    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    /**
     * ========================================================================
     * ENDPOINT PRINCIPAL DE PREDICCIÓN
     * ========================================================================
     * POST /api/predict
     * 
     * Acepta parámetro opcional ?mock=true para usar modo mock
     * Por defecto usa el modo real con integración ML
     * ========================================================================
     */
    @PostMapping("/predict")
    public ResponseEntity<PredictionResponseDTO> predict(
            @Valid @RequestBody PredictionRequestDTO request,
            @RequestParam(value = "mock", defaultValue = "false") boolean useMock) {

        try {
            logger.info("📥 Recibida solicitud de predicción: {} {} → {}",
                    request.getAerolinea(),
                    request.getOrigen(),
                    request.getDestino());

            // Validación adicional de negocio
            if (request.getOrigen().equals(request.getDestino())) {
                logger.warn("⚠️ Origen y destino son iguales: {}", request.getOrigen());
                return ResponseEntity.badRequest().build();
            }

            // Ejecutar predicción
            PredictionResponseDTO response = predictionService.predict(request, useMock);

            logger.info("📤 Respuesta enviada: {} (confianza: {}%)",
                    response.getPrediccion(),
                    response.getConfianza() * 100);

            return ResponseEntity.ok(response);

        } catch (org.springframework.web.server.ResponseStatusException e) {
            // Error de validación de datos
            logger.warn("⚠️ Error de validación: {}", e.getReason());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getReason());
            errorResponse.put("status", e.getStatusCode().value());

            return ResponseEntity.status(e.getStatusCode()).body(
                    PredictionResponseDTO.builder()
                            .prediccion("Error")
                            .probabilidadRetraso(0.0)
                            .confianza(0.0)
                            .distanciaKm(0.0)
                            .metadata(errorResponse)
                            .modoMock(false)
                            .build());
        } catch (Exception e) {
            logger.error("❌ Error en endpoint /predict: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * ========================================================================
     * ENDPOINT DE SALUD
     * ========================================================================
     * GET /api/health
     * Verifica el estado del backend
     * ========================================================================
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "FlightOnTime Backend");
        health.put("version", "1.0.0");
        health.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(health);
    }

    /**
     * ========================================================================
     * ENDPOINT DE DOCUMENTACIÓN
     * ========================================================================
     * GET /api/docs
     * Retorna información sobre los endpoints disponibles
     * ========================================================================
     */
    @GetMapping("/docs")
    public ResponseEntity<Map<String, Object>> docs() {
        Map<String, Object> docs = new HashMap<>();
        docs.put("servicio", "FlightOnTime API");
        docs.put("version", "1.0.0");

        Map<String, Object> endpoints = new HashMap<>();

        // Documentar endpoint de predicción
        Map<String, Object> predictEndpoint = new HashMap<>();
        predictEndpoint.put("metodo", "POST");
        predictEndpoint.put("url", "/api/predict");
        predictEndpoint.put("descripcion", "Predice si un vuelo será puntual o retrasado");
        predictEndpoint.put("parametros", Map.of(
                "mock", "boolean (opcional) - Usar modo mock si es true"));
        predictEndpoint.put("body_ejemplo", Map.of(
                "aerolinea", "LATAM",
                "origen", "GRU",
                "destino", "JFK",
                "fecha_partida", "2025-12-25T14:30:00"));

        endpoints.put("predict", predictEndpoint);

        // Documentar endpoint de salud
        Map<String, Object> healthEndpoint = new HashMap<>();
        healthEndpoint.put("metodo", "GET");
        healthEndpoint.put("url", "/api/health");
        healthEndpoint.put("descripcion", "Verifica el estado del servicio");

        endpoints.put("health", healthEndpoint);

        docs.put("endpoints", endpoints);

        return ResponseEntity.ok(docs);
    }

    /**
     * ========================================================================
     * MANEJO DE ERRORES DE VALIDACIÓN
     * ========================================================================
     */
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            org.springframework.web.bind.MethodArgumentNotValidException ex) {

        Map<String, Object> errors = new HashMap<>();
        errors.put("error", "Validación fallida");

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

        errors.put("campos", fieldErrors);

        logger.warn("⚠️ Error de validación: {}", fieldErrors);

        return ResponseEntity.badRequest().body(errors);
    }
}
