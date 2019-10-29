package com.dasa.splitspends.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.StreamUtils;

import com.dasa.splitspends.model.Group;
import com.dasa.splitspends.model.User;

public class GroupControllerTest extends BaseControllerTest {

	@Value("classpath:spain.json")
	private Resource spainGroupResource;
	
	private Group createGroup(Long id, String name, User[] members) {
		Group group = new Group();
		group.setId(id);
		group.setName(name);
		for (User u : members) {
			group.addMember(u);
		}
		return group;
	}

	@Test
	public void testGetGroupByID() throws Exception {
		String expected = StreamUtils.copyToString(spainGroupResource.getInputStream(), StandardCharsets.UTF_8);
		MvcResult result = mvc
				.perform(get("/groups/{id}", "2006").header("Authorization", "Bearer " + jwt)
						.accept("application/json;charset=UTF-8"))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.name", is("Spain"))).andReturn();
		String actual = result.getResponse().getContentAsString();
		JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
	}

	@Test
	@DirtiesContext
	public void testDeleteGroupById() throws Exception {
		mvc.perform(delete("/groups/{id}", "2006").header("Authorization", "Bearer " + jwt)
				.accept("application/json;charset=UTF-8")).andExpect(status().isOk());
		
		mvc.perform(get("/groups/{id}", "2006").header("Authorization", "Bearer " + jwt)
				.accept("application/json;charset=UTF-8"))
				.andExpect(status().isNotFound());
	}
	
	@Test
	@DirtiesContext
	public void testCreateNewGroup() throws Exception {
		Group newGroup = createGroup(null, "2brothers", new User[] { nsagar });
		MvcResult createResult = mvc.perform(post("/groups").header("Authorization", "Bearer " + jwt).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newGroup))).andExpect(status().isCreated()).andReturn();
		
		String id = createResult.getResponse().getHeader("location").split("groups/")[1];
		
		MvcResult findRresult = mvc
				.perform(get("/groups/{id}",id).header("Authorization", "Bearer " + jwt)
						.accept("application/json;charset=UTF-8"))
				.andExpect(status().isOk()).andExpect(content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.name", is("2brothers")))
				.andReturn();
		
		Group createdGroup = objectMapper.readValue(findRresult.getResponse().getContentAsString(), Group.class);
		
		assertTrue("members not found as expected - creater should be already a group member", createdGroup.getMembers().contains(nsahas));
		assertTrue("members not found as expected - creater should be already a group member", createdGroup.getMembers().contains(nsagar));
	}

}
