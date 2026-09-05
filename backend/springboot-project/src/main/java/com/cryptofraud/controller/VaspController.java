package com.cryptofraud.controller;

import com.cryptofraud.model.LastTraceablePoint;
import com.cryptofraud.model.Transaction;
import com.cryptofraud.model.VaspInteractionResult;
import com.cryptofraud.model.VaspReference;
import com.cryptofraud.service.VaspService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for VASP reference lookup and standalone VASP interaction / Last Traceable Point evaluation.
 */
@RestController
@RequestMapping("/api/vasp")
public class VaspController {

    private final VaspService vaspService;

    @Autowired
    public VaspController(VaspService vaspService) {
        this.vaspService = vaspService;
    }

    /**
     * Endpoint returning the list of mock VASP reference records.
     * GET /api/vasp/reference
     */
    @GetMapping("/reference")
    public ResponseEntity<List<VaspReference>> getVaspReferenceData() {
        return ResponseEntity.ok(vaspService.getReferenceVasps());
    }

    /**
     * Endpoint evaluating VASP interaction & Last Traceable Point for a given list of transactions.
     * POST /api/vasp/check
     */
    @PostMapping("/check")
    public ResponseEntity<Map<String, Object>> checkVaspInteraction(
            @RequestParam(required = false) String targetWallet,
            @RequestBody(required = false) List<Transaction> transactions) {

        VaspInteractionResult interaction = vaspService.checkVaspInteraction(transactions);
        LastTraceablePoint lastPoint = vaspService.determineLastTraceablePoint(transactions, targetWallet);

        Map<String, Object> response = new HashMap<>();
        response.put("vaspInteraction", interaction);
        response.put("lastTraceablePoint", lastPoint);

        return ResponseEntity.ok(response);
    }
}
