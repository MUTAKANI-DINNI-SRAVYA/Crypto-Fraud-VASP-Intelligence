package com.cryptofraud.service.blockchain;

import com.cryptofraud.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Service responsible for blockchain transaction retrieval.
 * Supports live Etherscan API querying as well as resilient mock & fallback modes.
 */
@Service
public class BlockchainService {

    private static final Logger log = LoggerFactory.getLogger(BlockchainService.class);

    // Matches standard 42-character Ethereum hex addresses as well as alphanumeric hackathon demo addresses
    private static final Pattern ETH_ADDRESS_PATTERN = Pattern.compile("(?i)^0x[a-z0-9]{40}$");

    private final EtherscanClient etherscanClient;
    private final MockTransactionLoader mockTransactionLoader;

    @Value("${app.mock.enabled:true}")
    private boolean mockEnabled;

    @Value("${app.blockchain.fallback-to-mock:true}")
    private boolean fallbackToMock;

    public BlockchainService(EtherscanClient etherscanClient, MockTransactionLoader mockTransactionLoader) {
        this.etherscanClient = etherscanClient;
        this.mockTransactionLoader = mockTransactionLoader;
    }

    /**
     * Validates if a string is a valid Ethereum or hackathon demo wallet address.
     */
    public static boolean isValidEthereumAddress(String address) {
        if (address == null || address.isBlank()) {
            return false;
        }
        return ETH_ADDRESS_PATTERN.matcher(address.trim()).matches();
    }

    /**
     * Retrieves transaction history for the specified Ethereum wallet address.
     *
     * @param walletAddress 42-character Ethereum wallet address (starting with 0x)
     * @return List of standardized Transaction objects
     * @throws IllegalArgumentException if the wallet address is null, blank, or malformed
     * @throws RuntimeException if transaction retrieval fails and fallback is disabled
     */
    public List<Transaction> getTransactions(String walletAddress) {
        if (walletAddress == null || walletAddress.isBlank()) {
            throw new IllegalArgumentException("Wallet address cannot be empty.");
        }

        String cleanedAddress = walletAddress.trim();
        if (!isValidEthereumAddress(cleanedAddress)) {
            throw new IllegalArgumentException(
                    "Invalid Ethereum wallet address format. Must be a 42-character address starting with '0x'."
            );
        }

        // Check if mock mode is explicitly enabled or if the Etherscan API key is unconfigured
        if (mockEnabled || !etherscanClient.isConfigured()) {
            log.info("Retrieving transactions in MOCK mode for wallet: {}", cleanedAddress);
            return mockTransactionLoader.getTransactionsForWallet(cleanedAddress);
        }

        // Live Etherscan retrieval with graceful fallback
        try {
            log.info("Querying LIVE blockchain data from Etherscan for wallet: {}", cleanedAddress);
            return etherscanClient.fetchTransactions(cleanedAddress);
        } catch (Exception e) {
            log.warn("Etherscan API query failed for {}: {}", cleanedAddress, e.getMessage());

            if (fallbackToMock) {
                log.info("Gracefully falling back to mock transactions dataset for wallet: {}", cleanedAddress);
                return mockTransactionLoader.getTransactionsForWallet(cleanedAddress);
            }

            throw new RuntimeException("Unable to retrieve blockchain transactions", e);
        }
    }

    public boolean isMockMode() {
        return mockEnabled || !etherscanClient.isConfigured();
    }

    public void setMockEnabled(boolean mockEnabled) {
        this.mockEnabled = mockEnabled;
    }

    public void setFallbackToMock(boolean fallbackToMock) {
        this.fallbackToMock = fallbackToMock;
    }
}
