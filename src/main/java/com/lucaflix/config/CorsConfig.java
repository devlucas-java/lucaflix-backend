package com.lucaflix.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Value("#{'${cors.allowed-origins}'.split(',')}")
    private List<String> allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Configurar origens permitidas
        config.setAllowedOrigins(allowedOrigins);

        // Permitir todos cabeçalhos
        config.setAllowedHeaders(List.of("*"));

        // Configurar métodos HTTP permitidos
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));

        // Permitir credenciais (importante para autenticação)
        config.setAllowCredentials(true);

        // Configurar cabeçalhos que podem ser expostos ao cliente
        config.setExposedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));

        // Definir tempo de cache para requisições preflight
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}