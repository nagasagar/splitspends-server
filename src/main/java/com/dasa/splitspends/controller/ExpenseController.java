package com.dasa.splitspends.controller;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
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
	    if(p.getId()==null) {
		Payment payment = new Payment();
		payment.setAmount(p.getAmount());
		payment.setExpense(targetExpense);
		payment.setPayee(p.getPayee());
		payments.add(paymentRepository.save(payment));
	    }else {
		payments.add(paymentRepository.save(p));
	    }
	    
	}
	targetExpense.setPayments(payments);
	Set<Share> shares = new HashSet<Share>();
	for(Share s: expense.getShares()) {
	    if(s.getId()==null) {
		Share share = new Share();
		share.setAmount(s.getAmount());
		share.setExpense(targetExpense);
		share.setSpender(s.getSpender());
		shares.add(shareRepository.save(share));
	    }else {
		shares.add(shareRepository.save(s));
	    }
	}
	targetExpense.setShares(shares);
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
	    validPayees = userRepository.getFriendsOfuser(currentUser.getId());
	} else {
	    Group expenseGroup = groupRepository.findById(grp.getId())
		    .orElseThrow(() -> new ResourceNotFoundException("Group", "id", grp.getId()));
	    validPayees = expenseGroup.getMembers();
	}

	for (Payment p : expense.getPayments()) {
	    if (validPayees.contains(p.getPayee())) {
		continue;
	    } else {
		return false;
	    }
	}
	return true;
    }

    private boolean areSpendersValid(Expense expense, User currentUser) {
	Collection<User> validSpenders;
	Group grp = expense.getGroup();
	if (grp == null) {
	    validSpenders = userRepository.getFriendsOfuser(currentUser.getId());
	} else {
	    Group expenseGroup = groupRepository.findById(grp.getId())
		    .orElseThrow(() -> new ResourceNotFoundException("Group", "id", grp.getId()));
	    validSpenders = expenseGroup.getMembers();
	}

	for (Share s : expense.getShares()) {
	    if (validSpenders.contains(s.getSpender())) {
		continue;
	    } else {
		return false;
	    }
	}
	return true;
    }

}
