package com.example.views;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

/**
 * Écran d'accueil de l'application (SIRUL). Première vue affichée.
 *
 * <ul>
 *   <li>« Continuer » → navigue vers {@link HomeView}.</li>
 *   <li>« Quitter » → ferme l'onglet courant du navigateur.</li>
 * </ul>
 */
@Route(value = "", layout = MainLayout.class)
@PageTitle("Accueil | SIRUL")
@SpringComponent
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class AcceuilView extends Div {

    private Button quitterButton;
    private Button continuerButton;

    public AcceuilView() {
        addClassName("acceuil-view");
        add(new AppHeader(), creerContenuCentral());
    }

    /** Bloc central : titre SIRUL, sous-titre et boutons d'action. */
    private Component creerContenuCentral() {
        H1 titre = new H1("SIRUL");
        titre.addClassName("acceuil-titre");

        Paragraph sousTitre = new Paragraph(
                "Système d'information sur la recherche à l'Université Laval");
        sousTitre.addClassName("acceuil-soustitre");

        quitterButton = new Button("Quitter", event -> quitterApplication());
        quitterButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        quitterButton.addClassName("acceuil-bouton-quitter");

        continuerButton = new Button("Continuer", event -> continuerVersMenu());
        continuerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        continuerButton.addClassName("acceuil-bouton-continuer");

        HorizontalLayout boutons = new HorizontalLayout(quitterButton, continuerButton);
        boutons.addClassName("acceuil-boutons");
        boutons.setJustifyContentMode(JustifyContentMode.CENTER);

        Div bloc = new Div(titre, sousTitre, boutons);
        bloc.addClassName("acceuil-bloc-central");

        Div centre = new Div(bloc);
        centre.addClassName("acceuil-centre");
        return centre;
    }

    /** « Continuer » : navigation vers le menu principal. */
    private void continuerVersMenu() {
        getUI().ifPresent(ui -> ui.navigate(HomeView.class));
    }

    /** « Quitter » : ferme l'onglet courant du navigateur.
     *  NB : les navigateurs n'autorisent window.close() de façon fiable que
     *  pour les onglets ouverts par script. */
    private void quitterApplication() {
        getUI().ifPresent(ui -> ui.getPage().executeJs("window.close();"));
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        MainLayout.rechercher(this)
                .ifPresent(layout -> layout.getStatusBar().setMessage("Accueil | Prêt"));
    }
}
