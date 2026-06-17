package com.example.views;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.FormItem;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep.LabelsPosition;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextAreaVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;

import com.example.util.MotifRecherche;
import com.example.util.OrganismePourvoyeur;
import com.example.util.OrganismesPourvoyeurs;
import com.example.views.shared.BarreOutils;
import com.example.views.shared.VueFormulaire;

import static com.example.views.shared.Champs.appliquerResultats;
import static com.example.views.shared.Champs.boutonRecherche;
import static com.example.views.shared.Champs.champAvecRecherche;
import static com.example.views.shared.Champs.champTexte;
import static com.example.views.shared.Champs.checkboxAvecLibelle;
import static com.example.views.shared.Champs.formulaireResponsive;
import static com.example.views.shared.Champs.groupeChamps;
import static com.example.views.shared.Champs.ligneFormulaire;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Vue de gestion des organismes pourvoyeurs (ORPV).
 *
 * <p>Cette vue est constituée uniquement du corps du formulaire (les sections
 * de saisie) ; elle n'embarque ni fenêtre ni barre de titre. Elle peut donc
 * être affichée telle quelle par {@link HomeView} dans un onglet.</p>
 */
@Route(value = "organismes-pourvoyeurs", layout = MainLayout.class)
@PageTitle("Gestion des organismes pourvoyeurs")
@SpringComponent
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class Tcmgorg1MainView extends VueFormulaire {

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

    // --- Onglet adresse principale ---
    private TextField adressePrincipaleLigne1Field;
    private TextField adressePrincipaleLigne2Field;
    private TextField adressePrincipaleLigne3Field;
    private TextField codePostalField;

    // --- Onglet adresse secondaire ---
    private TextField adresseSecondaireLigne1Field;
    private TextField adresseSecondaireLigne2Field;
    private TextField adresseSecondaireLigne3Field;
    private TextField codePostalSecondaireField;

    // --- Onglet remarque ---
    private TextArea remarqueArea;

    // --- Section bas de formulaire ---
    private TextField categorieCodeField;
    private Button categorieLookupButton;
    private TextField categorieLibelleField;
    private TextField siteWwwField;
    private TextField codeRevenuMeqField;
    private TextField anneeFinActiviteField;
    private TextField moisFermetureField;

    // --- Onglet accréditation ---
    private Grid<Accreditation> accreditationGrid;

    /** Onglet « Adresse secondaire » ; son libellé porte le nombre d'adresses (0 ou 1). */
    private Tab adresseSecondaireTab;

    /** Jeu de données fictives parcouru par les boutons de navigation. */
    private final List<OrganismePourvoyeur> enregistrements =
            OrganismesPourvoyeurs.generer(2000);

    /** Position courante (base 0) dans {@link #enregistrements}. */
    private int indexCourant;

    public Tcmgorg1MainView() {
        // Corps de formulaire seul (la classe orpv-body est posée par VueFormulaire) :
        // on ajoute directement la barre d'outils et les sections de saisie.
        add(creerBarreOutils(),
                creerFormulaireIdentite(),
                creerLigneOptions(),
                creerOnglets(),
                creerFormulaireComplement());

        // Affiche le premier enregistrement fictif dès la construction (la barre
        // d'état n'est mise à jour qu'une fois la vue attachée, cf. onAttach).
        afficherEnregistrement(0);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        majBarreStatut();
    }

    // ------------------------------------------------------------------
    // Barre d'outils
    // ------------------------------------------------------------------

    /**
     * Barre d'outils commune ({@link BarreOutils}) configurée pour cette vue :
     * navigation entre enregistrements et entrée/sortie du mode interrogation
     * (lequel active la liste de valeurs sur le code ORPV et vide les champs).
     * Les fonctions génériques (impression, presse-papier, éditeur) sont portées
     * par le composant lui-même.
     */
    private BarreOutils creerBarreOutils() {
        return new BarreOutils(this)
                .surQuitter(this::quitter)
                .surPremier(this::allerPremier)
                .surPrecedent(this::allerPrecedent)
                .surSuivant(this::allerSuivant)
                .surDernier(this::allerDernier)
                .surInterrogation(() -> {
                    activerLookupCodeOrpv(true);
                    effacerChamps();
                })
                .surFinInterrogation(() -> activerLookupCodeOrpv(false));
    }

    /**
     * Active ou désactive le bouton « liste de valeurs » du code ORPV. La garde
     * anti-{@code null} permet d'appeler la méthode pendant la construction de la
     * barre d'outils, avant que le champ ne soit instancié.
     */
    private void activerLookupCodeOrpv(boolean actif) {
        if (codeOrpvLookupButton != null) {
            codeOrpvLookupButton.setEnabled(actif);
        }
    }

    /**
     * Vide tous les champs du formulaire (champs texte, zone de remarque et cases
     * à cocher) afin de préparer la saisie des critères d'interrogation. Appelée
     * au passage en mode interrogation.
     */
    private void effacerChamps() {
        // Section identification
        codeOrpvField.clear();
        siruField.clear();
        messField.clear();
        acronymeField.clear();
        nomField.clear();
        autreNomField.clear();
        villeCodeField.clear();
        villeLibelleField.clear();

        // Options (cases à cocher)
        payeurFraisIndirectCheckbox.clear();
        comptabilisationFondsInternesCheckbox.clear();
        fondsDotationCheckbox.clear();

        // Onglet adresse principale
        adressePrincipaleLigne1Field.clear();
        adressePrincipaleLigne2Field.clear();
        adressePrincipaleLigne3Field.clear();
        codePostalField.clear();

        // Onglet adresse secondaire
        adresseSecondaireLigne1Field.clear();
        adresseSecondaireLigne2Field.clear();
        adresseSecondaireLigne3Field.clear();
        codePostalSecondaireField.clear();

        // Onglet remarque
        remarqueArea.clear();

        // Onglet accréditation : grille réinitialisée à des lignes vierges
        if (accreditationGrid != null) {
            accreditationGrid.setItems(accreditationsVierges());
        }

        // Section bas de formulaire
        categorieCodeField.clear();
        categorieLibelleField.clear();
        siteWwwField.clear();
        codeRevenuMeqField.clear();
        anneeFinActiviteField.clear();
        moisFermetureField.clear();
    }

    // ------------------------------------------------------------------
    // Navigation entre enregistrements (premier / précédent / suivant / dernier)
    // ------------------------------------------------------------------

    /** Va au premier enregistrement. */
    private void allerPremier() {
        afficherEnregistrement(0);
    }

    /** Va à l'enregistrement précédent (s'arrête au premier). */
    private void allerPrecedent() {
        afficherEnregistrement(indexCourant - 1);
    }

    /** Va à l'enregistrement suivant (s'arrête au dernier). */
    private void allerSuivant() {
        afficherEnregistrement(indexCourant + 1);
    }

    /** Va au dernier enregistrement. */
    private void allerDernier() {
        afficherEnregistrement(enregistrements.size() - 1);
    }

    /**
     * Positionne le curseur sur l'enregistrement {@code index} (borné à la plage
     * valide), reporte ses valeurs dans le formulaire et rafraîchit la barre
     * d'état.
     */
    private void afficherEnregistrement(int index) {
        if (enregistrements.isEmpty()) {
            return;
        }
        indexCourant = Math.max(0, Math.min(index, enregistrements.size() - 1));
        appliquer(enregistrements.get(indexCourant));
        majBarreStatut();
    }

    /** Reporte les valeurs d'un enregistrement dans tous les champs du formulaire. */
    private void appliquer(OrganismePourvoyeur o) {
        // Section identification
        codeOrpvField.setValue(o.codeOrpv());
        siruField.setValue(o.siru());
        messField.setValue(o.mess());
        acronymeField.setValue(o.acronyme());
        nomField.setValue(o.nom());
        autreNomField.setValue(o.autreNom());
        villeCodeField.setValue(o.villeCode());
        villeLibelleField.setValue(o.villeLibelle());

        // Options (cases à cocher)
        payeurFraisIndirectCheckbox.setValue(o.payeurFraisIndirect());
        comptabilisationFondsInternesCheckbox.setValue(o.comptabilisationFondsInternes());
        fondsDotationCheckbox.setValue(o.fondsDotation());

        // Onglet adresse principale
        var principale = o.adressePrincipale();
        adressePrincipaleLigne1Field.setValue(principale.ligne1());
        adressePrincipaleLigne2Field.setValue(principale.ligne2());
        adressePrincipaleLigne3Field.setValue(principale.ligne3());
        codePostalField.setValue(principale.codePostal());

        // Onglet adresse secondaire (le libellé porte le compte : 0 ou 1)
        var secondaire = o.adresseSecondaire();
        adresseSecondaireLigne1Field.setValue(secondaire.ligne1());
        adresseSecondaireLigne2Field.setValue(secondaire.ligne2());
        adresseSecondaireLigne3Field.setValue(secondaire.ligne3());
        codePostalSecondaireField.setValue(secondaire.codePostal());
        adresseSecondaireTab.setLabel(
                "Adresse secondaire (" + (secondaire.ligne1().isBlank() ? 0 : 1) + ")");

        // Onglet accréditation : lignes propres à l'enregistrement
        accreditationGrid.setItems(accreditationsPour(o));

        // Onglet remarque
        remarqueArea.setValue(o.remarque());

        // Section bas de formulaire
        categorieCodeField.setValue(o.categorieCode());
        categorieLibelleField.setValue(o.categorieLibelle());
        siteWwwField.setValue(o.siteWww());
        codeRevenuMeqField.setValue(o.codeRevenuMeq());
        anneeFinActiviteField.setValue(o.anneeFinActivite());
        moisFermetureField.setValue(o.moisFermeture());
    }

    /**
     * Met à jour la barre d'état partagée avec la position courante, p. ex.
     * « Eng. 1/2000 ». Sans effet tant que la vue n'est pas attachée à son
     * {@link MainLayout}.
     */
    private void majBarreStatut() {
        majStatut("Gestion des organismes pourvoyeurs | Eng. "
                + (indexCourant + 1) + "/" + enregistrements.size());
    }

    /** Bloc supérieur : codes, nom, ville. */
    private FormLayout creerFormulaireIdentite() {
        codeOrpvField = champTexte("11767");
        codeOrpvField.setWidthFull();
        codeOrpvLookupButton = boutonRecherche();
        // Identifiant stable pour les tests browserless (mode interrogation).
        codeOrpvLookupButton.setId("code-orpv-lookup");
        // Désactivé par défaut : seul le mode interrogation l'active.
        codeOrpvLookupButton.setEnabled(false);
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
        // Libellé de la ville : renseigné par la liste des lieux, non éditable au clavier.
        villeLibelleField.setReadOnly(true);
        villeLookupButton.addClickListener(e -> ouvrirListeLieux());
        ajoutLieuButton = new Button("Ajout d'un lieu");
        ajoutLieuButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
        ajoutLieuButton.addClickListener(e -> ouvrirGestionLieux());
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
        ligne.addClassName("orpv-options-row");
        ligne.setWidthFull();
        ligne.setAlignItems(Alignment.BASELINE);
        ligne.setJustifyContentMode(JustifyContentMode.AROUND);
        ligne.getStyle().set("gap", "16px");
        return ligne;
    }

    /** Bloc d'onglets : adresses, accréditation, remarque. */
    private TabSheet creerOnglets() {
        TabSheet onglets = new TabSheet();
        onglets.setWidthFull();
        onglets.addClassName("orpv-tabs");

        onglets.add("Adresse principale", creerContenuAdressePrincipale());
        adresseSecondaireTab = onglets.add("Adresse secondaire (0)", creerContenuAdresseSecondaire());
        onglets.add("Accréditation", creerContenuAccreditation());
        onglets.add("Remarque", creerContenuRemarque());
        return onglets;
    }

    /** Contenu de l'onglet « Adresse principale » : trois lignes d'adresse + code postal. */
    private FormLayout creerContenuAdressePrincipale() {
        adressePrincipaleLigne1Field = champTexte("");
        adressePrincipaleLigne2Field = champTexte("");
        adressePrincipaleLigne3Field = champTexte("");
        codePostalField = champTexte("");
        return creerFormulaireAdresse("Adresse principale",
                adressePrincipaleLigne1Field, adressePrincipaleLigne2Field,
                adressePrincipaleLigne3Field, codePostalField);
    }

    /** Contenu de l'onglet « Adresse secondaire » : trois lignes d'adresse + code postal. */
    private FormLayout creerContenuAdresseSecondaire() {
        adresseSecondaireLigne1Field = champTexte("");
        adresseSecondaireLigne2Field = champTexte("");
        adresseSecondaireLigne3Field = champTexte("");
        codePostalSecondaireField = champTexte("");
        return creerFormulaireAdresse("Adresse secondaire",
                adresseSecondaireLigne1Field, adresseSecondaireLigne2Field,
                adresseSecondaireLigne3Field, codePostalSecondaireField);
    }

    /**
     * Construit un formulaire d'adresse : trois lignes de saisie empilées sous
     * le libellé fourni, suivies du code postal.
     */
    private FormLayout creerFormulaireAdresse(String libelleAdresse, TextField ligne1,
            TextField ligne2, TextField ligne3, TextField codePostal) {
        ligne1.setWidthFull();
        ligne2.setWidthFull();
        ligne3.setWidthFull();

        VerticalLayout lignesAdresse = new VerticalLayout(ligne1, ligne2, ligne3);
        lignesAdresse.setPadding(false);
        lignesAdresse.setSpacing(false);
        lignesAdresse.setWidthFull();
        lignesAdresse.getStyle().set("gap", "6px");

        codePostal.setWidth("16em");

        FormLayout form = new FormLayout();
        form.setWidthFull();
        form.setResponsiveSteps(
                new ResponsiveStep("0", 1, LabelsPosition.TOP),
                new ResponsiveStep("30em", 1, LabelsPosition.ASIDE));
        form.setLabelWidth("110px");
        form.addFormItem(lignesAdresse, libelleAdresse);
        form.addFormItem(codePostal, "Code postal");
        return form;
    }

    // ------------------------------------------------------------------
    // Onglet « Accréditation »
    // ------------------------------------------------------------------

    /**
     * Contenu de l'onglet « Accréditation » : grille éditable des accréditations.
     * Chaque ligne couvre une période (début / fin), l'organisme payeur des frais
     * indirects (saisi ou choisi via le bouton liste) et les dates de création et
     * de modification (en lecture seule).
     */
    private Component creerContenuAccreditation() {
        Grid<Accreditation> grille = new Grid<>();
        accreditationGrid = grille;
        grille.addClassName("accreditation-grid");
        grille.setWidthFull();
        grille.setHeight("150px");
        // Les lignes sont renseignées par appliquer(...) selon l'enregistrement courant.

        grille.addComponentColumn(acc -> celluleEditable(acc.debut, v -> acc.debut = v))
                .setHeader("Début").setWidth("90px").setFlexGrow(0);
        grille.addComponentColumn(acc -> celluleEditable(acc.fin, v -> acc.fin = v))
                .setHeader("Fin").setWidth("80px").setFlexGrow(0);
        grille.addComponentColumn(acc -> {
            Button bouton = boutonRecherche();
            bouton.addClickListener(e -> ouvrirListeOrganismesPayeurs(acc));
            return bouton;
        }).setHeader("").setWidth("56px").setFlexGrow(0);
        grille.addComponentColumn(acc -> {
            TextField champ = celluleEditable(acc.organisme, v -> acc.organisme = v);
            acc.organismeChamp = champ;
            return champ;
        }).setHeader("Organisme payeur des frais indirects").setFlexGrow(1);
        grille.addComponentColumn(acc -> celluleLectureSeule(acc.creation))
                .setHeader("Création").setWidth("120px").setFlexGrow(0);
        grille.addComponentColumn(acc -> celluleLectureSeule(acc.modification))
                .setHeader("Modification").setWidth("120px").setFlexGrow(0);
        return grille;
    }

    /** Cellule de grille éditable : un champ texte qui reporte sa valeur dans la ligne. */
    private TextField celluleEditable(String valeur, Consumer<String> maj) {
        TextField champ = champTexte(valeur);
        champ.setWidthFull();
        champ.addValueChangeListener(e -> maj.accept(e.getValue()));
        return champ;
    }

    /** Cellule de grille en lecture seule (dates de création / modification). */
    private TextField celluleLectureSeule(String valeur) {
        TextField champ = champTexte(valeur);
        champ.setWidthFull();
        champ.setReadOnly(true);
        return champ;
    }

    /**
     * Convertit les accréditations de l'enregistrement en lignes éditables de la
     * grille, complétées de lignes vierges (au moins quatre rangées au total) pour
     * laisser de la place à la saisie.
     */
    private List<Accreditation> accreditationsPour(OrganismePourvoyeur o) {
        List<Accreditation> lignes = new ArrayList<>();
        for (var a : o.accreditations()) {
            lignes.add(new Accreditation(a.debut(), a.fin(), a.organisme(),
                    a.creation(), a.modification()));
        }
        while (lignes.size() < 4) {
            lignes.add(new Accreditation("", "", "", "", ""));
        }
        return lignes;
    }

    /**
     * Lignes d'accréditation entièrement vierges, utilisées pour réinitialiser la
     * grille au passage en mode interrogation.
     */
    private List<Accreditation> accreditationsVierges() {
        return List.of(
                new Accreditation("", "", "", "", ""),
                new Accreditation("", "", "", "", ""),
                new Accreditation("", "", "", "", ""));
    }

    /**
     * Ouvre la fenêtre modale « Liste des organismes payeurs des frais indirects ».
     * À la validation — bouton « OK » ou double-clic — le nom de l'organisme
     * sélectionné est reporté dans le champ « Organisme » de la ligne courante.
     */
    private void ouvrirListeOrganismesPayeurs(Accreditation acc) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Liste des organismes payeurs des frais indirects");
        dialog.setDraggable(true);
        dialog.setResizable(true);
        dialog.addClassName("lieux-dialog");
        dialog.setWidth("620px");
        dialog.setHeight("520px");

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

        Grid<String> grille = new Grid<>();
        grille.addClassName("lieux-grid");
        grille.setSizeFull();
        grille.setSelectionMode(Grid.SelectionMode.SINGLE);
        grille.addColumn(nom -> nom).setHeader("Organisme payeur des frais indirects")
                .setFlexGrow(1).setResizable(true);

        List<String> tousLesOrganismes = organismesPayeursSimules();
        appliquerResultats(grille, tousLesOrganismes);

        VerticalLayout contenu = new VerticalLayout(barreRecherche, grille);
        contenu.setPadding(false);
        contenu.setSpacing(true);
        contenu.setSizeFull();
        contenu.setFlexGrow(1, grille);
        dialog.add(contenu);

        Runnable rechercher = () -> {
            String motif = rechField.getValue();
            List<String> resultats = tousLesOrganismes.stream()
                    .filter(nom -> MotifRecherche.correspond(nom, motif))
                    .collect(java.util.stream.Collectors.toList());
            appliquerResultats(grille, resultats);
        };
        rechField.addKeyDownListener(Key.ENTER, e -> rechercher.run());

        Consumer<String> valider = nom -> {
            if (nom != null && acc.organismeChamp != null) {
                acc.organismeChamp.setValue(nom);
            }
            dialog.close();
        };
        grille.addItemDoubleClickListener(e -> valider.accept(e.getItem()));

        Button rechButton = new Button("Rech.", e -> rechercher.run());
        rechButton.addThemeVariants(ButtonVariant.LUMO_SMALL);

        Button okButton = new Button("OK",
                e -> valider.accept(grille.asSingleSelect().getValue()));
        okButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        okButton.addClassName("orpv-dialog-ok");

        Button annulerButton = new Button("Annuler", e -> dialog.close());
        annulerButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        annulerButton.addClickShortcut(Key.ESCAPE);

        dialog.getFooter().add(rechButton, okButton, annulerButton);
        dialog.open();
    }

    /**
     * Liste temporaire d'organismes payeurs, simulant le résultat d'une requête
     * en base. À remplacer ultérieurement par les données réelles.
     */
    private List<String> organismesPayeursSimules() {
        return List.of(
                "A E I Technologies Inc.",
                "Conseil de recherches en sciences naturelles et en génie",
                "Fondation canadienne pour l'innovation",
                "Fonds de recherche du Québec",
                "Instituts de recherche en santé du Canada",
                "Ministère de l'Économie et de l'Innovation",
                "Université Laval");
    }

    /** Ligne d'accréditation éditable présentée dans l'onglet « Accréditation ». */
    private static final class Accreditation {
        private String debut;
        private String fin;
        private String organisme;
        private final String creation;
        private final String modification;
        /** Champ « Organisme » de la ligne, pour report depuis la liste de valeurs. */
        private TextField organismeChamp;

        Accreditation(String debut, String fin, String organisme,
                String creation, String modification) {
            this.debut = debut;
            this.fin = fin;
            this.organisme = organisme;
            this.creation = creation;
            this.modification = modification;
        }
    }

    // ------------------------------------------------------------------
    // Onglet « Remarque »
    // ------------------------------------------------------------------

    /**
     * Contenu de l'onglet « Remarque » : une grande zone de texte libre occupant
     * toute la largeur, défilante lorsque le texte dépasse sa hauteur.
     */
    private Component creerContenuRemarque() {
        remarqueArea = new TextArea();
        remarqueArea.addThemeVariants(TextAreaVariant.LUMO_SMALL);
        remarqueArea.setWidthFull();
        remarqueArea.setHeight("150px");
        return remarqueArea;
    }

    /** Bloc inférieur : catégorie, site web, revenus. */
    private FormLayout creerFormulaireComplement() {
        categorieCodeField = champTexte("61");
        categorieCodeField.setWidth("70px");
        categorieCodeField.setId("categorie-code");
        categorieLookupButton = boutonRecherche();
        categorieLookupButton.setId("categorie-lookup");
        categorieLibelleField = champTexte("Non canadien: compagnies");
        categorieLibelleField.setWidthFull();
        categorieLibelleField.setId("categorie-libelle");
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
        siteWwwField.setId("site-www");

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
        okButton.addClassName("orpv-dialog-ok");
        okButton.addClickShortcut(Key.ENTER);

        Button annulerButton = new Button("Annuler", e -> dialog.close());
        annulerButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        annulerButton.addClickShortcut(Key.ESCAPE);

        dialog.getFooter().add(okButton, annulerButton);
        dialog.open();
    }

    // ------------------------------------------------------------------
    // Gestion des lieux (fenêtre « Ajout d'un lieu »)
    // ------------------------------------------------------------------

    /**
     * Ouvre la fenêtre modale « Système de la recherche - Gestion des lieux » :
     * formulaire de saisie d'un lieu (code, nom, nom abrégé, type de lieu, pays
     * attaché et code de lieu précédent). Le type de lieu est choisi dans une
     * liste déroulante. La fenêtre est déplaçable, redimensionnable et se ferme
     * via la croix de son en-tête.
     */
    private void ouvrirGestionLieux() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Système de la recherche - Gestion des lieux");
        dialog.setDraggable(true);
        dialog.setResizable(true);
        dialog.addClassName("gestion-lieux-dialog");
        dialog.setWidth("760px");
        dialog.setHeight("520px");

        Button fermer = new Button(VaadinIcon.CLOSE_SMALL.create(), e -> dialog.close());
        fermer.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL,
                ButtonVariant.LUMO_ICON);
        fermer.setAriaLabel("Fermer");
        dialog.getHeader().add(fermer);

        Span titre = new Span("Gestion des lieux");
        titre.addClassName("gestion-lieux-titre");

        // Code de lieu : champ court + bouton liste de valeurs.
        TextField codeLieuField = champTexte("");
        codeLieuField.setWidth("80px");
        HorizontalLayout codeLieuGroupe = groupeChamps(codeLieuField, boutonRecherche());

        // Nom : champ pleine largeur.
        TextField nomLieuField = champTexte("");
        nomLieuField.setWidthFull();

        // Nom abrégé.
        TextField nomAbregeField = champTexte("");
        nomAbregeField.setWidth("20em");

        // Type de lieu : liste déroulante.
        ComboBox<String> typeLieuCombo = new ComboBox<>();
        typeLieuCombo.setItems("  ", "Bonjour", "Bonsoir", "Samedi");
        typeLieuCombo.setWidth("20em");

        // Pays attaché : code + bouton + libellé (renseigné par la liste, en lecture seule).
        TextField paysCodeField = champTexte("");
        paysCodeField.setWidth("60px");
        TextField paysLibelleField = champTexte("");
        paysLibelleField.setWidthFull();
        paysLibelleField.setEnabled(false);
        HorizontalLayout paysGroupe =
                groupeChamps(paysCodeField, boutonRecherche(), paysLibelleField);
        paysGroupe.setWidthFull();
        paysGroupe.setFlexGrow(1, paysLibelleField);

        VerticalLayout formulaire = new VerticalLayout(
                ligneFormulaire("Code de lieu :", codeLieuGroupe, false),
                ligneFormulaire("Nom :", nomLieuField, true),
                ligneFormulaire("Nom abrégé :", nomAbregeField, false),
                ligneFormulaire("Type de lieu :", typeLieuCombo, false),
                ligneFormulaire("Pays attaché :", paysGroupe, true));
        formulaire.setPadding(false);
        formulaire.setSpacing(false);
        formulaire.setWidthFull();
        formulaire.getStyle().set("gap", "8px");

        // Code de lieu précédent : ligne autonome, libellé plus large que les autres.
        TextField precCodeField = champTexte("");
        precCodeField.setWidth("60px");
        TextField precCodeLibelleField = champTexte("");
        precCodeLibelleField.setWidth("90px");
        precCodeLibelleField.setEnabled(false);
        TextField precNomField = champTexte("");
        precNomField.setWidthFull();
        precNomField.setEnabled(false);
        Span precLabel = new Span("Code de lieu précédent :");
        precLabel.addClassName("gestion-lieux-label");
        HorizontalLayout precGroupe = groupeChamps(precCodeField, boutonRecherche(),
                precCodeLibelleField, precNomField);
        precGroupe.setWidthFull();
        precGroupe.setFlexGrow(1, precNomField);
        HorizontalLayout precRow = new HorizontalLayout(precLabel, precGroupe);
        precRow.setWidthFull();
        precRow.setAlignItems(Alignment.CENTER);
        precRow.setSpacing(false);
        precRow.getStyle().set("gap", "8px");
        precRow.setFlexGrow(1, precGroupe);
        precRow.addClassName("gestion-lieux-prec-row");

        VerticalLayout contenu = new VerticalLayout(titre, formulaire, precRow);
        contenu.setPadding(true);
        contenu.setSpacing(true);
        contenu.setWidthFull();
        dialog.add(contenu);
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
                    .filter(lieu -> MotifRecherche.correspond(lieu.nomLieu(), motif))
                    .collect(Collectors.toList());
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
        okButton.addClassName("orpv-dialog-ok");

        Button annulerButton = new Button("Annuler", e -> dialog.close());
        annulerButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        annulerButton.addClickShortcut(Key.ESCAPE);

        dialog.getFooter().add(rechButton, okButton, annulerButton);
        dialog.open();
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
                    .filter(categorie -> MotifRecherche.correspond(categorie.description(), motif))
                    .collect(java.util.stream.Collectors.toList());
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
        okButton.addClassName("orpv-dialog-ok");
        okButton.setId("categorie-ok");

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

}
