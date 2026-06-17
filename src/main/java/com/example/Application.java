package com.example;

import com.vaadin.flow.theme.aura.Aura;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;

@SpringBootApplication
@StyleSheet(Aura.STYLESHEET)
// Styles empaquetés par le build depuis src/main/frontend/styles/. app.css porte
// la structure et les valeurs par défaut ; chaque thème est une variante d'attribut
// (html[theme~="..."]) activée via l'attribut « theme » (cf. HomeView/MainLayout).
@CssImport("./styles/app.css")
@CssImport("./styles/themes/theme1.css")
@CssImport("./styles/themes/theme3.css")
@CssImport("./styles/themes/theme4.css")
@CssImport("./styles/themes/theme5.css")
@CssImport("./styles/themes/theme6.css")
@CssImport("./styles/themes/theme9.css")
@CssImport("./styles/themes/theme10.css")
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
