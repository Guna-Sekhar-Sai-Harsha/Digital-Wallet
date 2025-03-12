package com.hpe.SavingsService.dto;

import com.hpe.SavingsService.model.SavingsDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SavingsResponse {
    private String responseCode;
    private String responseMessage;
    private SavingsDetails savingsDetails;
}
