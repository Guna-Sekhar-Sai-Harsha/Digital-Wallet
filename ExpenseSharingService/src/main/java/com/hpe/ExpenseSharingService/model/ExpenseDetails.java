package com.hpe.ExpenseSharingService.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "Expenses")
public class ExpenseDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private String amount;
    private String paidBy;
    private Long groupId;

    @ManyToOne
    @JoinColumn(name = "group_reference_id", nullable = false, insertable = false, updatable = false)
    private GroupDetails groupDetails;

    // One-to-Many with ExpenseSplitDetails
    @OneToMany(mappedBy = "expenseDetails", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ExpenseSplitDetails> splitDetails = new HashSet<>();


    private LocalDateTime createdAt;
}
