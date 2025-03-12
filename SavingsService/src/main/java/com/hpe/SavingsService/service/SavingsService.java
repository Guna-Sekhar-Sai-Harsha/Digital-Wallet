package com.hpe.SavingsService.service;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.hpe.SavingsService.dto.*;
import com.hpe.SavingsService.model.SavingsDetails;
import com.hpe.SavingsService.repository.SavingsRepository;
import com.hpe.SavingsService.utils.SavingsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SavingsService implements SavingsServiceInterface {


    @Autowired
    private SavingsRepository savingsRepository;

    @Autowired
    private KafkaTemplate<String, String> template;

    @Autowired
    private WebClient.Builder webClientBuilder;


    @Override
    public ResponseEntity<SavingsResponse> createSavingsAccounts(SavingsDTO savingsDto) {



        Boolean walletUserExists = webClientBuilder.build().get()
                .uri("http://WalletService/wallet/checkuser/{username}", savingsDto.getUserName())
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        if(Boolean.TRUE.equals(walletUserExists)) {

            if (savingsRepository.existsByUserNameAndSavingsType(savingsDto.getUserName(),SavingsType.REGULAR_SAVINGS)) {
                return new ResponseEntity<>(SavingsResponse.builder()
                        .responseCode(SavingsUtils.SAVINGS_ACCOUNT_EXISTS_CODE)
                        .responseMessage(SavingsUtils.SAVINGS_ACCOUNT_EXISTS_MESSAGE)
                        .savingsDetails(null)
                        .build(), HttpStatus.FORBIDDEN);
            }

            SavingsDetails savingsDetails = SavingsDetails.builder()
                    .userName(savingsDto.getUserName())
                    .amount("0.00")
                    .currency(savingsDto.getCurrency())
                    .savingsType(SavingsType.REGULAR_SAVINGS)
                    .goal(savingsDto.getGoal())
                    .description(savingsDto.getDescription())
                    .apr(SavingsUtils.APR_REGULAR_SAVINGS)
                    .interestReceived("0.00")
                    .interestPending("0.00")
                    .maturityDate(null)
                    .canWithdraw(true)
                    .status("ACTIVE")
                    .createdAt(LocalDateTime.now())
                    .ModifiedAt(LocalDateTime.now())
                    .build();

            SavingsDetails createdSavingsAccount = savingsRepository.save(savingsDetails);

            NotificationDTO notificationDTO = new NotificationDTO();
            notificationDTO.setUserName(createdSavingsAccount.getUserName());
            notificationDTO.setMessage(SavingsUtils.SAVINGS_ACCOUNT_CREATED_MESSAGE);
            notificationDTO.setType("Push");
            template.send("Notification", JsonToString(notificationDTO));

            return new ResponseEntity<>(SavingsResponse.builder()
                    .responseCode(SavingsUtils.SAVINGS_ACCOUNT_CREATED_CODE)
                    .responseMessage(SavingsUtils.SAVINGS_ACCOUNT_CREATED_MESSAGE)
                    .savingsDetails(createdSavingsAccount)
                    .build(), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(SavingsResponse.builder()
                .responseCode(SavingsUtils.USER_NOT_FOUND_CODE)
                .responseMessage(SavingsUtils.USER_NOT_FOUND_MESSAGE)
                .savingsDetails(null)
                .build(), HttpStatus.FORBIDDEN);
    }

    @Override
    public ResponseEntity<SavingsResponse> createCashDeposit(SavingsDTO savingsDto){

        ResponseEntity<WalletResponse> walletResponse = webClientBuilder.build().get()
                .uri("http://WalletService/wallet/balance/{userName}", savingsDto.getUserName())
                .retrieve()
                .toEntity(WalletResponse.class)
                .block();


        if(walletResponse.getStatusCode().equals(HttpStatus.OK)){
            if(Double.parseDouble(walletResponse.getBody().getWalletDTO().getBalance())>=Double.parseDouble(savingsDto.getAmount())) {

                TransactionDTO transactionDTO = new TransactionDTO();
                transactionDTO.setWalletUserName(savingsDto.getUserName());
                transactionDTO.setCounterPartyUserName("Savings");
                transactionDTO.setAmount(savingsDto.getAmount());
                transactionDTO.setCurrency(savingsDto.getCurrency());
                transactionDTO.setType("Credit");
                transactionDTO.setDescription("Creating of Cash Deposit");
                template.send("SavingsTransaction", JsonToString(transactionDTO));


                SavingsDetails savingsDetails = SavingsDetails.builder()
                        .userName(savingsDto.getUserName())
                        .amount(savingsDto.getAmount())
                        .currency(savingsDto.getCurrency())
                        .savingsType(SavingsType.FIXED_DEPOSIT)
                        .goal(savingsDto.getGoal())
                        .description(savingsDto.getDescription())
                        .apr(SavingsUtils.APR_CASH_DEPOSIT)
                        .interestReceived("0.00")
                        .interestPending("0.00")
                        .maturityDate(savingsDto.getMaturityDate())
                        .canWithdraw(false)
                        .status("ACTIVE")
                        .createdAt(LocalDateTime.now())
                        .ModifiedAt(LocalDateTime.now())
                        .build();

                SavingsDetails createdCD = savingsRepository.save(savingsDetails);

                NotificationDTO notificationDTO = new NotificationDTO();
                notificationDTO.setUserName(createdCD.getUserName());
                notificationDTO.setMessage(SavingsUtils.CASH_DEPOSIT_CREATED_MESSAGE);
                notificationDTO.setType("Push");
                template.send("Notification", JsonToString(notificationDTO));

                return new ResponseEntity<>(SavingsResponse.builder()
                        .responseCode(SavingsUtils.CASH_DEPOSIT_CREATED_CODE)
                        .responseMessage(SavingsUtils.CASH_DEPOSIT_CREATED_MESSAGE)
                        .savingsDetails(createdCD)
                        .build(), HttpStatus.CREATED);

            }else{
                return new ResponseEntity<>(SavingsResponse.builder()
                        .responseCode(SavingsUtils.WALLET_INSUFFICIENT_BALANCE_CODE)
                        .responseMessage(SavingsUtils.WALLET_INSUFFICIENT_BALANCE_MESSAGE)
                        .savingsDetails(null)
                        .build(), HttpStatus.OK);
            }

        }
        return new ResponseEntity<>(SavingsResponse.builder()
                .responseCode(SavingsUtils.USER_NOT_FOUND_CODE)
                .responseMessage(SavingsUtils.USER_NOT_FOUND_MESSAGE)
                .savingsDetails(null)
                .build(), HttpStatus.FORBIDDEN);
    }

    @Override
    public ResponseEntity<SavingsResponse> withdrawCashDeposit(Long id){


        if (savingsRepository.existsById(id)){

            SavingsDetails savingsAccount = savingsRepository.findById(id).orElse(null);
            assert savingsAccount != null;

            if(savingsAccount.getMaturityDate().isBefore(LocalDate.now())
                    && savingsAccount.getStatus().equals("ACTIVE")
                    && savingsAccount.getSavingsType().equals(SavingsType.FIXED_DEPOSIT)
            ){



                String updatedBalance = updateCDAccount(savingsAccount.getAmount(),
                        savingsAccount.getInterestReceived(),
                        savingsAccount.getInterestPending());


                TransactionDTO transactionDTO = new TransactionDTO();
                transactionDTO.setWalletUserName("Savings");
                transactionDTO.setCounterPartyUserName(savingsAccount.getUserName());
                transactionDTO.setAmount(updatedBalance);
                transactionDTO.setCurrency(savingsAccount.getCurrency());
                transactionDTO.setType("Credit");
                transactionDTO.setDescription("Withdraw of Cash Deposit");
                template.send("SavingsTransaction", JsonToString(transactionDTO));

                savingsAccount.setAmount("0.00");
                savingsAccount.setInterestPending("0.00");
                savingsAccount.setInterestReceived("0.00");
                savingsAccount.setStatus("CLOSED");
                savingsAccount.setModifiedAt(LocalDateTime.now());
                savingsRepository.save(savingsAccount);

                NotificationDTO notificationDTO = new NotificationDTO();
                notificationDTO.setUserName(savingsAccount.getUserName());
                notificationDTO.setMessage(SavingsUtils.CASH_DEPOSIT_WITHDRAWN_MESSAGE);
                notificationDTO.setType("Push");
                template.send("Notification", JsonToString(notificationDTO));

                return new ResponseEntity<>(SavingsResponse.builder()
                        .responseCode(SavingsUtils.CASH_DEPOSIT_WITHDRAWN_CODE)
                        .responseMessage(SavingsUtils.CASH_DEPOSIT_WITHDRAWN_MESSAGE)
                        .savingsDetails(savingsAccount)
                        .build(), HttpStatus.OK);
            } else{
                return new ResponseEntity<>(SavingsResponse.builder()
                        .responseCode(SavingsUtils.CASH_DEPOSIT_NOT_AVAILABLE_FOR_WITHDRAW_CODE)
                        .responseMessage(SavingsUtils.CASH_DEPOSIT_NOT_AVAILABLE_FOR_WITHDRAW_MESSAGE)
                        .savingsDetails(savingsAccount)
                        .build(), HttpStatus.OK);
            }

        }
        return new ResponseEntity<>(SavingsResponse.builder()
                .responseCode(SavingsUtils.USER_NOT_FOUND_CODE)
                .responseMessage(SavingsUtils.USER_NOT_FOUND_MESSAGE)
                .savingsDetails(null)
                .build(), HttpStatus.FORBIDDEN);
    }

    @Override
    public ResponseEntity<SavingsResponse> withdrawSavingsAmount(SavingsDTO savingsDto){
        if (savingsRepository.existsByUserNameAndSavingsType(savingsDto.getUserName(), SavingsType.REGULAR_SAVINGS)){
            SavingsDetails savingsAccount = savingsRepository.findByUserNameAndSavingsType(savingsDto.getUserName(), SavingsType.REGULAR_SAVINGS);
            double savings_balance = Double.parseDouble(savingsAccount.getAmount());
            double requested_amount = Double.parseDouble(savingsDto.getAmount());
            if(savings_balance >= requested_amount){
                savingsAccount.setAmount(String.valueOf(savings_balance - requested_amount));
                savingsAccount.setModifiedAt(LocalDateTime.now());
                savingsRepository.save(savingsAccount);

                TransactionDTO transactionDTO = new TransactionDTO();
                transactionDTO.setWalletUserName("Savings");
                transactionDTO.setCounterPartyUserName(savingsDto.getUserName());
                transactionDTO.setAmount(savingsDto.getAmount());
                transactionDTO.setCurrency(savingsDto.getCurrency());
                transactionDTO.setType("Credit");
                transactionDTO.setDescription("Withdraw from Savings Account");
                template.send("SavingsTransaction", JsonToString(transactionDTO));

                NotificationDTO notificationDTO = new NotificationDTO();
                notificationDTO.setUserName(savingsAccount.getUserName());
                notificationDTO.setMessage(SavingsUtils.SAVINGS_ACCOUNT_WITHDRAWN_MESSAGE);
                notificationDTO.setType("Push");
                template.send("Notification", JsonToString(notificationDTO));

                return new ResponseEntity<>(SavingsResponse.builder()
                        .responseCode(SavingsUtils.SAVINGS_ACCOUNT_WITHDRAWN_CODE)
                        .responseMessage(SavingsUtils.SAVINGS_ACCOUNT_WITHDRAWN_MESSAGE)
                        .savingsDetails(savingsAccount)
                        .build(), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(SavingsResponse.builder()
                        .responseCode(SavingsUtils.SAVINGS_ACCOUNT_INSUFFICIENT_BALANCE_CODE)
                        .responseMessage(SavingsUtils.SAVINGS_ACCOUNT_INSUFFICIENT_BALANCE_MESSAGE)
                        .savingsDetails(savingsAccount)
                        .build(), HttpStatus.OK);
            }

        }
        return new ResponseEntity<>(SavingsResponse.builder()
                .responseCode(SavingsUtils.USER_NOT_FOUND_CODE)
                .responseMessage(SavingsUtils.USER_NOT_FOUND_MESSAGE)
                .savingsDetails(null)
                .build(), HttpStatus.FORBIDDEN);
    }

    @Override
    public ResponseEntity<SavingsResponse> AddFundsToSavingsAmount(SavingsDTO savingsDto){
        if (savingsRepository.existsByUserNameAndSavingsType(savingsDto.getUserName(), SavingsType.REGULAR_SAVINGS)){

            ResponseEntity<WalletResponse> walletResponse = webClientBuilder.build().get()
                    .uri("http://WalletService/wallet/balance/{userName}", savingsDto.getUserName())
                    .retrieve()
                    .toEntity(WalletResponse.class)
                    .block();


            if (walletResponse.getStatusCode().equals(HttpStatus.OK)) {
                if(Double.parseDouble(walletResponse.getBody().getWalletDTO().getBalance())>=Double.parseDouble(savingsDto.getAmount())){

                    TransactionDTO transactionDTO = new TransactionDTO();
                    transactionDTO.setWalletUserName(savingsDto.getUserName());
                    transactionDTO.setCounterPartyUserName("Savings");
                    transactionDTO.setAmount(savingsDto.getAmount());
                    transactionDTO.setCurrency(savingsDto.getCurrency());
                    transactionDTO.setType("Credit");
                    transactionDTO.setDescription("Addings Funds to Savings Account");
                    template.send("SavingsTransaction", JsonToString(transactionDTO));

                    SavingsDetails savingsAccount = savingsRepository.findByUserNameAndSavingsType(savingsDto.getUserName(), SavingsType.REGULAR_SAVINGS);
                    savingsAccount.setAmount(String.valueOf(Double.parseDouble(savingsAccount.getAmount()) + Double.parseDouble(savingsDto.getAmount())));
                    savingsRepository.save(savingsAccount);

                    NotificationDTO notificationDTO = new NotificationDTO();
                    notificationDTO.setUserName(savingsAccount.getUserName());
                    notificationDTO.setMessage(SavingsUtils.SAVINGS_ACCOUNT_FUNDS_ADDED_MESSAGE);
                    notificationDTO.setType("Push");
                    template.send("Notification", JsonToString(notificationDTO));

                    return new ResponseEntity<>(SavingsResponse.builder()
                            .responseCode(SavingsUtils.SAVINGS_ACCOUNT_FUNDS_ADDED_CODE)
                            .responseMessage(SavingsUtils.SAVINGS_ACCOUNT_FUNDS_ADDED_MESSAGE)
                            .savingsDetails(savingsAccount)
                            .build(), HttpStatus.OK);

                }else{
                    return new ResponseEntity<>(SavingsResponse.builder()
                            .responseCode(SavingsUtils.WALLET_INSUFFICIENT_BALANCE_CODE)
                            .responseMessage(SavingsUtils.WALLET_INSUFFICIENT_BALANCE_MESSAGE)
                            .savingsDetails(null)
                            .build(), HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<>(SavingsResponse.builder()
                .responseCode(SavingsUtils.USER_NOT_FOUND_CODE)
                .responseMessage(SavingsUtils.USER_NOT_FOUND_MESSAGE)
                .savingsDetails(null)
                .build(), HttpStatus.FORBIDDEN);
    }

    @Override
    public ResponseEntity<List<SavingsDetails>> getAllSavingsByUsername(String userName){
        return new ResponseEntity<>(savingsRepository.findAllByUserName(userName), HttpStatus.OK);
    }

    public String updateCDAccount(String amount, String interestReceived, String interestPending){

        return String.valueOf(Double.parseDouble(amount)+
                Double.parseDouble(interestReceived)+
                Double.parseDouble(interestPending));
    }

    public void updateSavingsAccount(){
        /*
            Update Savings Account
         */
    }

    public void updateInterest(){
        /*
            Interest update
         */
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
