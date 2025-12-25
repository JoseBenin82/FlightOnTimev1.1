package com.oracle.flightontime.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ============================================================================
 * DTO - DATOS METEOROLÓGICOS
 * ============================================================================
 * Información del clima en el aeropuerto de origen
 * ============================================================================
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherDataDTO {

    @JsonProperty("temperatura")
    private Double temperatura;

    @JsonProperty("humedad")
    private Integer humedad;

    @JsonProperty("presion")
    private Integer presion;

    @JsonProperty("visibilidad")
    private Integer visibilidad;

    @JsonProperty("viento_velocidad")
    private Double vientoVelocidad;

    @JsonProperty("condicion")
    private String condicion;

    @JsonProperty("descripcion")
    private String descripcion;
}
