package com.hpe.OfferManagementService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RewardDTO {

    private String userName;
    private String rewardType;
    private int points;
}
