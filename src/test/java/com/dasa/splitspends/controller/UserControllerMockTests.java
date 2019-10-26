package com.dasa.splitspends.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.dasa.splitspends.controller.UserController;
import com.dasa.splitspends.model.User;
import com.dasa.splitspends.repository.UserRepository;
import com.dasa.splitspends.security.UserPrincipal;

public class UserControllerMockTests {
    User u;

    @InjectMocks
    private UserController userController;

    @Mock
    private UserRepository userRepository;
    
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void init() {
	MockitoAnnotations.initMocks(this);
	u = new User();
        u.setId(1l);
        u.setEmail("something@anything.com");
    }

    @Test
    public void testGetUserByID() {
        when(userRepository.findById(1l)).thenReturn(Optional.of(u));
        User user = userController.getUserById(1L);
        verify(userRepository).findById(1l);
        assertThat(user.getId(), is(equalTo(1l)));
    }
    
    @Test
    public void testGetCurrentUser() {
        when(userRepository.findById(1l)).thenReturn(Optional.of(u));
        User user = userController.getCurrentUser(UserPrincipal.create(u));
        verify(userRepository).findById(1l);
        assertThat(user.getId(), is(equalTo(1l)));
    }
    
    @Test
    public void testGetUserByEmail() {
        when(userRepository.findByEmail("something@anything.com")).thenReturn(Optional.of(u));
        User user = userController.getUserByEmail("something@anything.com");
        verify(userRepository).findByEmail("something@anything.com");
        assertThat(user.getEmail(), is(equalTo("something@anything.com")));
    }
    
    @Test
    public void testgetFriendsOfUser() {
        when(userRepository.findById(1l)).thenReturn(Optional.of(u));
        when(userRepository.findFriendsOfuser(1l)).thenReturn(new ArrayList<User>());
        userController.getFriendsOfUser(UserPrincipal.create(u));
        verify(userRepository).findById(1l);
        verify(userRepository).findFriendsOfuser(1l);
    }
    
    @Test
    public void testaddingYourSelfAsFriendToUser() {
        when(userRepository.findById(1l)).thenReturn(Optional.of(u));
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("you cannot add yourself as a friend");
        userController.addFriendToUser(UserPrincipal.create(u),u);
    }
    
    @Test
    public void testaddFriendToUser() {
	
        when(userRepository.findById(1l)).thenReturn(Optional.of(u));
	User f = new User();
        f.setId(2l);
        f.setEmail("somefriend@anything.com");
        userController.addFriendToUser(UserPrincipal.create(u),f);
        u.addFriend(f);
        verify(userRepository).save(u);
        
    }
    
    @Test
    public void testremoveFriendFromUser() {
	
        when(userRepository.findById(1l)).thenReturn(Optional.of(u));
	User f = new User();
        f.setId(2l);
        f.setEmail("somefriend@anything.com");
        u.addFriend(f);
        userController.removeFriendToUser(UserPrincipal.create(u),f);
        u.removeFriend(f);
        verify(userRepository).save(u);
        
    }


}
