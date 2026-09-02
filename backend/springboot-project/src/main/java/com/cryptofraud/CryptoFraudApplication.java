package com.cryptofraud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Main entrypoint for Crypto Fraud & VASP Intelligence Spring Boot application.
 */
@SpringBootApplication
public class CryptoFraudApplication {

    public static void main(String[] args) {
        SpringApplication.run(CryptoFraudApplication.class, args);
        System.out.println("=========================================================");
        System.out.println("Crypto Fraud & VASP Intelligence Backend is running!");
        System.out.println("API Base URL: http://localhost:8080/api");
        System.out.println("Health Check: http://localhost:8080/api/health");
        System.out.println("=========================================================");
    }

    /**
     * Configures global Cross-Origin Resource Sharing (CORS)
     * so that the local React frontend (http://localhost:5173) can communicate smoothly.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:5173", "http://localhost:3000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }
}
