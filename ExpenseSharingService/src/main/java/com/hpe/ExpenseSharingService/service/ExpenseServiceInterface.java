package com.hpe.ExpenseSharingService.service;

import com.hpe.ExpenseSharingService.dto.ExpenseDTO;
import com.hpe.ExpenseSharingService.dto.ExpenseSharingExpenseResponse;
import org.springframework.http.ResponseEntity;


public interface ExpenseServiceInterface {

    ResponseEntity<ExpenseSharingExpenseResponse> createExpense(ExpenseDTO expenseDto);
    ResponseEntity<ExpenseSharingExpenseResponse> updateExpense(Long expenseId, ExpenseDTO expenseDto);
    ResponseEntity<ExpenseSharingExpenseResponse> deleteExpense(Long expenseId);
    ResponseEntity<ExpenseSharingExpenseResponse> getExpense(Long expenseId);
    ResponseEntity<ExpenseSharingExpenseResponse> sendPaymentRemainder(String OwedOwner, String OwesOwner, String amount);
    ResponseEntity<ExpenseSharingExpenseResponse> settleBetweenUsers(Long groupId, String OwedOwner, String OwesOwner, String amount);
}
