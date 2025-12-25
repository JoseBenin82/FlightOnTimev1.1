package com.oracle.flightontime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ============================================================================
 * FLIGHTONTIME - APLICACIÓN PRINCIPAL
 * ============================================================================
 * Sistema empresarial de predicción de puntualidad de vuelos.
 * Integra servicios de Machine Learning y datos meteorológicos en tiempo real.
 * 
 * @author Oracle Enterprise Partner
 * @version 1.0.0
 *          ============================================================================
 */
@SpringBootApplication
public class FlightOnTimeApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlightOnTimeApplication.class, args);
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         FlightOnTime Backend - INICIADO                   ║");
        System.out.println("║         Puerto: 8080                                       ║");
        System.out.println("║         Documentación: http://localhost:8080/api/docs      ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }
}
