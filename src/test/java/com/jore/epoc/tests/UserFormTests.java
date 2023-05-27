package com.jore.epoc.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import com.jore.epoc.dto.UserDto;
import com.jore.epoc.views.users.UserForm;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.textfield.TextField;

class UserFormTests {
    private Button getButton(Component component, String buttonName) {
        return getButtons((HasComponents) component).stream().filter(button -> button.getElement().getText().equals(buttonName)).findFirst().get();
    }

    private Collection<Button> getButtons(HasComponents components) {
        List<Button> result = new ArrayList<>();
        Stream<Component> children = ((Component) components).getChildren();
        Iterator<Component> iterator = children.iterator();
        while (iterator.hasNext()) {
            Component next = iterator.next();
            if (next instanceof HasComponents container) {
                result.addAll(getButtons(container));
            } else if (next instanceof Button button) {
                result.add(button);
            }
        }
        return result;
    }

    private Component getField(Component component, String fieldName) {
        return getFields((HasComponents) component).stream().filter(field -> field.getElement().getProperty("label").equals(fieldName)).findFirst().get();
    }

    private List<AbstractField<?, ?>> getFields(HasComponents components) {
        List<AbstractField<?, ?>> result = new ArrayList<>();
        Stream<Component> children = ((Component) components).getChildren();
        Iterator<Component> iterator = children.iterator();
        while (iterator.hasNext()) {
            Component next = iterator.next();
            if (next instanceof HasComponents container) {
                result.addAll(getFields(container));
            } else if (next instanceof AbstractField<?, ?> field) {
                result.add(field);
            }
        }
        return result;
    }

    @Test
    void testEnterValuesAndPressSave() {
        UserForm userForm = new UserForm();
        userForm.setUser(UserDto.builder().build());
        ((TextField) getField(userForm, "First Name")).setValue("Kirk");
        ((TextField) getField(userForm, "Last Name")).setValue("Douglas");
        ((TextField) getField(userForm, "Username")).setValue("kirkie");
        ((TextField) getField(userForm, "E-Mail")).setValue("kirk.douglas@bluesky.ch");
        ((TextField) getField(userForm, "Phone")).setValue("+41796592222");
        ((TextField) getField(userForm, "Phone")).setValue("+41796592222");
        ((Checkbox) getField(userForm, "Administrator")).setValue(true);
        Button button = getButton(userForm, "Save");
        AtomicReference<UserDto> savedContactRef = new AtomicReference<>(null);
        userForm.addListener(UserForm.SaveEvent.class, e -> {
            savedContactRef.set(e.getUser());
        });
        button.click();
        UserDto user = savedContactRef.get();
        assertEquals("Kirk", user.getFirstName());
        assertEquals("Douglas", user.getLastName());
        assertEquals("kirkie", user.getUsername());
        assertEquals("kirk.douglas@bluesky.ch", user.getEmail());
        assertEquals("+41796592222", user.getPhone());
        assertEquals(true, user.isAdministrator());
    }

    @Test
    void testFormFieldsPopulated() {
        UserForm userForm = new UserForm();
        UserDto build = UserDto.builder().firstName("Tom").lastName("Kranich").username("kranto").email("tom.k@bluesky.ch").phone("+41796592222").administrator(true).build();
        userForm.setUser(build);
        assertEquals("Tom", ((TextField) getField(userForm, "First Name")).getValue());
        assertEquals("Kranich", ((TextField) getField(userForm, "Last Name")).getValue());
        assertEquals("kranto", ((TextField) getField(userForm, "Username")).getValue());
        assertEquals("tom.k@bluesky.ch", ((TextField) getField(userForm, "E-Mail")).getValue());
        assertEquals("+41796592222", ((TextField) getField(userForm, "Phone")).getValue());
        assertEquals(true, ((Checkbox) getField(userForm, "Administrator")).getValue());
    }
}
