package com.hpe.RewardService.service;

import com.hpe.RewardService.dto.RewardDTO;
import com.hpe.RewardService.dto.RewardResponse;
import org.springframework.http.ResponseEntity;

public interface RewardServiceInterface {

    /*
        1. Creates a Reward Wallet for a user
        2. Triggered By User Service when creating a user and Input Parameter is Username.
        3. Once completed, Sends notification to user
        5. Return of type RewardResponse.
     */
    ResponseEntity<RewardResponse> createRewardForUser(String userName);
    /*
        1. Gets information about a Rewards by User and Input Parameter is Username.
        2. Return of type RewardResponse with username and points w.r.t that username (if exits else null).
     */
    ResponseEntity<RewardResponse> getRewardByUser(String userName);
    /*
        1. Redeem rewards for a User and Input Parameter is Username.
        2. Creates a Transaction to add rewards to wallet.
        3. Sends a TransactionDTO to transaction Service through Kafka.
        4. Updates the Rewards for a user by calling updateRewards().
        5. Return of type RewardResponse with username and points as 0 if exits else null.
     */
    ResponseEntity<RewardResponse> redeemRewards(String userName);
    /*
        1. Updates rewards for a User and Input Parameter is of type RewardDTO.
        2. RewardDTO type should be either "Credit" or "Debit".
        3. Receives a update request through Kafka from Reward Transaction Service or function call from redeemRewards().
        4. Return of type RewardResponse with username and updated points if user exits else null.
     */
    ResponseEntity<RewardResponse> updateRewards(RewardDTO rewardDTO);
}
