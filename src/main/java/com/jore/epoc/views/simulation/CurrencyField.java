package com.jore.epoc.views.simulation;

import com.jore.datatypes.currency.Currency;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;

@SuppressWarnings("serial")
public class CurrencyField extends CustomField<Currency> {
    private ComboBox<Currency> comboBox;

    public CurrencyField() {
        comboBox = new ComboBox<>("", Currency.getCurrencyMap().values());
        comboBox.setWidth("6em");
        add(comboBox);
    }

    public CurrencyField(String label) {
        this();
        setLabel(label);
    }

    @Override
    protected Currency generateModelValue() {
        return comboBox.getValue();
    }

    @Override
    protected void setPresentationValue(Currency newCurrency) {
        comboBox.setValue(newCurrency);
    }
}
