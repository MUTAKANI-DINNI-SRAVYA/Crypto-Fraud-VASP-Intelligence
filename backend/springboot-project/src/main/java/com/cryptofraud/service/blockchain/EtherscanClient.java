package com.cryptofraud.service.blockchain;

import com.cryptofraud.model.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Client for interacting with the public Etherscan API.
 * Converts raw Etherscan responses into standardized Transaction objects.
 */
@Component
public class EtherscanClient {

    private static final Logger log = LoggerFactory.getLogger(EtherscanClient.class);
    private static final BigDecimal WEI_PER_ETH = new BigDecimal("1000000000000000000"); // 10^18

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    @Value("${app.etherscan.base-url:https://api.etherscan.io/api}")
    private String baseUrl;

    @Value("${app.etherscan.api-key:}")
    private String apiKey;

    @Value("${app.etherscan.timeout-seconds:5}")
    private int timeoutSeconds;

    public EtherscanClient() {
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    @org.springframework.beans.factory.annotation.Autowired
    public EtherscanClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper != null ? objectMapper : new ObjectMapper();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    // Constructor for testing
    public EtherscanClient(ObjectMapper objectMapper, HttpClient httpClient, String baseUrl, String apiKey, int timeoutSeconds) {
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.timeoutSeconds = timeoutSeconds;
    }

    /**
     * Checks if a valid API key is configured.
     */
    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank() && !"YOUR_API_KEY".equalsIgnoreCase(apiKey.trim());
    }

    /**
     * Fetches transaction history for a given wallet address from Etherscan.
     */
    public List<Transaction> fetchTransactions(String walletAddress) throws Exception {
        if (!isConfigured()) {
            throw new IllegalStateException("Etherscan API key is not configured.");
        }

        String url = String.format(
                "%s?module=account&action=txlist&address=%s&startblock=0&endblock=99999999&page=1&offset=50&sort=desc&apikey=%s",
                baseUrl.trim(),
                walletAddress.trim(),
                apiKey.trim()
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(timeoutSeconds > 0 ? timeoutSeconds : 5))
                .GET()
                .build();

        log.info("Querying Etherscan API for wallet: {}", walletAddress);
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Etherscan API returned HTTP " + response.statusCode());
        }

        return parseEtherscanResponse(response.body());
    }

    /**
     * Parses Etherscan JSON response into standard Transaction objects.
     */
    public List<Transaction> parseEtherscanResponse(String json) throws Exception {
        JsonNode root = objectMapper.readTree(json);
        String status = root.path("status").asText("");
        String message = root.path("message").asText("");

        // Etherscan returns status 0 with "No transactions found" when address has zero transactions
        if ("0".equals(status) && message.toLowerCase().contains("no transactions found")) {
            log.info("Etherscan reported 0 transactions for address.");
            return Collections.emptyList();
        }

        if (!"1".equals(status)) {
            String errorDetails = root.path("result").isTextual() ? root.path("result").asText() : message;
            throw new RuntimeException("Etherscan API returned error: " + errorDetails);
        }

        JsonNode resultArray = root.path("result");
        if (!resultArray.isArray()) {
            return Collections.emptyList();
        }

        List<Transaction> transactions = new ArrayList<>();
        for (JsonNode txNode : resultArray) {
            String hash = txNode.path("hash").asText("");
            String from = txNode.path("from").asText("");
            String to = txNode.path("to").asText("");
            String valueWei = txNode.path("value").asText("0");
            String timeStamp = txNode.path("timeStamp").asText("0");

            double amountEth = weiToEth(valueWei);
            String isoTimestamp = formatEpochTimestamp(timeStamp);

            Transaction tx = new Transaction(
                    hash,
                    from,
                    to,
                    amountEth,
                    "ETH",
                    isoTimestamp
            );
            transactions.add(tx);
        }

        log.info("Successfully parsed {} transactions from Etherscan.", transactions.size());
        return transactions;
    }

    /**
     * Converts Wei to ETH double value without precision loss using BigDecimal.
     */
    public static double weiToEth(String weiStr) {
        if (weiStr == null || weiStr.isBlank()) {
            return 0.0;
        }
        try {
            BigDecimal wei = new BigDecimal(weiStr.trim());
            BigDecimal eth = wei.divide(WEI_PER_ETH, 8, RoundingMode.HALF_UP);
            return eth.doubleValue();
        } catch (Exception e) {
            log.warn("Failed to parse Wei amount '{}': {}", weiStr, e.getMessage());
            return 0.0;
        }
    }

    /**
     * Converts a Unix epoch timestamp (seconds) into ISO-8601 UTC string.
     */
    public static String formatEpochTimestamp(String epochSecondsStr) {
        if (epochSecondsStr == null || epochSecondsStr.isBlank()) {
            return Instant.now().toString();
        }
        try {
            long seconds = Long.parseLong(epochSecondsStr.trim());
            return Instant.ofEpochSecond(seconds).toString();
        } catch (Exception e) {
            log.warn("Failed to parse timestamp '{}': {}", epochSecondsStr, e.getMessage());
            return Instant.now().toString();
        }
    }
}
