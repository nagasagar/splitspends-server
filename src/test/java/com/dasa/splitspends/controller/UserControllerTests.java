package com.dasa.splitspends.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.hasSize;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.StreamUtils;

import com.dasa.splitspends.SpringSocialApplication;
import com.dasa.splitspends.config.DBConfiguration;
import com.dasa.splitspends.payload.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SpringSocialApplication.class, DBConfiguration.class })
@ActiveProfiles("web")
@AutoConfigureMockMvc
public class UserControllerTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Value("classpath:nsahas_friends.json")
    private Resource nsahasFriendsResource;

    private String jwt;

    @Before
    public void steUp() throws Exception {
	jwt = obtainAccessToken("nsahas@gmail.com", "splitspends");
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

    @Test
    public void testGetCurrentUser() throws Exception {
	mvc.perform(get("/users/me").header("Authorization", "Bearer " + jwt).accept("application/json;charset=UTF-8"))
		.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"))
		.andExpect(jsonPath("$.name", is("nsahas"))).andExpect(content().string(
			"{\"id\":1001,\"name\":\"nsahas\",\"email\":\"nsahas@gmail.com\",\"imageUrl\":null,\"emailVerified\":false,\"provider\":\"local\"}"));
    }

    @Test
    public void testGetuserByID() throws Exception {
	mvc.perform(
		get("/users/1001").header("Authorization", "Bearer " + jwt).accept("application/json;charset=UTF-8"))
		.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"))
		.andExpect(jsonPath("$.name", is("nsahas"))).andExpect(content().string(
			"{\"id\":1001,\"name\":\"nsahas\",\"email\":\"nsahas@gmail.com\",\"imageUrl\":null,\"emailVerified\":false,\"provider\":\"local\"}"));
    }

    @Test
    public void testGetuserByEmail() throws Exception {
	mvc.perform(get("/users/email").param("email", "nsahas@gmail.com").header("Authorization", "Bearer " + jwt)
		.accept("application/json;charset=UTF-8")).andExpect(status().isOk())
		.andExpect(content().contentType("application/json;charset=UTF-8"))
		.andExpect(jsonPath("$.name", is("nsahas"))).andExpect(content().string(
			"{\"id\":1001,\"name\":\"nsahas\",\"email\":\"nsahas@gmail.com\",\"imageUrl\":null,\"emailVerified\":false,\"provider\":\"local\"}"));
    }

    @Test
    public void testGetuserFriends() throws Exception {
	String expected = StreamUtils.copyToString(nsahasFriendsResource.getInputStream(), StandardCharsets.UTF_8);
	MvcResult result = mvc
		.perform(get("/friends").header("Authorization", "Bearer " + jwt)
			.accept("application/json;charset=UTF-8"))
		.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"))
		.andExpect(jsonPath("$", hasSize(3))).andReturn();
	String actual = result.getResponse().getContentAsString();
	JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    @DirtiesContext
    public void testAdduserFriend() throws Exception {
	MvcResult userResult = mvc
		.perform(get("/users/email").param("email", "babureddy@gmail.com")
			.header("Authorization", "Bearer " + jwt).accept("application/json;charset=UTF-8"))
		.andExpect(status().isOk()).andReturn();
	
	mvc.perform(post("/addfriend").header("Authorization", "Bearer " + jwt).contentType(MediaType.APPLICATION_JSON)
		.content(userResult.getResponse().getContentAsString()).accept("application/json;charset=UTF-8"))
		.andExpect(status().isOk());

	mvc.perform(get("/friends").header("Authorization", "Bearer " + jwt).accept("application/json;charset=UTF-8"))
		.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"))
		.andExpect(jsonPath("$", hasSize(4))).andExpect(jsonPath("$",
			hasItem(objectMapper.readValue(userResult.getResponse().getContentAsString(), Map.class))));
    }


    @Test
    @DirtiesContext
    public void testRemoveuserFriendDirect() throws Exception {
	MvcResult userResult = mvc
		.perform(get("/users/email").param("email", "shaurya@gmail.com")
			.header("Authorization", "Bearer " + jwt).accept("application/json;charset=UTF-8"))
		.andExpect(status().isOk()).andReturn();
	
	mvc.perform(post("/removefriend").header("Authorization", "Bearer " + jwt).contentType(MediaType.APPLICATION_JSON)
		.content(userResult.getResponse().getContentAsString()).accept("application/json;charset=UTF-8"))
		.andExpect(status().isOk());

	mvc.perform(get("/friends").header("Authorization", "Bearer " + jwt).accept("application/json;charset=UTF-8"))
		.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"))
		.andExpect(jsonPath("$", hasSize(2)))
		.andExpect(content().string(not(containsString("shaurya@gmail.com"))))
		.andExpect(jsonPath("$", not(hasItem(objectMapper.readValue(userResult.getResponse().getContentAsString(), Map.class)))));
    }
    
    @Test
    @DirtiesContext
    public void testRemoveuserFriendReverse() throws Exception {
	MvcResult userResult = mvc
		.perform(get("/users/email").param("email", "sirimm@gmail.com")
			.header("Authorization", "Bearer " + jwt).accept("application/json;charset=UTF-8"))
		.andExpect(status().isOk()).andReturn();
	
	mvc.perform(post("/removefriend").header("Authorization", "Bearer " + jwt).contentType(MediaType.APPLICATION_JSON)
		.content(userResult.getResponse().getContentAsString()).accept("application/json;charset=UTF-8"))
		.andExpect(status().isOk());

	mvc.perform(get("/friends").header("Authorization", "Bearer " + jwt).accept("application/json;charset=UTF-8"))
		.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"))
		.andExpect(jsonPath("$", hasSize(2)))
		.andExpect(content().string(not(containsString("sirimm@gmail.com"))))
		.andExpect(jsonPath("$", not(hasItem(objectMapper.readValue(userResult.getResponse().getContentAsString(), Map.class)))));
    }
}
