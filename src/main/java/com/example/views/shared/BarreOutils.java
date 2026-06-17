package com.example.views.shared;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextAreaVariant;
import com.vaadin.flow.component.textfield.TextField;

/**
 * Barre d'outils commune aux vues de migration (façon « smartbar » Oracle Forms) :
 * 14 boutons-icônes regroupés (fichier / édition / navigation / enregistrements /
 * requête), avec bascule entre un mode <b>principal</b> et un mode
 * <b>interrogation</b>.
 *
 * <p>Les fonctions <b>génériques</b> sont intégrées au composant :</p>
 * <ul>
 *   <li><b>Impression</b> ({@code window.print()}) ;</li>
 *   <li><b>Presse-papier</b> (couper / copier / coller) et <b>éditeur</b> : un
 *   script client est installé sur l'élément de la <i>vue hôte</i> ; il suit le
 *   dernier champ de saisie ayant eu le focus et opère dessus.</li>
 * </ul>
 *
 * <p>Les actions <b>propres à la vue</b> (quitter, navigation entre
 * enregistrements, entrée/sortie du mode interrogation, etc.) sont fournies par
 * callbacks via les setters fluides ({@link #surQuitter(Runnable)}…). Toute
 * action non configurée est un no-op (le bouton et son raccourci restent
 * présents, à des fins d'homogénéité visuelle entre écrans).</p>
 */
public final class BarreOutils extends Div {

    /** Vue hôte : son élément reçoit le presse-papier et les opérations d'édition. */
    private final Component hote;

    // --- Actions propres à la vue (no-op par défaut) ---
    private Runnable actionQuitter = () -> { };
    private Runnable actionEnregistrer = () -> { };
    private Runnable actionPremier = () -> { };
    private Runnable actionPrecedent = () -> { };
    private Runnable actionSuivant = () -> { };
    private Runnable actionDernier = () -> { };
    private Runnable actionNouveau = () -> { };
    private Runnable actionDetruire = () -> { };
    private Runnable actionInterrogation = () -> { };
    private Runnable actionFinInterrogation = () -> { };
    private Runnable actionExecuter = () -> { };
    private Runnable actionDecompter = () -> { };

    /**
     * @param hote la vue qui héberge la barre (et ses champs) ; son élément porte
     *             le presse-papier installé à l'attachement.
     */
    public BarreOutils(Component hote) {
        this.hote = hote;
        addClassName("orpv-toolbar");
        afficherPrincipale();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        // Installe (une seule fois) le presse-papier sur l'élément de la vue hôte,
        // ancêtre de tous ses champs : le suivi du focus y est global.
        hote.getElement().executeJs(CLIPBOARD_JS);
    }

    // ------------------------------------------------------------------
    // Configuration (setters fluides)
    // ------------------------------------------------------------------

    public BarreOutils surQuitter(Runnable r) { this.actionQuitter = r; return this; }
    public BarreOutils surEnregistrer(Runnable r) { this.actionEnregistrer = r; return this; }
    public BarreOutils surPremier(Runnable r) { this.actionPremier = r; return this; }
    public BarreOutils surPrecedent(Runnable r) { this.actionPrecedent = r; return this; }
    public BarreOutils surSuivant(Runnable r) { this.actionSuivant = r; return this; }
    public BarreOutils surDernier(Runnable r) { this.actionDernier = r; return this; }
    public BarreOutils surNouveau(Runnable r) { this.actionNouveau = r; return this; }
    public BarreOutils surDetruire(Runnable r) { this.actionDetruire = r; return this; }

    /** Action exécutée à l'<b>entrée</b> en mode interrogation (p. ex. vider les champs). */
    public BarreOutils surInterrogation(Runnable r) { this.actionInterrogation = r; return this; }

    /** Action exécutée à la <b>sortie</b> du mode interrogation (bouton « Annuler »). */
    public BarreOutils surFinInterrogation(Runnable r) { this.actionFinInterrogation = r; return this; }

