package com.dasa.splitspends.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = ExpenseController.class, secure = false)
public class ExpenseControllerWebMockTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private GroupRepository groupRepository;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private ExpenseRepository expenseRepository;

	@MockBean
	private PaymentRepository paymentRepository;

	@MockBean
	private ShareRepository shareRepository;

	@Autowired
	ObjectMapper objectMapper;

	private User alexa, siri, google, bixby;

	private Group assistants;

	private Expense groupTestExpense;

	private Expense nonGroupTestExpense;

	private Payment payment1, payment2, payment3, invalidGrpPayment, invalidNonGrpPayment;

	private Share share1, share2, share3, share4, invalidGrpShare, invalidNonGrpShare;

	@Before
	public void setUp() {
		alexa = createUser(101l, "alexa@gmail.com", "alexa", "P@55W0rd");
		siri = createUser(102l, "siri@gmail.com", "siri", "P@55W0rd");
		google = createUser(103l, "google@gmail.com", "google", "P@55W0rd");
		bixby = createUser(104l, "bixby@gmail.com", "bixby", "P@55W0rd");
		assistants = createGroup(201l, "assistants", new User[] { alexa, siri, google });
		setExpenseData();

	}

	private void setExpenseData() {
		groupTestExpense = createExpense(1001l, 25, "Internet Bill", alexa, assistants);
		nonGroupTestExpense = createExpense(1002l, 20, "Netflix subscription", google, null);

		payment1 = createPayment(2001l, alexa, 20, groupTestExpense);
		payment2 = createPayment(2002l, siri, 5, groupTestExpense);
		payment3 = createPayment(2003l, alexa, 20, nonGroupTestExpense);
		invalidGrpPayment = createPayment(2004l, bixby, 25, groupTestExpense);
		invalidNonGrpPayment = createPayment(2004l, bixby, 20, nonGroupTestExpense);
		
		share1 = createShare(3001l, alexa, 10, groupTestExpense);
		share2 = createShare(3002l, siri, 5, groupTestExpense);
		share3 = createShare(3003l, google, 10, groupTestExpense);
		share4 = createShare(3004l, siri, 20, nonGroupTestExpense);
		invalidGrpShare = createShare(3005l, bixby, 25, groupTestExpense);
		invalidNonGrpShare = createShare(3005l, bixby, 20, nonGroupTestExpense);

		groupTestExpense.addPayment(payment1);
		groupTestExpense.addPayment(payment2);
		groupTestExpense.addShare(share1);
		groupTestExpense.addShare(share2);
		groupTestExpense.addShare(share3);

		nonGroupTestExpense.addPayment(payment3);
		nonGroupTestExpense.addShare(share4);

	}

	private Expense createExpense(long id, int amount, String detail, User author, Group group) {
		Expense expense = new Expense();
		expense.setId(id);
		expense.setAmount(amount);
		expense.setDetail(detail);
		expense.setAuthor(author);
		expense.setGroup(group);
		return expense;
	}

	private Payment createPayment(long id, User payee, int amount, Expense expense) {
		Payment payment = new Payment();
		payment.setId(id);
		payment.setAmount(amount);
		payment.setExpense(expense);
		payment.setPayee(payee);
		return payment;

	}

	private Share createShare(long id, User spender, int amount, Expense expense) {
		Share share = new Share();
		share.setId(id);
		share.setAmount(amount);
		share.setExpense(expense);
		share.setSpender(spender);
		return share;

	}

	private User createUser(long id, String email, String name, String password) {
		User user = new User();
		user.setId(id);
		user.setEmail(email);
		user.setName(name);
		user.setPassword(password);
		return user;
	}

	private Group createGroup(Long id, String name, User[] members) {
		Group group = new Group();
		group.setId(id);
		group.setName(name);
		for (User u : members) {
			group.addMember(u);
		}
		return group;
	}

	@After
	public void cleanup() {
		reset(expenseRepository);
	}

	@Test
	public void givenId_whenGetExpenseByID_thenReturnExpenseJsonObject() throws Exception {
		when(expenseRepository.findById(Mockito.any())).thenReturn(Optional.of(groupTestExpense));
		mvc.perform(get("/expenses/{expenseId}", "1001").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id", is(1001)))
				.andExpect(jsonPath("$.detail", is(groupTestExpense.getDetail())))
				.andExpect(jsonPath("$.payments", hasSize(2)))
				.andExpect(jsonPath("$.payments",
						hasItem(objectMapper.readValue(objectMapper.writeValueAsString(payment1), Map.class))))
				.andExpect(jsonPath("$.payments",
						hasItem(objectMapper.readValue(objectMapper.writeValueAsString(payment2), Map.class))))
				.andExpect(jsonPath("$.shares", hasSize(3)))
				.andExpect(jsonPath("$.shares",
						hasItem(objectMapper.readValue(objectMapper.writeValueAsString(share1), Map.class))))
				.andExpect(jsonPath("$.shares",
						hasItem(objectMapper.readValue(objectMapper.writeValueAsString(share2), Map.class))))
				.andExpect(jsonPath("$.shares",
						hasItem(objectMapper.readValue(objectMapper.writeValueAsString(share3), Map.class))));
		
		when(expenseRepository.findById(Mockito.any())).thenReturn(Optional.of(nonGroupTestExpense));
		mvc.perform(get("/expenses/{expenseId}", "1002").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id", is(1002)))
				.andExpect(jsonPath("$.detail", is(nonGroupTestExpense.getDetail())))
				.andExpect(jsonPath("$.payments", hasSize(1)))
				.andExpect(jsonPath("$.payments",
						hasItem(objectMapper.readValue(objectMapper.writeValueAsString(payment3), Map.class))))
				.andExpect(jsonPath("$.shares", hasSize(1)))
				.andExpect(jsonPath("$.shares",
						hasItem(objectMapper.readValue(objectMapper.writeValueAsString(share4), Map.class))));
	}

	@Test
	public void givenId_whenDeleteExpenseByID_thenDeletesExpense() throws Exception {
		when(expenseRepository.findById(Mockito.any())).thenReturn(Optional.of(groupTestExpense));
		mvc.perform(delete("/expenses/{expenseId}", "1001").contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
		verify(expenseRepository).findById(groupTestExpense.getId());
		ArgumentCaptor<Expense> captor = ArgumentCaptor.forClass(Expense.class);
		verify(expenseRepository).delete(captor.capture());
		Expense argument = captor.getValue();
		assertTrue(argument.equals(groupTestExpense));
		
		
	}

	@Test
	public void givennewExpense_whenCreateExpense_thenExpenseTotalsAreValidated() throws Exception {
		groupTestExpense.setAmount(26);
		MvcResult result = mvc.perform(post("/expenses").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(groupTestExpense)))
				.andExpect(status().isBadRequest()).andReturn();
		assertThat(result.getResolvedException().getMessage(), is("expense amount doesnt match totals of payments and shares"));
	}
	
	@Test
	public void givennewExpense_whenCreateExpense_thenExpenseAuthorIsValidated() throws Exception {
		groupTestExpense.setAuthor(bixby);
		when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(bixby));
		when(groupRepository.findById(Mockito.any())).thenReturn(Optional.of(assistants));
		MvcResult result = mvc.perform(post("/expenses").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(groupTestExpense)))
				.andExpect(status().isBadRequest()).andReturn();
		assertThat(result.getResolvedException().getMessage(), is("only group members can add expenses to the group"));
	}
	
	@Test
	public void givennewExpense_whenCreateExpense_thenExpensePayeesAreValidated() throws Exception {
		groupTestExpense.getPayments().clear();
		groupTestExpense.addPayment(invalidGrpPayment);
		when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(alexa));
		when(groupRepository.findById(Mockito.any())).thenReturn(Optional.of(assistants));
		MvcResult result = mvc.perform(post("/expenses").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(groupTestExpense)))
				.andExpect(status().isBadRequest()).andReturn();
		assertThat(result.getResolvedException().getMessage(), is("payee should be either a group member in a group-expense or a friend in a non-group-expense"));
		nonGroupTestExpense.getPayments().clear();
		nonGroupTestExpense.addPayment(invalidNonGrpPayment);
		when(userRepository.findFriendsOfuser(Mockito.any())).thenReturn(Arrays.asList(siri));
		result = mvc.perform(post("/expenses").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(nonGroupTestExpense)))
				.andExpect(status().isBadRequest()).andReturn();
		assertThat(result.getResolvedException().getMessage(), is("payee should be either a group member in a group-expense or a friend in a non-group-expense"));
	}
	
	@Test
	public void givennewExpense_whenCreateExpense_thenExpenseSpendersAreValidated() throws Exception {
		groupTestExpense.getShares().clear();
		groupTestExpense.addShare(invalidGrpShare);
		when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(alexa));
		when(groupRepository.findById(Mockito.any())).thenReturn(Optional.of(assistants));
		MvcResult result = mvc.perform(post("/expenses").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(groupTestExpense)))
				.andExpect(status().isBadRequest()).andReturn();
		assertThat(result.getResolvedException().getMessage(), is("spender should be either a group member in a group-expense or a friend in a non-group-expense"));
		nonGroupTestExpense.getShares().clear();
		nonGroupTestExpense.addShare(invalidNonGrpShare);
		when(userRepository.findFriendsOfuser(Mockito.any())).thenReturn(Arrays.asList(siri));
		result = mvc.perform(post("/expenses").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(nonGroupTestExpense)))
				.andExpect(status().isBadRequest()).andReturn();
		assertThat(result.getResolvedException().getMessage(), is("spender should be either a group member in a group-expense or a friend in a non-group-expense"));
	}

}
