package com.hpe.UserService.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name="UserDetails")
public class UserDetails {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long userId;
        private String username;
        private String password;
        private String firstName;
        private String lastName;
        private String email;
        private String phoneNumber;
        private String countryCode = "+1";  // Default country code
        private LocalDate dateOfBirth;
        private String addressLine1;
        private String addressLine2;
        private String state;
        private String pincode;
        private String country = "USA";     // Default country
        private String currencyCode = "USD";    // Default currency
        private boolean enabled;
        private boolean isMember;
        @CreationTimestamp
        private LocalDateTime createdAt;
        @UpdateTimestamp
        private LocalDateTime modifiedAt;


}
