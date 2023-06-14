package com.jore.epoc.views;

import java.util.Optional;

import org.vaadin.lineawesome.LineAwesomeIcon;

import com.jore.epoc.dto.OpenUserSimulationDto;
import com.jore.epoc.dto.UserDto;
import com.jore.epoc.services.SimulationService;
import com.jore.epoc.services.UserAdminService;
import com.jore.epoc.views.about.AboutView;
import com.jore.epoc.views.company.CompanyView;
import com.jore.epoc.views.simulations.SimulationsView;
import com.jore.epoc.views.users.UserView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * The main view is a top-level placeholder for other views.
 */
@SuppressWarnings("serial")
public class MainLayout extends AppLayout {
    private H2 viewTitle;
    private final AccessAnnotationChecker accessChecker;
    private final UserAdminService userAdminService;
    private final AuthenticationContext authenticationContext;
    private SimulationService simulationService;

    public MainLayout(AccessAnnotationChecker accessChecker, UserAdminService currentUserService, AuthenticationContext authenticationContext, SimulationService simulationService) {
        this.accessChecker = accessChecker;
        this.userAdminService = currentUserService;
        this.authenticationContext = authenticationContext;
        this.simulationService = simulationService;
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addDrawerContent() {
        H1 appName = new H1("epoc");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);
        //        Scroller scroller = new Scroller(createNavigation());
        Scroller scroller = new Scroller(createNavigationWithRouters());
        addToDrawer(header, scroller, createFooter());
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.getElement().setAttribute("aria-label", "Menu toggle");
        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        addToNavbar(true, toggle, viewTitle);
    }

    private Footer createFooter() {
        Footer layout = new Footer();
        Optional<UserDto> maybeUser = userAdminService.getAuthenticatedUser();
        if (maybeUser.isPresent()) {
            UserDto user = maybeUser.get();
            Avatar avatar = new Avatar(getUserName(user));
            //            StreamResource resource = new StreamResource("profile-pic", () -> new ByteArrayInputStream(user.getProfilePicture()));
            //            avatar.setImageResource(resource);
            avatar.setThemeName("xsmall");
            avatar.getElement().setAttribute("tabindex", "-1");
            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");
            MenuItem userName = userMenu.addItem("");
            Div div = new Div();
            div.add(avatar);
            div.add(getUserName(user));
            div.add(new Icon("lumo", "dropdown"));
            div.getElement().getStyle().set("display", "flex");
            div.getElement().getStyle().set("align-items", "center");
            div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
            userName.add(div);
            userName.getSubMenu().addItem("Sign out", e -> {
                authenticationContext.logout();
            });
            layout.add(userMenu);
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            layout.add(loginLink);
        }
        return layout;
    }

    private Component createNavigationWithRouters() {
        Tabs result = new Tabs();
        result.setOrientation(Tabs.Orientation.VERTICAL);
        result.addThemeVariants(TabsVariant.LUMO_MINIMAL);
        result.setId("tabs");
        if (accessChecker.hasAccess(SimulationsView.class)) {
            result.add(embedRouterLink("Simulations", new RouterLink(SimulationsView.class), LineAwesomeIcon.LIST_SOLID));
        }
        if (accessChecker.hasAccess(CompanyView.class)) {
            Optional<UserDto> authenticatedUser = userAdminService.getAuthenticatedUser();
            if (authenticatedUser.isPresent()) {
                for (OpenUserSimulationDto simulatedCompany : simulationService.getOpenSimulationsForUser(authenticatedUser.get().getId())) {
                    result.add(embedRouterLink(simulatedCompany.getCompanyName(), new RouterLink(CompanyView.class, simulatedCompany.getCompanyId()), LineAwesomeIcon.GLOBE_SOLID));
                }
            }
        }
        if (accessChecker.hasAccess(UserView.class)) {
            result.add(embedRouterLink("Users", new RouterLink(UserView.class), LineAwesomeIcon.COLUMNS_SOLID));
        }
        if (accessChecker.hasAccess(AboutView.class)) {
            result.add(embedRouterLink("About", new RouterLink(AboutView.class), LineAwesomeIcon.FILE));
        }
        return result;
    }

    private Tab embedRouterLink(String title, RouterLink listLink, LineAwesomeIcon icon) {
        Tab result = new Tab();
        listLink.setText(title);
        result.getElement().appendChild(icon.create().getElement());
        result.getElement().appendChild(listLink.getElement());
        return result;
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }

    private String getUserName(UserDto user) {
        String result = null;
        if (user.getFirstName() != null & user.getLastName() != null) {
            result = user.getFirstName() + " " + user.getLastName();
        } else {
            result = user.getUsername();
        }
        return result;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }
}
