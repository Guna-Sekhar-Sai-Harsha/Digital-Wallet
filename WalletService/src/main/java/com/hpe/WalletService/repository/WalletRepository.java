package com.hpe.WalletService.repository;


import com.hpe.WalletService.model.WalletDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<WalletDetails, Long> {

    WalletDetails findByUsername(String username);
    Boolean existsByUsername(String username);
}
