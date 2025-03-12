package com.hpe.OfferManagementService.dto;

import com.hpe.OfferManagementService.model.ClaimedOffers;
import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class ClaimResponse {
    private String responseCode;
    private String responseMessage;
    private ClaimedOffers claimedOffers;

}
