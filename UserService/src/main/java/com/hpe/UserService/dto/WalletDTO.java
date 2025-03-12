package com.hpe.UserService.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WalletDTO {
    private String username;
    private String balance;
    private String Currency;
}
