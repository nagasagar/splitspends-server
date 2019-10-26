package com.dasa.splitspends.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.dasa.splitspends.SpringSocialApplication;
import com.dasa.splitspends.config.DBConfiguration;
import com.dasa.splitspends.repository.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= {SpringSocialApplication.class, DBConfiguration.class})
@ActiveProfiles("web")
public class UserControllerTests {
    
    	@Autowired
    	UserRepository userRepository;

	@Test
	public void getAllUsers() {
	    System.out.println(userRepository.findAll().size());
	}

}
