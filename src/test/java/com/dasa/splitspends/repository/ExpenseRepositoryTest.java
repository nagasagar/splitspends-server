package com.dasa.splitspends.repository;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.dasa.splitspends.model.Expense;


public class ExpenseRepositoryTest extends BaseJPARepositoryTest{

    @Autowired
    private ExpenseRepository expenseRepository;

   
    @Test
    public void testGetExpensesOfUser() {
	List<Expense> expenses = expenseRepository.findExpensesOfuser(1012l);
	assertTrue(expenses.size()==5);
	List<Long> resultIds = expenses.stream().map(e->e.getId()).collect(Collectors.toList());
	assertThat(resultIds, hasItems(9001l,9002l,9003l,9004l,9005l));
	
	expenses = expenseRepository.findExpensesOfuser(1012l);
	assertTrue(expenses.size()==5);
	resultIds = expenses.stream().map(e->e.getId()).collect(Collectors.toList());
	assertThat(resultIds, hasItems(9001l,9002l,9003l,9004l,9005l));
	
	expenses = expenseRepository.findExpensesOfuser(1014l);
	assertTrue(expenses.size()==3);
	resultIds = expenses.stream().map(e->e.getId()).collect(Collectors.toList());
	assertThat(resultIds, hasItems(9002l,9004l,9005l));
    }
    
}
