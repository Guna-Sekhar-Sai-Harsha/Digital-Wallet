package com.hpe.TransactionService.service;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.hpe.TransactionService.dto.NotificationDTO;
import com.hpe.TransactionService.dto.TransactionDTO;
import com.hpe.TransactionService.dto.TransactionResponse;
import com.hpe.TransactionService.dto.WalletDTO;
import com.hpe.TransactionService.model.TransactionDetails;
import com.hpe.TransactionService.repository.TransactionRepository;
import com.hpe.TransactionService.utils.TransactionUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService implements TransactionServiceInterface{

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private KafkaTemplate<String, String> template;

    @Autowired
    private WebClient.Builder webClientBuilder;


    @Override
    public ResponseEntity<TransactionResponse> createTransaction(TransactionDTO transaction){

        Boolean walletUserExists = webClientBuilder.build().get()
                .uri("http://WalletService/wallet/checkuser/{username}", transaction.getWalletUserName())
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        Boolean CounterPartyUserExists = webClientBuilder.build().get()
                .uri("http://WalletService/wallet/checkuser/{username}", transaction.getCounterPartyUserName())
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        System.out.println("Is Wallet Service exists 1: " +walletUserExists);
        System.out.println("Is Wallet Service exists 2: " + CounterPartyUserExists);


        if(walletUserExists && CounterPartyUserExists){
            TransactionDetails transactionDetails = new TransactionDetails();
            transactionDetails.setWalletUserName(transaction.getWalletUserName());
            transactionDetails.setCounterPartyUserName(transaction.getCounterPartyUserName());
            transactionDetails.setAmount(transaction.getAmount());
            transactionDetails.setCurrency(transaction.getCurrency());
            transactionDetails.setType(transaction.getType());
            transactionDetails.setDescription(transaction.getDescription());
            transactionDetails.setStatus("Pending");
            transactionDetails.setCreated(LocalDateTime.now());
            TransactionDetails savedTransaction = transactionRepository.save(transactionDetails);


            WalletDTO walletDTO = new WalletDTO();
            walletDTO.setUsername(transaction.getWalletUserName());
            walletDTO.setBalance(transaction.getAmount());
            walletDTO.setCurrency(transaction.getCurrency());



            if(transaction.getType().equals("Debit")) {
                template.send("WithdrawFunds", JsonToString(walletDTO));
            } else if (transaction.getType().equals("Credit")) {
                template.send("AddFunds", JsonToString(walletDTO));
            }

            NotificationDTO notificationDTO = new NotificationDTO();
            notificationDTO.setUserName(transaction.getWalletUserName());
            notificationDTO.setMessage("Your account has been "+ transaction.getType() +"ed with : "+transaction.getAmount()+transaction.getCurrency());
            notificationDTO.setType("Push");
            template.send("Notification", JsonToString(notificationDTO));

            return new ResponseEntity<>(TransactionResponse.builder()
                    .responseCode(TransactionUtils.TRANSACTION_CREATED_CODE)
                    .responseMessage(TransactionUtils.TRANSACTION_CREATED_MESSAGE)
                    .transactionDetails(TransactionDetails.builder()
                            .id(savedTransaction.getId())
                            .walletUserName(savedTransaction.getWalletUserName())
                            .counterPartyUserName(savedTransaction.getCounterPartyUserName())
                            .amount(savedTransaction.getAmount())
                            .type(savedTransaction.getType())
                            .currency(savedTransaction.getCurrency())
                            .description(savedTransaction.getDescription())
                            .status(savedTransaction.getStatus())
                            .created(savedTransaction.getCreated())
                            .build())
                    .build(), HttpStatus.OK);

        }
        return new ResponseEntity<>(TransactionResponse.builder()
                .responseCode(TransactionUtils.USER_NOT_FOUND_CODE)
                .responseMessage(TransactionUtils.USER_NOT_FOUND_MESSAGE)
                .transactionDetails(null)
                .build(), HttpStatus.FORBIDDEN);
    }

    @Transactional
    @Override
    public ResponseEntity<TransactionResponse> transferFunds(TransactionDTO transactionDTO){

        TransactionDTO updatedTransaction = TransactionDTO.builder()
                .walletUserName(transactionDTO.getWalletUserName())
                .counterPartyUserName(transactionDTO.getCounterPartyUserName())
                .type("Debit")
                .amount(transactionDTO.getAmount())
                .currency(transactionDTO.getCurrency())
                .description(transactionDTO.getDescription())
                .build();

        // Create debit transaction for sender
        ResponseEntity<TransactionResponse> savedTransactionDebit = createTransaction(updatedTransaction);
        /*
                update is not Atomic.
        */
        if(savedTransactionDebit.getStatusCode()==HttpStatus.OK){
        updatedTransaction.setWalletUserName(transactionDTO.getCounterPartyUserName());
        updatedTransaction.setCounterPartyUserName(transactionDTO.getWalletUserName());
        updatedTransaction.setType("Credit");

        // Create credit transaction for receiver
        ResponseEntity<TransactionResponse> savedTransactionCredit = createTransaction(updatedTransaction);

        if(savedTransactionCredit.getStatusCode()==HttpStatus.OK){
            TransactionDetails savedTransaction = savedTransactionCredit.getBody().getTransactionDetails();
            assert savedTransaction != null;
            savedTransaction.setStatus("Completed");
            transactionRepository.save(savedTransaction);
            savedTransaction = savedTransactionDebit.getBody().getTransactionDetails();
            assert savedTransaction != null;
            savedTransaction.setStatus("Completed");
            transactionRepository.save(savedTransaction);
            return new ResponseEntity<>(TransactionResponse.builder()
                    .responseCode(TransactionUtils.TRANSACTION_CREATED_CODE)
                    .responseMessage(TransactionUtils.TRANSACTION_CREATED_MESSAGE)
                    .transactionDetails(TransactionDetails.builder()
                            .id(savedTransaction.getId())
                            .walletUserName(savedTransaction.getWalletUserName())
                            .counterPartyUserName(savedTransaction.getCounterPartyUserName())
                            .amount(savedTransaction.getAmount())
                            .type(savedTransaction.getType())
                            .currency(savedTransaction.getType())
                            .description(savedTransaction.getDescription())
                            .status(savedTransaction.getStatus())
                            .created(savedTransaction.getCreated())
                            .build())
                    .build(), HttpStatus.OK);
        }
        }

        return new ResponseEntity<>(TransactionResponse.builder()
                .responseCode(TransactionUtils.USER_NOT_FOUND_CODE)
                .responseMessage(TransactionUtils.USER_NOT_FOUND_MESSAGE)
                .transactionDetails(null)
                .build(), HttpStatus.FORBIDDEN);
    }


    @Override
    public  ResponseEntity<TransactionResponse> getTransactionById(Long transactionId){
        if (transactionRepository.existsById(transactionId)){
            TransactionDetails transactionDetails = transactionRepository.getReferenceById(transactionId);
            return new ResponseEntity<>(TransactionResponse.builder()
                    .responseCode(TransactionUtils.TRANSACTION_FOUND_CODE)
                    .responseMessage(TransactionUtils.TRANSACTION_FOUND_MESSAGE)
                    .transactionDetails(TransactionDetails.builder()
                            .id(transactionDetails.getId())
                            .walletUserName(transactionDetails.getWalletUserName())
                            .counterPartyUserName(transactionDetails.getCounterPartyUserName())
                            .amount(transactionDetails.getAmount())
                            .currency(transactionDetails.getCurrency())
                            .type(transactionDetails.getType())
                            .description(transactionDetails.getDescription())
                            .status(transactionDetails.getStatus())
                            .created(transactionDetails.getCreated())
                            .build())
                    .build(), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(TransactionResponse.builder()
                    .responseCode(TransactionUtils.TRANSACTION_DO_NOT_EXISTS_CODE)
                    .responseMessage(TransactionUtils.TRANSACTION_DO_NOT_EXISTS_MESSAGE)
                    .transactionDetails(null)
                    .build(), HttpStatus.OK);
        }

    }

    @Override
    public ResponseEntity<Optional<List<TransactionDetails>>> getTransactionByUserName(String username){
        return new ResponseEntity<>(transactionRepository.findByWalletUserName(username),HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Optional<List<TransactionDetails>>> findByUserNameAndCreatedBetween(String username, LocalDateTime startDate, LocalDateTime endDate){
        return new ResponseEntity<>(transactionRepository.findAllByWalletUserNameAndCreatedBetween(username, startDate, endDate),HttpStatus.OK);
    }

    @Override
    public ResponseEntity<TransactionResponse> createSavingsTransaction(){
        return new ResponseEntity<>(null, HttpStatus.OK);
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
