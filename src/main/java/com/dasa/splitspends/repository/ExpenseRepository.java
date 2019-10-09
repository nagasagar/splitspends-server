package com.dasa.splitspends.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dasa.splitspends.model.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    
    Optional<Expense> findByDetail(String detail);

    Boolean existsByDetail(String detail);
    
    final String FIND_EXPENSES_SQL = "select * from expenses "
    	+ "where id in ("
    	+ "(select expense_id from payments where payee_id = :userID)"
    	+ " union "
    	+ "(select expense_id from shares where spender_id= :userID)"
    	+ ");";

    @Query(value = FIND_EXPENSES_SQL, nativeQuery = true)
    List<Expense> findExpensesOfuser(@Param("userID") Long id);

}
