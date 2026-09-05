package com.cryptofraud.service.blockchain;

import com.cryptofraud.model.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BlockchainServiceTest {

    private BlockchainService blockchainService;
    private MockTransactionLoader mockTransactionLoader;
    private EtherscanClient etherscanClient;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockTransactionLoader = new MockTransactionLoader(objectMapper);
        mockTransactionLoader.init();

        etherscanClient = new EtherscanClient(objectMapper);
        blockchainService = new BlockchainService(etherscanClient, mockTransactionLoader);
        blockchainService.setMockEnabled(true);
        blockchainService.setFallbackToMock(true);
    }

    @Test
    @DisplayName("Valid wallet request retrieves transaction history in mock mode")
    void testValidWalletRequestInMockMode() {
        String validAddress = "0xSCAM999999999999999999999999999999999999";
        List<Transaction> transactions = blockchainService.getTransactions(validAddress);

        assertNotNull(transactions, "Transactions list should not be null");
        assertFalse(transactions.isEmpty(), "Transactions list should not be empty for demo address");

        Transaction firstTx = transactions.get(0);
        assertNotNull(firstTx.getHash(), "Tx hash must be present");
        assertNotNull(firstTx.getFrom(), "Tx from address must be present");
        assertNotNull(firstTx.getTo(), "Tx to address must be present");
        assertTrue(firstTx.getAmount() > 0, "Tx amount should be positive");
        assertEquals("ETH", firstTx.getAsset(), "Tx asset must be ETH");
        assertNotNull(firstTx.getTimestamp(), "Tx timestamp must be present");
    }

    @Test
    @DisplayName("Empty or blank wallet address throws IllegalArgumentException")
    void testEmptyWalletAddressThrowsException() {
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () -> {
            blockchainService.getTransactions("");
        });
        assertTrue(ex1.getMessage().toLowerCase().contains("cannot be empty"));

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> {
            blockchainService.getTransactions("   ");
        });
        assertTrue(ex2.getMessage().toLowerCase().contains("cannot be empty"));

        IllegalArgumentException ex3 = assertThrows(IllegalArgumentException.class, () -> {
            blockchainService.getTransactions(null);
        });
        assertTrue(ex3.getMessage().toLowerCase().contains("cannot be empty"));
    }

    @Test
    @DisplayName("Invalid wallet address format throws IllegalArgumentException")
    void testInvalidWalletAddressThrowsException() {
        // Not starting with 0x
        assertThrows(IllegalArgumentException.class, () -> {
            blockchainService.getTransactions("123456789012345678901234567890123456789012");
        });

        // Too short
        assertThrows(IllegalArgumentException.class, () -> {
            blockchainService.getTransactions("0x123");
        });

        // Too long
        assertThrows(IllegalArgumentException.class, () -> {
            blockchainService.getTransactions("0x1234567890123456789012345678901234567890123456");
        });

        // Contains invalid non-alphanumeric characters
        assertThrows(IllegalArgumentException.class, () -> {
            blockchainService.getTransactions("0xSCAM9999999999999999999999999999999999!@");
        });
    }

    @Test
    @DisplayName("Wei to ETH conversion avoids precision loss")
    void testWeiToEthConversion() {
        // 0.25 ETH = 250,000,000,000,000,000 Wei
        double eth1 = EtherscanClient.weiToEth("250000000000000000");
        assertEquals(0.25, eth1, 0.000001);

        // 1.0 ETH = 1,000,000,000,000,000,000 Wei
        double eth2 = EtherscanClient.weiToEth("1000000000000000000");
        assertEquals(1.0, eth2, 0.000001);

        // 0 Wei = 0.0 ETH
        double eth3 = EtherscanClient.weiToEth("0");
        assertEquals(0.0, eth3, 0.000001);

        // Null / blank safety
        assertEquals(0.0, EtherscanClient.weiToEth(null), 0.000001);
        assertEquals(0.0, EtherscanClient.weiToEth(""), 0.000001);
    }

    @Test
    @DisplayName("Epoch timestamp conversion produces valid ISO-8601 string")
    void testTimestampConversion() {
        String iso = EtherscanClient.formatEpochTimestamp("1725273000");
        assertNotNull(iso);
        assertTrue(iso.endsWith("Z"), "Timestamp must be UTC ISO-8601 ending in Z");
        assertTrue(iso.contains("T"), "Timestamp must contain date-time separator T");

        // Null / empty safety fallback
        String fallback = EtherscanClient.formatEpochTimestamp(null);
        assertNotNull(fallback);
        assertTrue(fallback.endsWith("Z"));
    }

    @Test
    @DisplayName("Successful transaction parsing from realistic Etherscan JSON")
    void testEtherscanResponseParsing() throws Exception {
        String sampleEtherscanJson = "{\n" +
                "  \"status\": \"1\",\n" +
                "  \"message\": \"OK\",\n" +
                "  \"result\": [\n" +
                "    {\n" +
                "      \"hash\": \"0xabc123\",\n" +
                "      \"from\": \"0x1111111111111111111111111111111111111111\",\n" +
                "      \"to\": \"0x2222222222222222222222222222222222222222\",\n" +
                "      \"value\": \"500000000000000000\",\n" +
                "      \"timeStamp\": \"1725273000\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        List<Transaction> parsed = etherscanClient.parseEtherscanResponse(sampleEtherscanJson);
        assertEquals(1, parsed.size());

        Transaction tx = parsed.get(0);
        assertEquals("0xabc123", tx.getHash());
        assertEquals("0x1111111111111111111111111111111111111111", tx.getFrom());
        assertEquals("0x2222222222222222222222222222222222222222", tx.getTo());
        assertEquals(0.5, tx.getAmount(), 0.00001);
        assertEquals("ETH", tx.getAsset());
        assertTrue(tx.getTimestamp().endsWith("Z"));
    }

    @Test
    @DisplayName("API failure gracefully falls back to mock transactions")
    void testApiFailureMockFallback() {
        // Create a custom failing client
        EtherscanClient failingClient = new EtherscanClient(objectMapper) {
            @Override
            public boolean isConfigured() {
                return true;
            }

            @Override
            public List<Transaction> fetchTransactions(String walletAddress) throws Exception {
                throw new RuntimeException("Simulated Etherscan Network Timeout");
            }
        };

        BlockchainService resilientService = new BlockchainService(failingClient, mockTransactionLoader);
        resilientService.setMockEnabled(false); // Live mode requested
        resilientService.setFallbackToMock(true); // Fallback enabled

        String address = "0xSCAM999999999999999999999999999999999999";
        List<Transaction> fallbackTxs = resilientService.getTransactions(address);

        assertNotNull(fallbackTxs);
        assertFalse(fallbackTxs.isEmpty(), "Should fallback to mock transactions when live API fails");
    }
}
