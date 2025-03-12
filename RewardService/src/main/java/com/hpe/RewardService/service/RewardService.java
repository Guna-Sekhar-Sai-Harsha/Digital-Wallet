package com.hpe.RewardService.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hpe.RewardService.dto.NotificationDTO;
import com.hpe.RewardService.dto.RewardDTO;
import com.hpe.RewardService.dto.RewardResponse;
import com.hpe.RewardService.dto.TransactionDTO;
import com.hpe.RewardService.model.Reward;
import com.hpe.RewardService.repository.RewardRepository;
import com.hpe.RewardService.utils.RewardUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RewardService implements RewardServiceInterface{

    @Autowired
    private RewardRepository rewardRepository;

    @Autowired
    private KafkaTemplate<String, String> template;


    @Override
    public ResponseEntity<RewardResponse> createRewardForUser(String userName){

        Reward createdRewardAccount = rewardRepository.save(Reward.builder()
                .userName(userName)
                .points(0)
                .updatedAt(LocalDateTime.now())
                .build());

        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setUserName(createdRewardAccount.getUserName());
        notificationDTO.setMessage(RewardUtils.REWARD_ACCOUNT_CREATED_MESSAGE+".\nThe Reward Account ID is : "+createdRewardAccount.getUserName());
        notificationDTO.setType("Push");

        template.send("Notification", JsonToString(notificationDTO));

        return new ResponseEntity<>(RewardResponse.builder()
                .responseCode(RewardUtils.REWARD_ACCOUNT_Created_CODE)
                .responseMessage(RewardUtils.REWARD_ACCOUNT_CREATED_MESSAGE)
                .username(createdRewardAccount.getUserName())
                .points(String.valueOf(createdRewardAccount.getPoints()))
                .build(), HttpStatus.OK);

    }

    @Override
    public ResponseEntity<RewardResponse> getRewardByUser(String userName){


        if(rewardRepository.existsById(userName)){
            Reward userRewards = rewardRepository.getReferenceById(userName);
            return new ResponseEntity<>(RewardResponse.builder()
                    .responseCode(RewardUtils.REWARD_ACCOUNT_FOUND_CODE)
                    .responseMessage(RewardUtils.REWARD_ACCOUNT_FOUND_MESSAGE)
                    .username(userName)
                    .points(String.valueOf(userRewards.getPoints()))
                    .build(), HttpStatus.OK);
        }else {
            return new ResponseEntity<>(RewardResponse.builder()
                    .responseCode(RewardUtils.REWARD_ACCOUNT_NOT_FOUND_CODE)
                    .responseMessage(RewardUtils.REWARD_ACCOUNT_NOT_FOUND_MESSAGE)
                    .username(null)
                    .points(null)
                    .build(), HttpStatus.OK);
        }

    }

    @Override
    public ResponseEntity<RewardResponse> updateRewards(RewardDTO rewardDTO){


            if(rewardRepository.existsById(rewardDTO.getUserName())){
                Reward reward = rewardRepository.getReferenceById(rewardDTO.getUserName());
                if(rewardDTO.getRewardType().equals("Credit")){
                    reward.setPoints(reward.getPoints()+rewardDTO.getPoints());
                } else if ((rewardDTO.getRewardType().equals("Debit")) && (reward.getPoints()<= rewardDTO.getPoints())) {
                    reward.setPoints(reward.getPoints()-rewardDTO.getPoints());
                }
                reward.setUpdatedAt(LocalDateTime.now());
                Reward updateRewardAccount = rewardRepository.save(reward);
                return new ResponseEntity<>(RewardResponse.builder()
                        .responseCode(RewardUtils.REWARD_ACCOUNT_UPDATED_CODE)
                        .responseMessage(RewardUtils.REWARD_ACCOUNT_UPDATED_MESSAGE)
                        .username(rewardDTO.getUserName())
                        .points(String.valueOf(updateRewardAccount.getPoints()))
                        .build(), HttpStatus.OK);
            }



        return new ResponseEntity<>(RewardResponse.builder()
                    .responseCode(RewardUtils.REWARD_ACCOUNT_NOT_FOUND_CODE)
                    .responseMessage(RewardUtils.REWARD_ACCOUNT_NOT_FOUND_MESSAGE)
                    .username(null)
                    .points(null)
                    .build(), HttpStatus.OK);


    }

    @Override
    public ResponseEntity<RewardResponse> redeemRewards(String userName){

        if(rewardRepository.existsById(userName)){
            Reward reward = rewardRepository.getReferenceById(userName);
            TransactionDTO transactionDTO = new TransactionDTO();
            transactionDTO.setWalletUserName("Rewards");
            transactionDTO.setCounterPartyUserName(reward.getUserName());
            transactionDTO.setAmount(String.valueOf(reward.getPoints()/1000));
            transactionDTO.setCurrency("USD");
            transactionDTO.setType("Credit");
            transactionDTO.setDescription("Rewards Redeemed");

            template.send("Transaction", JsonToString(transactionDTO));
            reward.setPoints(0);
            reward.setUpdatedAt(LocalDateTime.now());
            rewardRepository.save(reward);

            return new ResponseEntity<>(RewardResponse.builder()
                    .responseCode(RewardUtils.REWARD_REDEEMED_CODE)
                    .responseMessage(RewardUtils.REWARD_REDEEMED_MESSAGE)
                    .username(userName)
                    .points(String.valueOf(0))
                    .build(), HttpStatus.OK);

        }else{
            return new ResponseEntity<>(RewardResponse.builder()
                    .responseCode(RewardUtils.REWARD_ACCOUNT_NOT_FOUND_CODE)
                    .responseMessage(RewardUtils.REWARD_ACCOUNT_NOT_FOUND_MESSAGE)
                    .username(null)
                    .points(null)
                    .build(), HttpStatus.OK);
        }

    }

    public String JsonToString(Object objectDTO){

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String sendNotification = objectMapper.writeValueAsString(objectDTO);
            return sendNotification;
        }catch (Exception e){
            System.out.println(e);
        }
        return null;

    }
}
