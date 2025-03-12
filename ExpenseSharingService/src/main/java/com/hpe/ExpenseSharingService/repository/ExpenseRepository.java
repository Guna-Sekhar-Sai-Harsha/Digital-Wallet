package com.hpe.ExpenseSharingService.repository;

import com.hpe.ExpenseSharingService.model.ExpenseDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseDetails, Long>{
}

