package com.dasa.splitspends.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dasa.splitspends.model.Share;
import com.dasa.splitspends.model.User;

public interface ShareRepository extends JpaRepository<Share, Long> {
    
    List<Share> findBySpender(User spender);
}
