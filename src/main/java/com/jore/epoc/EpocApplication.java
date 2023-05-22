package com.jore.epoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SuppressWarnings("serial")
@SpringBootApplication
@Theme(value = "epoc", variant = Lumo.DARK)
public class EpocApplication implements AppShellConfigurator {
    public static void main(String[] args) {
        SpringApplication.run(EpocApplication.class, args);
    }
}
