package com.hpe.RewardService.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RewardDTO {

    private String userName;
    private String rewardType;
    private int points;
}
