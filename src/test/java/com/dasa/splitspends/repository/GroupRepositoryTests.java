package com.dasa.splitspends.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.dasa.splitspends.model.Group;


public class GroupRepositoryTests extends BaseJPARepositoryTest{

    @Autowired
    private GroupRepository groupRepository;

   
    @Test
    public void testGetGroupByName() {
	Optional<Group> group = groupRepository.findByName("Prague");
	assertTrue(group.isPresent());
	assertThat(group.get().getMembers().size()).isEqualTo(2);
    }
    
    
    @Test
    public void testGroupExistsByName() {
	assertTrue(groupRepository.existsByName("Prague"));
	assertFalse(groupRepository.existsByName("Barcelona"));
    }

}
