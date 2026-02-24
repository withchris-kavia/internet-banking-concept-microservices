package com.javatodev.finance.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Request model for internal fund transfers between customer accounts.
 * 
 * REQ: FR-006 - Fund transfers can be initiated and persisted
 */
@Data
@Schema(description = "Request to transfer funds between internal bank accounts")
public class InternalTransferRequest {
    
    @Schema(description = "Source account number for the transfer", example = "123456789", required = true)
    private String fromAccount;
    
    @Schema(description = "Destination account number for the transfer", example = "987654321", required = true)
    private String toAccount;
    
    @Schema(description = "Transfer amount", example = "100.00", required = true)
    private BigDecimal amount;
    
    @Schema(description = "Authenticated user ID", example = "user@example.com", required = true)
    private String authID;
    
    @Schema(description = "Optional transfer description or memo", example = "Payment for services")
    private String description;
}
