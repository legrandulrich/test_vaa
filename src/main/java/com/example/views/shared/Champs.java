package com.example.views.shared;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep.LabelsPosition;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;

import java.util.List;

/**
 * Fabriques de composants communes à toutes les vues de migration (style et
 * densité « ORPV »). Méthodes statiques sans état : à importer statiquement
 * (<code>import static com.example.views.shared.Champs.*;</code>) afin que les
 * vues conservent des appels concis (<code>champTexte(...)</code>,
 * <code>boutonRecherche()</code>, …).
 *
 * <p>Extrait de {@code Tcmgorg1MainView} pour être réutilisé par toute nouvelle
 * vue (cf. {@code TcmgorgChercheurMainView}).</p>
 *
 * <h2>Pourquoi des méthodes {@code static} (et pourquoi c'est sûr)</h2>
 * <ul>
 *   <li><b>Pas de partage entre sessions/UI.</b> La règle d'or en Vaadin est :
 *   <i>ne jamais stocker un {@code Component} dans un champ statique</i> (un
 *   composant appartient à une seule {@code UI}/session ; le partager casse
 *   tout). {@code Champs} ne stocke rien — il fabrique à la demande. ✅</li>
 *   <li><b>Thread-safe par construction</b> : aucun état mutable partagé. ✅</li>
 *   <li><b>Pas de fuite mémoire</b> : les composants retournés sont référencés
 *   par la vue appelante, et libérés avec elle. ✅</li>
 *   <li><b>Spring</b> : c'est volontairement <b>pas</b> un bean — pas de
 *   dépendance à injecter, donc une fabrique statique est exactement l'idiome
 *   attendu (cf. {@link java.util.Collections}). À l'inverse, {@code BarreOutils}
 *   et les vues sont bien des composants / beans prototypes, <b>pas</b>
 *   statiques. ✅</li>
 * </ul>
 *
 * <p><b>Corollaire :</b> ne jamais introduire de champ statique conservant un
 * {@code Component} dans cette classe (cela le partagerait entre toutes les
 * {@code UI}). Les fabriques doivent rester des fonctions pures retournant une
 * <b>nouvelle</b> instance à chaque appel.</p>
 */
public final class Champs {

    private Champs() {
        // Classe utilitaire : pas d'instanciation.
    }

    /** Champ texte compact (variante {@code LUMO_SMALL}) pré-rempli. */
    public static TextField champTexte(String valeur) {
        TextField champ = new TextField();
        champ.setValue(valeur);
        champ.addThemeVariants(TextFieldVariant.LUMO_SMALL);
        return champ;
    }

    /**
     * {@link FormLayout} avec libellés à gauche (ASIDE), repassant en une colonne
     * sur mobile : 1 colonne (&lt;30em) → 2 (≥30em) → {@code colonnesMax} (≥60em).
     */
    public static FormLayout formulaireResponsive(int colonnesMax) {
        FormLayout form = new FormLayout();
        form.setWidthFull();
        form.setResponsiveSteps(
                new ResponsiveStep("0", 1, LabelsPosition.TOP),
                new ResponsiveStep("30em", 2, LabelsPosition.ASIDE),
                new ResponsiveStep("60em", colonnesMax, LabelsPosition.ASIDE));
        form.setLabelWidth("110px");
        return form;
    }

    /** Petit bouton « loupe / liste » (LOV) placé à droite d'un champ code. */
    public static Button boutonRecherche() {
        Button bouton = new Button(VaadinIcon.LIST.create());
        bouton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ICON,
                ButtonVariant.LUMO_TERTIARY);
        bouton.addClassName("orpv-lookup-button");
        bouton.setTooltipText("Liste de valeurs");
        return bouton;
    }

    /** Champ-code + bouton LOV collés (gap 4px), le champ prenant l'espace restant. */
    public static HorizontalLayout champAvecRecherche(TextField champ, Button bouton) {
        HorizontalLayout ligne = new HorizontalLayout(champ, bouton);
        ligne.setWidthFull();
        ligne.setSpacing(false);
        ligne.setAlignItems(Alignment.CENTER);
        ligne.getStyle().set("gap", "4px");
        ligne.setFlexGrow(1, champ);
        return ligne;
    }

    /** Libellé à gauche + case à cocher à droite, comme sur l'écran d'origine. */
    public static HorizontalLayout checkboxAvecLibelle(String libelle, Checkbox checkbox) {
        Span span = new Span(libelle);
        span.addClassName("orpv-checkbox-label");
        HorizontalLayout ligne = new HorizontalLayout(span, checkbox);
        ligne.setAlignItems(Alignment.CENTER);
        ligne.setSpacing(false);
        ligne.getStyle().set("gap", "6px");
        return ligne;
    }

    /** Groupe horizontal compact de composants (champ + bouton + libellé côte à côte). */
    public static HorizontalLayout groupeChamps(Component... composants) {
        HorizontalLayout groupe = new HorizontalLayout(composants);
        groupe.setPadding(false);
        groupe.setSpacing(false);
        groupe.setAlignItems(Alignment.CENTER);
        groupe.getStyle().set("gap", "5px");
        return groupe;
    }

    /**
     * Ligne « libellé : contenu » à colonne de libellé fixe (120px) pour aligner
     * les champs ; {@code etirer} indique si le contenu prend toute la largeur.
     */
    public static HorizontalLayout ligneFormulaire(String libelle, Component contenu, boolean etirer) {
        Span label = new Span(libelle);
        label.addClassName("gestion-lieux-label");
        label.setWidth("120px");
        label.setMinWidth("120px");
        HorizontalLayout ligne = new HorizontalLayout(label, contenu);
        ligne.setWidthFull();
        ligne.setAlignItems(Alignment.CENTER);
        ligne.setSpacing(false);
        ligne.getStyle().set("gap", "8px");
        if (etirer) {
            ligne.setFlexGrow(1, contenu);
        }
        return ligne;
    }

    /** Remplit la grille avec les résultats et présélectionne la première ligne. */
    public static <T> void appliquerResultats(Grid<T> grille, List<T> resultats) {
        grille.setItems(resultats);
        if (!resultats.isEmpty()) {
            grille.select(resultats.get(0));
        }
    }
}
