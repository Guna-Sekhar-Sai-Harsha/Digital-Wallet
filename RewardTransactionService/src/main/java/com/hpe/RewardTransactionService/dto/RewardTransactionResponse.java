package com.hpe.RewardTransactionService.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RewardTransactionResponse {

    private String responseCode;
    private String responseMessage;
    private String username;
    private String points;
}
