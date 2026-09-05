package com.cryptofraud.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BlockchainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/blockchain/transactions/{walletAddress} returns 200 with transaction array")
    void testGetTransactionsSuccess() throws Exception {
        mockMvc.perform(get("/api/blockchain/transactions/0xSCAM999999999999999999999999999999999999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", isA(Iterable.class)))
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[0].hash", notNullValue()))
                .andExpect(jsonPath("$[0].from", notNullValue()))
                .andExpect(jsonPath("$[0].to", notNullValue()))
                .andExpect(jsonPath("$[0].amount", notNullValue()))
                .andExpect(jsonPath("$[0].asset", is("ETH")))
                .andExpect(jsonPath("$[0].timestamp", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/blockchain/transactions/invalid-address returns 400 Bad Request with error payload")
    void testGetTransactionsInvalidAddress() throws Exception {
        mockMvc.perform(get("/api/blockchain/transactions/invalid-short-address")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is(true)))
                .andExpect(jsonPath("$.message", containsString("Invalid Ethereum wallet address format")));
    }

    @Test
    @DisplayName("GET /api/blockchain/transactions without address returns 400 Bad Request")
    void testGetTransactionsEmptyAddress() throws Exception {
        mockMvc.perform(get("/api/blockchain/transactions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error", is(true)))
                .andExpect(jsonPath("$.message", containsString("Wallet address cannot be empty")));
    }

    @Test
    @DisplayName("GET /api/wallet/{walletAddress}/transactions alias returns 200 with transaction array")
    void testGetTransactionsAliasSuccess() throws Exception {
        mockMvc.perform(get("/api/wallet/0xSCAM999999999999999999999999999999999999/transactions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(Iterable.class)))
                .andExpect(jsonPath("$", not(empty())));
    }
}
