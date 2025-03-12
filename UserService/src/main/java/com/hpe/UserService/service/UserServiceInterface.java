package com.hpe.UserService.service;


import com.hpe.UserService.dto.UserDTO;
import com.hpe.UserService.dto.UserResponse;
import org.springframework.http.ResponseEntity;

public interface UserServiceInterface {

    /*
        1. Create a user
        2. Checks if user exists by Phone number or email or Under Age
        3. Saves the user data to DB
        4. Creates a Wallet and Reward Account
        5. Send email through Notification Service
        6. Returns User response with username
     */
    ResponseEntity<UserResponse> createUser(UserDTO userDTO);

    /*
        1. Toggles the membership of a user.
        2. Input parameter is Username.
        3. Returns User response with username.
     */
    ResponseEntity<UserResponse> updateMembership(String userName);

    /*
        1. Checks if user is member or not.
        2. Input parameter is Username.
        3. Returns User response with username.
     */
    ResponseEntity<UserResponse> isMember(String userName);
}
