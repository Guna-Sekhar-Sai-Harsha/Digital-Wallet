package com.hpe.ExpenseSharingService.dto;

import com.hpe.ExpenseSharingService.model.GroupDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseSharingGroupResponse {
    private String responseCode;
    private String responseMessage;
    private GroupDetails group;

}
