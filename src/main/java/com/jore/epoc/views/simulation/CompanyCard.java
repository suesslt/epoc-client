package com.jore.epoc.views.simulation;

import java.util.Collections;

import com.jore.epoc.dto.CompanyDto;
import com.jore.epoc.dto.CompanyUserDto;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.lumo.LumoUtility;

// @Log4j2
@SuppressWarnings("serial")
public class CompanyCard extends VerticalLayout {
    public static class AddEmailEvent extends ComponentEvent<CompanyCard> {
        private CompanyUserDto companyUser;

        protected AddEmailEvent(CompanyCard source, CompanyUserDto companyUser) {
            super(source, false);
            this.companyUser = companyUser;
        }

        public CompanyUserDto getCompanyUser() {
            return companyUser;
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

    public static class DeleteCompanyEvent extends CompanyCardEvent {
        DeleteCompanyEvent(CompanyCard source, CompanyDto company) {
            super(source, company);
        }
    }

    public static class RemoveEmailEvent extends ComponentEvent<CompanyCard> {
        private CompanyUserDto companyUser;

        protected RemoveEmailEvent(CompanyCard source, CompanyUserDto companyUser) {
            super(source, false);
            this.companyUser = companyUser;
        }

        public CompanyUserDto getCompanyUser() {
            return companyUser;
        }
    }

    private TextField companyName = new TextField("Company Name");
    private MultipleEmailsComponent emails = new MultipleEmailsComponent();
    private CompanyDto company;

    public CompanyCard() {
        setHeight("30em");
        setWidth("20em");
        addClassNames(LumoUtility.Border.ALL, LumoUtility.Background.CONTRAST_10, LumoUtility.BoxShadow.LARGE, LumoUtility.BorderRadius.LARGE);
        Button deleteCompanyButton = new Button(new Icon("lumo", "cross"), click -> fireEvent(new DeleteCompanyEvent(this, company)));
        add(deleteCompanyButton);
        companyName.setRequired(true);
        companyName.setWidthFull();
        emails.setWidthFull();
        VerticalLayout verticalLayout = new VerticalLayout(companyName, emails);
        verticalLayout.setPadding(false);
        Scroller scroller = new Scroller(verticalLayout);
        scroller.setWidthFull();
        add(scroller);
        emails.addListener(MultipleEmailsComponent.AddEmailEvent.class, click -> fireEvent(new AddEmailEvent(this, CompanyUserDto.builder().companyId(company.getId()).email(click.getEmail()).build())));
        emails.addListener(MultipleEmailsComponent.RemoveEmailEvent.class, click -> fireEvent(new RemoveEmailEvent(this, CompanyUserDto.builder().companyId(company.getId()).email(click.getEmail()).build())));
    }

    @Override
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return super.addListener(eventType, listener);
    }

    public CompanyDto getCompany() {
        company.setEmails(emails.getValue());
        company.setName(companyName.getValue());
        return company;
    }

    @SuppressWarnings("unchecked")
    public void setCompany(CompanyDto companyDto) {
        this.company = companyDto;
        if (companyDto != null) {
            companyName.setValue(companyDto.getName());
            emails.setPresentationValue(companyDto.getEmails());
        } else {
            companyName.setValue(null);
            emails.setValue(Collections.EMPTY_LIST);
        }
    }
}
