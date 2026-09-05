package com.cryptofraud.controller;

import com.cryptofraud.dto.RiskAnalysisRequest;
import com.cryptofraud.model.RiskResult;
import com.cryptofraud.service.risk.RiskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * REST controller for cryptocurrency wallet risk evaluation.
 * Owned by Member 3 (Fraud / Risk Engineer).
 */
@RestController
@RequestMapping("/api/risk")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class RiskController {

    private final RiskService riskService;

    public RiskController(RiskService riskService) {
        this.riskService = riskService;
    }

    /**
     * Analyzes transaction activity for a target wallet and produces an explainable risk evaluation.
     *
     * @param request Payload containing the target wallet and transaction list
     * @return RiskResult JSON containing wallet, riskScore, riskLevel, and patterns
     */
    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeRisk(@RequestBody(required = false) RiskAnalysisRequest request) {
        if (request == null || request.getWallet() == null || request.getWallet().trim().isEmpty()) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, "Wallet address is required and cannot be empty.");
        }

        try {
            RiskResult result = riskService.analyzeRisk(request);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during risk analysis: " + e.getMessage());
        }
    }

    /**
     * Exception handler for malformed or missing arguments.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> errorBody = new LinkedHashMap<>();
        errorBody.put("timestamp", Instant.now().toString());
        errorBody.put("status", status.value());
        errorBody.put("error", status.getReasonPhrase());
        errorBody.put("message", message);
        errorBody.put("path", "/api/risk/analyze");
        return ResponseEntity.status(status).body(errorBody);
    }
}
