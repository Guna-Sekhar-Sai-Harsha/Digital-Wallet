package com.hpe.SavingsService.dto;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class SavingsDTO {
    private String userName;
    private String amount;
    private String currency;
    private String savingsType;
    private Integer goal;
    private String description;
    private Integer apr;
    private String interestReceived;
    private Boolean canWithdraw;
    private LocalDate maturityDate;
}
