package com.jore.epoc.views.simulations;

import org.springframework.beans.factory.annotation.Autowired;

import com.jore.epoc.dto.SimulationDto;
import com.jore.epoc.services.UserAdminService;
import com.jore.epoc.services.SimulationService;
import com.jore.epoc.views.MainLayout;
import com.jore.epoc.views.simulation.SimulationEditor;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import jakarta.annotation.security.RolesAllowed;

@SuppressWarnings("serial")
@PageTitle("My Simulations")
@Route(value = "simulations", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@RolesAllowed("USER")
public class SimulationsView extends Div {
    Grid<SimulationDto> grid = new Grid<>(SimulationDto.class, false);
    private final SimulationService simulationService;
    private final UserAdminService currentUserService;

    public SimulationsView(@Autowired SimulationService simulationService, @Autowired UserAdminService currentUserService) {
        this.simulationService = simulationService;
        this.currentUserService = currentUserService;
        addClassName("my-simulations-view");
        Button button = new Button("Buy simulations");
        button.addClickListener(click -> buySimulations());
        add(button, grid);
        configureGrid();
        updateList();
    }

    private void buySimulations() {
        simulationService.buySimulations(5, currentUserService.getAuthenticatedUser().get().getId());
        updateList();
    }

    private void configureGrid() {
        grid.addClassName("simulation-grid");
        grid.setSizeFull();
        grid.setColumns("name", "started", "startMonth", "nrOfMonths", "finished");
        grid.addColumn(SimulationDto::getNrOfCompanies).setHeader("#Companies");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.addItemDoubleClickListener(click -> editSimulation(click.getItem()));
    }

    private void editSimulation(SimulationDto user) {
        getUI().ifPresent(ui -> ui.navigate(SimulationEditor.class).ifPresent(editor -> editor.setSimulation(user)));
    }

    private void updateList() {
        grid.setItems(simulationService.getSimulationsForOwner(currentUserService.getAuthenticatedUser().get().getId()));
        grid.recalculateColumnWidths();
    }
}
