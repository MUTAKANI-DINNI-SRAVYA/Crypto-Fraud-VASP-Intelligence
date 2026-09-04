package com.cryptofraud.service.blockchain;

import com.cryptofraud.model.Transaction;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Loads mock blockchain transactions from sample-transactions.json.
 * Supports multiple file-resolution strategies and caching for fast hackathon demo performance.
 */
@Component
public class MockTransactionLoader {

    private static final Logger log = LoggerFactory.getLogger(MockTransactionLoader.class);

    private final ObjectMapper objectMapper;

    @Value("${app.mock.transactions-file:../../data/sample-transactions.json}")
    private String configuredFilePath = "../../data/sample-transactions.json";

    private List<Transaction> cachedTransactions = new ArrayList<>();

    public MockTransactionLoader() {
        this.objectMapper = new ObjectMapper();
    }

    @org.springframework.beans.factory.annotation.Autowired
    public MockTransactionLoader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper != null ? objectMapper : new ObjectMapper();
    }

    public MockTransactionLoader(ObjectMapper objectMapper, String configuredFilePath) {
        this.objectMapper = objectMapper != null ? objectMapper : new ObjectMapper();
        this.configuredFilePath = configuredFilePath;
    }

    public void setConfiguredFilePath(String configuredFilePath) {
        this.configuredFilePath = configuredFilePath;
    }

    @PostConstruct
    public void init() {
        loadTransactionsFromFile();
    }

    /**
     * Loads transactions from file using several fallback path resolutions.
     */
    public synchronized List<Transaction> loadTransactionsFromFile() {
        if (!cachedTransactions.isEmpty()) {
            return cachedTransactions;
        }

        try {
            // Strategy 1: Check configured path
            if (configuredFilePath != null && !configuredFilePath.isBlank()) {
                Path path = Paths.get(configuredFilePath);
                if (Files.exists(path)) {
                    log.info("Loading mock transactions from configured path: {}", path.toAbsolutePath());
                    this.cachedTransactions = parseTransactions(Files.readAllBytes(path));
                    if (!this.cachedTransactions.isEmpty()) {
                        return this.cachedTransactions;
                    }
                }
            }

            // Strategy 2: Common relative filesystem paths
            String[] candidatePaths = {
                    "../../data/sample-transactions.json",
                    "data/sample-transactions.json",
                    "../data/sample-transactions.json",
                    "src/main/resources/data/sample-transactions.json"
            };

            for (String candidate : candidatePaths) {
                Path candPath = Paths.get(candidate);
                if (Files.exists(candPath)) {
                    log.info("Loading mock transactions from relative path: {}", candPath.toAbsolutePath());
                    this.cachedTransactions = parseTransactions(Files.readAllBytes(candPath));
                    if (!this.cachedTransactions.isEmpty()) {
                        return this.cachedTransactions;
                    }
                }
            }

            // Strategy 3: Classpath resource
            try (InputStream is = getClass().getResourceAsStream("/data/sample-transactions.json")) {
                if (is != null) {
                    log.info("Loading mock transactions from classpath resource: /data/sample-transactions.json");
                    this.cachedTransactions = parseTransactions(is.readAllBytes());
                    if (!this.cachedTransactions.isEmpty()) {
                        return this.cachedTransactions;
                    }
                }
            }

            log.warn("Could not locate sample-transactions.json in filesystem or classpath!");
        } catch (Exception e) {
            log.error("Failed to load mock transactions: {}", e.getMessage(), e);
        }

        return Collections.emptyList();
    }

    /**
     * Parses byte array containing JSON into List of Transaction objects.
     */
    public List<Transaction> parseTransactions(byte[] jsonData) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonData);
            JsonNode txArrayNode = rootNode.has("transactions") ? rootNode.get("transactions") : rootNode;

            if (txArrayNode != null && txArrayNode.isArray()) {
                return objectMapper.readValue(
                        txArrayNode.traverse(),
                        new TypeReference<List<Transaction>>() {}
                );
            }
        } catch (Exception e) {
            log.error("Error parsing transactions JSON: {}", e.getMessage(), e);
        }
        return Collections.emptyList();
    }

    /**
     * Returns all available mock transactions.
     */
    public List<Transaction> getAllTransactions() {
        if (cachedTransactions.isEmpty()) {
            loadTransactionsFromFile();
        }
        return Collections.unmodifiableList(cachedTransactions);
    }

    /**
     * Retrieves transactions related to a given wallet address.
     * If matching transactions are found (as sender or recipient), returns those.
     * If no exact match is found (e.g. general test wallet), falls back to returning all sample
     * transactions so downstream demo components always have data.
     */
    public List<Transaction> getTransactionsForWallet(String walletAddress) {
        List<Transaction> all = getAllTransactions();
        if (walletAddress == null || walletAddress.isBlank() || "all".equalsIgnoreCase(walletAddress)) {
            return all;
        }

        String target = walletAddress.trim();
        List<Transaction> filtered = all.stream()
                .filter(tx -> (tx.getFrom() != null && tx.getFrom().equalsIgnoreCase(target)) ||
                              (tx.getTo() != null && tx.getTo().equalsIgnoreCase(target)))
                .collect(Collectors.toList());

        if (!filtered.isEmpty()) {
            log.info("Found {} matching mock transactions for address {}", filtered.size(), walletAddress);
            return filtered;
        }

        // Fallback for prototype testing: return full sample dataset if no exact match
        log.info("No direct transactions matched for {}; returning all {} sample transactions for prototype demo",
                walletAddress, all.size());
        return all;
    }
}
