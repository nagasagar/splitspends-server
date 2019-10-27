package com.dasa.splitspends.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = "email") })
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Email
    @Column(nullable = false)
    private String email;

    private String imageUrl;

    @Column(nullable = false)
    private Boolean emailVerified = false;

    @JsonIgnore
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @JsonIgnore
    private String providerId;

    @ManyToMany
    @JoinTable(name = "friends", 
    joinColumns = @JoinColumn(name = "userId", referencedColumnName = "id"), 
    inverseJoinColumns = @JoinColumn(name = "friendId", referencedColumnName = "id"))
    @JsonIgnore
    private Set<User> friends = new HashSet<>();
    
    @ManyToMany(mappedBy="friends")
    @JsonIgnore
    private Set<User> mates = new HashSet<User>();

    @ManyToMany(mappedBy = "members")
    @JsonIgnore
    private Set<Group> groups;

    @OneToMany(mappedBy = "payee", targetEntity = Payment.class, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Payment> payments;

    @OneToMany(mappedBy = "spender", targetEntity = Share.class, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Share> shares;

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getEmail() {
	return email;
    }

    public void setEmail(String email) {
	this.email = email;
    }

    public String getImageUrl() {
	return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
	this.imageUrl = imageUrl;
    }

    public Boolean getEmailVerified() {
	return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
	this.emailVerified = emailVerified;
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    public AuthProvider getProvider() {
	return provider;
    }

    public void setProvider(AuthProvider provider) {
	this.provider = provider;
    }

    public String getProviderId() {
	return providerId;
    }

    public void setProviderId(String providerId) {
	this.providerId = providerId;
    }

    public Set<User> getFriends() {
	return friends;
    }

    public void setFriends(Set<User> friends) {
	this.friends = friends;
    }

    public Set<Group> getGroups() {
	return groups;
    }

    public Set<Payment> getPayments() {
	return payments;
    }

    public Set<Share> getShares() {
	return shares;
    }

    public void addFriend(User friend) {
	this.friends.add(friend);
    }

    public boolean removeFriend(User friend) {
	return this.friends.remove(friend);
    }
    
    public boolean removeMate(User friend) {
	return this.mates.remove(friend);
    }

    @Override
    public boolean equals(Object o) {
	if (this == o)
	    return true;
	if (o == null)
	    return false;
	if (getClass() != o.getClass())
	    return false;
	User user = (User) o;
	return Objects.equals(id, user.id) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
	return Objects.hash(id, email);
    }
}
