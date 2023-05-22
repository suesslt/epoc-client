package com.jore.epoc.security;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.jore.epoc.dto.UserDto;
import com.jore.epoc.services.UserManagementService;
import com.vaadin.flow.spring.security.AuthenticationContext;

@Component
public class AuthenticatedUser {
    // TODO analyse and fix this
    private final AuthenticationContext authenticationContext;
    private final UserManagementService userService;

    public AuthenticatedUser(AuthenticationContext authenticationContext, UserManagementService userService) {
        this.userService = userService;
        this.authenticationContext = authenticationContext;
    }

    public Optional<UserDto> get() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class).map(userDetails -> userService.getByUsername(userDetails.getUsername()));
    }

    public void logout() {
        authenticationContext.logout();
    }
}
