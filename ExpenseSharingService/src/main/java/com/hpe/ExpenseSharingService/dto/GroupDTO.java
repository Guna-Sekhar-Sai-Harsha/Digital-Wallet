package com.hpe.ExpenseSharingService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class GroupDTO {
    private String groupName;
    private String description;
    private String createdBy;
}
