package com.hpe.SavingsService.dto;

import lombok.Data;

@Data
public class WalletResponse {
    private String responseCode;
    private String responseMessage;
    private WalletDTO walletDTO;

}
