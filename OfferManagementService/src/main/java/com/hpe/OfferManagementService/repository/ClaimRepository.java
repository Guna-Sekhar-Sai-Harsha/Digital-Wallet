package com.hpe.OfferManagementService.repository;

import com.hpe.OfferManagementService.model.ClaimedOffers;
import com.hpe.OfferManagementService.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClaimRepository extends JpaRepository<ClaimedOffers, Long> {
    Optional<ClaimedOffers> findByOfferIdAndUserName(Long offerId, String userName);
}
