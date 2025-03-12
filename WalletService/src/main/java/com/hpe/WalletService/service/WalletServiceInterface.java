package com.hpe.WalletService.service;


import com.hpe.WalletService.dto.WalletDTO;
import com.hpe.WalletService.dto.WalletResponse;
import org.springframework.http.ResponseEntity;

public interface WalletServiceInterface {

    /*
    1. Check if a wallet exists by username
    2. If No, Creates a wallet, Sends a notification and returns Wallet DTO.
    3. If Yes, sends account exists response
     */
    ResponseEntity<WalletResponse> createWallet(WalletDTO walletDTO);
    /*
        1. Fetches the balance of user from Wallet DB.
        2. Input Parameter is Username.
        3. Return of type WalletResponse.
     */
    ResponseEntity<WalletResponse> getBalance(String userName);
    /*
        1. Add funds to User Wallet.
        2. Fetches the wallet details of user from Wallet DB and add funds to wallet.
        3. Input Parameter is of type WalletDTO.
        4. Return of type WalletResponse with updated Balance.
     */
    ResponseEntity<WalletResponse> addFunds(WalletDTO walletDTO);
    /*
        1. Withdraw funds from User Wallet.
        2. Fetches the wallet details of user from Wallet DB and checks the funds are sufficient or not.
        3. If yes, withdraw funds from wallet and updates the wallet.
        4. Input Parameter is of type WalletDTO.
        5. Return of type WalletResponse with updated Balance.
     */
    ResponseEntity<WalletResponse> withdrawFunds(WalletDTO walletDTO);
    /*
        1. Checks if user has wallet or not.
        2. Input Parameter is Username.
        3. Returns Boolean.
     */
    Boolean existsByUsername(String username);
}
