package com.hpe.RewardService.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RewardsDTO {
    private String userName;
    private int points;
}
