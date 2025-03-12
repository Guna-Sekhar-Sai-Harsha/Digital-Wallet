package com.hpe.ExpenseSharingService.dto;

import lombok.Data;

@Data
public class GroupBalanceDTO {
    private String userOwes;
    private String userOwed;
    private String amount;
}
