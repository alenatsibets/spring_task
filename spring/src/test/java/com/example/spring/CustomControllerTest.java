package com.example.spring;

import com.example.spring.controller.CustomController;
import com.example.spring.model.User;
import com.example.spring.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CustomController.class)
class CustomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CustomController(userService)).build();
    }

    @Test
    public void testShowFormWhenUserNotLoggedIn() throws Exception {
        mockMvc.perform(get("/form"))
                .andExpect(status().isOk())
                .andExpect(view().name("logining"));
    }

    @Test
    public void testShowFormWhenUserLoggedIn() throws Exception {
        mockMvc.perform(get("/form").sessionAttr("user", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("menu"));
    }

    @Test
    public void testMultiplyNumberWhenUserLoggedIn() throws Exception {
        mockMvc.perform(post("/multiply").param("number", "5").sessionAttr("user", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("result", 25))
                .andExpect(view().name("result"));
    }

    @Test
    public void testShowListOfUsersWhenUserLoggedIn() throws Exception {
        List<User> users = Arrays.asList(new User("user1", "user1@example.com", "password1"),
                new User("user2", "user2@example.com", "password2"));
        when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/list_of_users").sessionAttr("user", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("result", users))
                .andExpect(view().name("users_list"));
    }

    @Test
    public void testShowListOfUsersByPatternWhenUserLoggedIn() throws Exception {

        List<User> users = Arrays.asList(new User("user1", "user1@example.com", "password1"),
                new User("user2", "user2@example.com", "password2"));
        when(userService.findByPartOfUsernameOrEmail("user")).thenReturn(users);

        mockMvc.perform(post("/list_of_users_by_pattern").param("pattern", "user").sessionAttr("user", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("result", users))
                .andExpect(view().name("users_list_by_pattern"));
    }

    @Test
    public void testRegister() throws Exception {
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";

        mockMvc.perform(post("/register")
                        .param("username", username)
                        .param("email", email)
                        .param("password", password))
                .andExpect(status().isOk())
                .andExpect(view().name("registration_result"));

        verify(userService, times(1)).saveUser(any(User.class));
    }

    @Test
    public void testLoginSuccessful() throws Exception {
        when(userService.authenticate("test@example.com", "password123")).thenReturn(true);

        mockMvc.perform(post("/login")
                        .param("email", "test@example.com")
                        .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("form"))
                .andExpect(model().attribute("message", ""))
                .andExpect(request().sessionAttribute("user", "test@example.com"));

    }

    @Test
    public void testLoginFailed() throws Exception {
        when(userService.authenticate("test@example.com", "wrongpassword")).thenReturn(false);

        mockMvc.perform(post("/login")
                        .param("email", "test@example.com")
                        .param("password", "wrongpassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("logining"))
                .andExpect(model().attribute("message", "incorrect email or password"));
    }

    @Test
    public void testLogout() throws Exception {
        mockMvc.perform(get("/logout").sessionAttr("user", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("logining"))
                .andExpect(request().sessionAttributeDoesNotExist("user"));
    }

    @Test
    public void testChangePasswordWhenUserLoggedIn() throws Exception {
        mockMvc.perform(post("/change-password").sessionAttr("user", "test@example.com")
                        .param("email", "test@example.com")
                        .param("oldPassword", "oldpassword")
                        .param("newPassword", "newpassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("password_change_result"))
                .andExpect(model().attributeExists("result"));

        verify(userService, times(1)).changePassword("test@example.com", "oldpassword", "newpassword");
    }

    @Test
    public void testDeleteUserWhenUserLoggedInAndAuthenticated() throws Exception {
        when(userService.authenticate("test@example.com", "password123")).thenReturn(true);

        mockMvc.perform(post("/delete").sessionAttr("user", "test@example.com")
                        .param("email", "test@example.com")
                        .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("delete_result"));

        verify(userService, times(1)).deleteByEmail("test@example.com");
    }

    @Test
    public void testDeleteUserWhenUserLoggedInAndNotAuthenticated() throws Exception {
        when(userService.authenticate("test@example.com", "wrongpassword")).thenReturn(false);

        mockMvc.perform(post("/delete").sessionAttr("user", "test@example.com")
                        .param("email", "test@example.com")
                        .param("password", "wrongpassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("deleting"))
                .andExpect(model().attribute("message", "incorrect email or password"));
    }

    @Test
    public void testChangeUsernameWhenUserLoggedIn() throws Exception {
        mockMvc.perform(post("/change-username").sessionAttr("user", "test@example.com")
                        .param("email", "test@example.com")
                        .param("password", "oldpassword")
                        .param("newUsername", "newusername"))
                .andExpect(status().isOk())
                .andExpect(view().name("username_change_result"))
                .andExpect(model().attributeExists("result"));

        verify(userService, times(1)).changeUsername("test@example.com", "oldpassword", "newusername");
    }
}
