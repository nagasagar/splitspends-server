package com.dasa.splitspends.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.reset;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.MatcherAssert.assertThat;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


import com.dasa.splitspends.controller.UserController;
import com.dasa.splitspends.model.User;
import com.dasa.splitspends.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.MediaType;


@RunWith(SpringRunner.class)
@WebMvcTest(controllers = UserController.class, secure = false)
public class UserControllerWebMockTets {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserRepository userRepository;
    
    @Autowired
    ObjectMapper objectMapper;
    
    private User alexa;
    
    private User siri;
    
    private User google;
    
    private User bixby;
    
    @Before
    public void setUp() {
	alexa = createUser(101l,"alexa@gmail.com","alex","P@55W0rd");
	siri = createUser(101l,"siri@gmail.com","siri","P@55W0rd");
	google = createUser(101l,"google@gmail.com","google","P@55W0rd");
	bixby = createUser(101l,"bixby@gmail.com","bixby","P@55W0rd");
	
    }
    
    private User createUser(long id, String email, String name, String password) {
	User user = new User();
	user.setId(id);
	user.setEmail(email);
	user.setName(name);
	user.setPassword(password);
	return user;
    }

    @After
    public void cleanup() {
	reset(userRepository);
    }

    @Test
    public void givenUser_whenGetUserMe_thenReturnUserJsonObject() throws Exception {
	when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(alexa));
	mvc.perform(get("/users/me").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
		.andExpect(jsonPath("$.id", is(101))).andExpect(jsonPath("$.email", is(alexa.getEmail())))
		.andExpect(jsonPath("$.name", is(alexa.getName()))).andExpect(jsonPath("$.provider", equalTo(null)))
		.andExpect(jsonPath("$.imageUrl", equalTo(null))).andExpect(jsonPath("$.emailVerified").value(false))
		.andExpect(jsonPath("$.providerId").doesNotExist()).andExpect(jsonPath("$.friends").doesNotExist())
		.andExpect(jsonPath("$.mates").doesNotExist()).andExpect(jsonPath("$.groups").doesNotExist())
		.andExpect(jsonPath("$.payments").doesNotExist()).andExpect(jsonPath("$.shares").doesNotExist())
		.andExpect(jsonPath("$.password").doesNotExist());
	verify(userRepository, VerificationModeFactory.times(1)).findById(Mockito.any());
    }
    
    @Test
    public void givenUser_whenGetUserById_thenReturnUserJsonObject() throws Exception {
	when(userRepository.findById(101l)).thenReturn(Optional.of(alexa));
	mvc.perform(get("/users/101").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
		.andExpect(jsonPath("$.id", is(101))).andExpect(jsonPath("$.email", is(alexa.getEmail())))
		.andExpect(jsonPath("$.name", is(alexa.getName()))).andExpect(jsonPath("$.provider", equalTo(null)))
		.andExpect(jsonPath("$.imageUrl", equalTo(null))).andExpect(jsonPath("$.emailVerified").value(false))
		.andExpect(jsonPath("$.providerId").doesNotExist()).andExpect(jsonPath("$.friends").doesNotExist())
		.andExpect(jsonPath("$.mates").doesNotExist()).andExpect(jsonPath("$.groups").doesNotExist())
		.andExpect(jsonPath("$.payments").doesNotExist()).andExpect(jsonPath("$.shares").doesNotExist())
		.andExpect(jsonPath("$.password").doesNotExist());
	ArgumentCaptor<Long> longCaptor = ArgumentCaptor.forClass(Long.class);
	verify(userRepository, VerificationModeFactory.times(1)).findById(longCaptor.capture());
	assertTrue(longCaptor.getValue()==101l);
    }
    
    @Test
    public void givenUser_whenGetUserByEmail_thenReturnUserJsonObject() throws Exception {
	when(userRepository.findByEmail("alexa@gmail.com")).thenReturn(Optional.of(alexa));
	mvc.perform(get("/users/email").param("email","alexa@gmail.com").contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andExpect(jsonPath("$.id", is(101)))
		.andExpect(jsonPath("$.email", is(alexa.getEmail()))).andExpect(jsonPath("$.name", is(alexa.getName())))
		.andExpect(jsonPath("$.provider", equalTo(null))).andExpect(jsonPath("$.imageUrl", equalTo(null)))
		.andExpect(jsonPath("$.emailVerified").value(false)).andExpect(jsonPath("$.providerId").doesNotExist())
		.andExpect(jsonPath("$.friends").doesNotExist()).andExpect(jsonPath("$.mates").doesNotExist())
		.andExpect(jsonPath("$.groups").doesNotExist()).andExpect(jsonPath("$.payments").doesNotExist())
		.andExpect(jsonPath("$.shares").doesNotExist()).andExpect(jsonPath("$.password").doesNotExist());
	ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
	verify(userRepository, VerificationModeFactory.times(1)).findByEmail(stringCaptor.capture());
	assertEquals(stringCaptor.getValue(), "alexa@gmail.com");
    }
    
    
    @Test
    public void givenUser_whenGetUserFriends_thenReturnFriendsJsonListObject() throws Exception {
	List<User> friends = Arrays.asList(siri, google, bixby);
	when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(alexa));
	when(userRepository.findFriendsOfuser(Mockito.any())).thenReturn(friends);
	mvc.perform(get("/friends").contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(3)))
		.andExpect(jsonPath("$[0].name", is(siri.getName())))
		.andExpect(jsonPath("$[1].name", is(google.getName())))
		.andExpect(jsonPath("$[2].name", is(bixby.getName())));
	verify(userRepository, VerificationModeFactory.times(1)).findFriendsOfuser(Mockito.any());
    }
    
    
    @Test
    public void givenUser_whenAddFriend_thenFriendAddedAndReturnSuccessResponse() throws Exception {
	when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(alexa));
	mvc.perform(post("/addfriend").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(siri)))
		.andExpect(status().isOk());
	ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
	verify(userRepository, VerificationModeFactory.times(1)).save(userCaptor.capture());
	assertThat(userCaptor.getValue().getFriends(), hasItem(siri));
    }
    
    @Test
    public void givenUser_whenRemoveFriend_thenFriendRemovedAndReturnSuccessResponse() throws Exception {
	when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(alexa));
	alexa.addFriend(siri);
	mvc.perform(post("/removefriend").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(siri)))
		.andExpect(status().isOk());
	ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
	verify(userRepository, VerificationModeFactory.times(1)).save(userCaptor.capture());
	assertThat(userCaptor.getValue().getFriends(), not(hasItem(siri)));
    }

}
