package com.javatodev.finance.service;

import com.javatodev.finance.exception.SimpleBankingGlobalException;
import com.javatodev.finance.model.TransactionStatus;
import com.javatodev.finance.model.dto.request.FundTransferRequest;
import com.javatodev.finance.model.dto.request.InternalTransferRequest;
import com.javatodev.finance.model.dto.response.FundTransferResponse;
import com.javatodev.finance.model.dto.response.InternalTransferResponse;
import com.javatodev.finance.model.entity.FundTransferEntity;
import com.javatodev.finance.model.repository.FundTransferRepository;
import com.javatodev.finance.service.rest.client.BankingCoreFeignClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for FundTransferService covering internal transfer functionality.
 * 
 * REQ: FR-006 - Fund transfers can be initiated and persisted
 */
@DisplayName("Fund Transfer Service Tests")
class FundTransferServiceTest {

    @Mock
    private FundTransferRepository fundTransferRepository;

    @Mock
    private BankingCoreFeignClient bankingCoreFeignClient;

    private FundTransferService fundTransferService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fundTransferService = new FundTransferService(fundTransferRepository, bankingCoreFeignClient);
    }

    // ==================== Internal Transfer Tests ====================

    @Test
    @DisplayName("Internal Transfer - Success Scenario")
    void internalTransfer_success() {
        // Given
        InternalTransferRequest request = new InternalTransferRequest();
        request.setFromAccount("ACC123");
        request.setToAccount("ACC456");
        request.setAmount(BigDecimal.valueOf(100.00));
        request.setAuthID("user@example.com");
        request.setDescription("Test transfer");

        FundTransferEntity savedEntity = new FundTransferEntity();
        savedEntity.setId(1L);
        savedEntity.setFromAccount("ACC123");
        savedEntity.setToAccount("ACC456");
        savedEntity.setAmount(BigDecimal.valueOf(100.00));
        savedEntity.setStatus(TransactionStatus.PENDING);

        FundTransferResponse coreResponse = new FundTransferResponse();
        coreResponse.setTransactionId("TXN-550e8400-e29b");
        coreResponse.setMessage("Transfer completed");

        when(fundTransferRepository.save(any(FundTransferEntity.class))).thenReturn(savedEntity);
        when(bankingCoreFeignClient.fundTransfer(any(FundTransferRequest.class))).thenReturn(coreResponse);

        // When
        InternalTransferResponse response = fundTransferService.internalTransfer(request);

        // Then
        assertNotNull(response, "Response should not be null");
        assertEquals("Internal transfer completed successfully", response.getMessage());
        assertEquals("TXN-550e8400-e29b", response.getTransactionId());
        assertEquals("ACC123", response.getFromAccount());
        assertEquals("ACC456", response.getToAccount());
        assertEquals(BigDecimal.valueOf(100.00), response.getAmount());
        assertEquals(TransactionStatus.SUCCESS.name(), response.getStatus());

        // Verify repository interactions
        ArgumentCaptor<FundTransferEntity> entityCaptor = ArgumentCaptor.forClass(FundTransferEntity.class);
        verify(fundTransferRepository, times(2)).save(entityCaptor.capture());
        
        List<FundTransferEntity> capturedEntities = entityCaptor.getAllValues();
        assertEquals(TransactionStatus.PENDING, capturedEntities.get(0).getStatus(), 
                     "First save should have PENDING status");
        assertEquals(TransactionStatus.SUCCESS, capturedEntities.get(1).getStatus(), 
                     "Second save should have SUCCESS status");
        assertEquals("TXN-550e8400-e29b", capturedEntities.get(1).getTransactionReference(), 
                     "Transaction reference should be set");

        // Verify Feign client interaction
        ArgumentCaptor<FundTransferRequest> requestCaptor = ArgumentCaptor.forClass(FundTransferRequest.class);
        verify(bankingCoreFeignClient, times(1)).fundTransfer(requestCaptor.capture());
        
        FundTransferRequest capturedRequest = requestCaptor.getValue();
        assertEquals("ACC123", capturedRequest.getFromAccount());
        assertEquals("ACC456", capturedRequest.getToAccount());
        assertEquals(BigDecimal.valueOf(100.00), capturedRequest.getAmount());
        assertEquals("user@example.com", capturedRequest.getAuthID());
    }

    @Test
    @DisplayName("Internal Transfer - Large Amount")
    void internalTransfer_largeAmount() {
        // Given
        InternalTransferRequest request = new InternalTransferRequest();
        request.setFromAccount("ACC123");
        request.setToAccount("ACC456");
        request.setAmount(BigDecimal.valueOf(50000.00));
        request.setAuthID("user@example.com");

        FundTransferEntity savedEntity = new FundTransferEntity();
        savedEntity.setId(1L);
        savedEntity.setStatus(TransactionStatus.PENDING);

        FundTransferResponse coreResponse = new FundTransferResponse();
        coreResponse.setTransactionId("TXN-LARGE-AMT");

        when(fundTransferRepository.save(any(FundTransferEntity.class))).thenReturn(savedEntity);
        when(bankingCoreFeignClient.fundTransfer(any(FundTransferRequest.class))).thenReturn(coreResponse);

        // When
        InternalTransferResponse response = fundTransferService.internalTransfer(request);

        // Then
        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(50000.00), response.getAmount());
        assertEquals("TXN-LARGE-AMT", response.getTransactionId());
    }

    @Test
    @DisplayName("Internal Transfer - Failure Due to Insufficient Funds")
    void internalTransfer_insufficientFunds() {
        // Given
        InternalTransferRequest request = new InternalTransferRequest();
        request.setFromAccount("ACC123");
        request.setToAccount("ACC456");
        request.setAmount(BigDecimal.valueOf(100.00));
        request.setAuthID("user@example.com");

        FundTransferEntity savedEntity = new FundTransferEntity();
        savedEntity.setId(1L);
        savedEntity.setStatus(TransactionStatus.PENDING);

        when(fundTransferRepository.save(any(FundTransferEntity.class))).thenReturn(savedEntity);
        when(bankingCoreFeignClient.fundTransfer(any(FundTransferRequest.class)))
            .thenThrow(new SimpleBankingGlobalException("INSUFFICIENT_FUNDS", "Insufficient balance in source account"));

        // When/Then
        SimpleBankingGlobalException exception = assertThrows(
            SimpleBankingGlobalException.class,
            () -> fundTransferService.internalTransfer(request),
            "Should throw exception when insufficient funds"
        );

        assertEquals("INSUFFICIENT_FUNDS", exception.getCode());
        assertTrue(exception.getMessage().contains("Insufficient balance"));

        // Verify entity was marked as FAILED
        ArgumentCaptor<FundTransferEntity> entityCaptor = ArgumentCaptor.forClass(FundTransferEntity.class);
        verify(fundTransferRepository, times(2)).save(entityCaptor.capture());
        
        List<FundTransferEntity> capturedEntities = entityCaptor.getAllValues();
        assertEquals(TransactionStatus.FAILED, capturedEntities.get(1).getStatus(),
                     "Entity should be marked as FAILED after exception");
    }

    @Test
    @DisplayName("Internal Transfer - Failure Due to Account Not Found")
    void internalTransfer_accountNotFound() {
        // Given
        InternalTransferRequest request = new InternalTransferRequest();
        request.setFromAccount("INVALID_ACC");
        request.setToAccount("ACC456");
        request.setAmount(BigDecimal.valueOf(100.00));
        request.setAuthID("user@example.com");

        FundTransferEntity savedEntity = new FundTransferEntity();
        savedEntity.setId(1L);
        savedEntity.setStatus(TransactionStatus.PENDING);

        when(fundTransferRepository.save(any(FundTransferEntity.class))).thenReturn(savedEntity);
        when(bankingCoreFeignClient.fundTransfer(any(FundTransferRequest.class)))
            .thenThrow(new SimpleBankingGlobalException("ACCOUNT_NOT_FOUND", "Source account not found"));

        // When/Then
        SimpleBankingGlobalException exception = assertThrows(
            SimpleBankingGlobalException.class,
            () -> fundTransferService.internalTransfer(request),
            "Should throw exception when account not found"
        );

        assertEquals("ACCOUNT_NOT_FOUND", exception.getCode());

        // Verify entity was marked as FAILED
        ArgumentCaptor<FundTransferEntity> entityCaptor = ArgumentCaptor.forClass(FundTransferEntity.class);
        verify(fundTransferRepository, times(2)).save(entityCaptor.capture());
        assertEquals(TransactionStatus.FAILED, entityCaptor.getAllValues().get(1).getStatus());
    }

    @Test
    @DisplayName("Internal Transfer - Failure Due to Network Error")
    void internalTransfer_networkError() {
        // Given
        InternalTransferRequest request = new InternalTransferRequest();
        request.setFromAccount("ACC123");
        request.setToAccount("ACC456");
        request.setAmount(BigDecimal.valueOf(100.00));
        request.setAuthID("user@example.com");

        FundTransferEntity savedEntity = new FundTransferEntity();
        savedEntity.setId(1L);
        savedEntity.setStatus(TransactionStatus.PENDING);

        when(fundTransferRepository.save(any(FundTransferEntity.class))).thenReturn(savedEntity);
        when(bankingCoreFeignClient.fundTransfer(any(FundTransferRequest.class)))
            .thenThrow(new RuntimeException("Connection timeout"));

        // When/Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> fundTransferService.internalTransfer(request),
            "Should propagate network errors"
        );

        assertTrue(exception.getMessage().contains("Connection timeout"));

        // Verify entity was marked as FAILED
        verify(fundTransferRepository, times(2)).save(any(FundTransferEntity.class));
    }

    @Test
    @DisplayName("Internal Transfer - Same Account Validation")
    void internalTransfer_sameAccount() {
        // Given - Same account for source and destination
        InternalTransferRequest request = new InternalTransferRequest();
        request.setFromAccount("ACC123");
        request.setToAccount("ACC123");
        request.setAmount(BigDecimal.valueOf(100.00));
        request.setAuthID("user@example.com");

        FundTransferEntity savedEntity = new FundTransferEntity();
        savedEntity.setId(1L);
        savedEntity.setStatus(TransactionStatus.PENDING);

        when(fundTransferRepository.save(any(FundTransferEntity.class))).thenReturn(savedEntity);
        when(bankingCoreFeignClient.fundTransfer(any(FundTransferRequest.class)))
            .thenThrow(new SimpleBankingGlobalException("SAME_ACCOUNT", "Cannot transfer to same account"));

        // When/Then
        SimpleBankingGlobalException exception = assertThrows(
            SimpleBankingGlobalException.class,
            () -> fundTransferService.internalTransfer(request)
        );

        assertEquals("SAME_ACCOUNT", exception.getCode());
    }

    @Test
    @DisplayName("Internal Transfer - Minimum Amount")
    void internalTransfer_minimumAmount() {
        // Given - Very small amount
        InternalTransferRequest request = new InternalTransferRequest();
        request.setFromAccount("ACC123");
        request.setToAccount("ACC456");
        request.setAmount(BigDecimal.valueOf(0.01));
        request.setAuthID("user@example.com");

        FundTransferEntity savedEntity = new FundTransferEntity();
        savedEntity.setId(1L);
        savedEntity.setStatus(TransactionStatus.PENDING);

        FundTransferResponse coreResponse = new FundTransferResponse();
        coreResponse.setTransactionId("TXN-MIN-AMT");

        when(fundTransferRepository.save(any(FundTransferEntity.class))).thenReturn(savedEntity);
        when(bankingCoreFeignClient.fundTransfer(any(FundTransferRequest.class))).thenReturn(coreResponse);

        // When
        InternalTransferResponse response = fundTransferService.internalTransfer(request);

        // Then
        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(0.01), response.getAmount());
    }

    // ==================== Regular Fund Transfer Tests ====================

    @Test
    @DisplayName("Fund Transfer - Success Scenario")
    void fundTransfer_success() {
        // Given
        FundTransferRequest request = new FundTransferRequest();
        request.setFromAccount("ACC123");
        request.setToAccount("ACC789");
        request.setAmount(BigDecimal.valueOf(250.00));
        request.setAuthID("user@example.com");

        FundTransferEntity savedEntity = new FundTransferEntity();
        savedEntity.setId(2L);
        savedEntity.setStatus(TransactionStatus.PENDING);

        FundTransferResponse coreResponse = new FundTransferResponse();
        coreResponse.setTransactionId("TXN-FUND-TRANSFER");
        coreResponse.setMessage("Transaction successfully completed");

        when(fundTransferRepository.save(any(FundTransferEntity.class))).thenReturn(savedEntity);
        when(bankingCoreFeignClient.fundTransfer(any(FundTransferRequest.class))).thenReturn(coreResponse);

        // When
        FundTransferResponse response = fundTransferService.fundTransfer(request);

        // Then
        assertNotNull(response);
        assertEquals("TXN-FUND-TRANSFER", response.getTransactionId());
        assertEquals("Fund Transfer Successfully Completed", response.getMessage());

        // Verify interactions
        verify(fundTransferRepository, times(2)).save(any(FundTransferEntity.class));
        verify(bankingCoreFeignClient, times(1)).fundTransfer(request);
    }

    @Test
    @DisplayName("Fund Transfer - Updates Transaction Reference")
    void fundTransfer_updatesTransactionReference() {
        // Given
        FundTransferRequest request = new FundTransferRequest();
        request.setFromAccount("ACC100");
        request.setToAccount("ACC200");
        request.setAmount(BigDecimal.valueOf(500.00));
        request.setAuthID("admin@example.com");

        FundTransferEntity savedEntity = new FundTransferEntity();
        savedEntity.setId(3L);
        savedEntity.setStatus(TransactionStatus.PENDING);

        FundTransferResponse coreResponse = new FundTransferResponse();
        coreResponse.setTransactionId("TXN-REF-12345");

        when(fundTransferRepository.save(any(FundTransferEntity.class))).thenReturn(savedEntity);
        when(bankingCoreFeignClient.fundTransfer(any(FundTransferRequest.class))).thenReturn(coreResponse);

        // When
        fundTransferService.fundTransfer(request);

        // Then
        ArgumentCaptor<FundTransferEntity> entityCaptor = ArgumentCaptor.forClass(FundTransferEntity.class);
        verify(fundTransferRepository, times(2)).save(entityCaptor.capture());
        
        FundTransferEntity updatedEntity = entityCaptor.getAllValues().get(1);
        assertEquals("TXN-REF-12345", updatedEntity.getTransactionReference());
        assertEquals(TransactionStatus.SUCCESS, updatedEntity.getStatus());
    }

    @Test
    @DisplayName("Fund Transfer - Handles Exception from Core Banking")
    void fundTransfer_coreException() {
        // Given
        FundTransferRequest request = new FundTransferRequest();
        request.setFromAccount("ACC123");
        request.setToAccount("ACC789");
        request.setAmount(BigDecimal.valueOf(1000.00));

        FundTransferEntity savedEntity = new FundTransferEntity();
        savedEntity.setId(4L);

        when(fundTransferRepository.save(any(FundTransferEntity.class))).thenReturn(savedEntity);
        when(bankingCoreFeignClient.fundTransfer(any(FundTransferRequest.class)))
            .thenThrow(new SimpleBankingGlobalException("CORE_ERROR", "Core banking service unavailable"));

        // When/Then
        assertThrows(SimpleBankingGlobalException.class, 
                     () -> fundTransferService.fundTransfer(request));

        // Verify entity was saved at least once (initial save)
        verify(fundTransferRepository, atLeastOnce()).save(any(FundTransferEntity.class));
    }
}
