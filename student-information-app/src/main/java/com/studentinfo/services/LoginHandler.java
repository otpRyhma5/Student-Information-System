package com.studentinfo.services;

import com.studentinfo.data.entity.User;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
public class LoginHandler {

    // Dependencies
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public LoginHandler(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    // Login Method
    public boolean login(String email, String password, boolean rememberMe) {
        try {
            // Authenticate the user using the user service
            Optional<User> authenticatedUserOpt = userService.authenticate(email, password);

            if (authenticatedUserOpt.isPresent()) {
                User user = authenticatedUserOpt.get();
                handleSuccessfulLogin(user, email, password, rememberMe);
                return true; // Indicate that login was successful
            } else {
                handleFailedLogin(email);
                return false; // Indicate that login failed
            }
        } catch (Exception e) {
            return false; // Indicate that login failed due to an error
        }
    }


    // Handle Successful Login
    private void handleSuccessfulLogin(User user, String email, String password, boolean rememberMe) {
        try {
            // Create authentication token
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(email, password, user.getAuthorities());

            // Authenticate the token
            Authentication authentication = authenticationManager.authenticate(authToken);

            // Set authentication in SecurityContextHolder
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Persist security context to session
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
            new HttpSessionSecurityContextRepository().saveContext(SecurityContextHolder.getContext(), request, response);

            // Set or remove the "Remember me" cookie
            assert response != null;
            handleRememberMeCookie(rememberMe, email, response);

            // Check the user's role and navigate accordingly
            navigateUserBasedOnRole(user);

            Notification.show("Logged in successfully!");
        } catch (Exception e) {
            System.err.println("Error during authentication: " + e.getMessage());
            Notification.show("Error during authentication.");
        }
    }

    // Handle Failed Login
    private void handleFailedLogin(String email) {
        System.out.println("Authentication failed for user: " + email);
        Notification.show("Authentication failed. Please check your credentials.");
    }

    // Set or remove the "Remember me" cookie
    private void handleRememberMeCookie(boolean rememberMe, String email, HttpServletResponse response) {
        Cookie cookie = new Cookie("rememberMe", rememberMe ? email : "");
        cookie.setMaxAge(rememberMe ? 60 * 60 * 24 * 30 : 0); // 30 days if "Remember me" is checked, 0 to delete the cookie
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    // Navigate User Based on Role
    private void navigateUserBasedOnRole(User user) {
        if (user.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_TEACHER"))) {
            // Redirect teachers to the teacher dashboard or profile
            UI.getCurrent().navigate("profile"); // Change to "teacher-dashboard" if needed
        } else if (user.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_STUDENT"))) {
            // Redirect students to their profile
            UI.getCurrent().navigate("profile");
        } else {
            // Fallback option if role is not recognized
            UI.getCurrent().navigate("profile");
        }
    }
}
