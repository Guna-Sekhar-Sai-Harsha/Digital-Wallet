package com.hpe.ExpenseSharingService.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hpe.ExpenseSharingService.dto.NotificationDTO;
import com.hpe.ExpenseSharingService.model.ExpenseDetails;
import com.hpe.ExpenseSharingService.model.ExpenseSplitDetails;
import com.hpe.ExpenseSharingService.model.GroupBalance;
import com.hpe.ExpenseSharingService.repository.GroupBalanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class GroupBalanceService implements GroupBalanceServiceInterface{

    @Autowired
    private GroupBalanceRepository groupBalanceRepository;

    @Autowired
    private KafkaTemplate<String, String> template;

    @Override
    public void updateGroupBalances(ExpenseDetails expense) {
        String paidBy = expense.getPaidBy();

        for (ExpenseSplitDetails split : expense.getSplitDetails()) {
            if (!split.getUsername().equals(paidBy)) {
                updateGroupBalance(expense.getGroupId(), paidBy, split.getUsername(), split.getAmount());

                NotificationDTO notificationDTO = new NotificationDTO();
                notificationDTO.setUserName(split.getUsername());
                String notificationMessage = "You owe "+paidBy+" USD "+ split.getAmount()+" for "+expense.getDescription();
                notificationDTO.setMessage(notificationMessage);
                notificationDTO.setType("Push");
                template.send("Notification", JsonToString(notificationDTO));

                log.info("Group Balance is successfully updated! and notification '{}' is successfully sent.", notificationMessage);
            }
        }
    }
    @Override
    public void reverseGroupBalances(ExpenseDetails expense) {
        String paidBy = expense.getPaidBy();

        for (ExpenseSplitDetails split : expense.getSplitDetails()) {
            if (!split.getUsername().equals(paidBy)) {
                updateGroupBalance(expense.getGroupId(), split.getUsername(), paidBy, split.getAmount());

                log.info("Group Balance is successfully reversed fot the expense {}", expense.toString());
            }
        }
    }

    @Override
    public void updateGroupBalance(Long groupId, String userOwed, String userOwes, String amount) {
        GroupBalance balance = (GroupBalance) groupBalanceRepository.findByGroupIdAndUserOwesAndUserOwed(groupId, userOwes, userOwed)
                .orElse(GroupBalance.builder()
                        .groupId(groupId)
                        .userOwes(userOwes)
                        .userOwed(userOwed)
                        .amount("0.00").build());

        balance.setAmount(String.valueOf(
                Double.parseDouble(balance.getAmount())
                        + Double.parseDouble(amount)));

        groupBalanceRepository.save(balance);


        GroupBalance reverseBalance = (GroupBalance) groupBalanceRepository.findByGroupIdAndUserOwesAndUserOwed(groupId, userOwed, userOwes)
                .orElse(GroupBalance.builder()
                        .groupId(groupId)
                        .userOwes(userOwes)
                        .userOwed(userOwed)
                        .amount("0.00").build());

        reverseBalance.setAmount(String.valueOf(
                Double.parseDouble(balance.getAmount())
                        + Double.parseDouble(amount)));
        groupBalanceRepository.save(reverseBalance);
    }

    @Override
    public ResponseEntity<List<GroupBalance>> getGroupBalances(Long groupId) {
        return new ResponseEntity<>(groupBalanceRepository.findAllByGroupId(groupId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<GroupBalance>> getAllBalancesUserOwes(String userName) {
        return new ResponseEntity<>(groupBalanceRepository.findAllByUserOwes(userName), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<GroupBalance>> getAllBalancesUserOwed(String userName) {
        return new ResponseEntity<>(groupBalanceRepository.findAllByUserOwed(userName), HttpStatus.OK);
    }

    public GroupBalance getBalanceOwedToUser(Long groupId, String OwedOwner, String OwesOwner){
        return groupBalanceRepository.findAllByGroupId(groupId)
                .stream()
                .filter(groupBalance -> groupBalance.getUserOwes().equals(OwesOwner) && groupBalance.getUserOwed().equals(OwedOwner))
                .findFirst().orElse(null);
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
