package com.hpe.RewardTransactionService.dto;

import lombok.Data;

@Data
public class TransactionDTO {
    private String walletUserName;
    private String counterPartyUserName;
    private String amount;
    private String currency;
    private String type;
    private String description;

}
