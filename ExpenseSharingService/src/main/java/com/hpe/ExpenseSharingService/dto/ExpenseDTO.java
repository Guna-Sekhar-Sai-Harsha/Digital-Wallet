package com.hpe.ExpenseSharingService.dto;

import com.hpe.ExpenseSharingService.model.ExpenseSplitDetails;
import com.hpe.ExpenseSharingService.model.GroupDetails;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseDTO {

    private String description;
    private String amount;
    private String paidBy;
    private Long groupId;
    private List<String> participants;
    private List<ExpenseSplitDTO> expenseSplitDetails;
}
