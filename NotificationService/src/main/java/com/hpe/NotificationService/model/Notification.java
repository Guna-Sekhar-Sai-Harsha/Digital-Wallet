package com.hpe.NotificationService.model;

import com.hpe.NotificationService.dto.EmailDetails;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "Notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String userName;
    @Column(nullable = false)
    private String message;
    @Column(nullable = false)
    private String type;  // e.g., "EMAIL", "SMS"
    @JdbcTypeCode(SqlTypes.JSON)
    private EmailDetails email;
    @Column(nullable = false)
    private boolean sent;
    @CreationTimestamp
    private LocalDateTime createdAt;


}
