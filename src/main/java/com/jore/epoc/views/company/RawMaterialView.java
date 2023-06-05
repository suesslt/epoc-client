package com.jore.epoc.views.company;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.IntegerField;

import lombok.extern.log4j.Log4j2;

@SuppressWarnings("serial")
@Log4j2
public class RawMaterialView extends AbstractProcessView {
    public RawMaterialView() {
        super();
        setHeaderText("Raw Material");
        add(createBuyRawMaterialField());
    }

    private Component createBuyRawMaterialField() {
        IntegerField result = new IntegerField();
        result.setLabel("Buy raw material:");
        result.setHelperText("Enter amount below or equal 1000 and press Enter");
        result.setSuffixComponent(VaadinIcon.ENTER.create());
        result.addKeyDownListener(Key.ENTER, click -> {
            Integer amount = result.getValue();
            if (amount != null) {
                log.info(amount);
                result.clear();
                result.setInvalid(false);
                fireEvent(new OrderEvent(this, amount));
            }
        });
        result.setWidthFull();
        return result;
    }
}
