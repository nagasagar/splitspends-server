package com.example.springsocial.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.dasa.splitspends.controller.UserController;
import com.dasa.splitspends.model.User;
import com.dasa.splitspends.repository.UserRepository;

public class UserControllerMockTests {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserRepository userRepository;

    @Before
    public void init() {
	MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetUserByID() {
	User u = new User();
        u.setId(1l);
        when(userRepository.findById(1l)).thenReturn(Optional.of(u));
        User user = userController.getUserById(1L);
        verify(userRepository).findById(1l);
        assertThat(user.getId(), is(equalTo(1l)));
    }

}
