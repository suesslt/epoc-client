package com.jore.epoc.views.users;

import com.jore.epoc.dto.UserDto;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

@SuppressWarnings("serial")
public class UserForm extends FormLayout {
    public static class CloseEvent extends UserFormEvent {
        CloseEvent(UserForm source) {
            super(source, null);
        }
    }

    public static class DeleteEvent extends UserFormEvent {
        DeleteEvent(UserForm source, UserDto user) {
            super(source, user);
        }
    }

    public static class SaveEvent extends UserFormEvent {
        SaveEvent(UserForm source, UserDto user) {
            super(source, user);
        }
    }

    public static abstract class UserFormEvent extends ComponentEvent<UserForm> {
        private UserDto user;

        protected UserFormEvent(UserForm source, UserDto user) {
            super(source, false);
            this.user = user;
        }

        public UserDto getUser() {
            return user;
        }
    }

    private final TextField firstName = new TextField("First Name");
    private final TextField lastName = new TextField("Last Name");
    private final TextField username = new TextField("Username");
    private final TextField email = new TextField("Email");
    private final TextField phone = new TextField("Phone");
    private final Checkbox isAdmin = new Checkbox("Is Administrator");
    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");
    private final BeanValidationBinder<UserDto> binder = new BeanValidationBinder<>(UserDto.class);
    private UserDto user;

    public UserForm() {
        addClassName("user-form");
        binder.bindInstanceFields(this);
        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        add(firstName, lastName, username, email, phone, isAdmin, createButtonsLayout());
    }

    @Override
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

    public void setUser(UserDto value) {
        this.user = value;
        binder.readBean(this.user);
    }

    private HorizontalLayout createButtonsLayout() {
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));
        save.addClickListener(event -> validateAndSave());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);
        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(user);
            fireEvent(new SaveEvent(this, user));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }
}
