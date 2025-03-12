package com.hpe.PaymentRequestService.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hpe.PaymentRequestService.dto.*;
import com.hpe.PaymentRequestService.model.PaymentRequest;
import com.hpe.PaymentRequestService.repository.PaymentRequestRepository;
import com.hpe.PaymentRequestService.utils.PaymentRequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentRequestService implements PaymentRequestServiceInterface {

    @Autowired
    private PaymentRequestRepository paymentRequestRepository;

    @Autowired
    private KafkaTemplate<String, String> template;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Override
    public ResponseEntity<PaymentRequestResponse> createPaymentRequest(PRequestDTO pRequestDTO){
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .payerUserName(pRequestDTO.getPayerUserName())
                .payeeUserName(pRequestDTO.getPayeeUserName())
                .amount(pRequestDTO.getAmount())
                .currency(pRequestDTO.getCurrency())
                .description(pRequestDTO.getDescription())
                .requestStatus(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
        PaymentRequest createdPR = paymentRequestRepository.save(paymentRequest);

        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setUserName(createdPR.getPayerUserName());
        notificationDTO.setMessage(createdPR.getPayeeUserName()+ " requested amount of "+ createdPR.getAmount()+" "+createdPR.getCurrency());
        notificationDTO.setType("Push");

        template.send("Notification", JsonToString(notificationDTO));

        return new ResponseEntity<>(PaymentRequestResponse.builder()
                .responseCode(PaymentRequestUtils.PAYMENT_REQUEST_CREATED_CODE)
                .responseMessage(PaymentRequestUtils.PAYMENT_REQUEST_CREATED_MESSAGE)
                .paymentRequest(createdPR)
                .build(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PaymentRequestResponse> approvePaymentRequest(Long requestId) {

        PaymentRequest requestedPR = paymentRequestRepository.findById(requestId).orElse(null);

        if (requestedPR != null && requestedPR.getRequestStatus().equals(RequestStatus.PENDING)) {
            ResponseEntity<WalletResponse> walletResponse = webClientBuilder.build().get()
                    .uri("http://WalletService/wallet/balance/{userName}", requestedPR.getPayerUserName())
                    .retrieve()
                    .toEntity(WalletResponse.class)
                    .block();

            if (walletResponse.getStatusCode().equals(HttpStatus.OK)) {
                if(Double.parseDouble(walletResponse.getBody().getWalletDTO().getBalance())>=Double.parseDouble(requestedPR.getAmount())){
                    TransactionDTO transactionDTO = new TransactionDTO();
                    transactionDTO.setWalletUserName(requestedPR.getPayerUserName());
                    transactionDTO.setCounterPartyUserName(requestedPR.getPayeeUserName());
                    transactionDTO.setAmount(requestedPR.getAmount());
                    transactionDTO.setCurrency(requestedPR.getCurrency());
                    transactionDTO.setType("Credit");
                    transactionDTO.setDescription(requestedPR.getDescription());

                    template.send("PaymentsRequestTransaction", JsonToString(transactionDTO));

                    requestedPR.setRequestStatus(RequestStatus.APPROVED);
                    paymentRequestRepository.save(requestedPR);

                    return new ResponseEntity<>(PaymentRequestResponse.builder()
                            .responseCode(PaymentRequestUtils.PAYMENT_REQUEST_APPROVED_CODE)
                            .responseMessage(PaymentRequestUtils.PAYMENT_REQUEST_APPROVED_MESSAGE)
                            .paymentRequest(requestedPR)
                            .build(), HttpStatus.OK);
                }

            }

            return new ResponseEntity<>(PaymentRequestResponse.builder()
                        .responseCode(PaymentRequestUtils.USER_NOT_FOUND_CODE)
                        .responseMessage(PaymentRequestUtils.USER_NOT_FOUND_MESSAGE)
                        .paymentRequest(requestedPR)
                        .build(), HttpStatus.OK);
        }else {
            return new ResponseEntity<>(PaymentRequestResponse.builder()
                    .responseCode(PaymentRequestUtils.PAYMENT_REQUEST_NOT_FOUND_CODE)
                    .responseMessage(PaymentRequestUtils.PAYMENT_REQUEST_NOT_FOUND_MESSAGE)
                    .paymentRequest(requestedPR)
                    .build(), HttpStatus.OK);
        }
    }


    @Override
    public ResponseEntity<PaymentRequestResponse> rejectPaymentRequest(Long requestId){
        PaymentRequest requestedPR = paymentRequestRepository.findById(requestId).orElse(null);

        if (requestedPR != null && requestedPR.getRequestStatus().equals(RequestStatus.PENDING)) {
            requestedPR.setRequestStatus(RequestStatus.REJECTED);
            paymentRequestRepository.save(requestedPR);

            NotificationDTO notificationDTO = new NotificationDTO();
            notificationDTO.setUserName(requestedPR.getPayeeUserName());
            notificationDTO.setMessage("The requested payment request with id: "+requestId+" is rejected  by "+requestedPR.getPayerUserName());
            notificationDTO.setType("Push");

            template.send("Notification", JsonToString(notificationDTO));

            return new ResponseEntity<>(PaymentRequestResponse.builder()
                    .responseCode(PaymentRequestUtils.PAYMENT_REQUEST_REJECTED_CODE)
                    .responseMessage(PaymentRequestUtils.PAYMENT_REQUEST_REJECTED_MESSAGE)
                    .paymentRequest(requestedPR)
                    .build(), HttpStatus.OK);
        }else {
            return new ResponseEntity<>(PaymentRequestResponse.builder()
                    .responseCode(PaymentRequestUtils.PAYMENT_REQUEST_NOT_FOUND_CODE)
                    .responseMessage(PaymentRequestUtils.PAYMENT_REQUEST_NOT_FOUND_MESSAGE)
                    .paymentRequest(requestedPR)
                    .build(), HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<List<PaymentRequest>> getPayerPendingRequests(String userName){
        // add request status too

        return new ResponseEntity<>(paymentRequestRepository.findAllByPayerUserName(userName), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PaymentRequestResponse> getPendingRequestDetails(Long requestId){
        PaymentRequest prDetails = paymentRequestRepository.findById(requestId).orElse(null);
        if (prDetails != null){
            return new ResponseEntity<>(PaymentRequestResponse.builder()
                .responseCode(PaymentRequestUtils.PAYMENT_REQUEST_FOUND_CODE)
                .responseMessage(PaymentRequestUtils.PAYMENT_REQUEST_FOUND_MESSAGE)
                .paymentRequest(prDetails)
                .build(), HttpStatus.OK);
        }else {
            return new ResponseEntity<>(PaymentRequestResponse.builder()
                    .responseCode(PaymentRequestUtils.PAYMENT_REQUEST_NOT_FOUND_CODE)
                    .responseMessage(PaymentRequestUtils.PAYMENT_REQUEST_NOT_FOUND_MESSAGE)
                    .paymentRequest(null)
                    .build(), HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<List<PaymentRequest>> getPayeePendingRequests(String userName){
        // add request status too
        return new ResponseEntity<>(paymentRequestRepository.findAllByPayeeUserName(userName), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<PaymentRequest>> getAllPaymentDue(String userName){
        return new ResponseEntity<>(paymentRequestRepository.findAllByPayerUserNameAndRequestStatus(userName, RequestStatus.PENDING), HttpStatus.OK);
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
