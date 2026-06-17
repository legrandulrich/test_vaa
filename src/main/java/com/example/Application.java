package com.example;

import com.vaadin.flow.theme.aura.Aura;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;

@SpringBootApplication
@StyleSheet(Aura.STYLESHEET)
// Styles de base servis depuis le dossier frontend (src/main/frontend/styles/app.css),
// empaquetés par le build. Les thèmes commutables restent en ressources statiques
// (chargés par URL au runtime, cf. HomeView/MainLayout).
@CssImport("./styles/app.css")
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
