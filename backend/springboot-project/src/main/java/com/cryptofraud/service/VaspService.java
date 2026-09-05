package com.cryptofraud.service;

import com.cryptofraud.model.LastTraceablePoint;
import com.cryptofraud.model.Transaction;
import com.cryptofraud.model.VaspInteractionResult;
import com.cryptofraud.model.VaspReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Service responsible for VASP reference lookup, VASP interaction checking,
 * and Last Traceable Point graph traversal.
 */
@Service
public class VaspService {

    @Value("${app.mock.vasps-file:../../data/sample-vasps.json}")
    private String vaspsFilePath;

    private final Map<String, VaspReference> vaspRegistry = new HashMap<>();

    @PostConstruct
    public void init() {
        loadMockVasps();
    }

    public void loadMockVasps() {
        vaspRegistry.clear();
        boolean loadedFromFile = false;

        try {
            File file = new File(vaspsFilePath);
            if (!file.exists()) {
                // Try relative to workspace root
                file = new File("data/sample-vasps.json");
            }
            if (file.exists()) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(file);
                JsonNode vaspsArray = rootNode.get("vasps");
                if (vaspsArray != null && vaspsArray.isArray()) {
                    for (JsonNode node : vaspsArray) {
                        VaspReference ref = new VaspReference();
                        ref.setAddress(node.path("address").asText(""));
                        ref.setVaspName(node.path("vaspName").asText(""));
                        ref.setCategory(node.path("category").asText("Exchange"));
                        ref.setCustodialType(node.path("custodialType").asText("Omnibus Hot Wallet"));
                        ref.setLastTraceablePoint(node.path("isLastTraceablePoint").asBoolean(true));
                        ref.setBoundaryNotice(node.path("boundaryNotice").asText("LAST TRACEABLE POINT: Further lawful off-chain records required."));
                        ref.setFictionalJurisdiction(node.path("fictionalJurisdiction").asText("Demo Jurisdiction"));
                        ref.setComplianceNotice(node.path("complianceNotice").asText("Off-chain internal ledger accounts require appropriate legal process."));
                        ref.setSource("Mock reference dataset");

                        if (!ref.getAddress().isEmpty()) {
                            vaspRegistry.put(ref.getAddress().toLowerCase(), ref);
                        }
                    }
                    loadedFromFile = true;
                }
            }
        } catch (Exception e) {
            // Fallback to in-memory mock registry if file reading fails
        }

