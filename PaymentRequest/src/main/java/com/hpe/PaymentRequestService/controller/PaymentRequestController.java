package com.hpe.PaymentRequestService.controller;

import com.google.gson.Gson;
import com.hpe.PaymentRequestService.dto.PRequestDTO;
import com.hpe.PaymentRequestService.dto.PaymentRequestResponse;
import com.hpe.PaymentRequestService.model.PaymentRequest;
import com.hpe.PaymentRequestService.service.PaymentRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/paymentRequests")
public class PaymentRequestController {

    @Autowired
    private PaymentRequestService paymentRequestService;

    @PostMapping("/request")
    public ResponseEntity<PaymentRequestResponse> createPaymentRequest(@RequestBody PRequestDTO pRequestDTO){
        return paymentRequestService.createPaymentRequest(pRequestDTO);
    }

    @PostMapping("/approve/{requestId}")
    public ResponseEntity<PaymentRequestResponse> approvePaymentRequest(@PathVariable Long requestId){
        return paymentRequestService.approvePaymentRequest(requestId);
    }

    @PostMapping("/reject/{requestId}")
    public ResponseEntity<PaymentRequestResponse> rejectPaymentRequest(@PathVariable Long requestId){
        return paymentRequestService.rejectPaymentRequest(requestId);
    }

    @GetMapping("/payerPendingRequests/{userName}")
    public ResponseEntity<List<PaymentRequest>> getPayerPendingRequests(@PathVariable String userName){
     return paymentRequestService.getPayerPendingRequests(userName);
    }

    @GetMapping("/paymentDues/{userName}")
    public ResponseEntity<List<PaymentRequest>> getAllPaymentDues(@PathVariable String userName){
        return paymentRequestService.getAllPaymentDue(userName);
    }

    @GetMapping("/payeePendingRequests/{userName}")
    public ResponseEntity<List<PaymentRequest>> getPayeePendingRequests(@PathVariable String userName){
        return paymentRequestService.getPayeePendingRequests(userName);
    }

    @GetMapping("/pendingRequest/{requestId}")
    public ResponseEntity<PaymentRequestResponse> getPendingRequestDetails(@PathVariable Long requestId){
        return paymentRequestService.getPendingRequestDetails(requestId);
    }

    @KafkaListener(topics = "ExpenseRequest", groupId = "ExpenseRequest-group")
    public ResponseEntity<PaymentRequestResponse> createPaymentRequestForGroup(String pRequestDTOString){
        try {
            Gson gson = new Gson();
            PRequestDTO pRequestDTO = gson.fromJson(pRequestDTOString, PRequestDTO.class);
            return paymentRequestService.createPaymentRequest(pRequestDTO);
        }catch (Exception e){
            System.out.println(e);
        }
        return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
    }

}
