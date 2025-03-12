package com.hpe.ExpenseSharingService.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "GroupBalance")
public class GroupBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private Long groupId;

    @Column(nullable = false)
    private String userOwes;

    @Column(nullable = false)
    private String userOwed;

    @Column(nullable = false)
    private String amount;

    // Many-to-One with GroupDetails
    @ManyToOne
    @JoinColumn(name = "group_reference_id", nullable = false, insertable = false, updatable = false)
    private GroupDetails groupDetails;
}
