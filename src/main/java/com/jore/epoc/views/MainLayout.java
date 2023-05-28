package com.jore.epoc.views;

import java.util.Optional;

import org.vaadin.lineawesome.LineAwesomeIcon;

import com.jore.epoc.components.appnav.AppNav;
import com.jore.epoc.components.appnav.AppNavItem;
import com.jore.epoc.dto.UserDto;
import com.jore.epoc.services.CurrentUserService;
import com.jore.epoc.views.about.AboutView;
import com.jore.epoc.views.mypersons.MyPersonsView;
import com.jore.epoc.views.mysimulations.SimulationsView;
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
    private final CurrentUserService currentUserService;
    private final AuthenticationContext authenticationContext;
    private AppNav result = new AppNav();

    public MainLayout(AccessAnnotationChecker accessChecker, CurrentUserService currentUserService, AuthenticationContext authenticationContext) {
        this.accessChecker = accessChecker;
        this.currentUserService = currentUserService;
        this.authenticationContext = authenticationContext;
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    public void addView(Component userForm) {
        //        if (accessChecker.hasAccess(userForm.getClass())) {
        //        AppNavItem appNavItem = new AppNavItem("This Person", MyPersonsView.class, LineAwesomeIcon.PERSON_BOOTH_SOLID.create());
        //        result.addItem(appNavItem);
        //        }
        RouterLink link = new RouterLink();
        addToDrawer(link);
    }

    private void addDrawerContent() {
        H1 appName = new H1("epoc");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);
        Scroller scroller = new Scroller(createNavigation());
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
        Optional<UserDto> maybeUser = currentUserService.getAuthenticatedUser();
        if (maybeUser.isPresent()) {
            UserDto user = maybeUser.get();
            Avatar avatar = new Avatar(user.getFirstName() + " " + user.getLastName());
            //            StreamResource resource = new StreamResource("profile-pic", () -> new ByteArrayInputStream(user.getProfilePicture()));
            //            avatar.setImageResource(resource);
            avatar.setThemeName("xsmall");
            avatar.getElement().setAttribute("tabindex", "-1");
            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");
            MenuItem userName = userMenu.addItem("");
            Div div = new Div();
            div.add(avatar);
            div.add(user.getFirstName() + " " + user.getLastName());
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

    private AppNav createNavigation() {
        if (accessChecker.hasAccess(SimulationsView.class)) {
            result.addItem(new AppNavItem("My Persons", MyPersonsView.class, LineAwesomeIcon.LIST_SOLID.create()));
        }
        if (accessChecker.hasAccess(SimulationsView.class)) {
            result.addItem(new AppNavItem("My Simulations", SimulationsView.class, LineAwesomeIcon.LIST_SOLID.create()));
        }
        if (accessChecker.hasAccess(UserView.class)) {
            result.addItem(new AppNavItem("Users", UserView.class, LineAwesomeIcon.COLUMNS_SOLID.create()));
        }
        if (accessChecker.hasAccess(AboutView.class)) {
            result.addItem(new AppNavItem("About", AboutView.class, LineAwesomeIcon.FILE.create()));
        }
        return result;
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }
}
