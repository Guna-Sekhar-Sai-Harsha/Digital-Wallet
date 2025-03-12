package com.hpe.SavingsService.controller;



import com.hpe.SavingsService.dto.SavingsDTO;
import com.hpe.SavingsService.dto.SavingsResponse;
import com.hpe.SavingsService.model.SavingsDetails;
import com.hpe.SavingsService.service.SavingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/savings")
public class SavingsController {

    @Autowired
    private SavingsService savingsService;

    @PostMapping("/cashDeposit")
    public ResponseEntity<SavingsResponse> createCashDeposit(@RequestBody SavingsDTO savingsDto){
        return savingsService.createCashDeposit(savingsDto);
    }
    @PostMapping("/createSavingsAccount")
    public ResponseEntity<SavingsResponse> createSavingsAccount(@RequestBody SavingsDTO savingsDto){
        return savingsService.createSavingsAccounts(savingsDto);
    }
    @PostMapping("/withdrawCD/{id}")
    public ResponseEntity<SavingsResponse> WithdrawCashDeposit(@PathVariable Long id){
        return savingsService.withdrawCashDeposit(id);
    }
    @PostMapping("/withdrawSavings")
    public ResponseEntity<SavingsResponse> WithdrawSavingsAmount(@RequestBody SavingsDTO savingsDto){
        return savingsService.withdrawSavingsAmount(savingsDto);
    }
    @PostMapping("/AddSavings")
    public ResponseEntity<SavingsResponse> AddFundsToSavingsAmount(@RequestBody SavingsDTO savingsDto){
        return savingsService.AddFundsToSavingsAmount(savingsDto);
    }
    @GetMapping("/get/{userName}")
    public ResponseEntity<List<SavingsDetails>> getAllSavings(@PathVariable String userName){
        return savingsService.getAllSavingsByUsername(userName);
    }


}
