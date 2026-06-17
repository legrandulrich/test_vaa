package com.example.views;

import com.example.views.shared.AppHeader;
import com.example.views.shared.MainLayout;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

/**
 * Écran de connexion (SIRUL). Réutilise l'en-tête {@link AppHeader} et le fond
 * gris commun de l'accueil, et présente une carte de connexion centrée et
 * responsive : utilisateur, mot de passe et base de données.
 *
 * <p>« Connexion » mène à l'accueil ({@link AcceuilView}).</p>
 */
@Route(value = "login", layout = MainLayout.class)
@PageTitle("Connexion | SIRUL")
@SpringComponent
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class LoginView extends Div {

    private TextField utilisateurField;
    private PasswordField motDePasseField;
    private TextField baseDonneesField;
    private Button connexionButton;

    public LoginView() {
        addClassNames("acceuil-view", "login-view");
        add(new AppHeader(), creerContenuCentral());
    }

    /** Bloc central : titre SIRUL, sous-titre et carte de connexion. */
    private Component creerContenuCentral() {
        H1 titre = new H1("SIRUL");
        titre.addClassName("acceuil-titre");

        Paragraph sousTitre = new Paragraph(
                "Système d'information sur la recherche à l'Université Laval");
        sousTitre.addClassName("acceuil-soustitre");

        Div bloc = new Div(titre, sousTitre, creerCarteConnexion());
        bloc.addClassName("acceuil-bloc-central");

        Div centre = new Div(bloc);
        centre.addClassName("acceuil-centre");
        return centre;
    }

    /** Carte blanche : titre, champs de saisie et bouton « Connexion ». */
    private Component creerCarteConnexion() {
        H2 titreCarte = new H2("Connectez-vous pour commencer");
        titreCarte.addClassName("login-card-titre");

        utilisateurField = new TextField("Utilisateur");
        utilisateurField.setWidthFull();
        utilisateurField.setAutofocus(true);

        motDePasseField = new PasswordField("Mot de passe");
        motDePasseField.setWidthFull();

        baseDonneesField = new TextField("Base de données");
        baseDonneesField.setValue("sgdvpj");
        baseDonneesField.setWidthFull();

        connexionButton = new Button("Connexion", event -> seConnecter());
        connexionButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        connexionButton.addClassName("login-bouton");
        connexionButton.setWidthFull();
        connexionButton.addClickShortcut(Key.ENTER);

        VerticalLayout corps = new VerticalLayout(titreCarte, utilisateurField,
                motDePasseField, baseDonneesField, connexionButton);
        corps.setPadding(false);
        corps.setSpacing(false);
        corps.addClassName("login-card-corps");

        Div carte = new Div(corps);
        carte.addClassName("login-card");
        return carte;
    }

    /** « Connexion » : navigation vers l'accueil. */
    private void seConnecter() {
        getUI().ifPresent(ui -> ui.navigate(AcceuilView.class));
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        MainLayout.rechercher(this)
                .ifPresent(layout -> layout.getStatusBar().setMessage("Connexion | Prêt"));
    }
}
