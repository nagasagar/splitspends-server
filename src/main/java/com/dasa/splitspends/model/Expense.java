package com.dasa.splitspends.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "expenses")
public class Expense {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(optional=false)
    @JoinColumn(name = "AUTHOR_ID")
    @JsonBackReference
    private User author;
    
    @ManyToOne(optional=true)
    @JoinColumn(name = "GROUP_ID")
    @JsonIgnoreProperties({"members","expenses"})
    private Group group;
        

    @Column(nullable = false)
    private String detail;
    
    @Column(nullable = false)
    private int amount;
    
    @OneToMany(mappedBy="expense",fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    private Set<Payment> payments= new HashSet<Payment>();
    
    @OneToMany(mappedBy="expense",fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    private Set<Share> shares= new HashSet<Share>();
    
    public Expense() {
	// No-Arg Constructor
    }
    
    public Expense(Expense e) {
	this.detail = e.detail;
	this.amount = e.amount;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Set<Payment> getPayments() {
        return payments;
    }

    public Set<Share> getShares() {
        return shares;
    }

    
    public void addPayment(Payment payment) {
	payments.add(payment);
    }

    public void removePayment(Payment payment) {
	payments.remove(payment);
    }
    
    public void addShare(Share share) {
	shares.add(share);
    }

    public void removeShare(Share share) {
	shares.remove(share);
    }
    
    public void setPayments(Set<Payment> payments) {
	this.payments = payments;
    }
    
    public void setShares(Set<Share> shares) {
	this.shares = shares;
    }
    
    

}
