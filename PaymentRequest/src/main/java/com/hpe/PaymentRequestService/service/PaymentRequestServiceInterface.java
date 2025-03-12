package com.hpe.PaymentRequestService.service;

import com.hpe.PaymentRequestService.dto.PRequestDTO;
import com.hpe.PaymentRequestService.dto.PaymentRequestResponse;
import com.hpe.PaymentRequestService.model.PaymentRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PaymentRequestServiceInterface {

    /*
        1.Creates a payment request.
        2. Then, sends a notification to payer.
        3. Input Parameter is of type PRequestDTO.
        8. Return of type PaymentRequestResponse with PR details.
     */
    ResponseEntity<PaymentRequestResponse> createPaymentRequest(PRequestDTO pRequestDTO);
    /*
        1. Approves a payment request and transfers funds from payer to payee.
        2. Checks if request ID exists and request status as PENDING.
        3. Then checks, if there is sufficient balance in payer's wallet.
        4. If exists, transfers funds from payer to payee's wallet and sets request status to APPROVED.
        5. Then, sends a notification to payee.
        6. If there is no sufficient balance, transaction doesn't take place.
        7. Input Parameter is Request ID.
        8. Return of type PaymentRequestResponse with PR details if request ID exists or else null.
     */
    ResponseEntity<PaymentRequestResponse> approvePaymentRequest(Long requestId);
    /*
        1. Rejects a payment request.
        2. Checks if request ID exists and request status as PENDING.
        3. If exists, sets request status to REJECTED and sends a notification to payee.
        4. Input Parameter is Request ID.
        5. Return of type PaymentRequestResponse with PR details if request ID exists or else null.
     */
    ResponseEntity<PaymentRequestResponse> rejectPaymentRequest(Long requestId);
    /*
        1. Gets a list of payers payments request  including PENDING, APPROVED and REJECTED.
        2. Input Parameter is Username.
        3. Return of list of payments request or null.
     */
    ResponseEntity<List<PaymentRequest>> getPayerPendingRequests(String userName);
    /*
        1. Gets a list of payees payments requests  including PENDING, APPROVED and REJECTED.
        2. Input Parameter is Username.
        3. Return of list of payments request or null.
     */
    ResponseEntity<List<PaymentRequest>> getPayeePendingRequests(String userName);
    /*
       1. Gets a list of payers payments request of request status PENDING.
       2. Input Parameter is Username.
       3. Return of list of pending payments request or null.
    */
    ResponseEntity<List<PaymentRequest>> getAllPaymentDue(String userName);
    /*
       1. Gets a details of payment request.
       2. Input Parameter is Request ID.
       3. Return of type PaymentRequestResponse with PR details if request ID exists or else null.
    */
    ResponseEntity<PaymentRequestResponse> getPendingRequestDetails(Long requestId);
}
