package com.cryptofraud.service;

import com.cryptofraud.model.LastTraceablePoint;
import com.cryptofraud.model.Transaction;
import com.cryptofraud.model.VaspInteractionResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

/**
 * Service for generating AI-assisted or deterministic rule-based investigation summaries.
 * Strictly adheres to safety & legal guidelines.
 */
@Service
public class AiExplanationService {

    @Value("${app.ai.enabled:false}")
    private boolean aiEnabled;

    @Value("${app.ai.api-key:}")
    private String apiKey;

    @Value("${app.ai.provider:gemini}")
    private String aiProvider;

    public String generateInvestigationExplanation(String wallet,
                                                   List<Transaction> transactions,
                                                   List<String> riskPatterns,
                                                   Integer riskScore,
                                                   VaspInteractionResult vaspResult,
                                                   LastTraceablePoint lastPoint) {

        if (aiEnabled && apiKey != null && !apiKey.trim().isEmpty()) {
            try {
                String aiSummary = callAiApi(wallet, transactions, riskPatterns, riskScore, vaspResult, lastPoint);
                if (aiSummary != null && !aiSummary.trim().isEmpty()) {
                    return sanitizeSummary(aiSummary, vaspResult, lastPoint);
                }
            } catch (Exception e) {
                // Fall back gracefully on AI service call error
            }
        }

        // Deterministic Rule-Based Fallback Summary
        return generateFallbackExplanation(wallet, transactions, riskPatterns, riskScore, vaspResult, lastPoint);
    }

    private String callAiApi(String wallet,
                             List<Transaction> transactions,
                             List<String> riskPatterns,
                             Integer riskScore,
                             VaspInteractionResult vaspResult,
                             LastTraceablePoint lastPoint) throws Exception {

        String prompt = buildPrompt(wallet, transactions, riskPatterns, riskScore, vaspResult, lastPoint);

        HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

        // Sample generic AI API payload structure
        String jsonPayload = String.format("{\"prompt\":\"%s\",\"max_tokens\":250}", escapeJson(prompt));

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.openai.com/v1/completions")) // or configured provider endpoint
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + apiKey)
            .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
            .timeout(Duration.ofSeconds(4))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            String body = response.body();
            // Basic extraction snippet
            int textIdx = body.indexOf("\"text\":\"");
            if (textIdx != -1) {
                int start = textIdx + 8;
                int end = body.indexOf("\"", start);
                if (end > start) {
                    return body.substring(start, end).replace("\\n", "\n").replace("\\\"", "\"");
                }
            }
        }
        return null;
    }

    private String buildPrompt(String wallet,
                               List<Transaction> transactions,
                               List<String> riskPatterns,
                               Integer riskScore,
                               VaspInteractionResult vaspResult,
                               LastTraceablePoint lastPoint) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are an investigation assistance system for cryptocurrency transaction analysis.\n");
        sb.append("Write a neutral, concise executive briefing (3-4 sentences).\n");
        sb.append("STRICT RULES:\n");
        sb.append("- NEVER accuse any VASP of criminal activity.\n");
        sb.append("- DO NOT invent identities, real-world KYC, bank accounts, or off-chain events.\n");
        sb.append("- Include exact phrase: 'Funds interacted with a VASP-associated address.' if VASP detected.\n");
        sb.append("- Include exact phrase: 'Further lawful off-chain records may be required.'\n\n");
        sb.append("Target Wallet: ").append(wallet).append("\n");
        sb.append("Risk Score: ").append(riskScore != null ? riskScore : "N/A").append("\n");
        sb.append("Patterns: ").append(riskPatterns != null ? riskPatterns.toString() : "None").append("\n");
        if (vaspResult != null && vaspResult.isVaspInteraction()) {
            sb.append("VASP Identified: ").append(vaspResult.getVaspName()).append(" (").append(vaspResult.getVaspType()).append(")\n");
        }
        if (lastPoint != null) {
            sb.append("Last Traceable Point: ").append(lastPoint.getAddress()).append(" (").append(lastPoint.getType()).append(")\n");
        }
        return sb.toString();
    }

    /**
     * Deterministic rule-based fallback generator.
     */
    public String generateFallbackExplanation(String wallet,
                                                List<Transaction> transactions,
                                                List<String> riskPatterns,
                                                Integer riskScore,
                                                VaspInteractionResult vaspResult,
                                                LastTraceablePoint lastPoint) {
        StringBuilder sb = new StringBuilder();

        sb.append("INVESTIGATION BRIEFING: ");
        sb.append("Target wallet ").append(wallet != null ? wallet : "N/A");

        if (riskScore != null) {
            sb.append(" has an evaluated risk score of ").append(riskScore).append("/100.");
        } else {
            sb.append(" is under heuristic analysis.");
        }

        if (riskPatterns != null && !riskPatterns.isEmpty()) {
            sb.append(" Detected flow patterns include: ").append(String.join(", ", riskPatterns)).append(".");
        }

        if (vaspResult != null && vaspResult.isVaspInteraction()) {
            sb.append(" Funds interacted with a VASP-associated address");
            if (vaspResult.getVaspName() != null && !vaspResult.getVaspName().isEmpty()) {
                sb.append(" (").append(vaspResult.getVaspName()).append(")");
            }
            sb.append(".");
        } else {
            sb.append(" No direct interaction with reference VASP entities detected in current transactions.");
        }

        if (lastPoint != null && lastPoint.getAddress() != null) {
            sb.append(" On-chain transaction tracing terminates at ").append(lastPoint.getAddress());
            if ("VASP-associated address".equals(lastPoint.getType())) {
                sb.append(" (VASP omnibus/custodial deposit).");
            } else {
                sb.append(" (unlisted wallet endpoint).");
            }
        }

        sb.append(" Further lawful off-chain records may be required.");

        return sanitizeSummary(sb.toString(), vaspResult, lastPoint);
    }

    /**
     * Enforces strict safety post-processing on AI outputs or fallback texts.
     */
    private String sanitizeSummary(String text, VaspInteractionResult vaspResult, LastTraceablePoint lastPoint) {
        if (text == null) return "";

        // Remove any unauthorized criminality accusations
        String sanitized = text.replaceAll("(?i)this vasp is a criminal organization", "Funds interacted with a VASP-associated address")
                               .replaceAll("(?i)criminal organization", "monitored entity")
                               .replaceAll("(?i)guilty of money laundering", "associated with high-velocity transactions");

        // Guarantee mandatory neutral safety phrases are present
        if (vaspResult != null && vaspResult.isVaspInteraction()) {
            if (!sanitized.contains("Funds interacted with a VASP-associated address.")) {
                sanitized += " Funds interacted with a VASP-associated address.";
            }
        }

        if (!sanitized.contains("Further lawful off-chain records may be required.")) {
            sanitized += " Further lawful off-chain records may be required.";
        }

        return sanitized;
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }

    // Setters for testing configuration
    public void setAiEnabled(boolean aiEnabled) {
        this.aiEnabled = aiEnabled;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
