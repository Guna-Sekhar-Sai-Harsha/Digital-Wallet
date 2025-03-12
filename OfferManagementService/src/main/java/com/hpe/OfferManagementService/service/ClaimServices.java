package com.hpe.OfferManagementService.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hpe.OfferManagementService.dto.ClaimResponse;
import com.hpe.OfferManagementService.dto.OfferResponse;
import com.hpe.OfferManagementService.dto.RewardDTO;
import com.hpe.OfferManagementService.model.ClaimedOffers;
import com.hpe.OfferManagementService.model.Offer;
import com.hpe.OfferManagementService.repository.ClaimRepository;
import com.hpe.OfferManagementService.repository.OfferRepository;
import com.hpe.OfferManagementService.utils.OfferManagementServiceUtils;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ClaimServices implements ClaimServicesInterface{
    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private KafkaTemplate<String, String> template;
    @Override
    public ResponseEntity<ClaimResponse> applyOffer(Long offerId, String userName) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found with id " + offerId));


        if (!offer.isActive() || offer.getEndDate().isBefore(LocalDateTime.now())) {
            return new ResponseEntity<>(ClaimResponse.builder()
                    .responseCode(OfferManagementServiceUtils.OFFER_NOT_FOUND_CODE)
                    .responseMessage(OfferManagementServiceUtils.OFFER_NOT_FOUND_MESSAGE)
                    .claimedOffers(null)
                    .build(), HttpStatus.OK);
        }

        ClaimedOffers existingClaim = claimRepository.findByOfferIdAndUserName(offerId, userName).orElse(null);
        if (existingClaim!=null) {
            if(existingClaim.getClaimedAt().isAfter(LocalDateTime.now().minusHours(3))){
                System.out.println("You have already claimed this offer.");
                return new ResponseEntity<>(ClaimResponse.builder()
                    .responseCode(OfferManagementServiceUtils.OFFER_ALREADY_CLAIMED_CODE)
                    .responseMessage(OfferManagementServiceUtils.OFFER_ALREADY_CLAIMED_MESSAGE)
                    .claimedOffers(null)
                    .build(), HttpStatus.OK);
            }
        }

        ClaimedOffers newClaim = claimRepository.save(ClaimedOffers.builder()
                .offerId(offerId)
                .userName(userName)
                .isClaimed(false)
                .appliedAt(LocalDateTime.now())
                .build());

        return new ResponseEntity<>(ClaimResponse.builder()
                .responseCode(OfferManagementServiceUtils.CLAIM_CREATED_CODE)
                .responseMessage(OfferManagementServiceUtils.CLAIM_CREATED_MESSAGE)
                .claimedOffers(newClaim)
                .build(), HttpStatus.CREATED);
    }
    @Override
    public  ResponseEntity<ClaimResponse> claimOffer(Long claimId, String userName, String txAmount){
        ClaimedOffers newClaim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found with id " + claimId));

        if(!newClaim.isClaimed()){

            if(newClaim.getAppliedAt().isAfter(LocalDateTime.now().minusHours(3))) {

                Offer offer = offerRepository.findById(newClaim.getOfferId())
                        .orElseThrow(() -> new ResourceNotFoundException("Offer not found with id " + newClaim.getOfferId()));


                RewardDTO rewardDTO = new RewardDTO();
                rewardDTO.setUserName(userName);
                rewardDTO.setRewardType(offer.getTitle());
                rewardDTO.setPoints(calculateReward(txAmount));

                template.send("RewardOffers", JsonToString(rewardDTO));

                newClaim.setClaimed(true);
                newClaim.setClaimedAt(LocalDateTime.now());
                ClaimedOffers claim = claimRepository.save(newClaim);
                return new ResponseEntity<>(ClaimResponse.builder()
                        .responseCode(OfferManagementServiceUtils.OFFER_CLAIMED_CODE)
                        .responseMessage(OfferManagementServiceUtils.OFFER_CLAIMED_MESSAGE)
                        .claimedOffers(claim)
                        .build(), HttpStatus.CREATED);
            }

            return new ResponseEntity<>(ClaimResponse.builder()
                    .responseCode(OfferManagementServiceUtils.CLAIM_EXPIRED_CODE)
                    .responseMessage(OfferManagementServiceUtils.CLAIM_EXPIRED_MESSAGE)
                    .claimedOffers(null)
                    .build(), HttpStatus.OK);

        }
        return new ResponseEntity<>(ClaimResponse.builder()
                .responseCode(OfferManagementServiceUtils.OFFER_ALREADY_CLAIMED_CODE)
                .responseMessage(OfferManagementServiceUtils.OFFER_ALREADY_CLAIMED_MESSAGE)
                .claimedOffers(null)
                .build(), HttpStatus.OK);
    }

    public int calculateReward(String txAmount){
        //Change calculation

        return (int) (4 * Double.valueOf(txAmount));
    }

    public String JsonToString(Object objectDTO){

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String sendNotification = objectMapper.writeValueAsString(objectDTO);
            return sendNotification;
        }catch (Exception e){
            System.out.println(e);
        }
        return null;
    }


}
