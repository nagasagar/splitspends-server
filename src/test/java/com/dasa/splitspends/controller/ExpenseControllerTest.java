package com.dasa.splitspends.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.hasSize;

import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.StreamUtils;

import com.dasa.splitspends.model.Expense;

public class ExpenseControllerTest extends ControllerTestBase {

	@Value("classpath:nsagar_expenses.json")
	private Resource nsagarExpensesResource;
	
	@Value("classpath:beerExpense.json")
	private Resource beerExpensesResource;
	
	@Before
	public void setUp() throws Exception {
		jwt = obtainAccessToken("nsagar@gmail.com", "splitspends");
	}

	@Test
	public void testGetExpensesOfUser() throws Exception {
		String expected = StreamUtils.copyToString(nsagarExpensesResource.getInputStream(), StandardCharsets.UTF_8);
		MvcResult result = mvc
				.perform(get("/expenses").header("Authorization", "Bearer " + jwt)
						.accept("application/json;charset=UTF-8"))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$", hasSize(5))).andReturn();
		String actual = result.getResponse().getContentAsString();
		JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);
	}

	@Test
	public void testGetExpenseByID() throws Exception {
		String expected = StreamUtils.copyToString(beerExpensesResource.getInputStream(), StandardCharsets.UTF_8);
		MvcResult result = mvc
				.perform(get("/expenses/9001").header("Authorization", "Bearer " + jwt)
						.accept("application/json;charset=UTF-8"))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.detail", is("beer"))).andReturn();
		String actual = result.getResponse().getContentAsString();
		JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);
	}

	@Test
	@DirtiesContext
	public void testCreateGroupExpense() throws Exception {
		String expenseRecord = StreamUtils.copyToString(beerExpensesResource.getInputStream(), StandardCharsets.UTF_8);
		Expense groupTestExpense = objectMapper.readValue(expenseRecord, Expense.class);
		groupTestExpense.setId(null);
		MvcResult result = mvc.perform(post("/expenses").header("Authorization", "Bearer " + jwt).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(groupTestExpense)))
				.andExpect(status().isOk()).andReturn();
		Expense createdTestExpense = objectMapper.readValue(result.getResponse().getContentAsString(), Expense.class);
		result = mvc
				.perform(get("/expenses/{id}", createdTestExpense.getId()).header("Authorization", "Bearer " + jwt)
						.accept("application/json;charset=UTF-8"))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.detail", is("beer"))).andReturn();
		
	}
	
	@Test
	@DirtiesContext
	public void testCreateNonGroupExpense() throws Exception {
		String expenseRecord = StreamUtils.copyToString(beerExpensesResource.getInputStream(), StandardCharsets.UTF_8);
		Expense nonGroupTestExpense = objectMapper.readValue(expenseRecord, Expense.class);
		nonGroupTestExpense.setId(null);
		nonGroupTestExpense.setGroup(null);
		MvcResult result = mvc.perform(post("/expenses").header("Authorization", "Bearer " + jwt).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(nonGroupTestExpense)))
				.andExpect(status().isOk()).andReturn();
		Expense createdTestExpense = objectMapper.readValue(result.getResponse().getContentAsString(), Expense.class);
		result = mvc
				.perform(get("/expenses/{id}", createdTestExpense.getId()).header("Authorization", "Bearer " + jwt)
						.accept("application/json;charset=UTF-8"))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.detail", is("beer"))).andReturn();
		
	}

	

	
}
