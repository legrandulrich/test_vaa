package com.example.views;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.FormItem;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep.LabelsPosition;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextAreaVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * Vue de gestion des organismes pourvoyeurs (ORPV).
 *
 * <p>Cette vue est constituée uniquement du corps du formulaire (les sections
 * de saisie) ; elle n'embarque ni fenêtre ni barre de titre. Elle peut donc
 * être affichée telle quelle par {@link HomeView} dans un onglet.</p>
 */
@Route(value = "organismes-pourvoyeurs", layout = MainLayout.class)
@PageTitle("Gestion des organismes pourvoyeurs")
@StyleSheet("styles/app.css")
@SpringComponent
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class GopView extends Div {

    // --- Section identification ---
    private TextField codeOrpvField;
    private Button codeOrpvLookupButton;
    private TextField siruField;
    private TextField messField;
    private TextField acronymeField;
    private TextField nomField;
    private TextField autreNomField;
    private TextField villeCodeField;
    private Button villeLookupButton;
    private TextField villeLibelleField;
    private Button ajoutLieuButton;

    // --- Options (cases à cocher) ---
    private final Checkbox payeurFraisIndirectCheckbox = new Checkbox();
    private final Checkbox comptabilisationFondsInternesCheckbox = new Checkbox();
    private final Checkbox fondsDotationCheckbox = new Checkbox();

    // --- Onglet adresse ---
    private TextArea adresseSecondaireArea;
    private TextField codePostalField;

    // --- Section bas de formulaire ---
    private TextField categorieCodeField;
    private Button categorieLookupButton;
    private TextField categorieLibelleField;
    private TextField siteWwwField;
    private TextField codeRevenuMeqField;
    private TextField anneeFinActiviteField;
    private TextField moisFermetureField;

    /** Action de fermeture de l'onglet hôte, injectée par {@link HomeView}. */
    private Runnable fermetureAction;

    public GopView() {
        // HomeView est constitué uniquement du corps du formulaire :
        // on ajoute directement les sections de saisie, sans fenêtre ni titre.
        addClassName("orpv-body");
        add(creerToolBar(),
            creerFormulaireIdentite(),
                creerLigneOptions(),
                creerOnglets(),
                creerFormulaireComplement());
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        MainLayout.rechercher(this)
                .ifPresent(layout -> layout.getStatusBar()
                        .setMessage("Gestion des organismes pourvoyeurs | Prêt"));
    }

    // ------------------------------------------------------------------
    // Barre d'outils
    // ------------------------------------------------------------------

    /**
     * Barre d'outils façon application bureautique : 14 boutons-icônes
     * regroupés (fichier / édition / navigation / enregistrements / requête).
     * Chaque bouton porte une info-bulle décrivant sa fonction et son
     * raccourci clavier, lequel déclenche le clic du bouton.
     */
    private Component creerToolBar() {
        Div barre = new Div(
                // 1 à 3 — fichier
                boutonOutilAction("quitter",
                        "Quitter l'application en cours et revenir à la page précédente ou à la page d'accueil (Ctrl+Q)",
                        this::quitter, Key.KEY_Q, KeyModifier.CONTROL),
                boutonOutil("enregistrer",
                        "Enregistrer les changements effectués à une entente de financement (F10)",
                        Key.F10),
                boutonOutil("imprimer",
                        "Imprimer la fenêtre courante (Maj+F8)",
                        Key.F8, KeyModifier.SHIFT),
                separateur(),
                // 4 à 7 — édition
                boutonOutil("couper",
                        "Couper le texte sélectionné (Ctrl+X)",
                        Key.KEY_X, KeyModifier.CONTROL),
                boutonOutil("copier",
                        "Copier le texte sélectionné (Ctrl+C)",
                        Key.KEY_C, KeyModifier.CONTROL),
                boutonOutil("coller",
                        "Coller le texte sélectionné (Ctrl+V)",
                        Key.KEY_V, KeyModifier.CONTROL),
                boutonOutil("editer",
                        "Éditer le contenu du champ de la zone de texte où le curseur est positionné (Ctrl+E)",
                        Key.KEY_E, KeyModifier.CONTROL),
                separateur(),
                // 8 à 11 — navigation entre enregistrements
                boutonOutil("premier",
                        "Aller au premier enregistrement"),
                boutonOutilNavigation("precedent",
                        "Aller à l'enregistrement précédent (Flèche haut)",
                        Key.ARROW_UP),
                boutonOutilNavigation("suivant",
                        "Aller à l'enregistrement suivant (Flèche bas)",
                        Key.ARROW_DOWN),
                boutonOutil("dernier",
                        "Aller au dernier enregistrement"),
                separateur(),
                // 12 à 13 — enregistrements
                boutonOutil("nouveau",
                        "Créer un nouvel enregistrement (F6)",
                        Key.F6),
                boutonOutil("detruire",
                        "Détruire ou effacer l'enregistrement courant identifié par le curseur (Maj+F6)",
                        Key.F6, KeyModifier.SHIFT),
                separateur(),
                // 14 — interrogation
                boutonOutil("interroger",
                        "Lancer le mode d'interrogation (F7)",
                        Key.F7));
        barre.addClassName("orpv-toolbar");
        return barre;
    }

    /** Bouton-icône (sans raccourci) : l'icône SVG est servie depuis /icons. */
    private Button boutonOutil(String icone, String infoBulle) {
        Image image = new Image("icons/" + icone + ".svg", infoBulle);
        image.addClassName("orpv-tool-icon");
        Button bouton = new Button(image);
        bouton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY,
                ButtonVariant.LUMO_SMALL);
        bouton.addClassName("orpv-tool-button");
        bouton.setTooltipText(infoBulle);
        bouton.setAriaLabel(infoBulle);
        return bouton;
    }

    /** Bouton-icône dont le raccourci clavier déclenche le clic. */
    private Button boutonOutil(String icone, String infoBulle, Key touche,
            KeyModifier... modificateurs) {
        Button bouton = boutonOutil(icone, infoBulle);
        bouton.addClickShortcut(touche, modificateurs);
        return bouton;
    }

    /** Bouton-icône avec une action au clic, en plus du raccourci clavier. */
    private Button boutonOutilAction(String icone, String infoBulle, Runnable action,
            Key touche, KeyModifier... modificateurs) {
        Button bouton = boutonOutil(icone, infoBulle);
        bouton.addClickListener(event -> action.run());
        bouton.addClickShortcut(touche, modificateurs);
        return bouton;
    }

    /**
     * Bouton de navigation dont le raccourci est une flèche : on laisse le
     * navigateur conserver son comportement par défaut (déplacement du curseur
     * dans les champs) en plus du déclenchement du bouton.
     */
    private Button boutonOutilNavigation(String icone, String infoBulle, Key touche) {
        Button bouton = boutonOutil(icone, infoBulle);
        bouton.addClickShortcut(touche).allowBrowserDefault();
        return bouton;
    }

    /** Séparateur vertical entre deux groupes de boutons. */
    private Span separateur() {
        Span sep = new Span();
        sep.addClassName("orpv-toolbar-sep");
        return sep;
    }

    /**
     * Définit l'action de « Quitter » : la fermeture de l'onglet hôte.
     * Injectée par {@link HomeView} à l'ouverture de l'onglet.
     */
    public void setFermetureAction(Runnable fermetureAction) {
        this.fermetureAction = fermetureAction;
    }

    /**
     * « Quitter » : ferme l'onglet hôte s'il existe, sinon (vue ouverte seule
     * via sa route) revient à l'écran d'accueil.
     */
    private void quitter() {
        if (fermetureAction != null) {
            fermetureAction.run();
        } else {
            getUI().ifPresent(ui -> ui.navigate(AcceuilView.class));
        }
    }

    /** Bloc supérieur : codes, nom, ville. */
    private FormLayout creerFormulaireIdentite() {
        codeOrpvField = champTexte("11767");
        codeOrpvField.setWidthFull();
        codeOrpvLookupButton = boutonRecherche();
        codeOrpvLookupButton.addClickListener(e -> ouvrirListeValeurs(
                "Nom de l'organisme", nomField, "Acronyme de l'organisme", acronymeField));
        HorizontalLayout codeOrpvRow = champAvecRecherche(codeOrpvField, codeOrpvLookupButton);

        siruField = champTexte("14531");
        siruField.setWidthFull();
        messField = champTexte("14531");
        messField.setWidthFull();
        acronymeField = champTexte("AEI");
        acronymeField.setWidthFull();

        nomField = champTexte("A E I Technologies Inc.");
        nomField.setWidthFull();
        autreNomField = champTexte("");
        autreNomField.setWidthFull();

        villeCodeField = champTexte("PIT");
        villeCodeField.setWidth("70px");
        villeLookupButton = boutonRecherche();
        villeLibelleField = champTexte("Pittsburgh  Pennsylvania, États-Unis");
        villeLibelleField.setWidthFull();
        villeLookupButton.addClickListener(e -> ouvrirListeLieux());
        ajoutLieuButton = new Button("Ajout d'un lieu");
        ajoutLieuButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        HorizontalLayout villeRow = new HorizontalLayout(
                villeCodeField, villeLookupButton, villeLibelleField, ajoutLieuButton);
        villeRow.setWidthFull();
        villeRow.setAlignItems(Alignment.CENTER);
        villeRow.setSpacing(false);
        villeRow.getStyle().set("gap", "6px");
        villeRow.setFlexGrow(1, villeLibelleField);

        FormLayout form = formulaireResponsive(4);
        FormItem itemCodeOrpv = form.addFormItem(codeOrpvRow, "Code ORPV");
        form.addFormItem(siruField, "SIRU");
        form.addFormItem(messField, "MESS");
        form.addFormItem(acronymeField, "Acronyme");
        FormItem itemNom = form.addFormItem(nomField, "Nom");
        FormItem itemAutreNom = form.addFormItem(autreNomField, "Autre nom");
        FormItem itemVille = form.addFormItem(villeRow, "Ville");

        form.setColspan(itemCodeOrpv, 1);
        form.setColspan(itemNom, 4);
        form.setColspan(itemAutreNom, 4);
        form.setColspan(itemVille, 4);
        return form;
    }

    /** Ligne des trois options à cocher. */
    private HorizontalLayout creerLigneOptions() {
        HorizontalLayout ligne = new HorizontalLayout(
                checkboxAvecLibelle("Payeur de frais indirect", payeurFraisIndirectCheckbox),
                checkboxAvecLibelle("Comptabilisation fonds internes", comptabilisationFondsInternesCheckbox),
                checkboxAvecLibelle("Fonds de dotation", fondsDotationCheckbox));
        ligne.setWidthFull();
        ligne.setAlignItems(Alignment.CENTER);
        ligne.setJustifyContentMode(JustifyContentMode.AROUND);
        ligne.getStyle().set("gap", "16px");
        return ligne;
    }

    /** Bloc d'onglets : adresses, accréditation, remarque. */
    private TabSheet creerOnglets() {
        TabSheet onglets = new TabSheet();
        onglets.setWidthFull();
        onglets.addClassName("orpv-tabs");

        onglets.add("Adresse principale", creerContenuAdresse());
        onglets.add("Adresse secondaire (0)", contenuVide("Aucune adresse secondaire."));
        onglets.add("Accréditation", contenuVide("Aucune accréditation."));
        onglets.add("Remarque", contenuVide("Aucune remarque."));
        return onglets;
    }

    /** Contenu de l'onglet d'adresse : zone de texte + code postal. */
    private FormLayout creerContenuAdresse() {
        adresseSecondaireArea = new TextArea();
        adresseSecondaireArea.addThemeVariants(TextAreaVariant.LUMO_SMALL);
        adresseSecondaireArea.setWidthFull();
        adresseSecondaireArea.setMinHeight("82px");

        codePostalField = champTexte("");
        codePostalField.setWidth("16em");

        FormLayout form = new FormLayout();
        form.setWidthFull();
        form.setResponsiveSteps(
                new ResponsiveStep("0", 1, LabelsPosition.TOP),
                new ResponsiveStep("30em", 1, LabelsPosition.ASIDE));
        form.setLabelWidth("110px");
        form.addFormItem(adresseSecondaireArea, "Adresse secondaire");
        form.addFormItem(codePostalField, "Code postal");
        return form;
    }

    /** Bloc inférieur : catégorie, site web, revenus. */
    private FormLayout creerFormulaireComplement() {
        categorieCodeField = champTexte("61");
        categorieCodeField.setWidth("70px");
        categorieLookupButton = boutonRecherche();
        categorieLibelleField = champTexte("Non canadien: compagnies");
        categorieLibelleField.setWidthFull();
        categorieLookupButton.addClickListener(e -> ouvrirListeCategories());
        HorizontalLayout categorieRow = new HorizontalLayout(
                categorieCodeField, categorieLookupButton, categorieLibelleField);
        categorieRow.setWidthFull();
        categorieRow.setAlignItems(Alignment.CENTER);
        categorieRow.setSpacing(false);
        categorieRow.getStyle().set("gap", "6px");
        categorieRow.setFlexGrow(1, categorieLibelleField);

        siteWwwField = champTexte("http://www.aeitecno.com/");
        siteWwwField.setWidthFull();

        codeRevenuMeqField = champTexte("430");
        codeRevenuMeqField.setWidthFull();
        anneeFinActiviteField = champTexte("");
        anneeFinActiviteField.setWidthFull();
        moisFermetureField = champTexte("03");
        moisFermetureField.setWidthFull();

        FormLayout form = formulaireResponsive(3);
        FormItem itemCategorie = form.addFormItem(categorieRow, "Catégorie");
        FormItem itemSiteWww = form.addFormItem(siteWwwField, "Site WWW");
        form.addFormItem(codeRevenuMeqField, "Code revenu MEQ");
        form.addFormItem(anneeFinActiviteField, "Année fin activité");
        form.addFormItem(moisFermetureField, "Mois fermeture");

        form.setColspan(itemCategorie, 3);
        form.setColspan(itemSiteWww, 3);
        return form;
    }

    // ------------------------------------------------------------------
    // Liste de valeurs (lookup ORPV)
    // ------------------------------------------------------------------

    /**
     * Ouvre la fenêtre modale « Critères pour la liste de valeurs ». Deux
     * critères de saisie sont proposés ; à la validation, les valeurs
     * renseignées sont reportées dans les champs cibles correspondants. Les
     * critères laissés vides n'écrasent pas la valeur existante.
     *
     * @param labelPrincipal   libellé du critère principal (champ large)
     * @param ciblePrincipale  champ destinataire du critère principal
     * @param labelSecondaire  libellé du critère secondaire (champ étroit)
     * @param cibleSecondaire  champ destinataire du critère secondaire
     */
    private void ouvrirListeValeurs(String labelPrincipal, TextField ciblePrincipale,
            String labelSecondaire, TextField cibleSecondaire) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Critères pour la liste de valeurs");
        dialog.setDraggable(true);
        dialog.setResizable(true);

        TextField criterePrincipal = new TextField(labelPrincipal);
        criterePrincipal.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        criterePrincipal.setWidthFull();

        TextField critereSecondaire = new TextField(labelSecondaire);
        critereSecondaire.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        critereSecondaire.setWidth("12em");

        VerticalLayout contenu = new VerticalLayout(criterePrincipal, critereSecondaire);
        contenu.setPadding(false);
        contenu.setSpacing(true);
        contenu.setWidth("32em");
        dialog.add(contenu);

        Button okButton = new Button("Ok", e -> {
            String valeurPrincipale = criterePrincipal.getValue().trim();
            String valeurSecondaire = critereSecondaire.getValue().trim();
            if (!valeurPrincipale.isEmpty()) {
                ciblePrincipale.setValue(valeurPrincipale);
            }
            if (!valeurSecondaire.isEmpty()) {
                cibleSecondaire.setValue(valeurSecondaire);
            }
            dialog.close();
        });
        okButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        okButton.addClickShortcut(Key.ENTER);

        Button annulerButton = new Button("Annuler", e -> dialog.close());
        annulerButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        annulerButton.addClickShortcut(Key.ESCAPE);

        dialog.getFooter().add(okButton, annulerButton);
        dialog.open();
    }

    // ------------------------------------------------------------------
    // Liste des lieux (lookup ville)
    // ------------------------------------------------------------------

    /**
     * Ouvre la fenêtre modale « Liste des lieux ». Une grille présente les lieux
     * disponibles (nom, code, type, pays attaché). L'utilisateur filtre la liste
     * via le champ « Rech » (le bouton « Rech. » ou la touche Entrée lancent la
     * recherche). À la validation — bouton « OK » ou double-clic sur une ligne —
     * le code et le nom du lieu sélectionné sont reportés respectivement dans
     * {@link #villeCodeField} et {@link #villeLibelleField}. « Annuler » referme
     * la fenêtre sans rien modifier.
     *
     * <p>La liste est pour l'instant simulée par {@link #lieuxSimules()} ; elle
     * sera remplacée ultérieurement par les données issues de la base.</p>
     */
    private void ouvrirListeLieux() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Liste des lieux");
        dialog.setDraggable(true);
        dialog.setResizable(true);
        dialog.addClassName("lieux-dialog");
        dialog.setWidth("780px");
        dialog.setHeight("540px");

        // --- Champ de recherche (« Rech ») ---
        TextField rechField = new TextField();
        rechField.setValue("%");
        rechField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        rechField.setWidthFull();
        Span rechLabel = new Span("Rech");
        rechLabel.addClassName("lieux-rech-label");
        HorizontalLayout barreRecherche = new HorizontalLayout(rechLabel, rechField);
        barreRecherche.setWidthFull();
        barreRecherche.setAlignItems(Alignment.CENTER);
        barreRecherche.setFlexGrow(1, rechField);

        // --- Grille des lieux ---
        Grid<Lieu> grille = new Grid<>();
        grille.addClassName("lieux-grid");
        grille.setSizeFull();
        grille.setSelectionMode(Grid.SelectionMode.SINGLE);
        grille.addColumn(Lieu::nomLieu).setHeader("Nom lieu")
                .setFlexGrow(2).setResizable(true);
        grille.addColumn(Lieu::code).setHeader("Code")
                .setWidth("80px").setFlexGrow(0).setResizable(true);
        grille.addColumn(Lieu::type).setHeader("Type")
                .setWidth("90px").setFlexGrow(0).setResizable(true);
        grille.addColumn(Lieu::paysAttache)
                .setHeader("Nom lieu précédent, nom pays attaché")
                .setFlexGrow(3).setResizable(true);

        List<Lieu> tousLesLieux = lieuxSimules();
        appliquerResultats(grille, tousLesLieux);

        VerticalLayout contenu = new VerticalLayout(barreRecherche, grille);
        contenu.setPadding(false);
        contenu.setSpacing(true);
        contenu.setSizeFull();
        contenu.setFlexGrow(1, grille);
        dialog.add(contenu);

        // --- Recherche : filtre la liste sur le motif saisi ---
        Runnable rechercher = () -> {
            String motif = rechField.getValue();
            List<Lieu> resultats = tousLesLieux.stream()
                    .filter(lieu -> correspond(lieu, motif))
                    .toList();
            appliquerResultats(grille, resultats);
        };
        rechField.addKeyDownListener(Key.ENTER, e -> rechercher.run());

        // --- Validation d'un lieu : report dans les champs ville ---
        Consumer<Lieu> valider = lieu -> {
            if (lieu != null) {
                villeCodeField.setValue(lieu.code());
                villeLibelleField.setValue(lieu.nomLieu());
            }
            dialog.close();
        };
        grille.addItemDoubleClickListener(e -> valider.accept(e.getItem()));

        // --- Boutons : Rech. / OK / Annuler ---
        Button rechButton = new Button("Rech.", e -> rechercher.run());
        rechButton.addThemeVariants(ButtonVariant.LUMO_SMALL);

        Button okButton = new Button("OK",
                e -> valider.accept(grille.asSingleSelect().getValue()));
        okButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);

        Button annulerButton = new Button("Annuler", e -> dialog.close());
        annulerButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        annulerButton.addClickShortcut(Key.ESCAPE);

        dialog.getFooter().add(rechButton, okButton, annulerButton);
        dialog.open();
    }

    /** Remplit la grille avec les résultats et présélectionne la première ligne. */
    private <T> void appliquerResultats(Grid<T> grille, List<T> resultats) {
        grille.setItems(resultats);
        if (!resultats.isEmpty()) {
            grille.select(resultats.get(0));
        }
    }

    /**
     * Indique si un lieu correspond au motif de recherche, en comparant son nom.
     *
     * @see #correspondMotif(String, String)
     */
    private static boolean correspond(Lieu lieu, String motif) {
        return correspondMotif(lieu.nomLieu(), motif);
    }

    /**
     * Indique si un texte correspond au motif de recherche. Le motif est
     * interprété façon « LIKE » SQL : le caractère {@code %} y joue le rôle de
     * joker. Un motif vide ou réduit à {@code %} laisse tout passer. La
     * comparaison est insensible à la casse.
     */
    private static boolean correspondMotif(String texte, String motif) {
        String m = (motif == null) ? "" : motif.trim();
        if (m.isEmpty() || m.equals("%")) {
            return true;
        }
        StringBuilder regex = new StringBuilder();
        for (char c : m.toCharArray()) {
            regex.append(c == '%' ? ".*" : Pattern.quote(String.valueOf(c)));
        }
        return Pattern.compile(regex.toString(), Pattern.CASE_INSENSITIVE)
                .matcher(texte == null ? "" : texte).find();
    }

    /**
     * Liste temporaire de lieux, simulant le résultat d'une requête en base.
     * À remplacer ultérieurement par les données réelles.
     */
    private List<Lieu> lieuxSimules() {
        return List.of(
                new Lieu("Ancón", "ACN", "VILLE", "Panama"),
                new Lieu("Aachen", "AAC", "VILLE", "Allemagne"),
                new Lieu("Aalborg", "ALG", "VILLE", "Danemark"),
                new Lieu("Aarhus", "AAR", "VILLE", "Danemark"),
                new Lieu("Abbotsford", "ABB", "VILLE", "Colombie-Britannique, Canada"),
                new Lieu("Aberdeen", "ABE", "VILLE", "Écosse"),
                new Lieu("Abidjan", "ABJ", "VILLE", "Côte-d'Ivoire"),
                new Lieu("Abingdon", "ABG", "VILLE", "Royaume-Uni"),
                new Lieu("Abo", "ABA", "VILLE", "Finlande"),
                new Lieu("Åbo", "ABO", "VILLE", "Finlande"),
                new Lieu("Abomey-Calavi", "ABU", "VILLE", "Bénin"),
                new Lieu("Acapulco", "APO", "VILLE", "Mexique"),
                new Lieu("Accra", "ACC", "VILLE", "Ghana"),
                new Lieu("Acton Vale", "ACV", "VILLE", "Québec, Canada"));
    }

    /** Lieu présenté dans la fenêtre « Liste des lieux ». */
    public record Lieu(String nomLieu, String code, String type, String paysAttache) {
    }

    // ------------------------------------------------------------------
    // Liste des catégories (lookup catégorie d'organisme pourvoyeur)
    // ------------------------------------------------------------------

    /**
     * Ouvre la fenêtre modale « Liste des catégories d'organismes pourvoyeurs ».
     * Une grille présente les catégories disponibles (description, code). Le
     * champ « Rech. » filtre la liste sur la description (le bouton « Rech. » ou
     * la touche Entrée lancent la recherche). À la validation — bouton « OK » ou
     * double-clic sur une ligne — et si une catégorie est sélectionnée, son code
     * et sa description sont reportés respectivement dans
     * {@link #categorieCodeField} et {@link #categorieLibelleField}, et
     * {@link #siteWwwField} est réinitialisé à « http:// ». « Annuler » referme
     * la fenêtre sans rien modifier.
     *
     * <p>La liste est pour l'instant simulée par {@link #categoriesSimulees()} ;
     * elle sera remplacée ultérieurement par les données issues de la base.</p>
     */
    private void ouvrirListeCategories() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Liste des catégories d'organismes pourvoyeurs");
        dialog.setDraggable(true);
        dialog.setResizable(true);
        dialog.addClassName("lieux-dialog");
        dialog.setWidth("620px");
        dialog.setHeight("540px");

        // --- Champ de recherche (« Rech. ») ---
        TextField rechField = new TextField();
        rechField.setValue("%");
        rechField.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        rechField.setWidthFull();
        Span rechLabel = new Span("Rech.");
        rechLabel.addClassName("lieux-rech-label");
        HorizontalLayout barreRecherche = new HorizontalLayout(rechLabel, rechField);
        barreRecherche.setWidthFull();
        barreRecherche.setAlignItems(Alignment.CENTER);
        barreRecherche.setFlexGrow(1, rechField);

        // --- Grille des catégories ---
        Grid<Categorie> grille = new Grid<>();
        grille.addClassName("lieux-grid");
        grille.setSizeFull();
        grille.setSelectionMode(Grid.SelectionMode.SINGLE);
        grille.addColumn(Categorie::description).setHeader("Description")
                .setFlexGrow(3).setResizable(true);
        grille.addColumn(Categorie::code).setHeader("Catégorie")
                .setWidth("100px").setFlexGrow(0).setResizable(true)
                .setTextAlign(com.vaadin.flow.component.grid.ColumnTextAlign.END);

        List<Categorie> toutesLesCategories = categoriesSimulees();
        appliquerResultats(grille, toutesLesCategories);

        VerticalLayout contenu = new VerticalLayout(barreRecherche, grille);
        contenu.setPadding(false);
        contenu.setSpacing(true);
        contenu.setSizeFull();
        contenu.setFlexGrow(1, grille);
        dialog.add(contenu);

        // --- Recherche : filtre la liste sur la description ---
        Runnable rechercher = () -> {
            String motif = rechField.getValue();
            List<Categorie> resultats = toutesLesCategories.stream()
                    .filter(categorie -> correspondMotif(categorie.description(), motif))
                    .toList();
            appliquerResultats(grille, resultats);
        };
        rechField.addKeyDownListener(Key.ENTER, e -> rechercher.run());

        // --- Validation d'une catégorie : report dans les champs catégorie ---
        Consumer<Categorie> valider = categorie -> {
            if (categorie != null) {
                categorieCodeField.setValue(categorie.code());
                categorieLibelleField.setValue(categorie.description());
                siteWwwField.setValue("http://");
            }
            dialog.close();
        };
        grille.addItemDoubleClickListener(e -> valider.accept(e.getItem()));

        // --- Boutons : Rech. / OK / Annuler ---
        Button rechButton = new Button("Rech.", e -> rechercher.run());
        rechButton.addThemeVariants(ButtonVariant.LUMO_SMALL);

        Button okButton = new Button("OK",
                e -> valider.accept(grille.asSingleSelect().getValue()));
        okButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);

        Button annulerButton = new Button("Annuler", e -> dialog.close());
        annulerButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        annulerButton.addClickShortcut(Key.ESCAPE);

        dialog.getFooter().add(rechButton, okButton, annulerButton);
        dialog.open();
    }

    /**
     * Liste temporaire de catégories d'organismes pourvoyeurs, simulant le
     * résultat d'une requête en base. À remplacer ultérieurement par les données
     * réelles.
     */
    private List<Categorie> categoriesSimulees() {
        return List.of(
                new Categorie("Compagnies", "11"),
                new Categorie("Divers", "40"),
                new Categorie("Établissements de santé", "33"),
                new Categorie("Établissements d'enseignement", "31"),
                new Categorie("Fondations, assoc., sociétés", "12"),
                new Categorie("Gouvernement du Canada", "21"),
                new Categorie("Gouvernement du Québec", "23"),
                new Categorie("Gouvernements autres provinces", "25"),
                new Categorie("Indéterminé", "0"),
                new Categorie("Municipalités", "22"),
                new Categorie("Non canadien: compagnies", "61"),
                new Categorie("Non canadien: divers", "90"),
                new Categorie("Non canadien: établiss. ens.", "81"),
                new Categorie("Non canadien: établiss. santé", "83"),
                new Categorie("Non canadien: fondations, assoc., sociétés", "62"));
    }

    /** Catégorie présentée dans la fenêtre « Liste des catégories ». */
    public record Categorie(String description, String code) {
    }

    // ------------------------------------------------------------------
    // Fabriques utilitaires
    // ------------------------------------------------------------------

    /** FormLayout avec labels à gauche, repassant en 1 colonne sur mobile. */
    private FormLayout formulaireResponsive(int colonnesMax) {
        FormLayout form = new FormLayout();
        form.setWidthFull();
        form.setResponsiveSteps(
                new ResponsiveStep("0", 1, LabelsPosition.TOP),
                new ResponsiveStep("30em", 2, LabelsPosition.ASIDE),
                new ResponsiveStep("60em", colonnesMax, LabelsPosition.ASIDE));
        form.setLabelWidth("110px");
        return form;
    }

    private TextField champTexte(String valeur) {
        TextField champ = new TextField();
        champ.setValue(valeur);
        champ.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        return champ;
    }

    /** Petit bouton « loupe / liste » placé à droite d'un champ code. */
    private Button boutonRecherche() {
        Button bouton = new Button(VaadinIcon.LIST.create());
        bouton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ICON,
                ButtonVariant.LUMO_TERTIARY);
        bouton.addClassName("orpv-lookup-button");
        bouton.setTooltipText("Liste de valeurs");
        return bouton;
    }

    private HorizontalLayout champAvecRecherche(TextField champ, Button bouton) {
        HorizontalLayout ligne = new HorizontalLayout(champ, bouton);
        ligne.setWidthFull();
        ligne.setSpacing(false);
        ligne.setAlignItems(Alignment.CENTER);
        ligne.getStyle().set("gap", "4px");
        ligne.setFlexGrow(1, champ);
        return ligne;
    }

    /** Libellé à gauche + case à cocher à droite, comme sur l'écran d'origine. */
    private HorizontalLayout checkboxAvecLibelle(String libelle, Checkbox checkbox) {
        Span span = new Span(libelle);
        span.addClassName("orpv-checkbox-label");
        HorizontalLayout ligne = new HorizontalLayout(span, checkbox);
        ligne.setAlignItems(Alignment.CENTER);
        ligne.setSpacing(false);
        ligne.getStyle().set("gap", "6px");
        return ligne;
    }

    private Component contenuVide(String message) {
        Span span = new Span(message);
        span.addClassName("orpv-empty-tab");
        Div conteneur = new Div(span);
        conteneur.setWidthFull();
        return conteneur;
    }
}
