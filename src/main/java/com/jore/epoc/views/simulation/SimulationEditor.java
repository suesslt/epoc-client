package com.jore.epoc.views.simulation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.jore.epoc.dto.CompanyDto;
import com.jore.epoc.dto.SimulationDto;
import com.jore.epoc.services.SimulationService;
import com.jore.epoc.services.UserAdminService;
import com.jore.epoc.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.RolesAllowed;

@PageTitle("Simulation Editor")
@Route(value = "simulation", layout = MainLayout.class)
@RolesAllowed("USER")
@CssImport("./styles/shared-styles.css")
public class SimulationEditor extends VerticalLayout {
    private static final long serialVersionUID = -8259979153821357006L;
    private Button addCompanyButton;
    private VerticalLayout simulationView = new VerticalLayout();
    private HorizontalLayout companyCardView = new HorizontalLayout();
    private List<CompanyDto> companies = new ArrayList<>();
    private SimulationDto simulation;
    private SimulationForm form = new SimulationForm();
    @Autowired
    private UserAdminService currentUserService;
    @Autowired
    private SimulationService simulationService;

    public SimulationEditor() {
        addClassName("simulation-editor");
        setSizeFull();
        createCompanyCardView();
        form.addListener(SimulationForm.SaveEvent.class, this::saveSimulation);
        form.addListener(SimulationForm.CloseEvent.class, e -> closeEditor());
        add(new VerticalLayout(new HorizontalLayout(simulationView, form), companyCardView));
        closeEditor();
        updateEditor();
    }

    public void editSimulation(SimulationDto simulation) {
        if (simulation == null) {
            closeEditor();
        } else {
            form.setSimulation(simulation);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    public void setSimulation(SimulationDto simulation) {
        this.simulation = simulation;
        if (simulation != null) {
            simulation.getCompanies().stream().forEach(company -> companies.add(company));
        } else {
            simulationView.removeAll();
        }
        updateEditor();
    }

    private void addEmail(CompanyCard.AddEmailEvent event) {
        simulationService.saveCompanyUser(event.getCompanyUser());
    }

    private void closeEditor() {
        form.setSimulation(null);
        form.setVisible(false);
        removeClassName("editing");
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
        updateEditor();
    }

    private void fillSimulationView() {
        simulationView.removeAll();
        simulationView.setPadding(false);
        if (simulation != null) {
            Span simulationName = new Span("Simulation " + simulation.getName());
            simulationName.addClassNames(LumoUtility.FontSize.XXLARGE);
            Span simulationStart = new Span("Simulation start: " + simulation.getStartMonth());
            Span simulationDuration = new Span("Simulation periods: " + simulation.getNrOfMonths() + " months.");
            Button editButton = new Button("Edit Simulation", click -> editSimulation(simulation));
            Button sendEmailsButton = new Button("Send Invitation Mails", click -> sendInvitationMails());
            simulationView.add(simulationName, simulationStart, simulationDuration, new HorizontalLayout(editButton, sendEmailsButton));
        }
    }

    private void newCompany() {
        companies.add(simulationService.saveCompany(CompanyDto.builder().simulationId(simulation.getId()).build()));
        updateEditor();
    }

    private void saveSimulation(SimulationForm.SaveEvent event) {
        simulationService.saveSimulation(event.getSimulation());
        updateEditor();
        closeEditor();
    }

    private void sendInvitationMails() {
        currentUserService.sendEmailsForNewUsers();
    }

    private void updateEditor() {
        fillSimulationView();
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
