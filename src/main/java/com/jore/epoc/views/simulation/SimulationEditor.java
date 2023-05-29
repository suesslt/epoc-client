package com.jore.epoc.views.simulation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.jore.epoc.dto.CompanyDto;
import com.jore.epoc.dto.SimulationDto;
import com.jore.epoc.services.SimulationService;
import com.jore.epoc.views.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.Autocomplete;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.RolesAllowed;

@PageTitle("Simulation Editor")
@Route(value = "simulation", layout = MainLayout.class)
@RolesAllowed("USER")
public class SimulationEditor extends VerticalLayout {
    private static final long serialVersionUID = -8259979153821357006L;
    private Button addCompanyButton;
    private VerticalLayout simulationView = new VerticalLayout();
    private HorizontalLayout companyCardView = new HorizontalLayout();
    private List<CompanyDto> companies = new ArrayList<>();
    private final SimulationService simulationService;
    private SimulationDto simulation;

    public SimulationEditor(@Autowired SimulationService simulationService) {
        this.simulationService = simulationService;
        createCompanyCardView();
        add(new VerticalLayout(simulationView, companyCardView));
        refreshCompanyView();
    }

    public void setSimulation(SimulationDto simulation) {
        this.simulation = simulation;
        if (simulation != null) {
            fillSimulationView();
            simulation.getCompanies().stream().forEach(company -> addCompany(company));
        } else {
            simulationView.removeAll();
        }
    }

    private void addCompany(CompanyDto company) {
        companies.add(company);
        refreshCompanyView();
    }

    private void addEmail(CompanyCard.AddEmailEvent event) {
        simulationService.saveCompanyUser(event.getCompanyUser());
    }

    private void createCompanyCardView() {
        addCompanyButton = new Button("Add Company", click -> newCompany());
        addCompanyButton.setHeight("30em");
        addCompanyButton.setWidth("20em");
        addCompanyButton.addClassNames(LumoUtility.BorderRadius.LARGE);
        companyCardView.setPadding(true);
    }

    private void deleteCompany(CompanyCard.DeleteEvent event) {
        simulationService.deleteCompany(event.getCompany());
        companies.remove(event.getCompany());
        refreshCompanyView();
    }

    private void editSimulation() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Edit Simulation");
        Button closeButton = new Button(new Icon("lumo", "cross"), (e) -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getHeader().add(closeButton);
        Button cancelButton = new Button("Cancel", e -> dialog.close());
        dialog.getFooter().add(new HorizontalLayout(cancelButton, new Button("Save")));
        FormLayout form = new FormLayout();
        TextField nameField = new TextField("Name");
        nameField.setAutocomplete(Autocomplete.NEW_PASSWORD);
        nameField.setValue(simulation.getName());
        form.add(nameField);
        YearMonthField startMonthField = new YearMonthField("Simulation Start Month");
        startMonthField.setValue(simulation.getStartMonth());
        form.add(startMonthField);
        TabSheet tabSheet = new TabSheet();
        tabSheet.add("Basics", new Div(new Text("My Text")));
        tabSheet.add("Settings", new Div(form));
        dialog.add(tabSheet);
        dialog.setOpened(true);
    }

    private void fillSimulationView() {
        simulationView.removeAll();
        simulationView.setPadding(false);
        Span simulationName = new Span("Simulation " + simulation.getName());
        simulationName.addClassNames(LumoUtility.FontSize.XXLARGE);
        Span simulationStart = new Span("Simulation start: " + simulation.getStartMonth());
        Span simulationDuration = new Span("Simulation periods: " + simulation.getNrOfMonths() + " months.");
        Button editButton = new Button("Edit Simulation", click -> editSimulation());
        simulationView.add(simulationName, simulationStart, simulationDuration, editButton);
    }

    private void newCompany() {
        addCompany(simulationService.saveCompany(CompanyDto.builder().simulationId(simulation.getId()).build()));
    }

    private void refreshCompanyView() {
        companyCardView.removeAll();
        for (CompanyDto company : companies) {
            CompanyCard companyCard = new CompanyCard();
            companyCard.setCompany(company);
            companyCard.addListener(CompanyCard.DeleteEvent.class, this::deleteCompany);
            companyCard.addListener(CompanyCard.AddEmailEvent.class, this::addEmail);
            companyCardView.add(companyCard);
        }
        companyCardView.add(addCompanyButton);
    }
}
