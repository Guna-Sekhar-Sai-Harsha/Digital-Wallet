package com.hpe.OfferManagementService.service;

import com.hpe.OfferManagementService.dto.OfferDTO;
import com.hpe.OfferManagementService.dto.OfferResponse;
import com.hpe.OfferManagementService.model.Offer;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OfferServiceInterface {

    ResponseEntity<OfferResponse> createOffer(OfferDTO offerDTO);
    ResponseEntity<OfferResponse> updateOffer(Long offerId, OfferDTO offerDTO);
    ResponseEntity<OfferResponse> deactivateOffer(Long offerId);
    ResponseEntity<OfferResponse> activateOffer(Long offerId);
    List<Offer> getOffersByBusinessId(Long businessId);
    List<Offer> getActiveOffers();
    ResponseEntity<OfferResponse> getOfferById(Long offerId);
}
