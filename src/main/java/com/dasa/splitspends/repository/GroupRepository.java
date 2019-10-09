package com.dasa.splitspends.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dasa.splitspends.model.Group;
import com.dasa.splitspends.model.User;

public interface GroupRepository extends JpaRepository<Group, Long> {
    
    Optional<User> findByName(String name);

    Boolean existsByName(String name);

}
