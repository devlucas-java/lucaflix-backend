//package com.lucaflix.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//import java.util.Arrays;
//import java.util.List;
//
//@Configuration
//public class CorsConfig implements WebMvcConfigurer {
//
//    @Value("${cors.allowed-origins}")
//    private String allowedOriginsString;
//
//    private List<String> getAllowedOrigins() {
//        // Add Stripe domains to the allowed origins
//        List<String> origins = Arrays.asList(allowedOriginsString.split(","));
//        // Clean up any potential whitespace in the origins
//        for (int i = 0; i < origins.size(); i++) {
//            origins.set(i, origins.get(i).trim());
//        }
//        return origins;
//    }
//
//    @Bean
//    public CorsFilter corsFilter() {
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration config = new CorsConfiguration();
//
//        // Allow specific origins from properties
//        config.setAllowedOrigins(getAllowedOrigins());
//
//        // Alternative: Allow all origins for webhook endpoints
//        // config.addAllowedOriginPattern("*");
//
//        // Allow all HTTP methods
//        config.addAllowedMethod("*");
//
//        // Allow all headers
//        config.addAllowedHeader("*");
//
//        // Allow credentials
//        config.setAllowCredentials(true);
//
//        source.registerCorsConfiguration("/**", config);
//        return new CorsFilter(source);
//    }
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins(getAllowedOrigins().toArray(new String[0]))
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                .allowedHeaders("*")
//                .allowCredentials(true);
//
//        // Create a special configuration for webhook endpoints that accepts all origins
//        registry.addMapping("/webhook/**")
//                .allowedOriginPatterns("*")
//                .allowedMethods("POST", "OPTIONS")
//                .allowedHeaders("*");
//
//        registry.addMapping("/api/payments/webhook/**")
//                .allowedOriginPatterns("*")
//                .allowedMethods("POST", "OPTIONS")
//                .allowedHeaders("*");
//    }
//}