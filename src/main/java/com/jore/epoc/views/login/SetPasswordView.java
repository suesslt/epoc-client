package com.jore.epoc.views.login;

import java.util.Optional;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.EnglishSequenceData;
import org.passay.IllegalSequenceRule;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.jore.epoc.dto.UserTokenDto;
import com.jore.epoc.services.UserAdminService;
import com.vaadin.flow.component.Text;
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
    private final UserAdminService currentUserService;
    private UserTokenDto userToken;
    private PasswordField passwordField;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public SetPasswordView(UserAdminService currentUserService) {
        this.currentUserService = currentUserService;
        FormLayout form = new FormLayout();
        passwordField = new PasswordField("Enter Password");
        form.add(passwordField, new Button("Set Password", click -> validateAndSetPassword()));
        add(form);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UserTokenDto> userByToken = currentUserService.getUserByToken(token);
        if (userByToken.isPresent()) {
            userToken = userByToken.get();
        } else {
            event.getUI().navigate("login");
        }
    }

    @Override
    public void setParameter(BeforeEvent event, String token) {
        this.token = token;
    }

    private PasswordValidator createValidator() {
        PasswordValidator validator = new PasswordValidator(
                // length between 8 and 16 characters
                new LengthRule(8, 16),
                // at least one upper-case character
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                // at least one lower-case character
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                // at least one digit character
                new CharacterRule(EnglishCharacterData.Digit, 1),
                // at least one symbol (special character)
                new CharacterRule(EnglishCharacterData.Special, 1),
                // define some illegal sequences that will fail when >= 5 chars long
                // alphabetical is of the form 'abcde', numerical is '34567', qwery is 'asdfg'
                // the false parameter indicates that wrapped sequences are allowed; e.g. 'xyzabc'
                new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 5, false), new IllegalSequenceRule(EnglishSequenceData.Numerical, 5, false), new IllegalSequenceRule(EnglishSequenceData.USQwerty, 5, false),
                // no whitespace
                new WhitespaceRule());
        return validator;
    }

    private void validateAndSetPassword() {
        if (!passwordField.isEmpty()) {
            PasswordValidator validator = createValidator();
            RuleResult validationResult = validator.validate(new PasswordData(passwordField.getValue()));
            if (validationResult.isValid()) {
                currentUserService.setPassword(userToken.getUserId(), passwordEncoder.encode(passwordField.getValue()));
                currentUserService.deleteUserToken(userToken.getUserTokenId());
                removeAll();
                add(new Text("Password successfully changed"));
            } else {
                // TODO Show Error message
            }
        } else {
            // TODO Show Error message
        }
    }
}
