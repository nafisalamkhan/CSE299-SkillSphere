package com.cse299.skillSphere.user;

import com.cse299.skillSphere.auth.User;
import com.cse299.skillSphere.auth.Role;
import com.cse299.skillSphere.auth.UserResponse;
import com.cse299.skillSphere.auth.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.stream.Collectors;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/profile")
    public String getUserProfile(Model model) {
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByUsername(principal.getUsername());
        UserResponse userResponse = new UserResponse(user.getId(), user.getName(), user.getUsername(), user.getEmail(),
                user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
        model.addAttribute("user", userResponse);
        return "profile";
    }
}
