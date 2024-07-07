package com.example.spring.controller;

import com.example.spring.model.User;
import com.example.spring.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CustomController {
    @Autowired
    private final UserService userService;

    public CustomController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/form")
    public String showForm(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "logining";
        }
        return "menu";
    }

    @PostMapping("/multiply")
    public String multiplyNumber(@RequestParam("number") int number, Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "logining";
        }
        int result = number * 5;
        model.addAttribute("result", result);
        return "result";
    }

    @GetMapping("/list_of_users")
    public String showListOfUsers(Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "logining";
        }
        List<User> listOfUsers = userService.findAll();
        model.addAttribute("result", listOfUsers);
        return "users_list";
    }

    @PostMapping("/list_of_users_by_pattern")
    public String showListOfUsersByPattern(@RequestParam("pattern") String pattern, Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "logining";
        }
        List<User> listOfUsers = userService.findByPartOfUsernameOrEmail(pattern);
        model.addAttribute("result", listOfUsers);
        return "users_list_by_pattern";
    }

    @GetMapping("/list_of_users_by_pattern")
    public String pattern(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "logining";
        }
        return "pattern";
    }

    @PostMapping("/register")
    public String register(@RequestParam("username") String username,
                           @RequestParam("email") String email,
                           @RequestParam("password") String password, Model model) {
        User user = new User(username, email, password);
        userService.saveUser(user);
        model.addAttribute("result", user);
        return "registration_result";
    }

    @GetMapping("/")
    public String viewLogin() {
        return "logining";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "logining";
    }

    @PostMapping("/login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password, Model model, HttpSession session) {
        session.setAttribute("user", email);
        String page;
        String message = "";
        if (userService.authenticate(email, password)) {
            page = "menu";
        } else {
            page = "logining";
            message = "incorrect email or password";
        }
        model.addAttribute("message", message);
        return page;
    }

    @GetMapping("/register")
    public String viewRegister() {
        return "registration";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String email,
                                 @RequestParam String oldPassword,
                                 @RequestParam String newPassword, Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "logining";
        }
        model.addAttribute("result", userService.changePassword(email, oldPassword, newPassword));
        return "password_change_result";
    }

    @GetMapping("/change-password")
    public String viewPasswordChange(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "logining";
        }
        return "password_change";
    }

    @Transactional
    @PostMapping("/delete")
    public String deleteUser(@RequestParam String email,
                             @RequestParam String password, Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "logining";
        }
        String page;
        String message = "";
        if (userService.authenticate(email, password)) {
            userService.deleteByEmail(email);
            page = "delete_result";
        } else {
            page = "deleting";
            message = "incorrect email or password";
        }
        model.addAttribute("message", message);
        return page;
    }

    @GetMapping("/delete")
    public String deleteUser(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "logining";
        }
        return "deleting";
    }

    @PostMapping("/change-username")
    public String changeUsername(@RequestParam String email,
                                 @RequestParam String password,
                                 @RequestParam String newUsername, Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "logining";
        }
        model.addAttribute("result", userService.changeUsername(email, password, newUsername));
        return "username_change_result";
    }

    @GetMapping("/change-username")
    public String viewUsernameChange(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "logining";
        }
        return "username_change";
    }
}
