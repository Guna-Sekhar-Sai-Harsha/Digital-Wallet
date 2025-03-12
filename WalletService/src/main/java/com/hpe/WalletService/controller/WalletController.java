package com.hpe.WalletService.controller;



import com.google.gson.Gson;
import com.hpe.WalletService.dto.WalletDTO;
import com.hpe.WalletService.dto.WalletResponse;
import com.hpe.WalletService.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @PostMapping("/createWallet")
    public ResponseEntity<WalletResponse> createWallet(@RequestBody WalletDTO walletDTO){
        return walletService.createWallet(walletDTO);
    }

    @GetMapping("/balance/{userName}")
    public ResponseEntity<WalletResponse> getBalance(@PathVariable String userName){
        return walletService.getBalance(userName);
    }

    @KafkaListener(topics = "AddFunds",groupId = "wallet-group1")
    public ResponseEntity<WalletResponse> addFunds(String walletDTOString){
        try {
            Gson gson = new Gson();
            WalletDTO walletDTO = gson.fromJson(walletDTOString, WalletDTO.class);
            return walletService.addFunds(walletDTO);
        }catch (Exception e){
            System.out.println(e);
        }
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @KafkaListener(topics = "WithdrawFunds",groupId = "wallet-group2")
    public ResponseEntity<WalletResponse> withdrawFunds(String walletDTOString){
        try {
            Gson gson = new Gson();
            WalletDTO walletDTO = gson.fromJson(walletDTOString, WalletDTO.class);
            return walletService.withdrawFunds(walletDTO);
        }catch (Exception e){
            System.out.println(e);
        }
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/checkuser/{username}")
    public Boolean existsByUsername(@PathVariable String username){
        return walletService.existsByUsername(username);
    }

}
