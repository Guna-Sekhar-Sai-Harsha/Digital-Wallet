package com.hpe.SavingsService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
    private String walletUserName;
    private String counterPartyUserName;
    private String amount;
    private String currency;
    private String type;
    private String description;

}
