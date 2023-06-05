package com.jore.epoc.views.company;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.lumo.LumoUtility;

@SuppressWarnings("serial")
public abstract class AbstractProcessView extends VerticalLayout {
    private H4 header = new H4();

    public AbstractProcessView() {
        setSpacing(false);
        setHeight("30em");
        setWidth("15em");
        addClassNames(LumoUtility.Border.ALL, LumoUtility.Background.CONTRAST_10);
        add(header);
    }

    @Override
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return super.addListener(eventType, listener);
    }

    public void setHeaderText(String headerText) {
        header.setText(headerText);
    }
}
