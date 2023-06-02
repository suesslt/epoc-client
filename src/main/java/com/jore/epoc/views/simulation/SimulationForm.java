package com.jore.epoc.views.simulation;

import com.jore.epoc.dto.SimulationDto;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

import lombok.extern.log4j.Log4j2;

@Log4j2
@SuppressWarnings("serial")
public class SimulationForm extends FormLayout {
    public static class CloseEvent extends SimulationFormEvent {
        CloseEvent(SimulationForm source) {
            super(source, null);
        }
    }

    public static class SaveEvent extends SimulationFormEvent {
        SaveEvent(SimulationForm source, SimulationDto simulation) {
            super(source, simulation);
        }
    }

    public static abstract class SimulationFormEvent extends ComponentEvent<SimulationForm> {
        private SimulationDto simulation;

        protected SimulationFormEvent(SimulationForm source, SimulationDto simulation) {
            super(source, false);
            this.simulation = simulation;
        }

        public SimulationDto getSimulation() {
            return simulation;
        }
    }

    private SimulationDto simulation;
    private final BeanValidationBinder<SimulationDto> binder = new BeanValidationBinder<>(SimulationDto.class);
    Button save = new Button("Save");
    Button close = new Button("Cancel");
    private TextField name = new TextField("Name");
    private YearMonthField startMonth = new YearMonthField("Start Month");
    private IntegerField nrOfMonths = new IntegerField("Nr of Months");
    private CurrencyField baseCurrency = new CurrencyField("Base Currency");
    private MoneyField costToBuildFactory = new MoneyField("Cost to Build Factory");

    public SimulationForm() {
        addClassName("user-form");
        add(name, startMonth, nrOfMonths, baseCurrency, costToBuildFactory, createButtonsLayout());
        setColspan(name, 3);
        binder.bindInstanceFields(this);
        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
    }

    @Override
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

    public void setSimulation(SimulationDto simulation) {
        this.simulation = simulation;
        binder.readBean(this.simulation);
    }

    private HorizontalLayout createButtonsLayout() {
        save.addClickListener(event -> validateAndSave());
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickShortcut(Key.ENTER);
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        close.addClickShortcut(Key.ESCAPE);
        return new HorizontalLayout(save, close);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(simulation);
            fireEvent(new SaveEvent(this, simulation));
        } catch (ValidationException e) {
            log.error(e);
        }
    }
}
