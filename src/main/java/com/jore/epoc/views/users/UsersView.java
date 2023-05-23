package com.jore.epoc.views.users;

import org.springframework.beans.factory.annotation.Autowired;

import com.jore.epoc.dto.UserDto;
import com.jore.epoc.services.UserManagementService;
import com.jore.epoc.views.MainLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@SuppressWarnings("serial")
@PageTitle("Users")
@Route(value = "master-detail/", layout = MainLayout.class)
//@Route(value = "master-detail/:samplePersonID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
@CssImport("./styles/shared-styles.css")
public class UsersView extends VerticalLayout {
    private final Grid<UserDto> grid = new Grid<>(UserDto.class, false);
    private UserForm form;
    private final UserManagementService userService;
    private TextField filterText = new TextField();

    public UsersView(@Autowired UserManagementService userService) {
        this.userService = userService;
        addClassName("user-view");
        setSizeFull();
        configureFilter();
        configureGrid();
        form = new UserForm();
        form.addListener(UserForm.SaveEvent.class, this::saveContact);
        form.addListener(UserForm.DeleteEvent.class, this::deleteContact);
        form.addListener(UserForm.CloseEvent.class, e -> closeEditor());
        Div content = new Div(grid, form);
        content.addClassName("content");
        content.setSizeFull();
        add(filterText, content);
        updateList();
        closeEditor();
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

    private void closeEditor() {
        form.setUser(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void configureFilter() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());
    }

    private void configureGrid() {
        grid.addClassName("user-grid");
        grid.setSizeFull();
        grid.setColumns("firstName", "lastName", "email", "phone", "username", "admin");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event -> editContact(event.getValue()));
    }

    private void deleteContact(UserForm.DeleteEvent event) {
        userService.delete(event.getUser());
        updateList();
        closeEditor();
    }

    private void saveContact(UserForm.SaveEvent event) {
        userService.saveUser(event.getUser());
        updateList();
        closeEditor();
    }

    private void updateList() {
        grid.setItems(userService.getAllFiltered(filterText.getValue()));
    }
}
