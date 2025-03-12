package com.hpe.ExpenseSharingService.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hpe.ExpenseSharingService.dto.ExpenseDTO;
import com.hpe.ExpenseSharingService.dto.ExpenseSharingExpenseResponse;
import com.hpe.ExpenseSharingService.dto.ExpenseSplitDTO;
import com.hpe.ExpenseSharingService.dto.PRequestDTO;
import com.hpe.ExpenseSharingService.model.ExpenseDetails;
import com.hpe.ExpenseSharingService.model.ExpenseSplitDetails;
import com.hpe.ExpenseSharingService.model.GroupBalance;
import com.hpe.ExpenseSharingService.repository.ExpenseRepository;
import com.hpe.ExpenseSharingService.repository.GroupRepository;
import com.hpe.ExpenseSharingService.utils.ExpenseSharingExpenseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ExpenseService implements ExpenseServiceInterface{


    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private GroupBalanceService groupBalanceService;
    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private KafkaTemplate<String, String> template;

    private ExpenseSplitDetails createExpenseSplit(String username, String amount, ExpenseDetails expense) {
        ExpenseSplitDetails split = new ExpenseSplitDetails();
        split.setUsername(username);
        split.setAmount(amount);
        split.setExpenseDetails(expense);
        return split;
    }


    @Override
    public ResponseEntity<ExpenseSharingExpenseResponse> createExpense(ExpenseDTO expenseDto) {

        if(groupRepository.existsById(expenseDto.getGroupId())){
            ExpenseDetails expenseDetails =new ExpenseDetails();

            expenseDetails.setGroupId(expenseDto.getGroupId());
            expenseDetails.setPaidBy(expenseDto.getPaidBy());
            expenseDetails.setAmount(expenseDto.getAmount());
            expenseDetails.setDescription(expenseDto.getDescription());

            Set<ExpenseSplitDetails> splits = new HashSet<>();
            for (ExpenseSplitDTO splitDTO : expenseDto.getExpenseSplitDetails()) {
                splits.add(createExpenseSplit(splitDTO.getUsername(), splitDTO.getAmount(), expenseDetails));
            }
            expenseDetails.setSplitDetails(splits);
            expenseDetails.setCreatedAt(LocalDateTime.now());
            ExpenseDetails savedExpenseDetails = expenseRepository.save(expenseDetails);
            groupBalanceService.updateGroupBalances(savedExpenseDetails);

            return new ResponseEntity<>(ExpenseSharingExpenseResponse.builder()
                    .responseCode(ExpenseSharingExpenseUtils.EXPENSE_CREATED_CODE)
                    .responseMessage(ExpenseSharingExpenseUtils.EXPENSE_CREATED_MESSAGE)
                    .expenseDetails(savedExpenseDetails)
                    .build(), HttpStatus.CREATED);
        }

        return new ResponseEntity<>(ExpenseSharingExpenseResponse.builder()
                .responseCode(ExpenseSharingExpenseUtils.EXPENSE_CREATED_UNSUCCESSFUL_CODE)
                .responseMessage(ExpenseSharingExpenseUtils.EXPENSE_CREATED_UNSUCCESSFUL_MESSAGE)
                .expenseDetails(null)
                .build(), HttpStatus.OK);
    }


    @Override
    public ResponseEntity<ExpenseSharingExpenseResponse> updateExpense(Long expenseId, ExpenseDTO expenseDto) {

        ExpenseDetails expenseDetails = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        groupBalanceService.reverseGroupBalances(expenseDetails);
        ExpenseDetails updatedExpense = createExpense(expenseDto).getBody().getExpenseDetails();

        if(updatedExpense!=null){
            return new ResponseEntity<>(ExpenseSharingExpenseResponse.builder()
                    .responseCode(ExpenseSharingExpenseUtils.EXPENSE_UPDATED_CODE)
                    .responseMessage(ExpenseSharingExpenseUtils.EXPENSE_UPDATED_MESSAGE)
                    .expenseDetails(updatedExpense)
                    .build(), HttpStatus.OK);
        }

        return new ResponseEntity<>(ExpenseSharingExpenseResponse.builder()
                .responseCode(ExpenseSharingExpenseUtils.EXPENSE_UPDATED_UNSUCCESSFUL_CODE)
                .responseMessage(ExpenseSharingExpenseUtils.EXPENSE_UPDATED_UNSUCCESSFUL_MESSAGE)
                .expenseDetails(null)
                .build(), HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<ExpenseSharingExpenseResponse> deleteExpense(Long expenseId) {
        ExpenseDetails expenseDetails = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        groupBalanceService.reverseGroupBalances(expenseDetails);
        expenseRepository.delete(expenseDetails);
        return new ResponseEntity<>(ExpenseSharingExpenseResponse.builder()
                .responseCode(ExpenseSharingExpenseUtils.EXPENSE_DELETED_CODE)
                .responseMessage(ExpenseSharingExpenseUtils.EXPENSE_DELETED_MESSAGE)
                .expenseDetails(null)
                .build(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ExpenseSharingExpenseResponse> getExpense(Long expenseId) {
        return new ResponseEntity<>(ExpenseSharingExpenseResponse.builder()
                .responseCode(ExpenseSharingExpenseUtils.EXPENSE_FETCH_CODE)
                .responseMessage(ExpenseSharingExpenseUtils.EXPENSE_FETCH_MESSAGE)
                .expenseDetails(expenseRepository.findById(expenseId).orElse(null))
                .build(), HttpStatus.OK);
    }
    @Override
    public ResponseEntity<ExpenseSharingExpenseResponse> sendPaymentRemainder(String OwedOwner, String OwesOwner, String amount){

        PRequestDTO pRequestDTO = new PRequestDTO();
        pRequestDTO.setPayeeUserName(OwedOwner);
        pRequestDTO.setPayerUserName(OwesOwner);
        pRequestDTO.setAmount(amount);
        pRequestDTO.setCurrency("USD");
        pRequestDTO.setDescription("Payment for expense owd to "+OwedOwner);

        template.send("ExpenseRequest", JsonToString(pRequestDTO));
        return new ResponseEntity<>(ExpenseSharingExpenseResponse.builder()
                .responseCode(ExpenseSharingExpenseUtils.PAYMENT_REQUEST_SENT_CODE)
                .responseMessage(ExpenseSharingExpenseUtils.PAYMENT_REQUEST_SENT_MESSAGE)
                .expenseDetails(null)
                .build(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ExpenseSharingExpenseResponse> settleBetweenUsers(Long groupId, String OwedOwner, String OwesOwner, String amount) {

        return createExpense(ExpenseDTO.builder()
                .description("Settlement")
                .amount(amount)
                .paidBy(OwesOwner)
                .participants(Arrays.asList(OwedOwner, OwesOwner))
                .expenseSplitDetails(Arrays.asList(new ExpenseSplitDTO[]{ExpenseSplitDTO.builder()
                        .username(OwedOwner)
                        .amount(amount)
                        .build()}))
                .build());
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



