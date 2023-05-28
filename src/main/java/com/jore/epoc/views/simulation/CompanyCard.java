package com.jore.epoc.views.simulation;

import java.util.ArrayList;
import java.util.List;

import com.jore.epoc.dto.CompanyDto;
import com.jore.epoc.dto.CompanyUserDto;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class CompanyCard extends VerticalLayout {
    private static final long serialVersionUID = -8087135648116057207L;
    private EmailField emailField = new EmailField("Users E-Mail");
    private TextField companyName = new TextField("Company Name");
    private List<CompanyUserDto> users = new ArrayList<>();
    private List<CompanyUserDto> selectedUsers = new ArrayList<>();
    private MultiSelectComboBox<CompanyUserDto> users2 = new MultiSelectComboBox<>("Users");

    public CompanyCard() {
        setHeight("30em");
        setWidth("20em");
        addClassNames(LumoUtility.Border.ALL, LumoUtility.Background.CONTRAST_10, LumoUtility.BoxShadow.LARGE, LumoUtility.BorderRadius.LARGE);
        Button addUserButton = new Button("Add User", click -> addUser());
        addUserButton.addClickShortcut(Key.ENTER);
        companyName.setSizeFull();
        companyName.setRequired(true);
        users2.setSizeFull();
        emailField.setSizeFull();
        add(companyName, users2, emailField, addUserButton);
    }

    public void setCompany(CompanyDto companyDto) {
        companyName.setValue(companyDto.getName());
    }

    private void addUser() {
        if (!emailField.isEmpty() && !emailField.isInvalid()) {
            CompanyUserDto user = CompanyUserDto.builder().email(emailField.getValue()).username(emailField.getValue()).build();
            emailField.clear();
            users.add(user);
            users2.setItems(users);
            selectedUsers.add(user);
            users2.select(selectedUsers);
        }
        //        userService.saveCompanyUser(CompanyUserDto.builder().email(emailField.getValue()).username(emailField.getValue()).companyId(1l).build());
    }
}
