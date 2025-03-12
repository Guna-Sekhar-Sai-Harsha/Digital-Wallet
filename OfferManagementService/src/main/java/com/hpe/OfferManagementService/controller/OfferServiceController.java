package com.hpe.OfferManagementService.controller;

import com.hpe.OfferManagementService.dto.ClaimResponse;
import com.hpe.OfferManagementService.dto.OfferDTO;
import com.hpe.OfferManagementService.dto.OfferResponse;
import com.hpe.OfferManagementService.model.Offer;
import com.hpe.OfferManagementService.service.ClaimServices;
import com.hpe.OfferManagementService.service.OfferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/offers")
public class OfferServiceController {

    @Autowired
    private OfferService offerService;
    @Autowired
    private ClaimServices claimServices;
    @PostMapping("/create")
    public ResponseEntity<OfferResponse> createOffer(@RequestBody OfferDTO offerDTO){

        return offerService.createOffer(offerDTO);
    }
    @PostMapping("/update/{offerId}")
    public ResponseEntity<OfferResponse> updateOffer(@PathVariable Long offerId,@RequestBody OfferDTO offerDTO){
        return offerService.updateOffer(offerId, offerDTO);
    }
    @PostMapping("/deactivate/{offerId}")
    public ResponseEntity<OfferResponse> deactivateOffer(@PathVariable Long offerId){

        return offerService.deactivateOffer(offerId);
    }
    @PostMapping("/activate/{offerId}")
    public ResponseEntity<OfferResponse> activateOffer(@PathVariable Long offerId){
        return offerService.activateOffer(offerId);
    }
    @GetMapping("/get/{businessId}")
    public List<Offer> getOffersByBusinessId(@PathVariable Long businessId){
        return offerService.getOffersByBusinessId(businessId);
    }
    @GetMapping("/")
    public List<Offer> getActiveOffers(){

        return offerService.getActiveOffers();
    }
    @GetMapping("/getOffer/{offerId}")
    public ResponseEntity<OfferResponse> getOfferById(@PathVariable Long offerId){

        return offerService.getOfferById(offerId);
    }
    @PostMapping("/apply/{offerId}/{userName}")
    public ResponseEntity<ClaimResponse> applyOffer(@PathVariable Long offerId, @PathVariable String userName){
        return claimServices.applyOffer(offerId, userName);
    }
    @PostMapping("/claim/{claimId}/{userName}/{txAmount}")
    public ResponseEntity<ClaimResponse> claimOffer(@PathVariable Long claimId, @PathVariable String userName, @PathVariable String txAmount){
        return claimServices.claimOffer(claimId, userName, txAmount);
    }


}
