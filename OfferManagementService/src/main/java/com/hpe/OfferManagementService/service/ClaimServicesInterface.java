package com.hpe.OfferManagementService.service;

import com.hpe.OfferManagementService.dto.ClaimResponse;
import org.springframework.http.ResponseEntity;

public interface ClaimServicesInterface {

    ResponseEntity<ClaimResponse> applyOffer(Long offerId, String userName);
    ResponseEntity<ClaimResponse> claimOffer(Long claimId, String userName, String txAmount);
}
