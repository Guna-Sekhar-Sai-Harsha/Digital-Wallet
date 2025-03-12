package com.hpe.OfferManagementService.service;

import com.hpe.OfferManagementService.dto.OfferDTO;
import com.hpe.OfferManagementService.dto.OfferResponse;
import com.hpe.OfferManagementService.model.Offer;
import com.hpe.OfferManagementService.repository.OfferRepository;
import com.hpe.OfferManagementService.utils.OfferManagementServiceUtils;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OfferService implements OfferServiceInterface{
    @Autowired
    private OfferRepository offerRepository;
    @Override
    public ResponseEntity<OfferResponse> createOffer(OfferDTO offerDTO){

        Offer savedOffer = offerRepository.save(Offer.builder()
                        .businessId(offerDTO.getBusinessId())
                        .title(offerDTO.getTitle())
                        .isActive(true)
                        .description(offerDTO.getDescription())
                        .startDate(offerDTO.getStartDate())
                        .endDate(offerDTO.getEndDate())
                        .createdAt(LocalDateTime.now())
                        .ModifiedAt(LocalDateTime.now())
                .build());


        return new ResponseEntity<>(OfferResponse.builder()
                .responseCode(OfferManagementServiceUtils.OFFER_CREATED_CODE)
                .responseMessage(OfferManagementServiceUtils.OFFER_CREATED_MESSAGE)
                .offer(savedOffer)
                .build(), HttpStatus.OK);
    }
    @Override
    public ResponseEntity<OfferResponse> updateOffer(Long offerId, OfferDTO offerDTO) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found with id " + offerId));

        offer.setTitle(offerDTO.getTitle());
        offer.setDescription(offerDTO.getDescription());
        offer.setStartDate(offerDTO.getStartDate());
        offer.setEndDate(offerDTO.getEndDate());
        Offer updatedOffer = offerRepository.save(offer);
        return new ResponseEntity<>(OfferResponse.builder()
                .responseCode(OfferManagementServiceUtils.OFFER_UPDATED_CODE)
                .responseMessage(OfferManagementServiceUtils.OFFER_UPDATED_MESSAGE)
                .offer(updatedOffer)
                .build(), HttpStatus.OK);
    }
    @Override
    public ResponseEntity<OfferResponse> deactivateOffer(Long offerId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found with id " + offerId));
        offer.setActive(false);
        Offer deactivatedOffer = offerRepository.save(offer);
        return new ResponseEntity<>(OfferResponse.builder()
                .responseCode(OfferManagementServiceUtils.OFFER_DEACTIVATED_CODE)
                .responseMessage(OfferManagementServiceUtils.OFFER_DEACTIVATED_MESSAGE)
                .offer(deactivatedOffer)
                .build(), HttpStatus.OK);
    }
    @Override
    public ResponseEntity<OfferResponse> activateOffer(Long offerId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found with id " + offerId));
        offer.setActive(true);
        Offer activatedOffer = offerRepository.save(offer);
        return new ResponseEntity<>(OfferResponse.builder()
                .responseCode(OfferManagementServiceUtils.OFFER_ACTIVATED_CODE)
                .responseMessage(OfferManagementServiceUtils.OFFER_ACTIVATED_MESSAGE)
                .offer(activatedOffer)
                .build(), HttpStatus.OK);
    }
    @Override
    public List<Offer> getOffersByBusinessId(Long businessId) {

        return offerRepository.findAllByBusinessId(businessId);
    }
    @Override
    public List<Offer> getActiveOffers() {
        return offerRepository.findAllByIsActiveTrueAndEndDateAfter(LocalDateTime.now());
    }
    @Override
    public ResponseEntity<OfferResponse> getOfferById(Long offerId) {
        Offer offer = offerRepository.findById(offerId)
                .orElse(null);
        if (offer!= null){
            return new ResponseEntity<>(OfferResponse.builder()
                    .responseCode(OfferManagementServiceUtils.OFFER_FOUND_CODE)
                    .responseMessage(OfferManagementServiceUtils.OFFER_FOUND_MESSAGE)
                    .offer(offer)
                    .build(), HttpStatus.OK);
        }
        return new ResponseEntity<>(OfferResponse.builder()
                .responseCode(OfferManagementServiceUtils.OFFER_NOT_FOUND_CODE)
                .responseMessage(OfferManagementServiceUtils.OFFER_NOT_FOUND_MESSAGE)
                .offer(null)
                .build(), HttpStatus.OK);

    }

}
