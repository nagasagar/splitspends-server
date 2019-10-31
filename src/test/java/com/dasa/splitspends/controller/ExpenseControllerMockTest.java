package com.dasa.splitspends.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.VerificationModeFactory;

import com.dasa.splitspends.exception.BadRequestException;
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
import com.dasa.splitspends.security.UserPrincipal;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class ExpenseControllerMockTest {

	Expense e, testExpense;

	User u;

	Group g;

	@InjectMocks
	private ExpenseController expenseController;

	@Mock
	private ExpenseRepository expenseRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private PaymentRepository paymentRepository;

	@Mock
	private ShareRepository shareRepository;

	@Mock
	private GroupRepository groupRepository;

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	@Before
	public void init() throws JsonParseException, JsonMappingException, IOException {
		MockitoAnnotations.initMocks(this);
		e = new Expense();
		e.setId(1l);
		e.setDetail("someExpense");

		testExpense = createExpense(1001l, 100, new int[] { 100 }, new int[] { 50, 50 });

		u = new User();
		u.setId(1l);
		u.setEmail("something@anything.com");

		g = new Group();
		g.setId(201l);

	}

	private Expense createExpense(Long id, int amount, int[] payments, int[] shares) {
		Expense e = new Expense();
		e.setId(id);
		e.setAmount(amount);
		for (int p : payments) {
			Payment payment = new Payment();
			payment.setAmount(p);
			e.addPayment(payment);
		}
		for (int s : shares) {
			Share share = new Share();
			share.setAmount(s);
			e.addShare(share);
		}

		return e;
	}

	@Test
	public void testGetExpenseByID() {
		when(expenseRepository.findById(1l)).thenReturn(Optional.of(e));
		Expense expense = expenseController.getExpenseById(1L);
		verify(expenseRepository).findById(1l);
		assertThat(expense.getId(), is(equalTo(1l)));
	}

	@Test
	public void testDeleteexpenseByID() {
		when(expenseRepository.findById(Mockito.any())).thenReturn(Optional.of(e));
		expenseController.deleteExpenseByID(e.getId());
		verify(expenseRepository).findById(e.getId());
		ArgumentCaptor<Expense> captor = ArgumentCaptor.forClass(Expense.class);
		verify(expenseRepository).delete(captor.capture());
		Expense argument = captor.getValue();
		assertTrue(argument.equals(e));
	}

	@Test
	public void testAddExpenseWithInvalidTotals() {
		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("expense amount doesnt match totals of payments and shares");
		expenseController.createOrEditExpense(UserPrincipal.create(u),
				createExpense(1001l, 100, new int[] { 99 }, new int[] { 50, 50 }));

		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("expense amount doesnt match totals of payments and shares");
		expenseController.createOrEditExpense(UserPrincipal.create(u),
				createExpense(1001l, 100, new int[] { 100 }, new int[] { 50, 40 }));

		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("expense amount doesnt match totals of payments and shares");
		expenseController.createOrEditExpense(UserPrincipal.create(u),
				createExpense(1001l, 100, new int[] { 99 }, new int[] { 51, 50 }));
	}

	@Test
	public void testGroupExpenseCreationWithInvalidAuthor() {
		testExpense.setGroup(g);
		testExpense.setAuthor(u);
		when(userRepository.findById(1l)).thenReturn(Optional.of(u));
		when(groupRepository.findById(201l)).thenReturn(Optional.of(g));

		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage("only group members can add expenses to the group");
		expenseController.createOrEditExpense(UserPrincipal.create(u), testExpense);

	}

	@Test
	public void testGroupExpenseCreationWithInvalidPayee() {
		g.addMember(u);
		testExpense.setGroup(g);
		testExpense.setAuthor(u);
		testExpense.getPayments().clear();
		Payment p = new Payment();
		p.setAmount(100);
		p.setPayee(new User());
		testExpense.getPayments().add(p);
		when(userRepository.findById(1l)).thenReturn(Optional.of(u));
		when(groupRepository.findById(201l)).thenReturn(Optional.of(g));

		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage(
				"payee should be either a group member in a group-expense or a friend in a non-group-expense");
		expenseController.createOrEditExpense(UserPrincipal.create(u), testExpense);

	}

	@Test
	public void testNonGroupExpenseCreationWithInvalidPayee() {
		testExpense.getPayments().clear();
		Payment p = new Payment();
		p.setAmount(100);
		p.setPayee(new User());
		testExpense.getPayments().add(p);
		when(userRepository.findById(1l)).thenReturn(Optional.of(u));
		when(groupRepository.findById(201l)).thenReturn(Optional.of(g));

		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage(
				"payee should be either a group member in a group-expense or a friend in a non-group-expense");
		expenseController.createOrEditExpense(UserPrincipal.create(u), testExpense);

	}

	@Test
	public void testGroupExpenseCreationWithInvalidSpender() {
		g.addMember(u);
		testExpense.setGroup(g);
		testExpense.setAuthor(u);
		testExpense.getPayments().clear();
		Payment p = new Payment();
		p.setAmount(100);
		p.setPayee(u);
		testExpense.getPayments().add(p);
		testExpense.getShares().clear();
		Share s = new Share();
		s.setAmount(100);
		s.setSpender(new User());
		testExpense.getShares().add(s);
		when(userRepository.findById(1l)).thenReturn(Optional.of(u));
		when(groupRepository.findById(201l)).thenReturn(Optional.of(g));

		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage(
				"spender should be either a group member in a group-expense or a friend in a non-group-expense");
		expenseController.createOrEditExpense(UserPrincipal.create(u), testExpense);

	}

	@Test
	public void testNonGroupExpenseCreationWithInvalidSpender() {
		testExpense.getPayments().clear();
		Payment p = new Payment();
		p.setAmount(100);
		p.setPayee(u);
		testExpense.getPayments().add(p);
		testExpense.getShares().clear();
		Share s = new Share();
		s.setAmount(100);
		s.setSpender(new User());
		testExpense.getShares().add(s);
		when(userRepository.findById(1l)).thenReturn(Optional.of(u));

		exceptionRule.expect(BadRequestException.class);
		exceptionRule.expectMessage(
				"spender should be either a group member in a group-expense or a friend in a non-group-expense");
		expenseController.createOrEditExpense(UserPrincipal.create(u), testExpense);

	}

	@Test
	public void testValidNonGroupExpenseCreation() {
		testExpense.setId(null);
		testExpense.setDetail("detail");
		testExpense.getPayments().clear();
		Payment p = new Payment();
		p.setAmount(100);
		p.setPayee(u);
		testExpense.getPayments().add(p);
		testExpense.getShares().clear();
		Share s = new Share();
		s.setAmount(100);
		s.setSpender(u);
		testExpense.getShares().add(s);
		when(userRepository.findById(1l)).thenReturn(Optional.of(u));
		when(expenseRepository.save(Mockito.any())).thenReturn(testExpense);
		expenseController.createOrEditExpense(UserPrincipal.create(u), testExpense);
		ArgumentCaptor<Expense> captor = ArgumentCaptor.forClass(Expense.class);
		verify(expenseRepository, VerificationModeFactory.times(2)).save(captor.capture());
		Expense argument = captor.getValue();
		assertTrue(argument.equals(testExpense));

	}

	@Test
	public void testValidGroupExpenseCreation() {
		g.addMember(u);
		testExpense.setGroup(g);
		testExpense.setAuthor(u);
		testExpense.setId(null);
		testExpense.setDetail("detail");
		testExpense.getPayments().clear();
		Payment p = new Payment();
		p.setAmount(100);
		p.setPayee(u);
		testExpense.getPayments().add(p);
		testExpense.getShares().clear();
		Share s = new Share();
		s.setAmount(100);
		s.setSpender(u);
		testExpense.getShares().add(s);
		when(userRepository.findById(1l)).thenReturn(Optional.of(u));
		when(expenseRepository.save(Mockito.any())).thenReturn(testExpense);
		when(groupRepository.findById(201l)).thenReturn(Optional.of(g));
		expenseController.createOrEditExpense(UserPrincipal.create(u), testExpense);
		ArgumentCaptor<Expense> captor = ArgumentCaptor.forClass(Expense.class);
		verify(expenseRepository, VerificationModeFactory.times(2)).save(captor.capture());
		Expense argument = captor.getValue();
		assertTrue(argument.equals(testExpense));

	}

	@Test
	public void testValidNonGroupExpenseUpdation() {
		testExpense.setDetail("detail");
		testExpense.getPayments().clear();
		Payment p = new Payment();
		p.setAmount(100);
		p.setPayee(u);
		testExpense.getPayments().add(p);
		testExpense.getShares().clear();
		Share s = new Share();
		s.setAmount(100);
		s.setSpender(u);
		testExpense.getShares().add(s);
		when(userRepository.findById(1l)).thenReturn(Optional.of(u));
		when(expenseRepository.findById(1001l)).thenReturn(Optional.of(testExpense));
		when(expenseRepository.save(Mockito.any())).thenReturn(testExpense);
		expenseController.createOrEditExpense(UserPrincipal.create(u), testExpense);
		ArgumentCaptor<Expense> captor = ArgumentCaptor.forClass(Expense.class);
		verify(expenseRepository, VerificationModeFactory.times(1)).save(captor.capture());
		Expense argument = captor.getValue();
		assertTrue(argument.equals(testExpense));

	}

	@Test
	public void testValidGroupExpenseUpdation() {
		g.addMember(u);
		testExpense.setGroup(g);
		testExpense.setAuthor(u);
		testExpense.setDetail("detail");
		testExpense.getPayments().clear();
		Payment p = new Payment();
		p.setAmount(100);
		p.setPayee(u);
		testExpense.getPayments().add(p);
		testExpense.getShares().clear();
		Share s = new Share();
		s.setAmount(100);
		s.setSpender(u);
		testExpense.getShares().add(s);
		when(userRepository.findById(1l)).thenReturn(Optional.of(u));
		when(expenseRepository.findById(1001l)).thenReturn(Optional.of(testExpense));
		when(expenseRepository.save(Mockito.any())).thenReturn(testExpense);
		when(groupRepository.findById(201l)).thenReturn(Optional.of(g));
		expenseController.createOrEditExpense(UserPrincipal.create(u), testExpense);
		ArgumentCaptor<Expense> captor = ArgumentCaptor.forClass(Expense.class);
		verify(expenseRepository, VerificationModeFactory.times(1)).save(captor.capture());
		Expense argument = captor.getValue();
		assertTrue(argument.equals(testExpense));

	}

}
