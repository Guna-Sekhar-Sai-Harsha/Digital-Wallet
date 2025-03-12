package com.hpe.PaymentRequestService.dto;

import com.hpe.PaymentRequestService.model.PaymentRequest;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentRequestResponse {
    private String responseCode;
    private String responseMessage;
    private PaymentRequest paymentRequest;
}
