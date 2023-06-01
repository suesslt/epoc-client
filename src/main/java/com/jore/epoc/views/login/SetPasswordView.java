package com.jore.epoc.views.login;

import java.util.Optional;

import com.jore.epoc.dto.UserDto;
import com.jore.epoc.services.CurrentUserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@SuppressWarnings("serial")
@AnonymousAllowed
@PageTitle("Set Password")
@Route(value = "set_password")
public class SetPasswordView extends VerticalLayout implements BeforeEnterObserver, HasUrlParameter<String> {
    private String token;
    private final CurrentUserService currentUserService;

    public SetPasswordView(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
        VerticalLayout verticalLayout = new VerticalLayout();
        FormLayout form = new FormLayout();
        PasswordField passwordField = new PasswordField("Enter Password");
        PasswordField resetPasswordField = new PasswordField("Re-enter Password");
        form.add(passwordField);
        form.add(resetPasswordField);
        form.add(new Button("Set Password"));
        verticalLayout.add(form);
        add(verticalLayout);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UserDto> user = currentUserService.getByToken(token);
        if (user.isPresent()) {
        } else {
            event.getUI().navigate("login");
            //            event.getUI().ifPresent(ui -> ui.navigate("login")); // TODO doesn't work
        }
    }

    @Override
    public void setParameter(BeforeEvent event, String token) {
        this.token = token;
    }
}
