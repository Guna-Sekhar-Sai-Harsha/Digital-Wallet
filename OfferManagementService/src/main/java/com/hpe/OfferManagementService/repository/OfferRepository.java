package com.hpe.OfferManagementService.repository;

import com.hpe.OfferManagementService.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    List<Offer> findByBusinessId(Long businessId);

    List<Offer> findByIsActiveTrueAndEndDateAfter(LocalDateTime now);

    List<Offer> findAllByBusinessId(Long businessId);

    List<Offer> findAllByIsActiveTrueAndEndDateAfter(LocalDateTime now);
}
