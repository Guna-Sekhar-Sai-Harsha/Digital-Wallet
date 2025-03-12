package com.hpe.RewardTransactionService.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hpe.RewardTransactionService.dto.NotificationDTO;
import com.hpe.RewardTransactionService.dto.RewardDTO;
import com.hpe.RewardTransactionService.dto.RewardTransactionResponse;
import com.hpe.RewardTransactionService.model.RewardTransactions;
import com.hpe.RewardTransactionService.repository.RewardTransactionRepository;
import com.hpe.RewardTransactionService.utils.RewardTransactionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RewardTransactionService implements RewardTransactionServiceInterface{



    @Autowired
    private RewardTransactionRepository rewardTransactionRepository;

    @Autowired
    private KafkaTemplate<String, String> template;

    @Override
    public ResponseEntity<RewardTransactionResponse> createRewardTransaction(RewardDTO rewardDTO){

            RewardTransactions rewardTransactions = new RewardTransactions();
            rewardTransactions.setUserName(rewardDTO.getUserName());
            rewardTransactions.setPoints(rewardDTO.getPoints());
            rewardTransactions.setRewardType(rewardDTO.getRewardType());
            rewardTransactions.setCreatedAt(LocalDateTime.now());
            RewardTransactions savedTransaction = rewardTransactionRepository.save(rewardTransactions);
            rewardDTO.setRewardType("Credit");
            template.send("RewardTransaction", JsonToString(rewardDTO));

            NotificationDTO notificationDTO = new NotificationDTO();
            notificationDTO.setUserName(savedTransaction.getUserName());
            notificationDTO.setMessage("You have received a Reward of "+ savedTransaction.getPoints()+ "Points");
            notificationDTO.setType("Push");

            template.send("Notification", JsonToString(notificationDTO));

            return new ResponseEntity<>(RewardTransactionResponse.builder()
                    .responseCode(RewardTransactionUtils.REWARD_CREATED_CODE)
                    .responseMessage(RewardTransactionUtils.REWARD_CREATED_MESSAGE)
                    .username(savedTransaction.getUserName())
                    .points(String.valueOf(savedTransaction.getPoints()))
                    .build(), HttpStatus.OK);

    }

    @Override
    public ResponseEntity<RewardTransactionResponse> getRewardById(Long id){

        if(rewardTransactionRepository.existsById(id)){
            RewardTransactions savedTransaction = rewardTransactionRepository.getById(id);
            return new ResponseEntity<>(RewardTransactionResponse.builder()
                    .responseCode(RewardTransactionUtils.REWARD_EXISTS_CODE)
                    .responseMessage(RewardTransactionUtils.REWARD_EXISTS_MESSAGE)
                    .username(savedTransaction.getUserName())
                    .points(String.valueOf(savedTransaction.getPoints()))
                    .build(), HttpStatus.OK);

        }else {
            return new ResponseEntity<>(RewardTransactionResponse.builder()
                    .responseCode(RewardTransactionUtils.REWARD_DO_NOT_EXISTS_CODE)
                    .responseMessage(RewardTransactionUtils.REWARD_DO_NOT_EXISTS_MESSAGE)
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
