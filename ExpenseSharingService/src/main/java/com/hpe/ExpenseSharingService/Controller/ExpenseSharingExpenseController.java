package com.hpe.ExpenseSharingService.Controller;

import com.hpe.ExpenseSharingService.dto.ExpenseDTO;
import com.hpe.ExpenseSharingService.dto.ExpenseSharingExpenseResponse;
import com.hpe.ExpenseSharingService.model.ExpenseDetails;
import com.hpe.ExpenseSharingService.model.GroupBalance;
import com.hpe.ExpenseSharingService.service.ExpenseService;
import com.hpe.ExpenseSharingService.service.GroupBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expenseSharing/expense")
public class ExpenseSharingExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private GroupBalanceService groupBalanceService;

    @PostMapping("create")
    public ResponseEntity<ExpenseSharingExpenseResponse> createExpense(@RequestBody ExpenseDTO expenseDto){
        return expenseService.createExpense(expenseDto);
    }
    @PostMapping("/update/{expenseId}")
    public ResponseEntity<ExpenseSharingExpenseResponse> updateExpense(@PathVariable Long expenseId, @RequestBody ExpenseDTO expenseDto){
        return expenseService.updateExpense(expenseId,expenseDto);
    }
    @DeleteMapping("/{expenseId}")
    public ResponseEntity<ExpenseSharingExpenseResponse> deleteExpense(@PathVariable Long expenseId){
        return expenseService.deleteExpense(expenseId);
    }
    @GetMapping("/{expenseId}")
    public ResponseEntity<ExpenseSharingExpenseResponse> getExpense(@PathVariable Long expenseId){
        return expenseService.getExpense(expenseId);
    }
    @PostMapping("/sendRemainder/{OwedOwner}/{OwesOwner}/{amount}")
    public ResponseEntity<ExpenseSharingExpenseResponse> sendPaymentRemainder(@PathVariable String OwedOwner,@PathVariable String OwesOwner,@PathVariable String amount){
        return expenseService.sendPaymentRemainder(OwedOwner, OwesOwner, amount);
    }
    @PostMapping("/settle/{groupId}/{OwedOwner}/{OwesOwner}/{amount}")
    public ResponseEntity<ExpenseSharingExpenseResponse> settleBetweenUsers(@PathVariable Long groupId,@PathVariable String OwedOwner,@PathVariable String OwesOwner,@PathVariable String amount){
        return expenseService.settleBetweenUsers(groupId, OwedOwner, OwesOwner, amount);
    }

    @GetMapping("/groupBalances/{groupId}")
    public ResponseEntity<List<GroupBalance>> getGroupBalances(Long groupId){
        return groupBalanceService.getGroupBalances(groupId);
    }
    @GetMapping("/userOwesbalances/{userName}")
    public ResponseEntity<List<GroupBalance>> getAllBalancesUserOwes(String userName){
        return groupBalanceService.getAllBalancesUserOwes(userName);
    }
    @GetMapping("/userOwedbalances/{userName}")
    public ResponseEntity<List<GroupBalance>> getAllBalancesUserOwed(String userName){
        return groupBalanceService.getAllBalancesUserOwed(userName);
    }

}
