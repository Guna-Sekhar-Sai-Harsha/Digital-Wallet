package com.hpe.RewardTransactionService.service;

import com.hpe.RewardTransactionService.dto.RewardDTO;
import com.hpe.RewardTransactionService.dto.RewardTransactionResponse;
import com.hpe.RewardTransactionService.model.RewardTransactions;
import org.springframework.http.ResponseEntity;

public interface RewardTransactionServiceInterface {

    /*
        1. Creates a Reward Transaction
        2. Triggered By Service and Input Parameter is of type RewardDTO
        3. Updates the Rewards of User in Reward Service through Kafka
        4. Once completed, Sends notification to user
        5. Return of type RewardTransactionResponse.
     */
    ResponseEntity<RewardTransactionResponse> createRewardTransaction(RewardDTO rewardDTO);

    /*
        1. Gets information about  a Reward Transaction and Input Parameter is of transaction ID.
        2. Return of type RewardTransactionResponse with username and points w.r.t that transaction else null.
     */
    ResponseEntity<RewardTransactionResponse> getRewardById(Long id);
}
