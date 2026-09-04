package com.cryptofraud.controller;

import com.cryptofraud.model.ErrorResponse;
import com.cryptofraud.model.Transaction;
import com.cryptofraud.service.blockchain.BlockchainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller providing blockchain transaction retrieval endpoints.
 * Owned by Member 2 (Swami - Blockchain/Data Module).
 */
@RestController
@RequestMapping("/api")
public class BlockchainController {

    private static final Logger log = LoggerFactory.getLogger(BlockchainController.class);

    private final BlockchainService blockchainService;

    public BlockchainController(BlockchainService blockchainService) {
        this.blockchainService = blockchainService;
    }

    /**
     * Primary endpoint: GET /api/blockchain/transactions/{walletAddress}
     * Alias endpoint: GET /api/wallet/{walletAddress}/transactions (for full team contract compatibility)
     */
    @GetMapping(
            value = {
                    "/blockchain/transactions/{walletAddress}",
                    "/wallet/{walletAddress}/transactions"
            },
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> getTransactions(@PathVariable(value = "walletAddress", required = false) String walletAddress) {
        try {
            List<Transaction> transactions = blockchainService.getTransactions(walletAddress);
            return ResponseEntity.ok(transactions);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid wallet address requested: '{}' - {}", walletAddress, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(true, e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to retrieve transactions for wallet: '{}' - {}", walletAddress, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(true, "Unable to retrieve blockchain transactions"));
        }
    }

    /**
     * Gracefully handles calls where wallet address path variable is omitted.
     */
    @GetMapping(
            value = {
                    "/blockchain/transactions",
                    "/blockchain/transactions/",
                    "/wallet/transactions",
                    "/wallet/transactions/"
            },
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ErrorResponse> getTransactionsMissingAddress() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(true, "Wallet address cannot be empty."));
    }
}
