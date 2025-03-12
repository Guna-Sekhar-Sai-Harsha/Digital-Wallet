package com.hpe.RewardService.controller;


import com.google.gson.Gson;
import com.hpe.RewardService.dto.RewardDTO;
import com.hpe.RewardService.dto.RewardResponse;
import com.hpe.RewardService.service.RewardServiceInterface;
import com.hpe.RewardService.utils.RewardUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rewards")
public class RewardController {

    @Autowired
    private RewardServiceInterface rewardService;

    @PostMapping("/createuser/{userName}")
    public ResponseEntity<RewardResponse> createRewardForUser(@PathVariable String userName){
        return rewardService.createRewardForUser(userName);
    }

    @GetMapping("/user/{userName}")
    public ResponseEntity<RewardResponse> getRewardByUser(@PathVariable String userName){
        return rewardService.getRewardByUser(userName);
    }

    @PostMapping("/redeemReward/{userName}")
    public ResponseEntity<RewardResponse> redeemRewards(@PathVariable String userName){
        return rewardService.redeemRewards(userName);
    }
    @KafkaListener(topics = "RewardTransaction",groupId = "Reward-group")
    public ResponseEntity<RewardResponse> updateRewards(String rewardDTOString){
        try {
            Gson gson = new Gson();
            RewardDTO rewardDTO = gson.fromJson(rewardDTOString, RewardDTO.class);
            return rewardService.updateRewards(rewardDTO);
        }catch (Exception e){
            System.out.println(e);
        }

       return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
