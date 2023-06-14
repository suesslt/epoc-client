package com.jore.epoc.views.company;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.jore.epoc.bo.orders.CreditEventDirection;
import com.jore.epoc.dto.AdjustCreditLineDto;
import com.jore.epoc.dto.BuildStorageDto;
import com.jore.epoc.dto.BuyRawMaterialDto;
import com.jore.epoc.dto.CompanyOrderDto;
import com.jore.epoc.dto.CompanySimulationStepDto;
import com.jore.epoc.dto.MessageDto;
import com.jore.epoc.services.SimulationService;
import com.jore.epoc.views.MainLayout;
import com.jore.epoc.views.simulation.MoneyField;
import com.vaadin.componentfactory.pdfviewer.PdfViewer;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.Scroller.ScrollDirection;
import com.vaadin.flow.component.orderedlayout.ScrollerVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.RolesAllowed;
import lombok.extern.log4j.Log4j2;

@SuppressWarnings("serial")
@PageTitle("Company")
@Route(value = "company", layout = MainLayout.class)
@RolesAllowed("USER")
@CssImport("./styles/shared-styles.css")
@Log4j2
public class CompanyView extends VerticalLayout implements HasUrlParameter<Long>, AfterNavigationObserver {
    @Autowired
    SimulationService simulationService;
    private CompanySimulationStepDto companyStep;
    private Long companyId;

    public CompanyView() {
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        buildView();
    }

    @Override
    public void setParameter(BeforeEvent event, Long companyId) {
        this.companyId = companyId;
        log.debug("Load current step for company: " + companyId);
    }

    private Component buildMessageList() {
        VerticalLayout result = new VerticalLayout();
        result.add(new H4("Messages from the HQ"));
        MessageList messageList = new MessageList();
        List<MessageListItem> items = new ArrayList<>();
        List<MessageDto> messages = companyStep.getMessages();
        for (MessageDto message : messages) {
            MessageListItem item = new MessageListItem();
            item.setText(message.getMessage());
            item.setUserAbbreviation("HQ");
            item.setUserName("Sarah from Finance Department");
            item.setTime(message.getRelevantMonth().atEndOfMonth().atStartOfDay().toInstant(ZoneOffset.UTC));
            item.setUserColorIndex(1);
            items.add(item);
        }
        messageList.setItems(items);
        Scroller scroller = new Scroller(messageList);
        scroller.addThemeVariants(ScrollerVariant.LUMO_OVERFLOW_INDICATORS);
        scroller.addClassName(LumoUtility.Border.ALL);
        result.add(scroller);
        result.setHeight("15em");
        result.setWidth("40em");
        result.setPadding(false);
        return result;
    }

    private void buildStorage(OrderEvent event) {
        simulationService.buildStorage(BuildStorageDto.builder().companyId(companyId).executionMonth(companyStep.getSimulationMonth()).capacity(event.getAmount()).build());
        buildView();
    }

    private void buildView() {
        companyStep = simulationService.getCurrentCompanySimulationStep(companyId).get();
        removeAll();
        add(new H2(companyStep.getCompanyName()));
        add(new H3("Simulation Month: " + companyStep.getSimulationMonth()));
        add(buildMessageList());
        //        add(new Text(companyStep.getMessages().toString()));
        add(new Text("Company value: " + companyStep.getCompanyValue()));
        add(createPdfView());
        add(createSalesChart());
        add(createFinanceView());
        add(createProcessView());
        add(createOrderView());
        add(new Button("Complete Step", click -> completeStep()));
    }

    private void buyRawMaterial(OrderEvent event) {
        simulationService.buyRawMaterial(BuyRawMaterialDto.builder().companyId(companyId).executionMonth(companyStep.getSimulationMonth()).amount(event.getAmount()).build());
        buildView();
    }

    private void completeStep() {
        simulationService.finishMoveFor(companyStep.getId());
        buildView();
    }

    private Component createAdjustCreditLineField() {
        HorizontalLayout result = new HorizontalLayout();
        result.setAlignItems(Alignment.BASELINE);
        Select<CreditEventDirection> select = new Select<>();
        select.setItems(CreditEventDirection.values());
        select.setLabel("Adjustment direction");
        result.add(select);
        MoneyField money = new MoneyField();
        money.setLabel("Adjustment amount");
        result.add(money);
        Button button = new Button("Add", click -> {
            if (select.getValue() != null && money.getValue() != null) {
                AdjustCreditLineDto adjustCreditLine = AdjustCreditLineDto.builder().amount(money.getValue()).companyId(companyId).executionMonth(companyStep.getSimulationMonth()).build();
                if (select.getValue().equals(CreditEventDirection.INCREASE)) {
                    simulationService.increaseCreditLine(adjustCreditLine); // TODO not very elegant...
                } else {
                    simulationService.decreaseCreditLine(adjustCreditLine);
                }
                buildView();
            }
        });
        result.add(button);
        return result;
    }

    private Component createCard(CompanyOrderDto order) {
        HorizontalLayout result = new HorizontalLayout();
        Span orderType = new Span(order.getOrderType());
        result.add(orderType);
        Span amount = new Span(order.getAmount().toString());
        result.add(amount);
        return result;
    }

    private Component createFinanceView() {
        VerticalLayout result = new VerticalLayout();
        result.setPadding(false);
        result.add(new H3("Finance"));
        result.add(createAdjustCreditLineField());
        return result;
    }

    private Component createOrderView() {
        VerticalLayout result = new VerticalLayout();
        result.add(new H4("Pending Orders for this Month"));
        Grid<CompanyOrderDto> grid = new Grid<>();
        grid.addThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addComponentColumn(order -> createCard(order));
        grid.setItems(companyStep.getOrders());
        result.setPadding(false);
        result.add(grid);
        result.setHeight("20em");
        return result;
    }

    private Component createPdfView() {
        PdfViewer pdfViewer = new PdfViewer();
        pdfViewer.setSrc(new StreamResource("BalanceSheet.pdf", () -> getClass().getResourceAsStream("/reports/BalanceSheet.pdf")));
        pdfViewer.setZoom("0.5");
        pdfViewer.setHeight("40em");
        pdfViewer.setWidth("30em");
        return pdfViewer;
    }

    private Component createProcessView() {
        HorizontalLayout result = new HorizontalLayout();
        result.setSpacing(false);
        RawMaterialView rawMaterialView = new RawMaterialView();
        rawMaterialView.addListener(OrderEvent.class, event -> buyRawMaterial(event));
        result.add(rawMaterialView);
        StorageRawView storageRawView = new StorageRawView();
        storageRawView.addListener(OrderEvent.class, event -> buildStorage(event));
        result.add(storageRawView);
        result.add(new FactoryView());
        result.add(new StorageProductsView());
        result.add(new DistributionView());
        Scroller scroller = new Scroller(result);
        scroller.setScrollDirection(ScrollDirection.HORIZONTAL);
        return scroller;
    }

    private Component createSalesChart() {
        SalesChart result = new SalesChart();
        return result;
    }
}
