package com.hpe.OfferManagementService.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ClaimedOffer")
public class ClaimedOffers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long claimId;

    private Long offerId;
    private String userName;
    private boolean isClaimed;
    private LocalDateTime appliedAt;
    private LocalDateTime claimedAt;
}
