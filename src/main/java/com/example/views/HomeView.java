package com.example.views;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.SubMenu;
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
@SpringComponent
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class HomeView extends Div {

    /** Titre de l'onglet « Gestion des unités ». */
    private static final String TITRE_GESTION_UNITES =
            "Système de la recherche - Gestion des organismes pourvoyeurs";

    /** Feuilles de style des thèmes proposés (sous Aide ▸ Thème). Le thème 9 est
     *  le thème par défaut, appliqué au chargement (cf. MainLayout) à défaut de
     *  choix mémorisé. Chemins relatifs à la racine (servis depuis META-INF/resources). */
    private static final String THEME_1 = "styles/themes/theme1.css";
    private static final String THEME_3 = "styles/themes/theme3.css";
    private static final String THEME_4 = "styles/themes/theme4.css";
    private static final String THEME_5 = "styles/themes/theme5.css";
    private static final String THEME_6 = "styles/themes/theme6.css";
    private static final String THEME_9 = "styles/themes/theme9.css";
    private static final String THEME_10 = "styles/themes/theme10.css";

    /**
     * Applique un thème : échange la feuille de style « orpv-theme » du
     * document, bascule (ou retire) le mode sombre de Lumo, puis mémorise le
     * choix dans le navigateur (localStorage) afin de le réappliquer lors des
     * visites suivantes — jusqu'à ce que l'utilisateur en choisisse un autre.
     * Paramètres : $0 = href de la feuille, $1 = mode sombre (booléen).
     */
    private static final String JS_APPLIQUER_THEME =
            "let lien = document.getElementById('orpv-theme');"
          + "if (!lien) { lien = document.createElement('link'); lien.id = 'orpv-theme';"
          + "  lien.rel = 'stylesheet'; document.head.appendChild(lien); }"
          + "if (lien.getAttribute('href') !== $0) { lien.setAttribute('href', $0); }"
          + "if ($1) { document.documentElement.setAttribute('theme', 'dark'); }"
          + "else { document.documentElement.removeAttribute('theme'); }"
          + "window.localStorage.setItem('orpv-theme-href', $0);"
          + "window.localStorage.setItem('orpv-theme-dark', $1 ? 'true' : 'false');";

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
        Span fichierItem = elementMenu("Fichier");
        attacherMenuFichier(fichierItem);

        Span gestionUnitesItem = elementMenu("Gestion des unités");
        gestionUnitesItem.addClickListener(event ->
                ouvrirOnglet(TITRE_GESTION_UNITES, new Tcmgorg1MainView()));

        Span pilotageItem = elementMenu("Pilotage");
        attacherMenuPilotage(pilotageItem);

        Span aideItem = elementMenu("Aide");
        attacherMenuAide(aideItem);

        Div barre = new Div(
                fichierItem,
                elementMenu("Projet"),
                elementMenu("Chercheur"),
                elementMenu("Grp. recherche"),
                gestionUnitesItem,
                pilotageItem,
                elementMenu("Fenêtre"),
                aideItem);
        barre.addClassName("orpv-menubar");
        return barre;
    }

    /**
     * Attache à l'élément « Fichier » un menu déroulant (ouverture au clic)
     * contenant l'entrée « Quitter », qui ferme l'onglet de l'application dans
     * le navigateur.
     */
    private void attacherMenuFichier(Span cible) {
        ContextMenu menu = new ContextMenu(cible);
        menu.setOpenOnClick(true);
        menu.addItem("Quitter", event -> fermerOngletNavigateur());
    }

    /**
     * Script de « Quitter » : tente d'abord de fermer l'onglet
     * ({@code window.close()}, efficace seulement pour un onglet ouvert par
     * script) ; si le navigateur le refuse, la page est remplacée par un écran de
     * fin invitant l'utilisateur à fermer l'onglet lui-même. Le fond reprend le
     * gris du thème par défaut.
     */
    private static final String JS_QUITTER = """
            try { window.close(); } catch (e) {}
            document.open();
            document.write('<!DOCTYPE html><html lang="fr"><head><meta charset="utf-8">'
              + '<title>Session terminée</title></head>'
              + '<body style="margin:0;height:100vh;display:flex;align-items:center;justify-content:center;font-family:Segoe UI,Roboto,Arial,sans-serif;background:#bfc6c4;color:#1e2422;">'
              + '<div style="text-align:center;padding:32px 40px;background:#eceff3;border:1px solid #8a928f;border-radius:6px;box-shadow:0 6px 20px rgba(0,0,0,.25);">'
              + '<h1 style="margin:0 0 10px;font-size:20px;font-weight:600;">Application fermée</h1>'
              + '<p style="margin:0;font-size:14px;color:#515855;">Vous pouvez fermer cet onglet du navigateur.</p>'
              + '</div></body></html>');
            document.close();
            """;

    /**
     * « Quitter » : ferme l'onglet du navigateur, ou — si le navigateur refuse de
     * fermer un onglet non ouvert par script — affiche un écran de fin.
     */
    private void fermerOngletNavigateur() {
        getUI().ifPresent(ui -> ui.getPage().executeJs(JS_QUITTER));
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

    /**
     * Attache à l'élément « Aide » un menu déroulant contenant un sous-menu
     * « Thème » : les variantes de présentation nommées d'après leur couleur
     * dominante (Gris vert, Bleu azur, Vert émeraude, Terracotta, Violet
     * améthyste, Bleu ardoise, Gris), toutes claires. « Bleu ardoise » est le
     * thème par défaut appliqué au chargement. Le thème choisi est appliqué
     * immédiatement et mémorisé dans le navigateur (cf. {@link #appliquerTheme}).
     */
    private void attacherMenuAide(Span cible) {
        ContextMenu menu = new ContextMenu(cible);
        menu.setOpenOnClick(true);

        SubMenu themes = menu.addItem("Thème").getSubMenu();
        themes.addItem("Gris vert", event -> appliquerTheme(THEME_1, false));
        themes.addItem("Bleu azur", event -> appliquerTheme(THEME_3, false));
        themes.addItem("Vert émeraude", event -> appliquerTheme(THEME_4, false));
        themes.addItem("Terracotta", event -> appliquerTheme(THEME_5, false));
        themes.addItem("Violet améthyste", event -> appliquerTheme(THEME_6, false));
        themes.addItem("Bleu ardoise", event -> appliquerTheme(THEME_9, false));
        themes.addItem("Gris", event -> appliquerTheme(THEME_10, false));
    }

    /**
     * Applique la feuille de thème indiquée et la mémorise dans le navigateur.
     *
     * @param fichierCss chemin de la feuille de thème à attacher
     * @param sombre     {@code true} pour activer aussi le mode sombre de Lumo
     */
    private void appliquerTheme(String fichierCss, boolean sombre) {
        getUI().ifPresent(ui ->
                ui.getPage().executeJs(JS_APPLIQUER_THEME, fichierCss, sombre));
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