        if (!loadedFromFile || vaspRegistry.isEmpty()) {
            loadDefaultMockRegistry();
        }
    }

    private void loadDefaultMockRegistry() {
        addMockVasp(new VaspReference(
            "0xVASP888888888888888888888888888888888888",
            "ApexExchange (Fictional Demo VASP)",
            "Centralized Exchange (CEX)",
            "Omnibus Deposit Hot Wallet",
            true,
            "LAST TRACEABLE POINT: Further lawful off-chain records required.",
            "Demo Island Regulatory Zone",
            "Off-chain internal ledger accounts require appropriate legal subpoena."
        ));

        addMockVasp(new VaspReference(
            "0xVASP777777777777777777777777777777777777",
            "NovaPay Crypto (Fictional Demo VASP)",
            "Crypto Payment Processor / Gateway",
            "Merchant Processing Pool",
            true,
            "LAST TRACEABLE POINT: Further lawful off-chain records required.",
            "Demo Metropolis Gateway",
            "Funds converted to merchant settlement credits off-chain."
        ));

        addMockVasp(new VaspReference(
            "0xVASP555555555555555555555555555555555555",
            "CoinHarbor (Fictional Demo VASP)",
            "Custodial Wallet Provider",
            "Institutional Cold/Warm Vault",
            true,
            "LAST TRACEABLE POINT: Further lawful off-chain records required.",
            "Demo Free Trade Zone",
            "Private institutional multi-sig custody."
        ));

        addMockVasp(new VaspReference(
            "0xVASP444444444444444444444444444444444444",
            "GlobalBit Gateway (Fictional Demo VASP)",
            "OTC Brokerage & Liquidity Desk",
            "Settlement Escrow",
            true,
            "LAST TRACEABLE POINT: Further lawful off-chain records required.",
            "Demo Federal District",
            "Bilateral trade executed off-order-book."
        ));

        addMockVasp(new VaspReference(
            "0xVASP001",
            "Example VASP A (Fictional Demo VASP)",
            "Exchange",
            "Custodial Deposit",
            true,
            "LAST TRACEABLE POINT: Further lawful off-chain records required.",
            "India",
            "Mock reference dataset"
        ));
    }

    private void addMockVasp(VaspReference ref) {
        vaspRegistry.put(ref.getAddress().toLowerCase(), ref);
    }

    public List<VaspReference> getReferenceVasps() {
        return new ArrayList<>(vaspRegistry.values());
    }

    public VaspReference findVaspByAddress(String address) {
        if (address == null) return null;
        return vaspRegistry.get(address.toLowerCase());
    }

    /**
     * Checks whether any address in the given transactions matches a mock reference VASP address.
     */
    public VaspInteractionResult checkVaspInteraction(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return new VaspInteractionResult(false, null, null, null, null);
        }

        for (Transaction tx : transactions) {
            VaspReference fromVasp = findVaspByAddress(tx.getFrom());
            if (fromVasp != null) {
                return new VaspInteractionResult(
                    true,
                    fromVasp.getVaspName(),
                    fromVasp.getCategory(),
                    fromVasp.getFictionalJurisdiction(),
                    fromVasp.getAddress()
                );
            }

            VaspReference toVasp = findVaspByAddress(tx.getTo());
            if (toVasp != null) {
                return new VaspInteractionResult(
                    true,
                    toVasp.getVaspName(),
                    toVasp.getCategory(),
                    toVasp.getFictionalJurisdiction(),
                    toVasp.getAddress()
                );
            }
        }

        return new VaspInteractionResult(false, null, null, null, null);
    }

    /**
     * Determines the Last Traceable Point along the transaction chain reachable from the starting target wallet.
     * Handles transactions in arbitrary input order by reconstructing the directed fund flow graph.
     */
    public LastTraceablePoint determineLastTraceablePoint(List<Transaction> transactions, String targetAddress) {
        if (transactions == null || transactions.isEmpty()) {
            String fallbackAddr = (targetAddress != null && !targetAddress.isEmpty()) ? targetAddress : "0x0000000000000000000000000000000000000000";
            VaspReference vaspRef = findVaspByAddress(fallbackAddr);
            if (vaspRef != null) {
                return new LastTraceablePoint(
                    vaspRef.getAddress(),
                    "VASP-associated address",
                    "Target address matches reference VASP-associated address.",
                    true,
                    "Further lawful off-chain records may be required."
                );
            }
            return new LastTraceablePoint(
                fallbackAddr,
                "Unidentified Wallet",
                "No transaction history provided; starting wallet marked as endpoint.",
                false,
                "On-chain trail terminates at starting wallet."
            );
        }

        // Build adjacency graph: sender -> list of outgoing transactions
        Map<String, List<Transaction>> adjacencyMap = new HashMap<>();
        Set<String> allAddresses = new HashSet<>();

        for (Transaction tx : transactions) {
            if (tx.getFrom() != null && tx.getTo() != null) {
                String from = tx.getFrom().toLowerCase();
                String to = tx.getTo().toLowerCase();
                allAddresses.add(from);
                allAddresses.add(to);
                adjacencyMap.computeIfAbsent(from, k -> new ArrayList<>()).add(tx);
            }
        }

        // Determine starting node for traversal
        String startNode = (targetAddress != null) ? targetAddress.toLowerCase() : null;
        if (startNode == null || !allAddresses.contains(startNode)) {
            // Find root sender or use first transaction sender
            Set<String> recipients = new HashSet<>();
            for (Transaction tx : transactions) {
                recipients.add(tx.getTo().toLowerCase());
            }
            for (Transaction tx : transactions) {
                String candidate = tx.getFrom().toLowerCase();
                if (!recipients.contains(candidate)) {
                    startNode = candidate;
                    break;
                }
            }
            if (startNode == null) {
                startNode = transactions.get(0).getFrom().toLowerCase();
            }
        }

        // Breadth-First Traversal (BFS) to find deepest terminal reachable node from startNode
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.add(startNode);
        visited.add(startNode);

        String lastReachedNode = startNode;
        boolean hitVasp = false;
        VaspReference reachedVasp = findVaspByAddress(startNode);

        if (reachedVasp != null) {
            hitVasp = true;
        }

        while (!queue.isEmpty()) {
            String current = queue.poll();
            lastReachedNode = current;

            // If current is a VASP, public on-chain tracing stops here
            VaspReference currentVasp = findVaspByAddress(current);
            if (currentVasp != null) {
                reachedVasp = currentVasp;
                hitVasp = true;
                break;
            }

            List<Transaction> outgoing = adjacencyMap.get(current);
            if (outgoing != null && !outgoing.isEmpty()) {
                // Sort outgoing transactions by timestamp if available for deterministic path traversal
                outgoing.sort(Comparator.comparing(t -> t.getTimestamp() != null ? t.getTimestamp() : ""));
                for (Transaction tx : outgoing) {
                    String nextNode = tx.getTo().toLowerCase();
                    if (!visited.contains(nextNode)) {
                        visited.add(nextNode);
                        queue.add(nextNode);
                    }
                }
            }
        }

        // If terminal node was not a VASP during traversal, check if lastReachedNode itself is a VASP
        if (!hitVasp) {
            VaspReference checkVasp = findVaspByAddress(lastReachedNode);
            if (checkVasp != null) {
                hitVasp = true;
                reachedVasp = checkVasp;
            }
        }

        if (hitVasp && reachedVasp != null) {
            return new LastTraceablePoint(
                reachedVasp.getAddress(),
                "VASP-associated address",
                "Funds reached a reference VASP-associated address (" + reachedVasp.getVaspName() + ").",
                true,
                "Further lawful off-chain records may be required."
            );
        } else {
            // Find original casing for address if present
            String originalCaseAddr = lastReachedNode;
            for (Transaction tx : transactions) {
                if (tx.getFrom() != null && tx.getFrom().equalsIgnoreCase(lastReachedNode)) {
                    originalCaseAddr = tx.getFrom();
                    break;
                }
                if (tx.getTo() != null && tx.getTo().equalsIgnoreCase(lastReachedNode)) {
                    originalCaseAddr = tx.getTo();
                    break;
                }
            }

            return new LastTraceablePoint(
                originalCaseAddr,
                "Unidentified Wallet",
                "Final reachable on-chain endpoint in analyzed transaction graph.",
                false,
                "On-chain trail reaches an unlisted wallet address."
            );
        }
    }
}
