package com.hpe.UserService.repository;

import com.hpe.UserService.model.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserDetails, Long> {


    Boolean existsByPhoneNumber(String phoneNumber);
    Boolean existsByEmail(String email);

    UserDetails findByUsername(String username);
}
