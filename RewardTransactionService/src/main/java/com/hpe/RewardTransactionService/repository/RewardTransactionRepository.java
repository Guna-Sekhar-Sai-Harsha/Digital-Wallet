package com.hpe.RewardTransactionService.repository;

import com.hpe.RewardTransactionService.model.RewardTransactions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RewardTransactionRepository extends JpaRepository<RewardTransactions, Long> {

}
