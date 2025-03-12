package com.hpe.WalletService.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hpe.WalletService.dto.NotificationDTO;
import com.hpe.WalletService.dto.WalletDTO;
import com.hpe.WalletService.dto.WalletResponse;
import com.hpe.WalletService.model.WalletDetails;
import com.hpe.WalletService.repository.WalletRepository;
import com.hpe.WalletService.utils.WalletUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@EnableKafka
public class WalletService implements WalletServiceInterface{

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    private KafkaTemplate<String, String> template;
    @Override
    public ResponseEntity<WalletResponse> createWallet(WalletDTO walletDTO){

        WalletResponse walletResponse=new WalletResponse();
        if(walletRepository.existsByUsername(walletDTO.getUsername())){
            walletResponse.setResponseCode(WalletUtils.WALLET_EXISTS_CODE);
            walletResponse.setResponseMessage(WalletUtils.WALLET_EXISTS_MESSAGE);
            walletResponse.setWalletDTO(null);
            return new ResponseEntity<>(walletResponse, HttpStatus.FORBIDDEN);
        }

        WalletDetails walletDetails = new WalletDetails();
        walletDetails.setUsername(walletDTO.getUsername());
        walletDetails.setBalance("0.00");
        walletDetails.setCurrency(walletDTO.getCurrency());
        walletDetails.setCreatedAt(LocalDateTime.now());
        walletDetails.setModifiedAt(LocalDateTime.now());

        WalletDetails savedWallet = walletRepository.save(walletDetails);

        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setUserName(savedWallet.getUsername());
        notificationDTO.setMessage(WalletUtils.Wallet_Created_MESSAGE+".\nThe Wallet ID is : "+savedWallet.getUsername());
        notificationDTO.setType("Push");

        template.send("Notification", JsonToString(notificationDTO));

        walletResponse.setResponseCode(WalletUtils.Wallet_Created_CODE);
        walletResponse.setResponseMessage(WalletUtils.Wallet_Created_MESSAGE);
        walletResponse.setWalletDTO(WalletDTO.builder()
                        .username(savedWallet.getUsername())
                        .balance(savedWallet.getBalance())
                        .Currency(savedWallet.getCurrency())
                .build());

        return new ResponseEntity<>(walletResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<WalletResponse> getBalance(String userName){

        WalletDetails wallet = walletRepository.findByUsername(userName);
        WalletResponse walletResponse=new WalletResponse();
        walletResponse.setResponseCode(WalletUtils.Wallet_BALANCE_FETCHED_CODE);
        walletResponse.setResponseMessage(WalletUtils.Wallet_BALANCE_FETCHED_MESSAGE);
        walletResponse.setWalletDTO(WalletDTO.builder()
                .username(wallet.getUsername())
                .balance(wallet.getBalance())
                .Currency(wallet.getCurrency())
                .build());

        return new ResponseEntity<>(walletResponse, HttpStatus.OK);

    }

    @Override
    public ResponseEntity<WalletResponse> addFunds(WalletDTO walletDTO){
        WalletDetails wallet = walletRepository.findByUsername(walletDTO.getUsername());

        wallet.setBalance(String.valueOf(Double.parseDouble(wallet.getBalance())+Double.parseDouble(walletDTO.getBalance())));

        WalletDetails savedWallet = walletRepository.save(wallet);

        WalletResponse walletResponse=new WalletResponse();
        walletResponse.setResponseCode(WalletUtils.Wallet_BALANCE_UPDATED_CODE);
        walletResponse.setResponseMessage(WalletUtils.Wallet_BALANCE_UPDATED_MESSAGE);
        walletResponse.setWalletDTO(WalletDTO.builder()
                .username(savedWallet.getUsername())
                .balance(savedWallet.getBalance())
                .Currency(savedWallet.getCurrency())
                .build());

        return new ResponseEntity<>(walletResponse, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<WalletResponse> withdrawFunds(WalletDTO walletDTO){
        WalletResponse walletResponse=new WalletResponse();
        WalletDetails wallet = walletRepository.findByUsername(walletDTO.getUsername());
        double previous_balance = Double.parseDouble(wallet.getBalance());
        double requested_amount = Double.parseDouble(walletDTO.getBalance());
        if(previous_balance < requested_amount){
            walletResponse.setResponseCode(WalletUtils.Wallet_BALANCE_INSUFFICIENT_CODE);
            walletResponse.setResponseMessage(WalletUtils.Wallet_BALANCE_INSUFFICIENT_MESSAAGE);
            walletResponse.setWalletDTO(WalletDTO.builder()
                    .username(wallet.getUsername())
                    .balance(wallet.getBalance())
                    .Currency(wallet.getCurrency())
                    .build());
            return new ResponseEntity<>(walletResponse, HttpStatus.FORBIDDEN);
        }

        wallet.setBalance(String.valueOf(previous_balance - requested_amount));
        WalletDetails savedWallet = walletRepository.save(wallet);

        walletResponse.setResponseCode(WalletUtils.Wallet_BALANCE_UPDATED_CODE);
        walletResponse.setResponseMessage(WalletUtils.Wallet_BALANCE_UPDATED_MESSAGE);
        walletResponse.setWalletDTO(WalletDTO.builder()
                .username(savedWallet.getUsername())
                .balance(savedWallet.getBalance())
                .Currency(savedWallet.getCurrency())
                .build());

        return new ResponseEntity<>(walletResponse, HttpStatus.OK);
    }
    @Override
    public Boolean existsByUsername(String username){

        return walletRepository.existsByUsername(username);
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
