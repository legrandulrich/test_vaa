package com.example.views;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.FormItem;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextAreaVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;

import com.example.views.shared.BarreOutils;
import com.example.views.shared.VueFormulaire;

import static com.example.views.shared.Champs.champTexte;
import static com.example.views.shared.Champs.checkboxAvecLibelle;
import static com.example.views.shared.Champs.formulaireResponsive;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

import java.util.Locale;

/**
 * Vue de saisie de l'<b>identité numérique d'un chercheur</b> (personne). Comme
 * {@link Tcmgorg1MainView}, c'est un <b>corps de formulaire</b> ouvert en onglet
 * par {@link HomeView}, doté de la barre d'outils commune
 * ({@link BarreOutils}) et de la base partagée ({@link VueFormulaire}).
 *
 * <p>Le formulaire couvre l'identité civile (civilité, nom, prénom, date de
 * naissance, sexe, nationalité) et l'identité numérique / le compte (numéro
 * d'identité, courriel, mot de passe, langue, options, notes). Il met en œuvre
 * plusieurs <b>types de champs</b> distincts : {@link RadioButtonGroup},
 * {@link TextField}, {@link DatePicker}, {@link ComboBox}, {@link IntegerField},
 * {@link EmailField}, {@link PasswordField}, {@link Checkbox} et {@link TextArea}.</p>
 */
