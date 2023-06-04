package com.jore.epoc.views.simulation;

import java.util.List;

import com.jore.epoc.views.simulation.EmailListEntry.DeleteEmailEvent;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.shared.Registration;

@SuppressWarnings("serial")
public class MultipleEmailsComponent extends CustomField<List<String>> {
    public class AddEmailEvent extends ComponentEvent<MultipleEmailsComponent> {
        private final String email;

        public AddEmailEvent(MultipleEmailsComponent source, String email) {
            super(source, false);
            this.email = email;
        }

        public String getEmail() {
            return email;
        }
    }

    public class RemoveEmailEvent extends ComponentEvent<MultipleEmailsComponent> {
        private String email;

        public RemoveEmailEvent(MultipleEmailsComponent source, String email) {
            super(source, false);
            this.email = email;
        }

        public String getEmail() {
            return email;
        }
    }

    private EmailField textField = new EmailField();
    private VerticalLayout items = new VerticalLayout();

    MultipleEmailsComponent() {
        setLabel("Users (E-Mail Addresses):");
        VerticalLayout layout = new VerticalLayout();
        items.setPadding(false);
        items.setSpacing(false);
        layout.add(items, textField);
        layout.setPadding(false);
        layout.setSpacing(false);
        add(layout);
        textField.setWidthFull();
        textField.addKeyDownListener(Key.ENTER, click -> addEmail());
        textField.setHelperText("To Add User Enter E-Mail and Press Enter");
    }

    @Override
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return super.addListener(eventType, listener);
    }

    private void addEmail() {
        if (!textField.isInvalid()) {
            addEmailComponent(textField.getValue());
            fireEvent(new AddEmailEvent(this, textField.getValue()));
            textField.clear();
        }
    }

    private void addEmailComponent(String email) {
        EmailListEntry newEmail = new EmailListEntry(email);
        newEmail.addListener(EmailListEntry.DeleteEmailEvent.class, click -> removeEmail(click));
        items.add(newEmail);
    }

    private void removeEmail(DeleteEmailEvent click) {
        EmailListEntry source = click.getSource();
        items.remove(source);
        fireEvent(new RemoveEmailEvent(this, click.getSource().generateModelValue()));
    }

    @Override
    protected List<String> generateModelValue() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void setPresentationValue(List<String> newPresentationValue) {
        for (String email : newPresentationValue) {
            addEmailComponent(email);
        }
    }
}
