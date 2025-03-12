package com.hpe.PaymentRequestService.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PRequestDTO {
    private String payerUserName;
    private String payeeUserName;
    private String amount;
    private String currency;
    private String description;
}