    public BarreOutils surExecuter(Runnable r) { this.actionExecuter = r; return this; }
    public BarreOutils surDecompter(Runnable r) { this.actionDecompter = r; return this; }

    // ------------------------------------------------------------------
    // Bascule de mode
    // ------------------------------------------------------------------

    /** Rétablit (ou installe) les boutons de la barre principale. */
    private void afficherPrincipale() {
        removeAll();
        add(boutonsPrincipaux());
    }

    /** Passe en mode interrogation puis notifie la vue ({@link #actionInterrogation}). */
    private void entrerInterrogation() {
        removeAll();
        add(boutonsInterrogation());
        actionInterrogation.run();
    }

    /** Quitte le mode interrogation (bouton « Annuler ») et notifie la vue. */
    private void quitterInterrogation() {
        afficherPrincipale();
        actionFinInterrogation.run();
    }

    /**
     * Les 14 boutons de la barre principale, recréés à chaque appel. Les actions
     * configurables sont invoquées par <b>indirection</b> ({@code () -> actionX.run()})
     * pour être lues <b>au moment du clic</b> : les boutons sont créés dès le
     * constructeur, avant que les setters fluides (cf. {@link #surQuitter(Runnable)})
     * n'aient renseigné les champs.
     */
    private Component[] boutonsPrincipaux() {
        return new Component[] {
                // 1 à 3 — fichier
                boutonOutilAction("quitter",
                        "Quitter l'application en cours et revenir à la page précédente ou à la page d'accueil (Ctrl+Q)",
                        () -> actionQuitter.run(), Key.KEY_Q, KeyModifier.CONTROL),
                boutonOutilAction("enregistrer",
                        "Enregistrer les changements effectués à une entente de financement (F10)",
                        () -> actionEnregistrer.run(), Key.F10),
                boutonOutilAction("imprimer",
                        "Imprimer la fenêtre courante (Maj+F8)",
                        this::imprimer, Key.F8, KeyModifier.SHIFT),
                separateur(),
                // 4 à 7 — édition
                boutonCouper(),
                boutonCopier(),
                boutonColler(),
                boutonEditer(),
                separateur(),
                // 8 à 11 — navigation entre enregistrements
                boutonOutilAction("premier",
                        "Aller au premier enregistrement",
                        () -> actionPremier.run()),
                boutonOutilNavigationAction("precedent",
                        "Aller à l'enregistrement précédent (Flèche haut)",
                        Key.ARROW_UP, () -> actionPrecedent.run()),
                boutonOutilNavigationAction("suivant",
                        "Aller à l'enregistrement suivant (Flèche bas)",
                        Key.ARROW_DOWN, () -> actionSuivant.run()),
                boutonOutilAction("dernier",
                        "Aller au dernier enregistrement",
                        () -> actionDernier.run()),
                separateur(),
                // 12 à 13 — enregistrements
                boutonOutilAction("nouveau",
                        "Créer un nouvel enregistrement (F6)",
                        () -> actionNouveau.run(), Key.F6),
                boutonOutilAction("detruire",
                        "Détruire ou effacer l'enregistrement courant identifié par le curseur (Maj+F6)",
                        () -> actionDetruire.run(), Key.F6, KeyModifier.SHIFT),
                separateur(),
                // 14 — interrogation
                boutonOutilAction("interroger",
                        "Lancer le mode d'interrogation (F7)",
                        this::entrerInterrogation, Key.F7)
        };
    }

    /**
     * Les boutons du mode interrogation : impression et édition qui reviennent,
     * puis les actions propres à l'interrogation (exécuter, décompter, annuler).
     */
    private Component[] boutonsInterrogation() {
        return new Component[] {
                boutonOutilAction("imprimer",
                        "Imprimer la fenêtre courante (Maj+F8)",
                        this::imprimer, Key.F8, KeyModifier.SHIFT),
                separateur(),
                boutonCouper(),
                boutonCopier(),
                boutonColler(),
                boutonEditer(),
                separateur(),
                boutonOutilAction("executer",
                        "Exécuter l'interrogation", () -> actionExecuter.run()),
                boutonOutilAction("decompter",
                        "Décompter", () -> actionDecompter.run()),
                boutonOutilAction("annuler",
                        "Annuler l'opération",
                        this::quitterInterrogation)
        };
    }

