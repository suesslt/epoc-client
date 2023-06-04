package com.jore.epoc.views.simulation;

import java.util.Objects;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.shared.Registration;

@SuppressWarnings("serial")
public class EmailListEntry extends CustomField<String> {
    public class DeleteEmailEvent extends ComponentEvent<EmailListEntry> {
        public DeleteEmailEvent(EmailListEntry source) {
            super(source, false);
        }
    }

    private Text emailText;

    public EmailListEntry(String text) {
        emailText = new Text(text);
        Icon icon = new Icon(VaadinIcon.CLOSE_SMALL);
        icon.setColor("red");
        Button deleteButton = new Button(icon);
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY_INLINE);
        deleteButton.getElement().setAttribute("aria-label", "Delete");
        deleteButton.setTooltipText("Delete E-Mail From List");
        deleteButton.setMaxHeight("20px");
        deleteButton.addClickListener(click -> fireEvent(new DeleteEmailEvent(this)));
        add(deleteButton, emailText);
    }

    @Override
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return super.addListener(eventType, listener);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EmailListEntry other = (EmailListEntry) obj;
        return Objects.equals(emailText, other.emailText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(emailText);
    }

    @Override
    public String toString() {
        return emailText.getText();
    }

    @Override
    protected String generateModelValue() {
        return emailText.getText();
    }

    @Override
    protected void setPresentationValue(String newPresentationValue) {
        emailText.setText(newPresentationValue);
    }
}
