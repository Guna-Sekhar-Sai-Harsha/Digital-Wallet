package com.hpe.ExpenseSharingService.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "ExpenseSplit")
public class ExpenseSplitDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String amount;

    @ManyToOne
    @JoinColumn(name = "expense_id", nullable = false)
    private ExpenseDetails expenseDetails;

    @Column(nullable = false)
    private boolean isPaid = false;


}
