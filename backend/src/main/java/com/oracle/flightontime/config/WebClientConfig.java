package com.oracle.flightontime.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * ============================================================================
 * CONFIGURACIÓN DE WEBCLIENT
 * ============================================================================
 * Configura el cliente HTTP reactivo para llamadas al servicio ML
 * ============================================================================
 */
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
