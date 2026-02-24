package com.javatodev.finance.service;

import com.javatodev.finance.model.TransactionStatus;
import com.javatodev.finance.model.dto.FundTransfer;
import com.javatodev.finance.model.dto.request.FundTransferRequest;
import com.javatodev.finance.model.dto.request.InternalTransferRequest;
import com.javatodev.finance.model.dto.response.FundTransferResponse;
import com.javatodev.finance.model.dto.response.InternalTransferResponse;
import com.javatodev.finance.model.entity.FundTransferEntity;
import com.javatodev.finance.model.mapper.FundTransferMapper;
import com.javatodev.finance.model.repository.FundTransferRepository;
import com.javatodev.finance.service.rest.client.BankingCoreFeignClient;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for managing fund transfers and internal account transfers.
 * Orchestrates validation, persistence, and delegation to core banking service.
 * 
 * REQ: FR-006 - Fund transfers can be initiated and persisted
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class FundTransferService {

    private final FundTransferRepository fundTransferRepository;
    private final BankingCoreFeignClient bankingCoreFeignClient;

    private FundTransferMapper mapper = new FundTransferMapper();

    // PUBLIC_INTERFACE
    /**
     * Process a fund transfer request.
     * Creates a pending transfer record, executes the transfer via core banking,
     * then updates the record with the transaction reference and success status.
     * 
     * REQ: FR-006 - Fund transfers can be initiated and persisted
     * 
     * @param request the fund transfer request
     * @return FundTransferResponse containing transaction details
     */
    public FundTransferResponse fundTransfer(FundTransferRequest request) {
        log.info("Sending fund transfer request {}", request.toString());

        // REQ: FR-006 - Persist transfer record before processing
        FundTransferEntity entity = new FundTransferEntity();
        BeanUtils.copyProperties(request, entity);
        entity.setStatus(TransactionStatus.PENDING);
        FundTransferEntity optFundTransfer = fundTransferRepository.save(entity);

        // REQ: FR-006 - Delegate to core banking for authoritative balance updates
        FundTransferResponse fundTransferResponse = bankingCoreFeignClient.fundTransfer(request);
        
        // REQ: FR-006 - Update transfer record with transaction reference
        optFundTransfer.setTransactionReference(fundTransferResponse.getTransactionId());
        optFundTransfer.setStatus(TransactionStatus.SUCCESS);
        fundTransferRepository.save(optFundTransfer);

        fundTransferResponse.setMessage("Fund Transfer Successfully Completed");
        return fundTransferResponse;
    }

    // PUBLIC_INTERFACE
    /**
     * Process an internal transfer between two accounts within the banking system.
     * This method implements the complete internal transfer flow:
     * 1. Persists a PENDING transfer record locally
     * 2. Converts request to core banking format
     * 3. Invokes core banking to execute the transfer with balance validation
     * 4. Updates local record to SUCCESS status with transaction reference
     * 5. Returns detailed transfer response
     * 
     * REQ: FR-006 - Fund transfers can be initiated and persisted
     * 
     * Contract:
     * - Inputs: InternalTransferRequest with fromAccount, toAccount, amount, authID, optional description
     * - Outputs: InternalTransferResponse with transactionId, accounts, amount, status
     * - Errors: Propagates exceptions from core banking (insufficient funds, account not found)
     * - Side effects: Creates FundTransferEntity record, updates account balances in core banking
     * 
     * @param request the internal transfer request
     * @return InternalTransferResponse containing complete transaction details
     */
    public InternalTransferResponse internalTransfer(InternalTransferRequest request) {
        log.info("Processing internal transfer - from: {}, to: {}, amount: {}", 
                 request.getFromAccount(), request.getToAccount(), request.getAmount());

        // REQ: FR-006 - Persist transfer record with PENDING status before processing
        FundTransferEntity entity = new FundTransferEntity();
        entity.setFromAccount(request.getFromAccount());
        entity.setToAccount(request.getToAccount());
        entity.setAmount(request.getAmount());
        entity.setStatus(TransactionStatus.PENDING);
        FundTransferEntity savedEntity = fundTransferRepository.save(entity);
        
        log.debug("Created pending transfer record with ID: {}", savedEntity.getId());

        try {
            // REQ: FR-006 - Convert internal transfer request to core banking fund transfer format
            FundTransferRequest coreRequest = new FundTransferRequest();
            coreRequest.setFromAccount(request.getFromAccount());
            coreRequest.setToAccount(request.getToAccount());
            coreRequest.setAmount(request.getAmount());
            coreRequest.setAuthID(request.getAuthID());

            // REQ: FR-006 - Delegate to core banking for authoritative balance updates and validation
            log.debug("Invoking core banking for internal transfer execution");
            FundTransferResponse coreResponse = bankingCoreFeignClient.fundTransfer(coreRequest);
            
            // REQ: FR-006 - Update transfer record with transaction reference and SUCCESS status
            savedEntity.setTransactionReference(coreResponse.getTransactionId());
            savedEntity.setStatus(TransactionStatus.SUCCESS);
            fundTransferRepository.save(savedEntity);
            
            log.info("Internal transfer completed successfully - transactionId: {}", coreResponse.getTransactionId());

            // REQ: FR-006 - Build detailed response with all transfer information
            return InternalTransferResponse.builder()
                    .message("Internal transfer completed successfully")
                    .transactionId(coreResponse.getTransactionId())
                    .fromAccount(request.getFromAccount())
                    .toAccount(request.getToAccount())
                    .amount(request.getAmount())
                    .status(TransactionStatus.SUCCESS.name())
                    .build();
                    
        } catch (Exception e) {
            // REQ: FR-006 - On failure, mark transfer record as FAILED and propagate error
            log.error("Internal transfer failed - from: {}, to: {}, error: {}", 
                     request.getFromAccount(), request.getToAccount(), e.getMessage());
            savedEntity.setStatus(TransactionStatus.FAILED);
            fundTransferRepository.save(savedEntity);
            throw e;
        }
    }

    // PUBLIC_INTERFACE
    /**
     * Retrieve all fund transfers with pagination.
     * 
     * @param pageable pagination parameters
     * @return list of fund transfer DTOs
     */
    public List<FundTransfer> readAllTransfers(Pageable pageable) {
        return mapper.convertToDtoList(fundTransferRepository.findAll(pageable).getContent());
    }
}
