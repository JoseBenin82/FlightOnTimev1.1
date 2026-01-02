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

    // Getters
    public Double getTemperatura() { return temperatura; }
    public Integer getHumedad() { return humedad; }
    public Integer getPresion() { return presion; }
    public Integer getVisibilidad() { return visibilidad; }
    public Double getVientoVelocidad() { return vientoVelocidad; }
    public String getCondicion() { return condicion; }
    public String getDescripcion() { return descripcion; }

    // Setters
    public void setTemperatura(Double temperatura) { this.temperatura = temperatura; }
    public void setHumedad(Integer humedad) { this.humedad = humedad; }
    public void setPresion(Integer presion) { this.presion = presion; }
    public void setVisibilidad(Integer visibilidad) { this.visibilidad = visibilidad; }
    public void setVientoVelocidad(Double vientoVelocidad) { this.vientoVelocidad = vientoVelocidad; }
    public void setCondicion(String condicion) { this.condicion = condicion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