@Route(value = "chercheur", layout = MainLayout.class)
@PageTitle("Identité numérique d'un chercheur")
@SpringComponent
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class TcmgorgChercheurMainView extends VueFormulaire {

    // --- Identité civile ---
    private final RadioButtonGroup<String> civiliteGroup = new RadioButtonGroup<>();
    private TextField nomField;
    private TextField prenomField;
    private final DatePicker naissanceDate = new DatePicker();
    private final ComboBox<String> sexeCombo = new ComboBox<>();
    private final ComboBox<String> nationaliteCombo = new ComboBox<>();

    // --- Identité numérique / compte ---
    private final IntegerField numeroIdentiteField = new IntegerField();
    private final EmailField courrielField = new EmailField();
    private final PasswordField motDePasseField = new PasswordField();
    private final ComboBox<String> langueCombo = new ComboBox<>();
    private final Checkbox compteActifCheckbox = new Checkbox();
    private final Checkbox consentementCheckbox = new Checkbox();

    // --- Notes ---
    private final TextArea notesArea = new TextArea();

    public TcmgorgChercheurMainView() {
        // Corps de formulaire seul (classe orpv-body posée par VueFormulaire).
        add(creerBarreOutils(),
                creerSectionIdentiteCivile(),
                creerSectionIdentiteNumerique(),
                creerLigneOptions(),
                creerNotes());
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        majStatut("Identité numérique d'un chercheur | Saisie");
    }

    /**
     * Barre d'outils commune configurée pour cette vue : « Quitter » ferme
     * l'onglet, « Enregistrer » valide la saisie et « Nouveau » réinitialise le
     * formulaire. Les autres boutons restent présents (homogénéité des écrans).
     */
    private BarreOutils creerBarreOutils() {
        return new BarreOutils(this)
                .surQuitter(this::quitter)
                .surEnregistrer(this::enregistrer)
                .surNouveau(this::reinitialiser);
    }

    /** Identité civile : civilité, nom, prénom, naissance, sexe, nationalité. */
    private FormLayout creerSectionIdentiteCivile() {
        // Civilité — RadioButtonGroup.
        civiliteGroup.setItems("M.", "Mme", "Autre");
        civiliteGroup.setValue("Mme");

        nomField = champTexte("Tremblay");
        nomField.setWidthFull();
        prenomField = champTexte("Marie");
        prenomField.setWidthFull();

        // Date de naissance — DatePicker (format/locale FR).
        naissanceDate.setLocale(Locale.CANADA_FRENCH);
        naissanceDate.setPlaceholder("jj/mm/aaaa");
        naissanceDate.setWidthFull();

        // Sexe — ComboBox.
        sexeCombo.setItems("Féminin", "Masculin", "Autre", "Préfère ne pas répondre");
        sexeCombo.setValue("Féminin");
        sexeCombo.setWidthFull();

        // Nationalité — ComboBox.
        nationaliteCombo.setItems("Canadienne", "Française", "Américaine", "Autre");
        nationaliteCombo.setValue("Canadienne");
        nationaliteCombo.setWidthFull();

        FormLayout form = formulaireResponsive(2);
        FormItem itemCivilite = form.addFormItem(civiliteGroup, "Civilité");
        form.addFormItem(nomField, "Nom");
        form.addFormItem(prenomField, "Prénom");
        form.addFormItem(naissanceDate, "Date de naissance");
        form.addFormItem(sexeCombo, "Sexe");
        form.addFormItem(nationaliteCombo, "Nationalité");
        form.setColspan(itemCivilite, 2);
        return form;
    }

    /** Identité numérique / compte : NI, courriel, mot de passe, langue. */
    private FormLayout creerSectionIdentiteNumerique() {
        // Numéro d'identité — IntegerField.
        numeroIdentiteField.setWidthFull();
        numeroIdentiteField.setMin(0);
        numeroIdentiteField.setStepButtonsVisible(true);

        // Courriel — EmailField.
        courrielField.setWidthFull();
        courrielField.setPlaceholder("prenom.nom@ulaval.ca");
        courrielField.setClearButtonVisible(true);

        // Mot de passe — PasswordField.
        motDePasseField.setWidthFull();

        // Langue — ComboBox.
        langueCombo.setItems("Français", "Anglais", "Espagnol");
        langueCombo.setValue("Français");
        langueCombo.setWidthFull();

        FormLayout form = formulaireResponsive(2);
        form.addFormItem(numeroIdentiteField, "Numéro d'identité");
        FormItem itemCourriel = form.addFormItem(courrielField, "Courriel");
        form.addFormItem(motDePasseField, "Mot de passe");
        form.addFormItem(langueCombo, "Langue");
        form.setColspan(itemCourriel, 2);
        return form;
    }

    /** Ligne d'options à cocher : compte actif et consentement. */
    private HorizontalLayout creerLigneOptions() {
        HorizontalLayout ligne = new HorizontalLayout(
                checkboxAvecLibelle("Compte actif", compteActifCheckbox),
                checkboxAvecLibelle("Consentement aux conditions d'utilisation", consentementCheckbox));
        ligne.addClassName("orpv-options-row");
        ligne.setWidthFull();
        ligne.setAlignItems(Alignment.BASELINE);
        ligne.setJustifyContentMode(JustifyContentMode.AROUND);
        ligne.getStyle().set("gap", "16px");
        compteActifCheckbox.setValue(true);
        return ligne;
    }

    /** Notes libres sur le chercheur — TextArea pleine largeur. */
    private FormLayout creerNotes() {
        notesArea.addThemeVariants(TextAreaVariant.LUMO_SMALL);
        notesArea.setWidthFull();
        notesArea.setHeight("110px");

        FormLayout form = formulaireResponsive(1);
        FormItem item = form.addFormItem(notesArea, "Notes");
        form.setColspan(item, 1);
        return form;
    }

    /**
     * « Enregistrer » : validation minimale (nom, prénom, courriel) puis
     * confirmation. La persistance n'est pas câblée (cf. spec — données simulées).
     */
    private void enregistrer() {
        if (nomField.isEmpty() || prenomField.isEmpty()) {
            Notification.show("Le nom et le prénom sont obligatoires.");
            return;
        }
        if (courrielField.isInvalid()) {
            Notification.show("Le courriel n'est pas valide.");
            return;
        }
        Notification.show("Identité numérique enregistrée pour "
                + prenomField.getValue() + " " + nomField.getValue() + ".");
    }

    /** « Nouveau » : réinitialise le formulaire pour une nouvelle saisie. */
    private void reinitialiser() {
        civiliteGroup.clear();
        nomField.clear();
        prenomField.clear();
        naissanceDate.clear();
        sexeCombo.clear();
        nationaliteCombo.clear();
        numeroIdentiteField.clear();
        courrielField.clear();
        motDePasseField.clear();
        langueCombo.clear();
        compteActifCheckbox.clear();
        consentementCheckbox.clear();
        notesArea.clear();
    }
}
