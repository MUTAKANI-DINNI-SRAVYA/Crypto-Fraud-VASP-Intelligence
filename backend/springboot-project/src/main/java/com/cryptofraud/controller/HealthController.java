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
public class HealthController {

    @Value("${app.mock.enabled:true}")
    private boolean mockEnabled;

    @Value("${spring.application.name:Crypto Fraud & VASP Intelligence}")
    private String appName;

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> rootWelcome() {
        Map<String, Object> status = new java.util.LinkedHashMap<>();
        status.put("status", "UP");
        status.put("application", appName);
        status.put("message", "Crypto Fraud & VASP Intelligence Backend is operational.");
        status.put("version", "1.0.0-SNAPSHOT");
        status.put("frontendUrl", "http://localhost:5173");
        status.put("endpoints", Map.of(
                "health", "/api/health",
                "walletTransactions", "/api/wallet/{walletAddress}/transactions",
                "riskAnalysis", "/api/risk/analyze",
                "vaspReference", "/api/vasp/reference",
                "vaspCheck", "/api/vasp/check",
                "investigationReport", "/api/investigation/analyze"
        ));
        status.put("timestamp", Instant.now().toString());
        return ResponseEntity.ok(status);
    }

    @GetMapping({"/api", "/api/health"})
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
