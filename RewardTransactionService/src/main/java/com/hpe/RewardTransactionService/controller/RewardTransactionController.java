package com.hpe.RewardTransactionService.controller;


import com.google.gson.Gson;
import com.hpe.RewardTransactionService.dto.RewardDTO;
import com.hpe.RewardTransactionService.dto.RewardTransactionResponse;
import com.hpe.RewardTransactionService.model.RewardTransactions;
import com.hpe.RewardTransactionService.service.RewardTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rewardTransaction")
public class RewardTransactionController {

    @Autowired
    RewardTransactionService rewardTransactionService;

    @PostMapping("/create")
    public ResponseEntity<RewardTransactionResponse> createRewardTransaction(@RequestBody RewardDTO rewardDTO){
        return rewardTransactionService.createRewardTransaction(rewardDTO);
    }

    @GetMapping("getReward/{id}")
    public ResponseEntity<RewardTransactionResponse> getRewardById(@PathVariable Long id){
        return rewardTransactionService.getRewardById(id);
    }

    @KafkaListener(topics = "RewardOffers", groupId = "RewardOffers-group")
    public ResponseEntity<RewardTransactionResponse> createRewardOffersTransaction(String rewardDTOString){
        try {
            Gson gson = new Gson();
            RewardDTO rewardDTO = gson.fromJson(rewardDTOString, RewardDTO.class);
            return rewardTransactionService.createRewardTransaction(rewardDTO);
        }catch (Exception e){
            System.out.println(e);
        }
        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }
}
