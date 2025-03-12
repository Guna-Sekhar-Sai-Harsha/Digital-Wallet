package com.hpe.ExpenseSharingService.repository;

import com.hpe.ExpenseSharingService.model.ExpenseSplitDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseSplitRepository extends JpaRepository<ExpenseSplitDetails, Long>{
}


