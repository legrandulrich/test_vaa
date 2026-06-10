package com.example.views;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

import java.util.HashMap;
import java.util.Map;

/**
 * Écran d'accueil de l'application (SIRUL). Première vue affichée.
 *
 * <ul>
 *   <li>« Gestion des unités » → ouvre {@link Tcmgorg1MainView} dans un onglet
 *       fermable du corps. Tant qu'aucun onglet n'est ouvert, le corps
 *       affiche le contenu d'accueil (titre SIRUL et bouton « Quitter »).
 *       Un onglet déjà ouvert n'est jamais dupliqué : il est réactivé.</li>
 *   <li>« Quitter » → ferme l'onglet courant du navigateur.</li>
 * </ul>
 */
@Route(value = "home", layout = MainLayout.class)
@PageTitle("Menu | SIRUL")
@StyleSheet("styles/app.css")
@SpringComponent
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class HomeView extends Div {

    /** Titre de l'onglet « Gestion des unités ». */
    private static final String TITRE_GESTION_UNITES =
            "Système de la recherche - Gestion des organismes pourvoyeurs";

    /** Zone centrale accueillant les onglets fermables. */
    private final TabSheet zoneOnglets = new TabSheet();

    /** Onglets actuellement ouverts, indexés par titre, pour éviter les doublons. */
    private final Map<String, Tab> ongletsOuverts = new HashMap<>();

    /** Corps de l'accueil : affiche soit le contenu par défaut, soit les onglets. */
    private Div corps;

    /** Conteneur des onglets (créé à la première ouverture). */
    private Div conteneurOnglets;

    /** Contenu d'accueil affiché quand aucun onglet n'est ouvert. */
    private Component contenuParDefaut;

    private Button quitterButton;

    public HomeView() {
        addClassName("acceuil-view");
        Div barreMenu = creerBarreMenu();
        corps = creerCorps();
        add(new AppHeader(), barreMenu, corps);
    }

    /** Barre de menu sombre de l'en-tête. */
    private Div creerBarreMenu() {
        Span gestionUnitesItem = elementMenu("Gestion des unités");
        gestionUnitesItem.addClickListener(event ->
                ouvrirOnglet(TITRE_GESTION_UNITES, new Tcmgorg1MainView()));

        Span pilotageItem = elementMenu("Pilotage");
        attacherMenuPilotage(pilotageItem);

        Div barre = new Div(
                elementMenu("Fichier"),
                elementMenu("Projet"),
                elementMenu("Chercheur"),
                elementMenu("Grp. recherche"),
                gestionUnitesItem,
                pilotageItem,
                elementMenu("Fenêtre"),
                elementMenu("Aide"));
        barre.addClassName("orpv-menubar");
        return barre;
    }

    /**
     * Attache à l'élément « Pilotage » un menu déroulant (ouverture au clic).
     * « Organisme » ouvre l'onglet {@link Tcmgorg1MainView} ; les trois premières
     * entrées possèdent un sous-menu (contenu à préciser).
     */
    private void attacherMenuPilotage(Span cible) {
        ContextMenu menu = new ContextMenu(cible);
        menu.setOpenOnClick(true);

        menu.addItem("Projet").getSubMenu().addItem("(à compléter)");
        menu.addItem("Comité d'évaluation").getSubMenu().addItem("(à compléter)");
        menu.addItem("Gestion des risques").getSubMenu().addItem("(à compléter)");

        menu.addItem("Lieu");
        menu.addItem("Organisme", event ->
                ouvrirOnglet(TITRE_GESTION_UNITES, new Tcmgorg1MainView()));
        menu.addItem("Catégorie d'organisme");
        menu.addItem("Domaine de recherche");
        menu.addItem("Gestion des défis");
        menu.addItem("Gestion des axes");
        menu.addItem("Type de table de domaine");
        menu.addItem("Secteur d'activité de recherche");
        menu.addItem("Domaine cru");
        menu.addItem("Correspondance clarder cru");
        menu.addItem("Unite domaine");
        menu.addItem("Conseillers");
        menu.addItem("Gestion des mots-clés");
        menu.addItem("Langue");
        menu.addItem("Pilotage général");
    }

    /** Corps de l'accueil : contient le contenu par défaut, puis les onglets. */
    private Div creerCorps() {
        contenuParDefaut = creerContenuCentral();
        Div corpsPrincipal = new Div(contenuParDefaut);
        corpsPrincipal.addClassName("acceuil-corps");
        return corpsPrincipal;
    }

    /** Élément cliquable de la barre de menu. */
    private Span elementMenu(String libelle) {
        Span item = new Span(libelle);
        item.addClassName("orpv-menu-item");
        return item;
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

        HorizontalLayout boutons = new HorizontalLayout(quitterButton);
        boutons.addClassName("acceuil-boutons");
        boutons.setJustifyContentMode(JustifyContentMode.CENTER);

        Div bloc = new Div(titre, sousTitre, boutons);
        bloc.addClassName("acceuil-bloc-central");

        Div centre = new Div(bloc);
        centre.addClassName("acceuil-centre");
        return centre;
    }

    /**
     * Ouvre l'onglet portant ce titre et l'active. Si un onglet de même titre
     * est déjà ouvert, il est simplement réactivé (pas de doublon).
     */
    private void ouvrirOnglet(String titre, Component contenu) {
        if (conteneurOnglets == null) {
            creerConteneurOnglets();
        }

        Tab ongletExistant = ongletsOuverts.get(titre);
        if (ongletExistant != null) {
            zoneOnglets.setSelectedTab(ongletExistant);
            return;
        }

        Button boutonFermer = new Button(VaadinIcon.CLOSE_SMALL.create());
        boutonFermer.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE,
                ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ICON);
        boutonFermer.addClassName("orpv-tab-close");

        Span libelle = new Span(titre);
        libelle.addClassName("orpv-tab-label");
        HorizontalLayout enteteOnglet = new HorizontalLayout(libelle, boutonFermer);
        enteteOnglet.addClassName("orpv-tab-header");
        enteteOnglet.setAlignItems(Alignment.CENTER);
        enteteOnglet.setSpacing(false);
        enteteOnglet.getStyle().set("gap", "8px");

        Tab onglet = new Tab(enteteOnglet);
        zoneOnglets.add(onglet, contenu);
        zoneOnglets.setSelectedTab(onglet);
        ongletsOuverts.put(titre, onglet);

        Runnable fermerOnglet = () -> {
            zoneOnglets.remove(onglet);
            ongletsOuverts.remove(titre);
            mettreAJourAffichage();
        };
        boutonFermer.addClickListener(event -> fermerOnglet.run());
        if (contenu instanceof Tcmgorg1MainView gopView) {
            gopView.setFermetureAction(fermerOnglet);
        }

        mettreAJourAffichage();
    }

    private void creerConteneurOnglets() {
        zoneOnglets.setSizeFull();
        zoneOnglets.addClassName("orpv-main-tabs");

        conteneurOnglets = new Div(zoneOnglets);
        conteneurOnglets.addClassName("acceuil-onglets");
        conteneurOnglets.setSizeFull();
        corps.add(conteneurOnglets);
    }

    /**
     * Affiche le contenu d'accueil quand aucun onglet n'est ouvert, sinon les
     * onglets. Le conteneur d'onglets est retiré une fois le dernier fermé.
     */
    private void mettreAJourAffichage() {
        boolean hasTabs = !ongletsOuverts.isEmpty();
        contenuParDefaut.setVisible(!hasTabs);
        if (conteneurOnglets != null) {
            conteneurOnglets.setVisible(hasTabs);
        }
        if (!hasTabs && conteneurOnglets != null) {
            corps.remove(conteneurOnglets);
            conteneurOnglets = null;
        }
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
