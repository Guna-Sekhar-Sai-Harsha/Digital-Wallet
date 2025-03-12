package com.hpe.RewardTransactionService.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "RewardTransactions")
public class RewardTransactions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String userName;
    @Column(nullable = false)
    private String rewardType;
    @Column(nullable = false)
    private int points;
    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
