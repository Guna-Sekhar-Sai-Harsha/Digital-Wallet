package com.hpe.TransactionService.service;


import com.hpe.TransactionService.dto.TransactionDTO;
import com.hpe.TransactionService.dto.TransactionResponse;
import com.hpe.TransactionService.model.TransactionDetails;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionServiceInterface {

    /*
        1. Creates a transaction and updates funds in Wallet Service.
        2. Input parameter is of type Transaction DTO.
        3. Checks a user exits or not by calling Wallet service using Webclient.
        4. If both exists, creates a transaction in DB and
            sends a request through Kafka to Wallet Service to update Balance based on type of transaction.
        5. Returns a type of TransactionResponse.
     */

    ResponseEntity<TransactionResponse> createTransaction(TransactionDTO transaction);
    /*
        1. Transfers funds between two users.
        2. Input parameter is of type Transaction DTO.
        3. First, creates a debit transaction for sender by calling createTransaction().
        4. Then, creates credit transaction for receiver by calling createTransaction().
        5. Asserts credit and debit transactions to not null.
        6. Returns a type of TransactionResponse.
     */
    ResponseEntity<TransactionResponse> transferFunds(TransactionDTO transaction);
    /*
        1. Find a transactions by transaction id.
        2. Input parameter is Transaction ID.
        3. Checks if transactions exits and returns transaction response or else null.
     */
    ResponseEntity<TransactionResponse> getTransactionById(Long transactionId);
    /*
        1. Find a list of all transactions by username.
        2. Input parameters are Username.
        3. Returns a list of transaction details.
     */
    ResponseEntity<Optional<List<TransactionDetails>>> getTransactionByUserName(String username);
    /*
        1. Find a list of transactions by username between two 2 dates.
        2. Input parameters are Username and dates
        3. Returns a list of transaction details.
     */
    ResponseEntity<Optional<List<TransactionDetails>>> findByUserNameAndCreatedBetween(String username, LocalDateTime startDate, LocalDateTime endDate);

    ResponseEntity<TransactionResponse> createSavingsTransaction();
}