    // ------------------------------------------------------------------
    // Fabriques de boutons-icônes
    // ------------------------------------------------------------------

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

    /** Bouton-icône avec une action au clic (sans raccourci clavier). */
    private Button boutonOutilAction(String icone, String infoBulle, Runnable action) {
        Button bouton = boutonOutil(icone, infoBulle);
        bouton.addClickListener(event -> action.run());
        return bouton;
    }

    /** Bouton-icône avec une action au clic, en plus du raccourci clavier. */
    private Button boutonOutilAction(String icone, String infoBulle, Runnable action,
            Key touche, KeyModifier... modificateurs) {
        Button bouton = boutonOutilAction(icone, infoBulle, action);
        bouton.addClickShortcut(touche, modificateurs);
        return bouton;
    }

    /**
     * Bouton de navigation (raccourci flèche) doté d'une action au clic. On laisse
     * le navigateur conserver son comportement par défaut (déplacement du curseur
     * dans les champs) en plus du déclenchement du bouton.
     */
    private Button boutonOutilNavigationAction(String icone, String infoBulle, Key touche,
            Runnable action) {
        Button bouton = boutonOutil(icone, infoBulle);
        bouton.addClickShortcut(touche).allowBrowserDefault();
        bouton.addClickListener(event -> action.run());
        return bouton;
    }

    /** Séparateur vertical entre deux groupes de boutons. */
    private Span separateur() {
        Span sep = new Span();
        sep.addClassName("orpv-toolbar-sep");
        return sep;
    }

    /**
     * « Imprimer » : déclenche la boîte de dialogue d'impression du navigateur
     * pour la fenêtre courante.
     */
    private void imprimer() {
        getUI().ifPresent(ui -> ui.getPage().executeJs("window.print()"));
    }

    // ------------------------------------------------------------------
    // Édition : couper / copier / coller / éditer (presse-papier client)
    // ------------------------------------------------------------------

    private Button boutonEdition(String icone, String infoBulle, String fonction, Key touche) {
        Button bouton = boutonOutil(icone, infoBulle);
        bouton.addClickListener(e -> hote.getElement()
                .executeJs("this." + fonction + " && this." + fonction + "()"));
        bouton.addClickShortcut(touche, KeyModifier.CONTROL);
        return bouton;
    }

    private Button boutonCouper() {
        return boutonEdition("couper",
                "Couper le texte sélectionné (Ctrl+X)", "__orpvCut", Key.KEY_X);
    }

    private Button boutonCopier() {
        return boutonEdition("copier",
                "Copier le texte sélectionné (Ctrl+C)", "__orpvCopy", Key.KEY_C);
    }

    private Button boutonColler() {
        return boutonEdition("coller",
                "Coller le texte précédemment coupé ou copié (Ctrl+V)", "__orpvPaste", Key.KEY_V);
    }

    private Button boutonEditer() {
        Button bouton = boutonOutil("editer",
                "Éditer le contenu du champ de la zone de texte où le curseur est positionné (Ctrl+E)");
        bouton.addClickListener(e -> editer());
        bouton.addClickShortcut(Key.KEY_E, KeyModifier.CONTROL);
        return bouton;
    }

    /**
     * « Éditer » : récupère le contenu du dernier champ ayant eu le focus puis
     * ouvre la fenêtre « Éditeur » pré-remplie. Sans champ ciblé ({@code null}),
     * aucune fenêtre n'est ouverte.
     */
    private void editer() {
        hote.getElement().executeJs("return (this.__orpvEditText ? this.__orpvEditText() : null);")
                .then(String.class, texte -> {
                    if (texte != null) {
                        ouvrirEditeur(texte);
                    }
                });
    }

