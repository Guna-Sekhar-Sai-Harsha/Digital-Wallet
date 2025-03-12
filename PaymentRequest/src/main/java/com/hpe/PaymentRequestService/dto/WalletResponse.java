package com.hpe.PaymentRequestService.dto;

import lombok.Data;

@Data
public class WalletResponse {
    private String responseCode;
    private String responseMessage;
    private WalletDTO walletDTO;

}
