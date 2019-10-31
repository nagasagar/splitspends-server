package com.dasa.splitspends.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.dasa.splitspends.SpringSocialApplication;
import com.dasa.splitspends.config.DBConfiguration;
import com.dasa.splitspends.model.User;
import com.dasa.splitspends.payload.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringSocialApplication.class, DBConfiguration.class })
@ActiveProfiles("web")
@AutoConfigureMockMvc
public class ControllerTestBase {
	
	String jwt;
	
	@Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;
    
    User nsagar;
    User nsahas;
    User sirimm;
    User nsanvi;
    User sneham;
    
    @Before
    public void setUp() throws Exception {
	jwt = obtainAccessToken("nsahas@gmail.com", "splitspends");
	nsagar = getUser(1002l);
	nsahas = getUser(1001l);
	sirimm = getUser(1003l);
	sneham = getUser(1004l);
	nsanvi = getUser(1008l);
    }

    private User getUser(Long id) throws Exception {
    	MvcResult result = mvc
    			.perform(get("/users/{id}",id.toString()).header("Authorization", "Bearer " + jwt).contentType(MediaType.APPLICATION_JSON))
    			.andDo(print()).andExpect(status().isOk()).andReturn();
    	
		return objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
	}

	private String obtainAccessToken(String email, String password) throws Exception {

	LoginRequest loginRequest = new LoginRequest();
	loginRequest.setEmail(email);
	loginRequest.setPassword(password);

	ResultActions result = mvc
		.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(loginRequest))
			.accept("application/json;charset=UTF-8"))
		.andDo(print()).andExpect(status().isOk())
		.andExpect(content().contentType("application/json;charset=UTF-8"));

	String resultString = result.andReturn().getResponse().getContentAsString();

	JacksonJsonParser jsonParser = new JacksonJsonParser();
	return jsonParser.parseMap(resultString).get("accessToken").toString();
    }

}
