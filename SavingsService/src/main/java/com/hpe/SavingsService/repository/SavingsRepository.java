package com.hpe.SavingsService.repository;

import com.hpe.SavingsService.dto.SavingsType;
import com.hpe.SavingsService.model.SavingsDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface SavingsRepository extends JpaRepository<SavingsDetails, Long> {


    Boolean existsByUserNameAndSavingsType(String username, SavingsType savingsType);

    SavingsDetails findByUserNameAndSavingsType(String username, SavingsType savingsType);

    List<SavingsDetails> findAllByUserName(String UserName);
}
