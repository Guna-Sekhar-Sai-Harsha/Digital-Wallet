package com.hpe.PaymentRequestService.repository;


import com.hpe.PaymentRequestService.dto.PaymentRequestResponse;
import com.hpe.PaymentRequestService.dto.RequestStatus;
import com.hpe.PaymentRequestService.model.PaymentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PaymentRequestRepository extends JpaRepository<PaymentRequest, Long> {
    List<PaymentRequest> findAllByPayerUserName(String userName);

    List<PaymentRequest> findAllByPayeeUserName(String userName);

    List<PaymentRequest> findAllByPayerUserNameAndRequestStatus(String userName, RequestStatus requestStatus);
}
