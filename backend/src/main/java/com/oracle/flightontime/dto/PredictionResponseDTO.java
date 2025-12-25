package com.oracle.flightontime.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * ============================================================================
 * DTO DE SALIDA - RESPUESTA DE PREDICCIÓN
 * ============================================================================
 * Contrato de integración para respuestas de predicción.
 * Incluye predicción, probabilidades, clima y metadata.
 * ============================================================================
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionResponseDTO {

    /**
     * Predicción textual: "Puntual" o "Retrasado"
     */
    @JsonProperty("prediccion")
    private String prediccion;

    /**
     * Probabilidad de retraso (0.0 a 1.0)
     */
    @JsonProperty("probabilidad_retraso")
    private Double probabilidadRetraso;

    /**
     * Nivel de confianza de la predicción (0.0 a 1.0)
     */
    @JsonProperty("confianza")
    private Double confianza;

    /**
     * Distancia del vuelo en kilómetros (calculada automáticamente)
     */
    @JsonProperty("distancia_km")
    private Double distanciaKm;

    /**
     * Datos meteorológicos del aeropuerto de origen
     */
    @JsonProperty("clima_origen")
    private WeatherDataDTO climaOrigen;

    /**
     * Metadata adicional (aerolínea, ruta, timestamps, etc.)
     */
    @JsonProperty("metadata")
    private Map<String, Object> metadata;

    /**
     * Indicador de si se usó el modo mock
     */
    @JsonProperty("modo_mock")
    private Boolean modoMock;
}
