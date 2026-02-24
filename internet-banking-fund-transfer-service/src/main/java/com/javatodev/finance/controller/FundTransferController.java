package com.javatodev.finance.controller;

import com.javatodev.finance.model.dto.request.FundTransferRequest;
import com.javatodev.finance.model.dto.request.InternalTransferRequest;
import com.javatodev.finance.service.FundTransferService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Fund Transfer API", description = "API for processing fund transfers")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/transfer")
public class FundTransferController {

    private final FundTransferService fundTransferService;

    // PUBLIC_INTERFACE
    /**
     * Process a fund transfer request.
     * 
     * REQ: FR-006 - Fund transfers can be initiated and persisted
     * 
     * @param fundTransferRequest the fund transfer request details
     * @return ResponseEntity containing the fund transfer response
     */
    @Operation(summary = "Send Fund Transfer", description = "Process a fund transfer request")
    @PostMapping
    public ResponseEntity sendFundTransfer(@RequestBody FundTransferRequest fundTransferRequest) {
        log.info("Got fund transfer request from API {}", fundTransferRequest.toString());
        return ResponseEntity.ok(fundTransferService.fundTransfer(fundTransferRequest));
    }

    // PUBLIC_INTERFACE
    /**
     * Process an internal transfer between existing bank accounts.
     * This endpoint provides a dedicated API for transferring funds between
     * accounts owned by customers within the same banking system.
     * 
     * REQ: FR-006 - Fund transfers can be initiated and persisted
     * 
     * @param internalTransferRequest the internal transfer request details
     * @return ResponseEntity containing the internal transfer response with transaction details
     */
    @Operation(
        summary = "Internal Account Transfer", 
        description = "Transfer funds between two internal bank accounts. " +
                      "Both accounts must exist and the source account must have sufficient balance."
    )
    @PostMapping("/internal")
    public ResponseEntity internalTransfer(@RequestBody InternalTransferRequest internalTransferRequest) {
        log.info("Got internal transfer request from API - from: {}, to: {}, amount: {}", 
                 internalTransferRequest.getFromAccount(), 
                 internalTransferRequest.getToAccount(), 
                 internalTransferRequest.getAmount());
        return ResponseEntity.ok(fundTransferService.internalTransfer(internalTransferRequest));
    }

    // PUBLIC_INTERFACE
    /**
     * Retrieve a paginated list of fund transfers.
     * 
     * @param pageable pagination parameters
     * @return ResponseEntity containing the list of fund transfers
     */
    @Operation(summary = "Read Fund Transfers", description = "Retrieve a paginated list of fund transfers")
    @GetMapping
    public ResponseEntity readFundTransfers(Pageable pageable) {
        log.info("Reading fund transfers from core");
        return ResponseEntity.ok(fundTransferService.readAllTransfers(pageable));
    }
}
