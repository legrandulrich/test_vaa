package com.example.views;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.FormItem;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep.LabelsPosition;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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

    /** Bloc supérieur : codes, nom, ville. */
    private FormLayout creerFormulaireIdentite() {
        codeOrpvField = champTexte("11767");
        codeOrpvField.setWidthFull();
        codeOrpvLookupButton = boutonRecherche();
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
