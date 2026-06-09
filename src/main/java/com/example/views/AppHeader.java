package com.example.views;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;

/**
 * En-tête applicatif commun : barre blanche pleine largeur affichant le logo
 * de l'Université Laval et l'intitulé du vice-rectorat. Réutilisé par
 * {@link AcceuilView} et {@link HomeView}.
 */
@StyleSheet("styles/app.css")
public class AppHeader extends Div {

    /** Logo « signature » officiel de l'Université Laval (logo + texte). */
    private static final String LOGO_UNIVERSITE_LAVAL_URL =
            "https://upload.wikimedia.org/wikipedia/fr/b/bf/Universit%C3%A9_Laval_logo_et_texte.svg";

    public AppHeader() {
        addClassName("app-header");

        Image logoUniversiteLaval = new Image(LOGO_UNIVERSITE_LAVAL_URL, "Université Laval");
        logoUniversiteLaval.addClassName("app-header-logo");

        Span titre = new Span("Vice-rectorat à la recherche");
        titre.addClassName("app-header-title");

        add(logoUniversiteLaval, titre);
    }
}
