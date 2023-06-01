package com.jore.epoc.views.login;

import com.jore.epoc.services.UserAdminService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@SuppressWarnings("serial")
@AnonymousAllowed
@PageTitle("Login")
@Route(value = "login")
public class LoginView extends LoginOverlay implements BeforeEnterObserver {
    private final UserAdminService authenticatedUser;

    public LoginView(UserAdminService authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
        setAction(RouteUtil.getRoutePath(VaadinService.getCurrent().getContext(), getClass()));
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("epoc");
        i18n.setAdditionalInformation(null);
        setI18n(i18n);
        setForgotPasswordButtonVisible(true);
        addForgotPasswordListener(click -> resetPassword());
        setOpened(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.getAuthenticatedUser().isPresent()) {
            // Already logged in
            setOpened(false);
            event.forwardTo("");
        }
        setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }

    private void resetPassword() {
        Dialog dialog = new Dialog();
        dialog.setHeight("15em");
        dialog.setWidth("30em");
        dialog.setHeaderTitle("Reset Password");
        Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getHeader().add(closeButton);
        EmailField emailField = new EmailField("Enter E-Mail");
        emailField.setRequired(true);
        emailField.setWidth("20em");
        emailField.setSizeFull();
        dialog.add(emailField);
        Button cancelButton = new Button("Cancel", e -> dialog.close());
        dialog.getFooter().add(new HorizontalLayout(cancelButton, new Button("Send", click -> sendResetPasswordLink(dialog, emailField.getValue()))));
        dialog.open();
    }

    private void sendResetPasswordLink(Dialog dialog, String email) {
        dialog.setOpened(false);
        authenticatedUser.sendResetPasswordLink(email);
    }
}
