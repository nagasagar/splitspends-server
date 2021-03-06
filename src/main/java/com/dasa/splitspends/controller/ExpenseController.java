package com.dasa.splitspends.controller;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dasa.splitspends.exception.BadRequestException;
import com.dasa.splitspends.exception.ResourceNotFoundException;
import com.dasa.splitspends.model.Expense;
import com.dasa.splitspends.model.Group;
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

    @DeleteMapping("/expenses/{expenseId}")
    public void deleteExpenseByID(@PathVariable long expenseId) {
	Expense expense = expenseRepository.findById(expenseId)
		.orElseThrow(() -> new ResourceNotFoundException("Group", "id", expenseId));
	
	expenseRepository.delete(expense);
    }

    @PostMapping("/expenses")
    public Expense createOrEditExpense(@CurrentUser UserPrincipal userPrincipal, @RequestBody Expense expense) {

	if (!isExpenseTotalValid(expense)) {
	    throw (new BadRequestException("expense amount doesnt match totals of payments and shares"));
	}
	User usr = userRepository.findById(userPrincipal.getId())
		.orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
	if (!isAuthorValid(expense, usr)) {
	    throw (new BadRequestException("only group members can add expenses to the group"));
	}
	if (!arePayeesValid(expense, usr)) {
	    throw (new BadRequestException("payee should be either a group member in a group-expense or a friend in a non-group-expense"));
	}
	if (!areSpendersValid(expense, usr)) {
	    throw (new BadRequestException("spender should be either a group member in a group-expense or a friend in a non-group-expense"));
	}
	Long expenseId = expense.getId();
	Expense targetExpense;
	if (expenseId !=null) {
	    targetExpense = expenseRepository.findById(expenseId)
		    .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", expenseId));
	    targetExpense.setAuthor(usr);
	    targetExpense.getPayments().clear();
	    targetExpense.getShares().clear();
	} else {
	    Expense newExpense = new Expense(expense);
	    newExpense.setAuthor(usr);
	    targetExpense = expenseRepository.save(newExpense);
	}
	targetExpense.setDetail(expense.getDetail());
	targetExpense.setAmount(expense.getAmount());
	targetExpense.setGroup(expense.getGroup());
	Set<Payment> payments = new HashSet<Payment>();
	for(Payment p: expense.getPayments()) {
		Payment payment = new Payment();
		payment.setAmount(p.getAmount());
		payment.setExpense(targetExpense);
		payment.setPayee(p.getPayee());
		payments.add(paymentRepository.save(payment));
	    
	}
	targetExpense.getPayments().addAll(payments);
	Set<Share> shares = new HashSet<Share>();
	for(Share s: expense.getShares()) {
		Share share = new Share();
		share.setAmount(s.getAmount());
		share.setExpense(targetExpense);
		share.setSpender(s.getSpender());
		shares.add(shareRepository.save(share));
	}
	targetExpense.getShares().addAll(shares);
	return expenseRepository.save(targetExpense);

    }

    private boolean isExpenseTotalValid(Expense expense) {
	int paymentsTotal = expense.getPayments().stream().mapToInt(p -> p.getAmount()).sum();
	int sharesTotal = expense.getShares().stream().mapToInt(s -> s.getAmount()).sum();
	if (paymentsTotal == expense.getAmount() && sharesTotal == expense.getAmount()) {
	    return true;
	} else {
	    return false;
	}
    }

    private boolean isAuthorValid(Expense expense, User currentUser) {
	Group grp = expense.getGroup();
	if (grp == null) {
	    return true;
	}
	Group expenseGroup = groupRepository.findById(grp.getId())
		.orElseThrow(() -> new ResourceNotFoundException("Group", "id", grp.getId()));
	Set<User> groupMembers = expenseGroup.getMembers();
	return groupMembers.contains(currentUser);
    }

    private boolean arePayeesValid(Expense expense, User currentUser) {
	Collection<User> validPayees;
	Group grp = expense.getGroup();
	if (grp == null) {
	    validPayees = userRepository.findFriendsOfuser(currentUser.getId());
	} else {
	    Group expenseGroup = groupRepository.findById(grp.getId())
		    .orElseThrow(() -> new ResourceNotFoundException("Group", "id", grp.getId()));
	    validPayees = expenseGroup.getMembers();
	}

	for (Payment p : expense.getPayments()) {
	    if (validPayees.contains(p.getPayee())) {
		continue;
	    } else if (p.getPayee().equals(currentUser)){
		continue;
	    }else {
		return false;
	    }
	}
	return true;
    }

    private boolean areSpendersValid(Expense expense, User currentUser) {
	Collection<User> validSpenders;
	Group grp = expense.getGroup();
	if (grp == null) {
	    validSpenders = userRepository.findFriendsOfuser(currentUser.getId());
	} else {
	    Group expenseGroup = groupRepository.findById(grp.getId())
		    .orElseThrow(() -> new ResourceNotFoundException("Group", "id", grp.getId()));
	    validSpenders = expenseGroup.getMembers();
	}

	for (Share s : expense.getShares()) {
	    if (validSpenders.contains(s.getSpender())) {
		continue;
	    } else if (s.getSpender().equals(currentUser)){
		continue;
	    } else {
		return false;
	    }
	}
	return true;
    }

}
