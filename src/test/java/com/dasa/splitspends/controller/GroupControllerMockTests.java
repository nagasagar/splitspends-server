package com.dasa.splitspends.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.dasa.splitspends.model.Group;
import com.dasa.splitspends.model.User;
import com.dasa.splitspends.repository.GroupRepository;
import com.dasa.splitspends.repository.UserRepository;
import com.dasa.splitspends.security.UserPrincipal;

public class GroupControllerMockTests {
    Group g;
    User u,m;

    @InjectMocks
    private GroupController groupController;
    
    @Mock
    private UserRepository userRepository;

    @Mock
    private GroupRepository groupRepository;
    
    @Mock
    private User mockUser;
    
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void init() {
	MockitoAnnotations.initMocks(this);
	g = new Group();
        g.setId(1l);
        g.setName("someGroup");
        
        u = new User();
        u.setId(1l);
        u.setEmail("something@anything.com");
        m = new User();
        m.setId(2l);
    }

    @Test
    public void testGetGroupByID() {
        when(groupRepository.findById(1l)).thenReturn(Optional.of(g));
        Group group = groupController.getGroupById(1L);
        verify(groupRepository).findById(1l);
        assertThat(group.getId(), is(equalTo(1l)));
    }
    
    
    @Test
    public void testDeleteGroupByID() {
        when(groupRepository.findById(Mockito.any())).thenReturn(Optional.of(g));
        groupController.deleteGroupByID(g.getId());
        verify(groupRepository).findById(g.getId());
        ArgumentCaptor<Group> captor = ArgumentCaptor.forClass(Group.class);
        verify(groupRepository).delete(captor.capture());
        Group argument = captor.getValue();
        assertTrue(argument.equals(g));
    }
 
    @Test
    public void testaddMemberToGroup() {
	
        when(groupRepository.findById(1l)).thenReturn(Optional.of(g));
        groupController.addmemberToGroup(1,m);
        ArgumentCaptor<Group> captor = ArgumentCaptor.forClass(Group.class);
        verify(groupRepository).save(captor.capture());
        Group argument = captor.getValue();
        assertTrue(argument.getMembers().contains(m));
        
    }
    
    @Test
    public void testremoveMemberFromGroup() {
	g.addMember(u);
	g.addMember(m);
	 when(groupRepository.findById(1l)).thenReturn(Optional.of(g));
	
	 groupController.removeMemberFromGroup(1,m);
        ArgumentCaptor<Group> captor = ArgumentCaptor.forClass(Group.class);
        verify(groupRepository).save(captor.capture());
        Group argument = captor.getValue();
        assertTrue(argument.getMembers().size()==1);
        assertFalse(argument.getMembers().contains(m));
        assertTrue(argument.getMembers().contains(u));
        
    }
    
}
