package com.cryptofraud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Health check controller providing operational status and configuration state.
 * Owned by Member 1 (Team Lead / Integrator).
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    @Value("${app.mock.enabled:true}")
    private boolean mockEnabled;

    @Value("${spring.application.name:Crypto Fraud & VASP Intelligence}")
    private String appName;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> checkHealth() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("application", appName);
        status.put("timestamp", Instant.now().toString());
        status.put("mockModeEnabled", mockEnabled);
        status.put("version", "1.0.0-SNAPSHOT");
        status.put("message", "Crypto Fraud & VASP Intelligence Backend is operational.");
        return ResponseEntity.ok(status);
    }
}
