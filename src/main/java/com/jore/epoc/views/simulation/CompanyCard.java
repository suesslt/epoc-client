package com.jore.epoc.views.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.jore.epoc.dto.CompanyDto;
import com.jore.epoc.dto.CompanyUserDto;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.lumo.LumoUtility;

import lombok.extern.log4j.Log4j2;

@Log4j2
@SuppressWarnings("serial")
public class CompanyCard extends VerticalLayout {
    public static class AddEmailEvent extends ComponentEvent<CompanyCard> {
        private CompanyUserDto companyUserDto;

        protected AddEmailEvent(CompanyCard source, CompanyUserDto companyUserDto) {
            super(source, false);
            this.companyUserDto = companyUserDto;
        }

        public CompanyUserDto getCompanyUser() {
            return companyUserDto;
        }
    }

    public static abstract class CompanyCardEvent extends ComponentEvent<CompanyCard> {
        private CompanyDto company;

        protected CompanyCardEvent(CompanyCard source, CompanyDto company) {
            super(source, false);
            this.company = company;
        }

        public CompanyDto getCompany() {
            return company;
        }
    }

    public static class DeleteEvent extends CompanyCardEvent {
        DeleteEvent(CompanyCard source, CompanyDto company) {
            super(source, company);
        }
    }

    private static final long serialVersionUID = -8087135648116057207L;
    private EmailField emailField = new EmailField("Users E-Mail");
    private TextField companyName = new TextField("Company Name");
    private List<String> emailAdresses = new ArrayList<>();
    private List<String> selectedEmailAdresses = new ArrayList<>();
    private MultiSelectComboBox<String> emailCombobox = new MultiSelectComboBox<>("Users");
    private CompanyDto company;

    public CompanyCard() {
        emailCombobox.addSelectionListener(e -> selectionChanged(e.getRemovedSelection()));
        setHeight("30em");
        setWidth("20em");
        addClassNames(LumoUtility.Border.ALL, LumoUtility.Background.CONTRAST_10, LumoUtility.BoxShadow.LARGE, LumoUtility.BorderRadius.LARGE);
        Button closeButton = new Button(new Icon("lumo", "cross"), click -> deleteCompany());
        add(closeButton);
        HorizontalLayout textWithButton = new HorizontalLayout();
        textWithButton.setAlignItems(Alignment.BASELINE);
        Button addUserButton = new Button(new Icon("lumo", "plus"), click -> addUser());
        addUserButton.setMaxWidth("2em");
        textWithButton.addClickShortcut(Key.ENTER);
        textWithButton.add(emailField);
        textWithButton.add(addUserButton);
        companyName.setSizeFull();
        companyName.setRequired(true);
        emailCombobox.setSizeFull();
        emailField.setSizeFull();
        add(companyName, emailCombobox, textWithButton);
    }

    @Override
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return super.addListener(eventType, listener);
    }

    public CompanyDto getCompany() {
        return company;
    }

    public void setCompany(CompanyDto companyDto) {
        this.company = companyDto;
        companyName.setValue(companyDto.getName());
    }

    private void addUser() {
        if (!emailField.isEmpty() && !emailField.isInvalid()) {
            String email = emailField.getValue();
            emailField.clear();
            emailAdresses.add(email);
            emailCombobox.setItems(emailAdresses);
            selectedEmailAdresses.add(email);
            emailCombobox.select(selectedEmailAdresses);
            CompanyUserDto companyUserDto = CompanyUserDto.builder().companyId(company.getId()).email(email).build();
            fireEvent(new AddEmailEvent(this, companyUserDto));
        }
    }

    private void deleteCompany() {
        fireEvent(new DeleteEvent(this, company));
    }

    private void selectionChanged(Set<String> removedItems) {
        log.info(removedItems);
    }
}
