package com.jore.epoc.views.simulation;

import com.jore.datatypes.money.Money;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.BigDecimalField;

@SuppressWarnings("serial")
public class MoneyField extends CustomField<Money> {
    private CurrencyField currencyField;
    private BigDecimalField amountField;

    public MoneyField() {
        currencyField = new CurrencyField();
        amountField = new BigDecimalField();
        add(currencyField, new Span(" "), amountField);
    }

    public MoneyField(String label) {
        this();
        setLabel(label);
    }

    @Override
    protected Money generateModelValue() {
        return (currencyField.getValue() != null && amountField.getValue() != null) ? Money.of(currencyField.getValue(), amountField.getValue()) : null;
    }

    @Override
    protected void setPresentationValue(Money newPresentationValue) {
        if (newPresentationValue != null) {
            currencyField.setValue(newPresentationValue.getCurrency());
            amountField.setValue(newPresentationValue.getAmount());
        } else {
            currencyField.setValue(null);
            amountField.setValue(null);
        }
    }
}
