package com.jore.epoc.security;

import org.springframework.stereotype.Component;

import com.jore.epoc.services.UserService;
import com.vaadin.flow.spring.security.AuthenticationContext;

@Component
public class AuthenticatedUser {
    private final AuthenticationContext authenticationContext;
    private final UserService userService;

    public AuthenticatedUser(AuthenticationContext authenticationContext, UserService userService) {
        this.userService = userService;
        this.authenticationContext = authenticationContext;
    }
    //    public Optional<UserDto> get() {
    //        return authenticationContext.getAuthenticatedUser(UserDetails.class).map(userDetails -> userService.getByUsername(userDetails.getUsername()));
    //    }

    public void logout() {
        authenticationContext.logout();
    }
}