    /**
     * Fenêtre modale « Éditeur » : une grande zone de texte pré-remplie. « OK »
     * reporte le texte dans le champ d'origine, « Annuler » referme et
     * « Rechercher » sélectionne une occurrence dans la zone d'édition.
     */
    private void ouvrirEditeur(String texte) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Éditeur");
        dialog.setDraggable(true);
        dialog.setResizable(true);
        dialog.addClassName("editeur-dialog");
        dialog.setWidth("480px");
        dialog.setHeight("320px");

        TextArea zone = new TextArea();
        zone.setValue(texte);
        zone.setWidthFull();
        zone.setHeight("100%");
        zone.addThemeVariants(TextAreaVariant.LUMO_SMALL);
        zone.focus();

        VerticalLayout contenu = new VerticalLayout(zone);
        contenu.setPadding(false);
        contenu.setSpacing(false);
        contenu.setSizeFull();
        contenu.setFlexGrow(1, zone);
        dialog.add(contenu);

        Button okButton = new Button("OK", e -> {
            hote.getElement().executeJs("this.__orpvSetText && this.__orpvSetText($0)", zone.getValue());
            dialog.close();
        });
        okButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        okButton.addClassName("orpv-dialog-ok");

        Button annulerButton = new Button("Annuler", e -> dialog.close());
        annulerButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        annulerButton.addClickShortcut(Key.ESCAPE);

        Button rechercherButton = new Button("Rechercher", e -> ouvrirRechercheEditeur(zone));
        rechercherButton.addThemeVariants(ButtonVariant.LUMO_SMALL);

