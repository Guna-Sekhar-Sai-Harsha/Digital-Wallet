package com.hpe.TransactionService.model;

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
@Table(name="TransactionDetails")
public class TransactionDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String walletUserName;
    private String counterPartyUserName;
    private String amount;
    private String currency;
    private String description;
    private String type;
    private String status;
    @Column(updatable = false)
    private LocalDateTime created;
}
