package com.example.spring;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.spring.model.User;
import com.example.spring.repository.UserRepository;
import com.example.spring.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;


    @Test
    public void testSaveUser() {
        User user = new User("username", "test@example.com", "password");
        when(userRepository.save(user)).thenReturn(user);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        User savedUser = userService.saveUser(user);
        assertTrue(encoder.matches("password", savedUser.getPassword()));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testUpdateUser() {
        User user = new User("username", "test@example.com", "password");
        when(userRepository.save(user)).thenReturn(user);

        User updatedUser = userService.updateUser(user);

        assertEquals(user, updatedUser);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testDeleteUser() {
        User user = new User("username", "test@example.com", "password");
        doNothing().when(userRepository).delete(user);

        userService.deleteUser(user);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    public void testDeleteUserById() {
        Long userId = 1L;
        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUserById(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    public void testDeleteByEmail() {
        String email = "test@example.com";
        doNothing().when(userRepository).deleteByEmail(email);

        userService.deleteByEmail(email);

        verify(userRepository, times(1)).deleteByEmail(email);
    }

    @Test
    public void testFindAll() {
        List<User> users = new ArrayList<>();
        users.add(new User("username", "test1@example.com", "password1"));
        users.add(new User("username2", "test2@example.com", "password2"));

        when(userRepository.findAll()).thenReturn(users);

        List<User> foundUsers = userService.findAll();

        assertEquals(users, foundUsers);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testFindByPartOfUsernameOrEmail() {
        List<User> users = new ArrayList<>();
        users.add(new User("username", "test1@example.com", "password1"));
        users.add(new User("username2", "test2@example.com", "password2"));

        when(userRepository.findByPartOfUsernameOrEmail("test")).thenReturn(users);

        List<User> foundUsers = userService.findByPartOfUsernameOrEmail("test");

        assertEquals(users, foundUsers);
        verify(userRepository, times(1)).findByPartOfUsernameOrEmail("test");
    }

    @Test
    public void testFindById() {
        Long userId = 1L;
        User user = new User("username", "test@example.com", "password");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.findById(userId);

        assertTrue(foundUser.isPresent());
        assertEquals(user, foundUser.get());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testFindByEmail() {
        String email = "test@example.com";
        User user = new User("username", email, "password");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.findByEmail(email);

        assertTrue(foundUser.isPresent());
        assertEquals(user, foundUser.get());
        verify(userRepository, times(1)).findByEmail(email);
    }
}
