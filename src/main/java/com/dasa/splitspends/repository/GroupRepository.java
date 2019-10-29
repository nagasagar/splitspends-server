package com.dasa.splitspends.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dasa.splitspends.model.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {
    
    Optional<Group> findByName(String name);

    Boolean existsByName(String name);

}
