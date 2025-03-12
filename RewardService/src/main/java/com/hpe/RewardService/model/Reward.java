package com.hpe.RewardService.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@Table(name = "Rewards")
public class Reward {

    @Id
    @Column(nullable = false)
    private String userName;
    @Column(nullable = false)
    private int points;
    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
