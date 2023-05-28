package com.jore.epoc.views.simulation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.jore.epoc.dto.CompanyDto;
import com.jore.epoc.dto.SimulationDto;
import com.jore.epoc.services.SimulationService;
import com.jore.epoc.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

@PageTitle("Simulation Editor")
@Route(value = "simulation", layout = MainLayout.class)
@AnonymousAllowed
public class SimulationEditor extends VerticalLayout {
    private static final long serialVersionUID = -8259979153821357006L;
    private Button addCompanyButton;
    private HorizontalLayout horizontalLayout = new HorizontalLayout();
    private List<Component> companyCards = new ArrayList<>();
    private final SimulationService simulationService;
    private SimulationDto simulation;

    public SimulationEditor(@Autowired SimulationService simulationService) {
        this.simulationService = simulationService;
        addCompanyButton = new Button("Add Company", click -> newCompany());
        addCompanyButton.setHeight("30em");
        addCompanyButton.setWidth("20em");
        addCompanyButton.addClassNames(LumoUtility.BorderRadius.LARGE);
        horizontalLayout.setPadding(true);
        horizontalLayout.add(companyCards);
        horizontalLayout.add(addCompanyButton);
        add(horizontalLayout);
    }

    public void setSimulation(SimulationDto simulation) {
        this.simulation = simulation;
        simulation.getCompanies().stream().forEach(company -> addCompany(company));
    }

    private void addCompany(CompanyDto company) {
        CompanyCard companyCard = new CompanyCard();
        companyCard.setCompany(company);
        companyCards.add(companyCard);
        horizontalLayout.removeAll();
        horizontalLayout.add(companyCards);
        horizontalLayout.add(addCompanyButton);
    }

    private void newCompany() {
        addCompany(simulationService.saveCompany(CompanyDto.builder().simulationId(simulation.getId()).build()));
    }
}
