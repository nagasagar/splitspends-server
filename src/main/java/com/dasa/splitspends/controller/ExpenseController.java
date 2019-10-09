package com.dasa.splitspends.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dasa.splitspends.exception.BadRequestException;
import com.dasa.splitspends.exception.ResourceNotFoundException;
import com.dasa.splitspends.model.Expense;
import com.dasa.splitspends.model.Payment;
import com.dasa.splitspends.model.Share;
import com.dasa.splitspends.model.User;
import com.dasa.splitspends.repository.ExpenseRepository;
import com.dasa.splitspends.repository.GroupRepository;
import com.dasa.splitspends.repository.PaymentRepository;
import com.dasa.splitspends.repository.ShareRepository;
import com.dasa.splitspends.repository.UserRepository;
import com.dasa.splitspends.security.CurrentUser;
import com.dasa.splitspends.security.UserPrincipal;

@RestController
public class ExpenseController {
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private ShareRepository shareRepository;
    
    @Autowired
    private GroupRepository groupRepository;
    
    @GetMapping("/expenses/{expenseId}")
    public Expense getExpenseById(@PathVariable long expenseId) {
	Expense expense = expenseRepository.findById(expenseId)
		.orElseThrow(() -> new ResourceNotFoundException("Group", "id", expenseId));

	return expense;
    }
    
    @GetMapping("/expenses")
    public List<Expense> getExpenses(@CurrentUser UserPrincipal userPrincipal) {
	User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
	return expenseRepository.findExpensesOfuser(user.getId());
    }
    
    
    @GetMapping("/expenses/test")
    public Expense getSampleExpense() {
	Expense expense = new Expense();
	expense.setDetail("mysore expense");
	expense.setAmount(10);
	expense.setAuthor(userRepository.getOne((long) 5));
	expense.setGroup(groupRepository.getOne((long) 5));
	expense = expenseRepository.save(expense);
	
	Payment payment = new Payment();
	payment.setPayee(userRepository.getOne((long) 5));
	payment.setAmount(10);
	payment.setExpense(expense);
	payment = paymentRepository.save(payment);
	expense.getPayments().add(payment);
	expense = expenseRepository.save(expense);
	
	Share share1 = new Share();
	share1.setSpender(userRepository.getOne((long) 4));
	share1.setAmount(5);
	share1.setExpense(expense);
	share1 = shareRepository.save(share1);
	expense.addShare(share1);
	
	Share share2 = new Share();
	share2.setSpender(userRepository.getOne((long) 5));
	share2.setAmount(5);
	share2.setExpense(expense);
	share2 = shareRepository.save(share2);
	expense.addShare(share2);
	
	return expenseRepository.save(expense);
	
    }
    
    @PostMapping("/expenses")
    public Expense addMemberToGroup(@CurrentUser UserPrincipal userPrincipal, @RequestBody Expense expense) {
	Optional<Expense> exp = expenseRepository.findById(expense.getId());
	if(exp.isPresent()) {
	    //TODO group already exists
	    return exp.get();
	}else {
	    int paymentsTotal = expense.getPayments().stream().mapToInt(p -> p.getAmount()).sum();
	    int sharesTotal = expense.getShares().stream().mapToInt(s -> s.getAmount()).sum();
	    if(paymentsTotal != expense.getAmount() || sharesTotal != expense.getAmount()) {
		throw(new BadRequestException("expense amount doesnt match totals of payments and shares"));
	    }
	    Expense newExpense = new Expense();
	    newExpense.setDetail(expense.getDetail());
	    User usr = userRepository.findById(userPrincipal.getId())
	                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
	    newExpense.setAuthor(usr);
	    newExpense.setAmount(expense.getAmount());
	    for (Payment payment : expense.getPayments()) {
		newExpense.addPayment(payment);
	    }
	    for (Share share : expense.getShares()) {
		newExpense.addShare(share);
	    }
		return expenseRepository.save(newExpense);
	}
	
    }

}
