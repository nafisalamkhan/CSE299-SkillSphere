package com.cse299.skillSphere.auth;

import com.cse299.skillSphere.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class AuthController {
    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRequest());
        model.addAttribute("roles", List.of(Map.of("value", "ROLE_INSTRUCTOR", "text", "Instructor"),
                Map.of("value", "ROLE_USER", "text", "User")));
        return "register";
    }
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") @Valid UserRequest userRequest, Model model) {
        userService.registerUser(userRequest);
        model.addAttribute("message", "Registration successful.");
        return "login";
    }
    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error, Model model) {
        model.addAttribute("error", error);
        return "login";
    }
}
