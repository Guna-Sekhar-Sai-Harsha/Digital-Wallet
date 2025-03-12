package com.hpe.ExpenseSharingService.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "Group")
public class GroupDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String groupName;
    private String description;
    private String createdBy;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime ModifiedAt;
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupMembersDetails> members;

    @OneToMany(mappedBy = "groupDetails", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExpenseDetails> expenses;

}
