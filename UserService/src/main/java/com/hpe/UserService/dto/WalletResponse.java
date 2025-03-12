package com.hpe.UserService.dto;

import lombok.Data;

@Data
public class WalletResponse {
    private String responseCode;
    private String responseMessage;
    private WalletDTO walletDTO;

}
