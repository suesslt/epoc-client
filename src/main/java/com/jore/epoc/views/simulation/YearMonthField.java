package com.jore.epoc.views.simulation;

import java.time.YearMonth;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.IntegerField;

public class YearMonthField extends CustomField<YearMonth> {
    private static final long serialVersionUID = 6990561329745046700L;
    private IntegerField yearField = new IntegerField();
    private IntegerField monthField = new IntegerField();

    public YearMonthField() {
        yearField.setWidth("5em");
        yearField.setMin(1980);
        yearField.setMax(2080);
        monthField.setWidth("4em");
        monthField.setMin(1);
        monthField.setMax(12);
        add(yearField, new Span("-"), monthField);
    }

    public YearMonthField(String label) {
        this();
        setLabel(label);
    }

    @Override
    protected YearMonth generateModelValue() {
        return (yearField.getValue() != null && monthField != null) ? YearMonth.of(yearField.getValue(), monthField.getValue()) : null;
    }

    @Override
    protected void setPresentationValue(YearMonth newPresentationValue) {
        yearField.setValue(newPresentationValue != null ? newPresentationValue.getYear() : null);
        monthField.setValue(newPresentationValue != null ? newPresentationValue.getMonthValue() : null);
    }
}