        dialog.getFooter().add(okButton, annulerButton, rechercherButton);
        dialog.open();
    }

    /**
     * Sous-fenêtre « Rechercher » de l'éditeur : à la validation, la première
     * occurrence trouvée dans la zone d'édition est sélectionnée ; sinon une
     * notification signale l'échec.
     */
    private void ouvrirRechercheEditeur(TextArea zone) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Rechercher");
        dialog.setDraggable(true);

        TextField terme = new TextField("Texte à rechercher");
        terme.setWidthFull();
        terme.focus();

        VerticalLayout contenu = new VerticalLayout(terme);
        contenu.setPadding(false);
        contenu.setWidth("20em");
        dialog.add(contenu);

        Runnable rechercher = () -> {
            zone.getElement().executeJs(
                    "const i = this.querySelector('textarea')"
                  + "    || (this.shadowRoot ? this.shadowRoot.querySelector('textarea') : null);"
                  + "const t = $0;"
                  + "if (!i || !t) { return false; }"
                  + "const idx = i.value.indexOf(t);"
                  + "if (idx < 0) { return false; }"
                  + "i.focus(); i.setSelectionRange(idx, idx + t.length); return true;",
                    terme.getValue())
                    .then(Boolean.class, trouve -> {
                        if (!Boolean.TRUE.equals(trouve)) {
                            Notification.show("Texte introuvable");
                        }
                    });
            dialog.close();
        };

        Button okButton = new Button("OK", e -> rechercher.run());
        okButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        okButton.addClassName("orpv-dialog-ok");
        okButton.addClickShortcut(Key.ENTER);

        Button annulerButton = new Button("Annuler", e -> dialog.close());
        annulerButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        annulerButton.addClickShortcut(Key.ESCAPE);

        dialog.getFooter().add(okButton, annulerButton);
        dialog.open();
    }

    /**
     * Script client installé une seule fois sur l'élément de la vue hôte. Il suit
     * le dernier champ ({@code vaadin-text-field} / {@code vaadin-text-area}) ayant
     * eu le focus et expose, sur ce même élément, les fonctions de couper / copier /
     * coller / édition. Le presse-papier ({@code __orpvClip}) est conservé côté
     * navigateur : un texte coupé ou copié dans un champ peut être collé dans un
     * autre. Chaque modification est répercutée vers le serveur via les événements
     * {@code input} et {@code change}.
     */
    private static final String CLIPBOARD_JS = """
            const root = this;
            if (root.__orpvInstalled) { return; }
            root.__orpvInstalled = true;

            // L'input est dans le light DOM : l'événement de focus cible l'<input>
            // lui-même, d'où la remontée vers le host du champ via closest().
            const champHote = (el) => (el && el.closest)
                    ? el.closest('vaadin-text-field, vaadin-text-area, vaadin-password-field')
                    : null;

            const interne = (hote) => {
                if (!hote) { return null; }
                // Vaadin 24+/25 place l'input dans le light DOM (slotté), pas dans le shadow.
                let i = hote.querySelector ? hote.querySelector('input, textarea') : null;
                if (i) { return i; }
                if (hote.shadowRoot) {
                    i = hote.shadowRoot.querySelector('input, textarea');
                    if (i) { return i; }
                }
                return (hote.localName === 'input' || hote.localName === 'textarea') ? hote : null;
            };

            root.addEventListener('focusin', (e) => {
                const hote = champHote(e.target);
                if (hote) { root.__orpvLast = hote; }
            }, true);

            // À la perte du focus, on mémorise la sélection : un navigateur peut la
            // replier après le blur, alors qu'on en a besoin lors du clic sur un bouton.
            root.addEventListener('focusout', (e) => {
                const hote = champHote(e.target);
                if (hote) {
                    const i = interne(hote);
                    if (i) {
                        root.__orpvSel = { hote: hote, debut: i.selectionStart, fin: i.selectionEnd };
                    }
                }
            }, true);

            const selection = (hote, i) => {
                let d = i.selectionStart, f = i.selectionEnd;
                if (d === f && root.__orpvSel && root.__orpvSel.hote === hote) {
                    d = root.__orpvSel.debut;
                    f = root.__orpvSel.fin;
                }
                return { debut: Math.min(d, f), fin: Math.max(d, f) };
            };

            const appliquer = (i, valeur, curseur) => {
                i.value = valeur;
                i.setSelectionRange(curseur, curseur);
                i.dispatchEvent(new Event('input', { bubbles: true }));
                i.dispatchEvent(new Event('change', { bubbles: true }));
                i.focus();
            };

            root.__orpvCut = () => {
                const hote = root.__orpvLast, i = interne(hote);
                if (!i || i.readOnly || i.disabled) { return; }
                const s = selection(hote, i);
                if (s.debut === s.fin) { return; }
                root.__orpvClip = i.value.substring(s.debut, s.fin);
                try { if (navigator.clipboard) { navigator.clipboard.writeText(root.__orpvClip); } } catch (e) {}
                appliquer(i, i.value.slice(0, s.debut) + i.value.slice(s.fin), s.debut);
            };

            root.__orpvCopy = () => {
                const hote = root.__orpvLast, i = interne(hote);
                if (!i) { return; }
                const s = selection(hote, i);
                if (s.debut === s.fin) { return; }
                root.__orpvClip = i.value.substring(s.debut, s.fin);
                try { if (navigator.clipboard) { navigator.clipboard.writeText(root.__orpvClip); } } catch (e) {}
                i.focus();
                i.setSelectionRange(s.debut, s.fin);
            };

            root.__orpvPaste = () => {
                const hote = root.__orpvLast, i = interne(hote);
                if (!i || i.readOnly || i.disabled) { return; }
                const clip = root.__orpvClip || '';
                if (!clip) { return; }
                const s = selection(hote, i);
                appliquer(i, i.value.slice(0, s.debut) + clip + i.value.slice(s.fin), s.debut + clip.length);
            };

            // Mémorise le champ cible puis renvoie son contenu pour la fenêtre Éditeur.
            root.__orpvEditText = () => {
                root.__orpvEditTarget = root.__orpvLast;
                const i = interne(root.__orpvEditTarget);
                return i ? i.value : null;
            };

            // Reporte le texte édité dans le champ mémorisé par __orpvEditText.
            root.__orpvSetText = (texte) => {
                const i = interne(root.__orpvEditTarget);
                if (!i || i.readOnly || i.disabled) { return; }
                const valeur = texte == null ? '' : texte;
                appliquer(i, valeur, valeur.length);
            };
            """;
}
