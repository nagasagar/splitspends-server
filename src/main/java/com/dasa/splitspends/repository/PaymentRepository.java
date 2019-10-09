package com.dasa.splitspends.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dasa.splitspends.model.Payment;
import com.dasa.splitspends.model.User;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    List<Payment> findByPayee(User payee);
  
}
