package com.hpe.SavingsService.service;


import com.hpe.SavingsService.dto.SavingsDTO;
import com.hpe.SavingsService.dto.SavingsResponse;
import com.hpe.SavingsService.model.SavingsDetails;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SavingsServiceInterface {

    /*
        1. Creates a cash deposit for a user
        2. First, checks if user exits or not and balance by calling Wallet Service.
        3. If exists and has sufficient balance, transfers money from wallet to savings account by using Transaction Service.
        4. Sends a notification upon completion.
        5. Input parameter is of type SavingsDTO.
        6. Returns a Savings Response.
     */

    ResponseEntity<SavingsResponse> createCashDeposit(SavingsDTO savingsDto);

    /*
        1. Creates a savings account for a user
        2. First, checks if user for exits or not by calling Wallet Service.
        3. If exists, checks user has savings Account, if not creates a savings DB.
        4. Sends a notification upon completion.
        5. Input parameter is of type SavingsDTO.
        6. Returns a Savings Response.
     */
    ResponseEntity<SavingsResponse> createSavingsAccounts(SavingsDTO savingsDto);
    /*
        1. Withdraw funds from Cash Deposit (CD).
        2. Checks if CD exists or not, then checks if today's date is after maturity date and status.
        3. Then update the balance by adding amount and all interests.
        4. Then transfers money to user's wallet from savings account by using Transaction Service.
        5. Marks the CD with given id in savings DB as completed
        6. Sends a notification upon completion.
        7. Input parameter is of type SavingsDTO.
        8. Returns a Savings Response.
     */
    ResponseEntity<SavingsResponse> withdrawCashDeposit(Long id);
    /*
        1. Withdraw funds from Savings Account.
        2. Checks if user has savings Account
        3. Fetches the savings Account details of user from savings DB and checks the requested funds are sufficient or not.
        4. If exists and has sufficient balance, transfers money to wallet from savings account by using Transaction Service.
        5. Sends a notification upon completion.
        6. Input parameter is of type SavingsDTO.
        7. Returns a Savings Response.
     */
    ResponseEntity<SavingsResponse> withdrawSavingsAmount(SavingsDTO savingsDto);
    /*
        1. Add funds to Savings Account.
        2. First, checks if user has savings Account or not and balance by calling Wallet Service.
        3. If exists and has sufficient balance, transfers money from wallet to savings account by using Transaction Service.
        4. Then updates the Savings DB.
        5. Sends a notification upon completion.
        6. Input parameter is of type SavingsDTO.
        7. Returns a Savings Response.
     */

    ResponseEntity<SavingsResponse> AddFundsToSavingsAmount(SavingsDTO savingsDto);

    /*
        1. Returns a list of all cash deposits and savings account for a user.
        2. Input parameter is username.
     */
    ResponseEntity<List<SavingsDetails>> getAllSavingsByUsername(String userName);
}
