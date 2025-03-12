package com.hpe.OfferManagementService.dto;

import com.hpe.OfferManagementService.model.Offer;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OfferResponse {
    private String responseCode;
    private String responseMessage;
    private Offer offer;

}
