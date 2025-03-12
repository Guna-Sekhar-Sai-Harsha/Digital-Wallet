package com.hpe.ExpenseSharingService.repository;

import com.hpe.ExpenseSharingService.model.ExpenseDetails;
import com.hpe.ExpenseSharingService.model.GroupBalance;
import org.apache.el.stream.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupBalanceRepository extends JpaRepository<GroupBalance, Long> {
    Optional findByGroupIdAndUserOwesAndUserOwed(Long groupId, String userOwes, String userOwed);

    List<GroupBalance> findAllByGroupId(Long groupId);

    List<GroupBalance> findAllByUserOwes(String userName);

    List<GroupBalance> findAllByUserOwed(String userName);
}
