package com.hpe.TransactionService.repository;

import com.hpe.TransactionService.model.TransactionDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<TransactionDetails, Long> {

    Optional<List<TransactionDetails>> findByWalletUserName(String WalletUserName);

    @Query("SELECT t FROM TransactionDetails t WHERE t.walletUserName = :walletUserName AND t.created BETWEEN :startDate AND :endDate")
    Optional<List<TransactionDetails>> findAllByWalletUserNameAndCreatedBetween(String walletUserName, LocalDateTime startDate, LocalDateTime endDate);
}
