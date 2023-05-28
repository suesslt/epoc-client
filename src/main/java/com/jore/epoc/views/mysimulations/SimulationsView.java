package com.jore.epoc.views.mysimulations;

import org.springframework.beans.factory.annotation.Autowired;

import com.jore.epoc.dto.SimulationDto;
import com.jore.epoc.services.CurrentUserService;
import com.jore.epoc.services.SimulationService;
import com.jore.epoc.views.MainLayout;
import com.jore.epoc.views.users.UserForm;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import jakarta.annotation.security.RolesAllowed;

@SuppressWarnings("serial")
@PageTitle("My Simulations")
@Route(value = "simulation", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@RolesAllowed("USER")
public class SimulationsView extends Div implements AfterNavigationObserver {
    Grid<SimulationDto> grid = new Grid<>(SimulationDto.class, false);
    private final SimulationService simulationService;
    private final CurrentUserService currentUserService;

    public SimulationsView(@Autowired SimulationService simulationService, @Autowired CurrentUserService currentUserService) {
        this.simulationService = simulationService;
        this.currentUserService = currentUserService;
        addClassName("my-simulations-view");
        Button button = new Button("Buy simulations");
        button.addClickListener(click -> buySimulations());
        add(button, grid);
        configureGrid();
        updateList();
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
    }

    private void buySimulations() {
        simulationService.buySimulations(10, currentUserService.getAuthenticatedUser().get().getId());
        updateList();
    }

    private void configureGrid() {
        grid.addClassName("simulation-grid");
        grid.setSizeFull();
        grid.setColumns("name", "started", "startMonth", "nrOfMonths", "finished");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.addItemDoubleClickListener(click -> editSimulation(click.getItem()));
        //        grid.asSingleSelect().addValueChangeListener(event -> editContact(event.getValue()));
        //        grid.setColumnReorderingAllowed(true);
        //        grid.setHeight("100%");
        //        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
    }

    private void editSimulation(SimulationDto item) {
        ((MainLayout) this.getParent().get()).addView(new UserForm());
    }

    private void updateList() {
        grid.setItems(simulationService.getSimulationsForOwner(currentUserService.getAuthenticatedUser().get().getId()));
        grid.recalculateColumnWidths();
    }
}
