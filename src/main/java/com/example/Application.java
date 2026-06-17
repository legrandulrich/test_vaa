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
@CssImport("./styles/themes/gris_vert.css")
@CssImport("./styles/themes/bleu_azur.css")
@CssImport("./styles/themes/vert_emeraude.css")
@CssImport("./styles/themes/terracotta.css")
@CssImport("./styles/themes/violet_amethyste.css")
@CssImport("./styles/themes/bleu_ardoise.css")
@CssImport("./styles/themes/gris.css")
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
