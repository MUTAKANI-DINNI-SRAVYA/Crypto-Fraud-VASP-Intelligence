package com.cryptofraud.controller;

import com.cryptofraud.model.InvestigationRequest;
import com.cryptofraud.model.InvestigationReport;
import com.cryptofraud.service.InvestigationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for submitting investigation analysis requests.
 */
@RestController
@RequestMapping("/api/investigation")
public class InvestigationController {

    private final InvestigationService investigationService;

    @Autowired
    public InvestigationController(InvestigationService investigationService) {
        this.investigationService = investigationService;
    }

    /**
     * Main endpoint for Member 6 investigation report generation.
     * POST /api/investigation/analyze
     */
    @PostMapping("/analyze")
    public ResponseEntity<InvestigationReport> analyzeInvestigation(@RequestBody(required = false) InvestigationRequest request) {
        InvestigationReport report = investigationService.analyze(request);
        return ResponseEntity.ok(report);
    }
}
