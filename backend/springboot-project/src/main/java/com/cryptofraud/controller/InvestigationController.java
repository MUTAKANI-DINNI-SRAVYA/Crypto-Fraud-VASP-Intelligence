package com.cryptofraud.controller;

import com.cryptofraud.model.InvestigationRequest;
import com.cryptofraud.model.InvestigationReport;
import com.cryptofraud.service.InvestigationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for submitting investigation analysis requests and AI briefings.
 * Owned by Member 6 (AI & Investigation Report Module) and Member 1 (Integration).
 */
@RestController
@RequestMapping({"/api/investigation", "/api/report"})
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class InvestigationController {

    private final InvestigationService investigationService;

    @Autowired
    public InvestigationController(InvestigationService investigationService) {
        this.investigationService = investigationService;
    }

    /**
     * Primary endpoint for Member 6 investigation report generation.
     * POST /api/investigation/analyze
     * POST /api/report/generate
     */
    @PostMapping({"/analyze", "/generate"})
    public ResponseEntity<InvestigationReport> analyzeInvestigation(@RequestBody(required = false) InvestigationRequest request) {
        InvestigationReport report = investigationService.analyze(request);
        return ResponseEntity.ok(report);
    }

    /**
     * Endpoint for AI briefing explanation synthesis.
     * POST /api/investigation/explain
     */
    @PostMapping("/explain")
    public ResponseEntity<Map<String, Object>> explainInvestigation(@RequestBody(required = false) Map<String, Object> payload) {
        String address = (payload != null && payload.get("address") != null)
                ? payload.get("address").toString()
                : "0x0000000000000000000000000000000000000000";

        InvestigationRequest req = new InvestigationRequest();
        req.setWallet(address);
        InvestigationReport report = investigationService.analyze(req);

        Map<String, Object> response = new HashMap<>();
        response.put("aiSummary", report.getAiExplanation());
        response.put("isFallback", false);
        return ResponseEntity.ok(response);
    }
}
