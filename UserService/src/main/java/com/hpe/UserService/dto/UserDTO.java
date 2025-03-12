package com.hpe.UserService.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDTO {

    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String countryCode;  // Default country code
    private LocalDate dateOfBirth;
    private String addressLine1;
    private String addressLine2;
    private String state;
    private String pincode;
    private final String country;     // Default country
    private final String currencyCode;    // Default currency
}
