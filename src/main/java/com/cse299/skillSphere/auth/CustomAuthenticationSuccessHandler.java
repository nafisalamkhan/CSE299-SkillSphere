package com.cse299.skillSphere.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        // You can customize the redirect URL based on user roles or other logic
        String redirectUrl = "/"; // Default URL after login

        // For example, you can check the roles of the user and redirect accordingly
        if (authentication.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_INSTRUCTOR"))) {
            redirectUrl = "/courses";  // Redirect to admin dashboard
        } else if (authentication.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_USER"))) {
            redirectUrl = "/"; // Redirect to user home page
        }

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
