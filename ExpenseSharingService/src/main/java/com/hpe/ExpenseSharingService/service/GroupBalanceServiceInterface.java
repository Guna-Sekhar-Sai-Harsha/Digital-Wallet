package com.hpe.ExpenseSharingService.service;

import com.hpe.ExpenseSharingService.model.ExpenseDetails;
import com.hpe.ExpenseSharingService.model.GroupBalance;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface GroupBalanceServiceInterface {

    void updateGroupBalances(ExpenseDetails expense);
    void reverseGroupBalances(ExpenseDetails expense);
    void updateGroupBalance(Long groupId, String userOwed, String userOwes, String amount);
    ResponseEntity<List<GroupBalance>> getGroupBalances(Long groupId);
    ResponseEntity<List<GroupBalance>> getAllBalancesUserOwes(String userName);
    ResponseEntity<List<GroupBalance>> getAllBalancesUserOwed(String userName);
}
