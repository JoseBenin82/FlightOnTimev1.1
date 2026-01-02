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
     * Predicción numérica: 0 = Puntual, 1 = Retrasado
     * Según alcance sugerido del proyecto y contrato de integración
     */
    @JsonProperty("prediccion")
    private Integer prediccion;

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
     * Datos meteorológicos del aeropuerto de destino
     */
    @JsonProperty("clima_destino")
    private WeatherDataDTO climaDestino;

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

    // Getters
    public Integer getPrediccion() { return prediccion; }
    public Double getProbabilidadRetraso() { return probabilidadRetraso; }
    public Double getConfianza() { return confianza; }
    public Double getDistanciaKm() { return distanciaKm; }
    public WeatherDataDTO getClimaOrigen() { return climaOrigen; }
    public WeatherDataDTO getClimaDestino() { return climaDestino; }
    public Map<String, Object> getMetadata() { return metadata; }
    public Boolean getModoMock() { return modoMock; }

    // Setters
    public void setPrediccion(Integer prediccion) { this.prediccion = prediccion; }
    public void setProbabilidadRetraso(Double probabilidadRetraso) { this.probabilidadRetraso = probabilidadRetraso; }
    public void setConfianza(Double confianza) { this.confianza = confianza; }
    public void setDistanciaKm(Double distanciaKm) { this.distanciaKm = distanciaKm; }
    public void setClimaOrigen(WeatherDataDTO climaOrigen) { this.climaOrigen = climaOrigen; }
    public void setClimaDestino(WeatherDataDTO climaDestino) { this.climaDestino = climaDestino; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    public void setModoMock(Boolean modoMock) { this.modoMock = modoMock; }
}
