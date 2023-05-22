package com.jore.epoc.views.users;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import com.jore.epoc.dto.UserDto;
import com.jore.epoc.services.UserManagementService;
import com.jore.epoc.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;

import jakarta.annotation.security.RolesAllowed;

@SuppressWarnings("serial")
@PageTitle("Users")
@Route(value = "master-detail/:samplePersonID?/:action?(edit)", layout = MainLayout.class)
//@AnonymousAllowed
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class UsersView extends Div implements BeforeEnterObserver {
    private final String USER_ID = "samplePersonID";
    private final String USER_EDIT_ROUTE_TEMPLATE = "master-detail/%s/edit";
    private final Grid<UserDto> grid = new Grid<>(UserDto.class, false);
    private TextField firstName;
    private TextField lastName;
    private TextField username;
    private TextField email;
    private TextField phone;
    private Checkbox isAdmin;
    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final BeanValidationBinder<UserDto> binder;
    private UserDto user;
    private UserForm form = new UserForm();
    private final UserManagementService userService;

    public UsersView(@Autowired UserManagementService userService) {
        this.userService = userService;
        addClassNames("users-view");
        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);
        add(splitLayout);
        // Configure Grid
        grid.addColumn("firstName").setAutoWidth(true);
        grid.addColumn("lastName").setAutoWidth(true);
        grid.addColumn("username").setAutoWidth(true);
        grid.addColumn("email").setAutoWidth(true);
        grid.addColumn("phone").setAutoWidth(true);
        LitRenderer<UserDto> adminRenderer = LitRenderer.<UserDto>of("<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>").withProperty("icon", important -> important.isAdmin() ? "check" : "minus")
                .withProperty("color", admin -> admin.isAdmin() ? "var(--lumo-primary-text-color)" : "var(--lumo-disabled-text-color)");
        grid.addColumn(adminRenderer).setHeader("Is Administrator").setAutoWidth(true);
        grid.setItems(query -> userService.list(PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query))).stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(USER_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(UsersView.class);
            }
        });
        // Configure Form
        binder = new BeanValidationBinder<>(UserDto.class);
        // Bind fields. This is where you'd define e.g. validation rules
        binder.bindInstanceFields(this);
        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });
        save.addClickListener(e -> {
            try {
                if (this.user == null) {
                    this.user = UserDto.builder().build();
                }
                binder.writeBean(this.user);
                userService.saveUser(this.user);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(UsersView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show("Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
        form.addListener(UserForm.SaveEvent.class, this::saveUser);
        form.addListener(UserForm.DeleteEvent.class, this::deleteUser);
        form.addListener(UserForm.CloseEvent.class, e -> closeEditor());
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> userId = event.getRouteParameters().get(USER_ID).map(Long::parseLong);
        if (userId.isPresent()) {
            Optional<UserDto> userFromBackend = userService.getById(userId.get());
            if (userFromBackend.isPresent()) {
                populateForm(userFromBackend.get());
            } else {
                Notification.show(String.format("The requested samplePerson was not found, ID = %s", userId.get()), 3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(UsersView.class);
            }
        }
    }

    public void editContact(UserDto user) {
        if (user == null) {
            closeEditor();
        } else {
            form.setUser(user);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void clearForm() {
        populateForm(null);
    }

    private void closeEditor() {
        form.setUser(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");
        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);
        FormLayout formLayout = new FormLayout();
        firstName = new TextField("First Name");
        lastName = new TextField("Last Name");
        username = new TextField("Username");
        email = new TextField("Email");
        phone = new TextField("Phone");
        isAdmin = new Checkbox("Is Administrator");
        formLayout.add(firstName, lastName, username, email, phone, isAdmin);
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);
        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void deleteUser(UserForm.DeleteEvent deleteEvent) {
        userService.delete(deleteEvent.getUser());
        updateList();
        closeEditor();
    }

    private void populateForm(UserDto value) {
        this.user = value;
        binder.readBean(this.user);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void saveUser(UserForm.SaveEvent saveEvent) {
        userService.saveUser(saveEvent.getUser());
        updateList();
        closeEditor();
    }

    private void updateList() {
        grid.setItems(userService.list(null));
    }
}
