package com.jore.epoc.views.users;

import com.jore.epoc.dto.UserDto;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
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
    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final BeanValidationBinder<UserDto> binder = new BeanValidationBinder<>(UserDto.class);
    private UserDto user;

    public UserForm() {
        addClassName("user-form");
        buildComponent();
    }

    @Override
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

    public void setUser(UserDto value) {
        this.user = value;
        binder.readBean(this.user);
    }

    private void buildComponent() {
        binder.bindInstanceFields(this);
        add(firstName, lastName, username, email, phone, isAdmin);
        cancel.addClickListener(event -> fireEvent(new CloseEvent(this)));
        //        cancel.addClickListener(e -> {
        //            clearForm();
        //            //            refreshGrid();
        //        });
        save.addClickListener(event -> validateAndSave());
        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        //        save.addClickListener(e -> {
        //            try {
        //                if (this.user == null) {
        //                    this.user = UserDto.builder().build();
        //                }
        //                binder.writeBean(this.user);
        //                userService.saveUser(this.user);
        //                clearForm();
        //                //                refreshGrid();
        //                Notification.show("Data updated");
        //                UI.getCurrent().navigate(UsersView.class);
        //            } catch (ObjectOptimisticLockingFailureException exception) {
        //                Notification n = Notification.show("Error updating the data. Somebody else has updated the record while you were making changes.");
        //                n.setPosition(Position.MIDDLE);
        //                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        //            } catch (ValidationException validationException) {
        //                Notification.show("Failed to update the data. Check again that all values are valid");
        //            }
        //        });
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        add(buttonLayout);
    }

    private void clearForm() {
        setUser(null);
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
