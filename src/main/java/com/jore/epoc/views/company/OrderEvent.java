package com.jore.epoc.views.company;

import com.vaadin.flow.component.ComponentEvent;

@SuppressWarnings("serial")
public class OrderEvent extends ComponentEvent<AbstractProcessView> {
    private Integer amount;

    public OrderEvent(AbstractProcessView source, Integer amount) {
        super(source, false);
        this.amount = amount;
    }

    public Integer getAmount() {
        return amount;
    }
}
