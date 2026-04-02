package com.javatodev.finance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javatodev.finance.model.TransactionStatus;
import com.javatodev.finance.model.dto.request.FundTransferRequest;
import com.javatodev.finance.model.dto.request.InternalTransferRequest;
import com.javatodev.finance.model.dto.response.FundTransferResponse;
import com.javatodev.finance.model.dto.response.InternalTransferResponse;
import com.javatodev.finance.service.FundTransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for FundTransferController REST endpoints.
 * Tests HTTP layer behavior including request/response handling.
 * 
 * REQ: FR-006 - Fund transfers can be initiated and persisted
 */
@WebMvcTest(FundTransferController.class)
@DisplayName("Fund Transfer Controller Integration Tests")
class FundTransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FundTransferService fundTransferService;

    // ==================== Internal Transfer Endpoint Tests ====================

    @Test
    @DisplayName("POST /api/v1/transfer/internal - Success")
    void internalTransfer_success() throws Exception {
        // Given
        InternalTransferRequest request = new InternalTransferRequest();
        request.setFromAccount("ACC123");
        request.setToAccount("ACC456");
        request.setAmount(BigDecimal.valueOf(100.00));
        request.setAuthID("user@example.com");
        request.setDescription("Test internal transfer");

        InternalTransferResponse expectedResponse = InternalTransferResponse.builder()
                .message("Internal transfer completed successfully")
                .transactionId("TXN-550e8400-e29b")
                .fromAccount("ACC123")
                .toAccount("ACC456")
                .amount(BigDecimal.valueOf(100.00))
                .status(TransactionStatus.SUCCESS.name())
                .build();

        when(fundTransferService.internalTransfer(any(InternalTransferRequest.class)))
                .thenReturn(expectedResponse);

        // When/Then
        mockMvc.perform(post("/api/v1/transfer/internal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Internal transfer completed successfully")))
                .andExpect(jsonPath("$.transactionId", is("TXN-550e8400-e29b")))
                .andExpect(jsonPath("$.fromAccount", is("ACC123")))
                .andExpect(jsonPath("$.toAccount", is("ACC456")))
                .andExpect(jsonPath("$.amount", is(100.00)))
                .andExpect(jsonPath("$.status", is("SUCCESS")));

        verify(fundTransferService, times(1)).internalTransfer(any(InternalTransferRequest.class));
    }

    @Test
    @DisplayName("POST /api/v1/transfer/internal - Large Amount Transfer")
    void internalTransfer_largeAmount() throws Exception {
        // Given
        InternalTransferRequest request = new InternalTransferRequest();
        request.setFromAccount("ACC123");
        request.setToAccount("ACC456");
        request.setAmount(BigDecimal.valueOf(50000.00));
        request.setAuthID("user@example.com");

        InternalTransferResponse expectedResponse = InternalTransferResponse.builder()
                .message("Internal transfer completed successfully")
                .transactionId("TXN-LARGE-AMT")
                .fromAccount("ACC123")
                .toAccount("ACC456")
                .amount(BigDecimal.valueOf(50000.00))
                .status(TransactionStatus.SUCCESS.name())
                .build();

        when(fundTransferService.internalTransfer(any(InternalTransferRequest.class)))
                .thenReturn(expectedResponse);

        // When/Then
        mockMvc.perform(post("/api/v1/transfer/internal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount", is(50000.00)))
                .andExpect(jsonPath("$.transactionId", is("TXN-LARGE-AMT")));

        verify(fundTransferService, times(1)).internalTransfer(any(InternalTransferRequest.class));
    }

    @Test
    @DisplayName("POST /api/v1/transfer/internal - With Optional Description")
    void internalTransfer_withDescription() throws Exception {
        // Given
        InternalTransferRequest request = new InternalTransferRequest();
        request.setFromAccount("ACC123");
        request.setToAccount("ACC456");
        request.setAmount(BigDecimal.valueOf(200.00));
        request.setAuthID("user@example.com");
        request.setDescription("Payment for consulting services");

        InternalTransferResponse expectedResponse = InternalTransferResponse.builder()
                .message("Internal transfer completed successfully")
                .transactionId("TXN-WITH-DESC")
                .fromAccount("ACC123")
                .toAccount("ACC456")
                .amount(BigDecimal.valueOf(200.00))
                .status(TransactionStatus.SUCCESS.name())
                .build();

        when(fundTransferService.internalTransfer(any(InternalTransferRequest.class)))
                .thenReturn(expectedResponse);

        // When/Then
        mockMvc.perform(post("/api/v1/transfer/internal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId", notNullValue()))
                .andExpect(jsonPath("$.status", is("SUCCESS")));
    }

    @Test
    @DisplayName("POST /api/v1/transfer/internal - Insufficient Funds")
    void internalTransfer_insufficientFunds() throws Exception {
        // Given
        InternalTransferRequest request = new InternalTransferRequest();
        request.setFromAccount("ACC123");
        request.setToAccount("ACC456");
        request.setAmount(BigDecimal.valueOf(10000.00));
        request.setAuthID("user@example.com");

        when(fundTransferService.internalTransfer(any(InternalTransferRequest.class)))
                .thenThrow(new RuntimeException("Insufficient balance in source account"));

        // When/Then
        mockMvc.perform(post("/api/v1/transfer/internal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(fundTransferService, times(1)).internalTransfer(any(InternalTransferRequest.class));
    }

    @Test
    @DisplayName("POST /api/v1/transfer/internal - Account Not Found")
    void internalTransfer_accountNotFound() throws Exception {
        // Given
        InternalTransferRequest request = new InternalTransferRequest();
        request.setFromAccount("INVALID_ACC");
        request.setToAccount("ACC456");
        request.setAmount(BigDecimal.valueOf(100.00));
        request.setAuthID("user@example.com");

        when(fundTransferService.internalTransfer(any(InternalTransferRequest.class)))
                .thenThrow(new RuntimeException("Source account not found"));

        // When/Then
        mockMvc.perform(post("/api/v1/transfer/internal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/transfer/internal - Invalid JSON Request")
    void internalTransfer_invalidJson() throws Exception {
        // Given
        String invalidJson = "{\"fromAccount\": \"ACC123\", \"amount\": \"invalid\"}";

        // When/Then
        mockMvc.perform(post("/api/v1/transfer/internal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(fundTransferService, never()).internalTransfer(any(InternalTransferRequest.class));
    }

    @Test
    @DisplayName("POST /api/v1/transfer/internal - Missing Required Fields")
    void internalTransfer_missingFields() throws Exception {
        // Given - Request with missing toAccount
        InternalTransferRequest request = new InternalTransferRequest();
        request.setFromAccount("ACC123");
        request.setAmount(BigDecimal.valueOf(100.00));
        request.setAuthID("user@example.com");
        // toAccount is missing

        InternalTransferResponse expectedResponse = InternalTransferResponse.builder()
                .message("Internal transfer completed successfully")
                .transactionId("TXN-TEST")
                .fromAccount("ACC123")
                .toAccount(null)
                .amount(BigDecimal.valueOf(100.00))
                .status(TransactionStatus.SUCCESS.name())
                .build();

        when(fundTransferService.internalTransfer(any(InternalTransferRequest.class)))
                .thenReturn(expectedResponse);

        // When/Then
        mockMvc.perform(post("/api/v1/transfer/internal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // ==================== Regular Fund Transfer Endpoint Tests ====================

    @Test
    @DisplayName("POST /api/v1/transfer - Success")
    void fundTransfer_success() throws Exception {
        // Given
        FundTransferRequest request = new FundTransferRequest();
        request.setFromAccount("ACC123");
        request.setToAccount("ACC789");
        request.setAmount(BigDecimal.valueOf(250.00));
        request.setAuthID("user@example.com");

        FundTransferResponse expectedResponse = new FundTransferResponse();
        expectedResponse.setTransactionId("TXN-FUND-123");
        expectedResponse.setMessage("Fund Transfer Successfully Completed");

        when(fundTransferService.fundTransfer(any(FundTransferRequest.class)))
                .thenReturn(expectedResponse);

        // When/Then
        mockMvc.perform(post("/api/v1/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId", is("TXN-FUND-123")))
                .andExpect(jsonPath("$.message", is("Fund Transfer Successfully Completed")));

        verify(fundTransferService, times(1)).fundTransfer(any(FundTransferRequest.class));
    }

    @Test
    @DisplayName("POST /api/v1/transfer - Service Exception")
    void fundTransfer_serviceException() throws Exception {
        // Given
        FundTransferRequest request = new FundTransferRequest();
        request.setFromAccount("ACC123");
        request.setToAccount("ACC789");
        request.setAmount(BigDecimal.valueOf(250.00));
        request.setAuthID("user@example.com");

        when(fundTransferService.fundTransfer(any(FundTransferRequest.class)))
                .thenThrow(new RuntimeException("Service temporarily unavailable"));

        // When/Then
        mockMvc.perform(post("/api/v1/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/transfer - Retrieve Transfers")
    void readFundTransfers_success() throws Exception {
        // When/Then
        mockMvc.perform(get("/api/v1/transfer")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(fundTransferService, times(1)).readAllTransfers(any());
    }

    @Test
    @DisplayName("GET /api/v1/transfer - With Pagination")
    void readFundTransfers_withPagination() throws Exception {
        // When/Then
        mockMvc.perform(get("/api/v1/transfer")
                        .param("page", "2")
                        .param("size", "20")
                        .param("sort", "id,desc"))
                .andExpect(status().isOk());

        verify(fundTransferService, times(1)).readAllTransfers(any());
    }

    // ==================== Content Type and Method Tests ====================

    @Test
    @DisplayName("POST /api/v1/transfer/internal - Wrong Content Type")
    void internalTransfer_wrongContentType() throws Exception {
        // Given
        InternalTransferRequest request = new InternalTransferRequest();
        request.setFromAccount("ACC123");
        request.setToAccount("ACC456");
        request.setAmount(BigDecimal.valueOf(100.00));
        request.setAuthID("user@example.com");

        // When/Then
        mockMvc.perform(post("/api/v1/transfer/internal")
                        .contentType(MediaType.APPLICATION_XML)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnsupportedMediaType());

        verify(fundTransferService, never()).internalTransfer(any(InternalTransferRequest.class));
    }

    @Test
    @DisplayName("GET /api/v1/transfer/internal - Method Not Allowed")
    void internalTransfer_methodNotAllowed() throws Exception {
        // When/Then
        mockMvc.perform(get("/api/v1/transfer/internal"))
                .andExpect(status().isMethodNotAllowed());

        verify(fundTransferService, never()).internalTransfer(any(InternalTransferRequest.class));
    }
}
