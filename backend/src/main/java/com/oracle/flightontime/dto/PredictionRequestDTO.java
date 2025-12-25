package com.oracle.flightontime.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ============================================================================
 * DTO DE ENTRADA - SOLICITUD DE PREDICCIÓN
 * ============================================================================
 * Contrato de integración para solicitudes de predicción de vuelos.
 * Cumple con estándares de aviación IATA.
 * ============================================================================
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionRequestDTO {

    /**
     * Código de aerolínea (ej: LATAM, GOL, AZUL, AVIANCA)
     */
    @NotBlank(message = "La aerolínea es obligatoria")
    @JsonProperty("aerolinea")
    private String aerolinea;

    /**
     * Código IATA del aeropuerto de origen (3 letras mayúsculas)
     */
    @NotBlank(message = "El aeropuerto de origen es obligatorio")
    @Pattern(regexp = "^[A-Z]{3}$", message = "El código de origen debe ser 3 letras mayúsculas (ej: GRU)")
    @JsonProperty("origen")
    private String origen;

    /**
     * Código IATA del aeropuerto de destino (3 letras mayúsculas)
     */
    @NotBlank(message = "El aeropuerto de destino es obligatorio")
    @Pattern(regexp = "^[A-Z]{3}$", message = "El código de destino debe ser 3 letras mayúsculas (ej: JFK)")
    @JsonProperty("destino")
    private String destino;

    /**
     * Fecha y hora de partida en formato ISO-8601
     * Ejemplo: 2025-12-25T14:30:00
     */
    @JsonProperty("fecha_partida")
    private String fechaPartida;
}
