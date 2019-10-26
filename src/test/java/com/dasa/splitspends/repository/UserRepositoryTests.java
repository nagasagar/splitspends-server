package com.dasa.splitspends.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.dasa.splitspends.model.User;
import com.dasa.splitspends.repository.UserRepository;


public class UserRepositoryTests extends BaseJPARepositoryTest{

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFriendRelationIsBidirectional() {
        User nonFriendlyUser1 = userRepository.findById(1001l).get();
        User nonFriendlyUser2 = userRepository.findById(1002l).get();
        assertNotNull(nonFriendlyUser1);
        assertNotNull(nonFriendlyUser2);
        assertThat(userRepository.findFriendsOfuser(1001l).size()).isEqualTo(0);
        assertThat(userRepository.findFriendsOfuser(1002l).size()).isEqualTo(0);
        nonFriendlyUser1.addFriend(nonFriendlyUser2);
        userRepository.save(nonFriendlyUser2);
        
        nonFriendlyUser1 = userRepository.findById(1001l).get();
        nonFriendlyUser2 = userRepository.findById(1002l).get();
        List<User> friendsOfUser1 = userRepository.findFriendsOfuser(1001l);
        List<User> friendsOfUser2 = userRepository.findFriendsOfuser(1002l);
        assertThat(friendsOfUser1).contains(nonFriendlyUser2);
        assertThat(friendsOfUser2).contains(nonFriendlyUser1);
        assertThat(friendsOfUser1.size()).isEqualTo(1);
        assertThat(friendsOfUser2.size()).isEqualTo(1);
    }
    
    @Test
    public void testGetUserByName() {
	List<User> users = userRepository.findByName("samenameuser");
	assertThat(users.size()==2);
    }
    
    @Test
    public void testGetUserByEmail() {
	Optional<User> user = userRepository.findByEmail("samename2@gmail.com");
	assertThat(user.isPresent());
	user = userRepository.findByEmail("nonexistantuser@gmail.com");
	assertThat(user.isPresent());
    }
    
    @Test
    public void testExistsByEmail() {
	assertTrue(userRepository.existsByEmail("samename2@gmail.com"));
	assertFalse(userRepository.existsByEmail("nonexistantuser@gmail.com"));
    }

}
