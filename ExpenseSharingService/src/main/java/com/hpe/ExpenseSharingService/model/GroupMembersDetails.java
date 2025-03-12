package com.hpe.ExpenseSharingService.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "GroupMembers")
public class GroupMembersDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;

    @ManyToOne
    @JoinColumn(name = "group_reference_id", nullable = false)
    private GroupDetails group;

    private boolean isAdmin;
}
