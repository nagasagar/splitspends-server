package com.dasa.splitspends.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Entity
@Table(name = "shares")
public class Share {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(optional=false)
    @JoinColumn(name = "SPENDER_ID")
    @JsonIgnoreProperties({"imageUrl","emailVerified","provider"})
    private User spender;
    
    @Column(nullable = false)
    private int amount;
    
    @ManyToOne(optional=false)
    @JoinColumn(name = "EXPENSE_ID")
    @JsonIgnore
    private Expense expense;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSpender() {
        return spender;
    }

    public void setSpender(User spender) {
        this.spender = spender;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Expense getExpense() {
        return expense;
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
    }
    
    

}
