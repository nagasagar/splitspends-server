package com.dasa.splitspends.controller;

import com.dasa.splitspends.exception.ResourceNotFoundException;
import com.dasa.splitspends.model.User;
import com.dasa.splitspends.repository.UserRepository;
import com.dasa.splitspends.security.CurrentUser;
import com.dasa.splitspends.security.UserPrincipal;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users/me")
    @PreAuthorize("hasRole('USER')")
    public User getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    }
    
    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('USER')")
    public User getUserById(@PathVariable long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }
    
    @GetMapping("/users/email")
    @PreAuthorize("hasRole('USER')")
    public User getUserByEmail(@RequestParam String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }
    
    
    @GetMapping("/friends")
    public List<User> getFriendsOfUser(@CurrentUser UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
       
        return  userRepository.findFriendsOfuser(user.getId());
    }
    
    @PostMapping("/addfriend")
    public void addFriendToUser(@CurrentUser UserPrincipal userPrincipal,@RequestBody User friend) {
	 User user = userRepository.findById(userPrincipal.getId())
	                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
	if(user.equals(friend)) {
	    throw new IllegalArgumentException("you cannot add yourself as a friend");
	}
        user.addFriend(friend);
        userRepository.save(user);
    }
    
    @PostMapping("/removefriend")
    public void removeFriendToUser(@CurrentUser UserPrincipal userPrincipal,@RequestBody User friend) {
	 User user = userRepository.findById(userPrincipal.getId())
	                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
        user.removeFriend(friend);
        userRepository.save(user);
    }

}
