package com.javatodev.finance.model.dto.request;

import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO used to request a fund transfer between two accounts.
 */
@Data
@NoArgsConstructor
public class FundTransferRequest {
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;

    public FundTransferRequest(String fromAccount, String toAccount, BigDecimal amount) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
    }
}
