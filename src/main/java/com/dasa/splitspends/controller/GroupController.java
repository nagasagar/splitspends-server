package com.dasa.splitspends.controller;

import java.net.URI;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.dasa.splitspends.exception.ResourceNotFoundException;
import com.dasa.splitspends.model.Group;
import com.dasa.splitspends.model.User;
import com.dasa.splitspends.payload.ApiResponse;
import com.dasa.splitspends.repository.GroupRepository;
import com.dasa.splitspends.repository.UserRepository;
import com.dasa.splitspends.security.CurrentUser;
import com.dasa.splitspends.security.UserPrincipal;

@RestController
public class GroupController {

    @Autowired
    private GroupRepository groupRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    //TODO only group members should be able to get group details, add members, remove members
    
    @GetMapping("/groups/{groupId}")
    public Group getGroupById(@PathVariable long groupId) {
	Group group = groupRepository.findById(groupId)
		.orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));

	return group;
    }
    @DeleteMapping("/groups/{groupId}")
    public void deleteGroupByID(@PathVariable long groupId) {
	Group group = groupRepository.findById(groupId)
		.orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));
	
	groupRepository.delete(group);
    }
    
    @GetMapping("/groups")
    public Set<Group> getgroupsOfUser(@CurrentUser UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
        return  user.getGroups();
    }
    
    @PostMapping("/groups")
    public ResponseEntity<?> createNewGroup(@CurrentUser UserPrincipal userPrincipal, @RequestBody Group group) {
	if(group.getId()!=null) {
	    //TODO group already exists
		URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/groups/{id}")
                .buildAndExpand(group.getId()).toUri();
		 return ResponseEntity.created(location)
	                .body(new ApiResponse(true, "User registered successfully"));
	}else {
	    Group newGroup = new Group();
	    newGroup.setName(group.getName());
	    User usr = userRepository.findById(userPrincipal.getId())
	                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
	    newGroup.addMember(usr);
	    for (User user : group.getMembers()) {
		newGroup.addMember(user);
	    }
	    Group result =groupRepository.save(newGroup);
	    
	    URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/groups/{id}")
                .buildAndExpand(result.getId()).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "Group created successfully"));
	}
	
    }

    @PostMapping("/groups/{groupId}/addmember")
    public void addmemberToGroup(@PathVariable long groupId, @RequestBody User user) {
	Group group = groupRepository.findById(groupId)
		.orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));
	group.addMember(user);
	groupRepository.save(group);
    }

    @PostMapping("/groups/{groupId}/removemember")
    public void removeMemberFromGroup(@PathVariable long groupId, @RequestBody User user) {
	Group group = groupRepository.findById(groupId)
		.orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));
	group.removeMember(user);
	groupRepository.save(group);
    }

}
