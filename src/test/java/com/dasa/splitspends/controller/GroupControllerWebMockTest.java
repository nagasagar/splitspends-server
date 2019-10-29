package com.dasa.splitspends.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.dasa.splitspends.model.Group;
import com.dasa.splitspends.model.User;
import com.dasa.splitspends.repository.GroupRepository;
import com.dasa.splitspends.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = GroupController.class, secure = false)
public class GroupControllerWebMockTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private GroupRepository groupRepository;

	@MockBean
	private UserRepository userRepository;

	@Autowired
	ObjectMapper objectMapper;

	private User alexa;

	private User siri;

	private User google;

	private User bixby;

	private User cortana;

	private Group assistants;

	@Before
	public void setUp() {
		alexa = createUser(101l, "alexa@gmail.com", "alexa", "P@55W0rd");
		siri = createUser(102l, "siri@gmail.com", "siri", "P@55W0rd");
		google = createUser(103l, "google@gmail.com", "google", "P@55W0rd");
		bixby = createUser(104l, "bixby@gmail.com", "bixby", "P@55W0rd");
		cortana = createUser(105l, "cortana@gmail.com", "cortana", "P@55W0rd");

		assistants = createGroup(201l, "assistants", new User[] { alexa, siri, google, bixby });

	}

	private User createUser(long id, String email, String name, String password) {
		User user = new User();
		user.setId(id);
		user.setEmail(email);
		user.setName(name);
		user.setPassword(password);
		return user;
	}

	private Group createGroup(Long id, String name, User[] members) {
		Group group = new Group();
		group.setId(id);
		group.setName(name);
		for (User u : members) {
			group.addMember(u);
		}
		return group;
	}

	@After
	public void cleanup() {
		reset(groupRepository);
	}

	@Test
	public void givenId_whenGetGroupByID_thenReturnGroupJsonObject() throws Exception {
		when(groupRepository.findById(Mockito.any())).thenReturn(Optional.of(assistants));
		mvc.perform(get("/groups/{id}", "201").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(201))).andExpect(jsonPath("$.name", is(assistants.getName())))
				.andExpect(jsonPath("$.members", hasSize(4)))
				.andExpect(jsonPath("$.members",
						hasItem(objectMapper.readValue(objectMapper.writeValueAsString(alexa), Map.class))))
				.andExpect(jsonPath("$.members",
						hasItem(objectMapper.readValue(objectMapper.writeValueAsString(siri), Map.class))))
				.andExpect(jsonPath("$.members",
						hasItem(objectMapper.readValue(objectMapper.writeValueAsString(google), Map.class))))
				.andExpect(jsonPath("$.members",
						hasItem(objectMapper.readValue(objectMapper.writeValueAsString(bixby), Map.class))));
		verify(groupRepository, VerificationModeFactory.times(1)).findById(Mockito.any());
	}

	@Test
	public void givenId_whenDeleteGroupByID_thenDeletesGroup() throws Exception {
		when(groupRepository.findById(Mockito.any())).thenReturn(Optional.of(assistants));
		mvc.perform(delete("/groups/{id}", "201").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
		verify(groupRepository, VerificationModeFactory.times(1)).findById(Mockito.any());
		ArgumentCaptor<Group> captor = ArgumentCaptor.forClass(Group.class);
		verify(groupRepository).delete(captor.capture());
		Group argument = captor.getValue();
		assertTrue(argument.equals(assistants));
	}

	@Test
	public void givennewGroup_whenCreateGroup_thenCreatesGroup() throws Exception {
		when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(alexa));
		
		Group newGroup = createGroup(null, "top2", new User[] { google });
		Group created = createGroup(10001l, "top2", new User[] { google });
		when(groupRepository.save(Mockito.any())).thenReturn(created);
		mvc.perform(post("/groups").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newGroup))).andExpect(status().isCreated());
		verify(userRepository).findById(Mockito.any());
		ArgumentCaptor<Group> captor = ArgumentCaptor.forClass(Group.class);
		verify(groupRepository).save(captor.capture());
		Group argument = captor.getValue();
		assertTrue(argument.getMembers().size() == 2);
		assertTrue(argument.getMembers().contains(alexa));
		assertTrue(argument.getMembers().contains(google));
	}

	@Test
	public void givenGroup_whenAddmember_thenAddsMember() throws Exception {

		when(groupRepository.findById(201l)).thenReturn(Optional.of(assistants));
		mvc.perform(post("/groups/{id}/addmember", "201").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(cortana))).andExpect(status().isOk());
		ArgumentCaptor<Group> captor = ArgumentCaptor.forClass(Group.class);
		verify(groupRepository).save(captor.capture());
		Group argument = captor.getValue();
		assertTrue(argument.getMembers().contains(cortana));

	}
	
	@Test
	public void givenGroup_whenRemovemember_thenRemovesMember() throws Exception {

		when(groupRepository.findById(201l)).thenReturn(Optional.of(assistants));
		mvc.perform(post("/groups/{id}/removemember", "201").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(bixby))).andExpect(status().isOk());
		ArgumentCaptor<Group> captor = ArgumentCaptor.forClass(Group.class);
		verify(groupRepository).save(captor.capture());
		Group argument = captor.getValue();
		assertFalse(argument.getMembers().contains(bixby));

	}

}
