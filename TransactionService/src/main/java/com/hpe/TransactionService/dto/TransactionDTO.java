package com.hpe.TransactionService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionDTO {
    private String walletUserName;
    private String counterPartyUserName;
    private String amount;
    private String currency;
    private String type;
    private String description;

}
