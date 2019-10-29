package com.dasa.splitspends.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.dasa.splitspends.SpringSocialApplication;
import com.dasa.splitspends.config.DBConfiguration;
import com.dasa.splitspends.payload.LoginRequest;
import com.dasa.splitspends.payload.SignUpRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringSocialApplication.class, DBConfiguration.class })
@ActiveProfiles("web")
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void testLoginSuccesfull() throws JsonProcessingException, Exception {
	LoginRequest loginRequest = new LoginRequest();
	loginRequest.setEmail("nsagar@gmail.com");
	loginRequest.setPassword("splitspends");

	mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
		.content(objectMapper.writeValueAsString(loginRequest)).accept("application/json;charset=UTF-8"))
		.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"))
		.andExpect(jsonPath("$.accessToken").exists()).andExpect(jsonPath("$.tokenType", is("Bearer")));
    }

    @Test
    public void testLoginFailure() throws JsonProcessingException, Exception {
	LoginRequest loginRequest = new LoginRequest();
	loginRequest.setEmail("nsagar@gmail.com");
	loginRequest.setPassword("splitspends1");

	mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
		.content(objectMapper.writeValueAsString(loginRequest)).accept("application/json;charset=UTF-8"))
		.andExpect(status().isUnauthorized()).andExpect(status().reason(containsString("Bad credentials")))
		.andExpect(unauthenticated());
    }
    
    @Test
    @DirtiesContext
    public void testSuccessfullRegistration() throws JsonProcessingException, Exception {
	SignUpRequest signUpRequest = new SignUpRequest();
	signUpRequest.setEmail("suhani@gmail.com");
	signUpRequest.setName("suhani");
	signUpRequest.setPassword("splitspends");
	
	mvc.perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON)
		.content(objectMapper.writeValueAsString(signUpRequest)).accept("application/json;charset=UTF-8"))
		.andExpect(status().isCreated()).andExpect(content().contentType("application/json;charset=UTF-8"))
		.andExpect(jsonPath("$.success", is(true))).andExpect(jsonPath("$.message", is("User registered successfully")));
	
	LoginRequest loginRequest = new LoginRequest();
	loginRequest.setEmail("suhani@gmail.com");
	loginRequest.setPassword("splitspends");

	mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
		.content(objectMapper.writeValueAsString(loginRequest)).accept("application/json;charset=UTF-8"))
		.andExpect(status().isOk());
	
	
    }
    
    @Test
    public void testExistingEmailRegistration() throws JsonProcessingException, Exception {
	SignUpRequest signUpRequest = new SignUpRequest();
	signUpRequest.setEmail("nsagar@gmail.com");
	signUpRequest.setName("nsagar");
	signUpRequest.setPassword("splitspends");
	
	String errorMessage = mvc.perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON)
		.content(objectMapper.writeValueAsString(signUpRequest)).accept("application/json;charset=UTF-8")).andDo(print())
		.andExpect(status().isBadRequest())
		.andExpect(unauthenticated()).andReturn().getResolvedException().getMessage();
	assertEquals(errorMessage, "Email address already in use.");
	
    }
}
