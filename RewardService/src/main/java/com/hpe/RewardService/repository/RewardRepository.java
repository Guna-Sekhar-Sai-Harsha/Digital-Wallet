package com.hpe.RewardService.repository;


import com.hpe.RewardService.model.Reward;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RewardRepository extends JpaRepository<Reward, String> {
}
