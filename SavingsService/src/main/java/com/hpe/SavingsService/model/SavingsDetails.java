package com.hpe.SavingsService.model;

import com.hpe.SavingsService.dto.SavingsType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;



@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="SavingsDetails")
public class SavingsDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;
    private String amount;
    private String currency;
    private SavingsType savingsType;
    private Integer goal;
    private String description;
    private Integer apr;
    private String interestReceived;
    private String interestPending;
    @Column(nullable = false)
    private Boolean canWithdraw;
    private String status;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime ModifiedAt;
    private LocalDate maturityDate;
}
