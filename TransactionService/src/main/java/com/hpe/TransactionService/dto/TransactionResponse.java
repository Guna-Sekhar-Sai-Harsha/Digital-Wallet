package com.hpe.TransactionService.dto;

import com.hpe.TransactionService.model.TransactionDetails;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionResponse {
    private String responseCode;
    private String responseMessage;
    private TransactionDetails transactionDetails;
}
