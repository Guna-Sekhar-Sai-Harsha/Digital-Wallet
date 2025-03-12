package com.hpe.TransactionService.controller;


import com.google.gson.Gson;
import com.hpe.TransactionService.dto.TransactionDTO;
import com.hpe.TransactionService.dto.TransactionResponse;
import com.hpe.TransactionService.model.TransactionDetails;
import com.hpe.TransactionService.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @PostMapping("/createTransaction")
    public ResponseEntity<TransactionResponse> createTransaction(@RequestBody TransactionDTO transaction) {
        return transactionService.createTransaction(transaction);
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transferFunds(@RequestBody TransactionDTO transaction) {
        return transactionService.transferFunds(transaction);
    }

    @GetMapping("id/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Long transactionId) {
        return transactionService.getTransactionById(transactionId);
    }

    @GetMapping("/{username}")
    public ResponseEntity<Optional<List<TransactionDetails>>> getTransactionByUserName(@PathVariable String username){
        return transactionService.getTransactionByUserName(username);
    }

    @GetMapping("/statement/{userName}")
    public ResponseEntity<Optional<List<TransactionDetails>>> getTransactionsByUserNameAndDateRange(@PathVariable String userName,
                                                                                                    @RequestParam String startDate,
                                                                                                    @RequestParam String endDate) {
        return transactionService.findByUserNameAndCreatedBetween(userName, LocalDate.parse(startDate).atStartOfDay(), LocalDate.parse(endDate).atStartOfDay());
    }

    @KafkaListener(topics = "Transaction",groupId = "Transaction-group")
    public ResponseEntity<TransactionResponse> RedeemFunds(String transactionString) {
        try {
            Gson gson = new Gson();
            TransactionDTO transaction = gson.fromJson(transactionString, TransactionDTO.class);
            return transactionService.transferFunds(transaction);
        }catch (Exception e){
            System.out.println(e);
        }
        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }

    @KafkaListener(topics = "SavingsTransaction", groupId = "SavingsTransaction-group")
    public ResponseEntity<TransactionResponse> createSavingsTransaction(String transactionString){
        try {
            Gson gson = new Gson();
            TransactionDTO transaction = gson.fromJson(transactionString, TransactionDTO.class);
            System.out.println(transaction.toString());
            return transactionService.transferFunds(transaction);
        }catch (Exception e){
            System.out.println(e);
        }
        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }

    @KafkaListener(topics = "PaymentsRequestTransaction", groupId = "PaymentsRequestTransaction-group")
    public ResponseEntity<TransactionResponse> createPaymentsRequestTransaction(String transactionString){
        try {
            Gson gson = new Gson();
            TransactionDTO transaction = gson.fromJson(transactionString, TransactionDTO.class);
            System.out.println(transaction.toString());
            return transactionService.transferFunds(transaction);
        }catch (Exception e){
            System.out.println(e);
        }
        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }



}
