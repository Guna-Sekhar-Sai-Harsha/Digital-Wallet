package com.hpe.RewardService.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RewardResponse {

    private String responseCode;
    private String responseMessage;
    private String username;
    private String points;
}
