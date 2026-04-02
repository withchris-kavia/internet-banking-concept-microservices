package com.javatodev.finance.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Response model for internal fund transfers.
 * 
 * REQ: FR-006 - Fund transfers can be initiated and persisted
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing internal transfer transaction details")
public class InternalTransferResponse {
    
    @Schema(description = "Result message", example = "Internal transfer completed successfully")
    private String message;
    
    @Schema(description = "Unique transaction identifier", example = "550e8400-e29b-41d4-a716-446655440000")
    private String transactionId;
    
    @Schema(description = "Source account number", example = "123456789")
    private String fromAccount;
    
    @Schema(description = "Destination account number", example = "987654321")
    private String toAccount;
    
    @Schema(description = "Transfer amount", example = "100.00")
    private BigDecimal amount;
    
    @Schema(description = "Transfer status", example = "SUCCESS")
    private String status;
}
