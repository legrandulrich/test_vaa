package com.example.views.shared;

import com.example.views.AcceuilView;
import com.vaadin.flow.component.html.Div;

/**
 * Base commune (abstraite) des vues de migration ouvertes en onglet par
 * {@code HomeView} : un <b>corps de formulaire</b> ({@code extends Div}, classe
 * {@code orpv-body}), sans fenêtre ni barre de titre. {@code Tcmgorg1MainView} et
 * {@code TcmgorgChercheurMainView} en héritent au lieu d'{@code extends Div}.
 *
 * <h2>Ce qu'elle apporte (et que les vues n'ont donc plus à répéter)</h2>
 * <ol>
 *   <li><b>{@code extends Div} + {@code addClassName("orpv-body")}</b> — une vue
 *   est un <i>corps de formulaire</i> (pas une fenêtre) ; la classe
 *   {@code orpv-body} porte la densité compacte et le thème partagés. Posé une
 *   fois pour toutes par le constructeur.</li>
 *   <li><b>{@link #setFermetureAction(Runnable)}</b> — point d'injection : à
 *   l'ouverture d'un onglet, {@code HomeView} y dépose l'action qui ferme
 *   <i>cet</i> onglet (cf. {@code HomeView.ouvrirOnglet}, qui traite uniformément
 *   toute {@code VueFormulaire}).</li>
 *   <li><b>{@link #quitter()}</b> — comportement du bouton « Quitter » : ferme
 *   l'onglet hôte si une action de fermeture est définie, sinon (vue ouverte
 *   seule via sa route) revient à {@code AcceuilView}. Chaque vue le branche sur
 *   sa barre d'outils via {@code surQuitter(this::quitter)}.</li>
 *   <li><b>{@link #majStatut(String)}</b> — met à jour la barre d'état partagée
 *   ({@code MainLayout.rechercher(this) → getStatusBar().setMessage(...)}). Chaque
 *   vue l'appelle dans son {@code onAttach}.</li>
 * </ol>
 *
 * <h2>Pourquoi ce design</h2>
 * <ul>
 *   <li><b>Anti-duplication</b> : ces quatre éléments étaient identiques d'une vue
 *   à l'autre ; ils vivent désormais à un seul endroit.</li>
 *   <li><b>Polymorphisme utile</b> : {@code HomeView} manipule n'importe quelle
 *   {@code VueFormulaire} de façon uniforme ({@code instanceof VueFormulaire}),
 *   sans connaître la vue concrète — toute future vue qui en hérite est donc
 *   automatiquement « fermable » et intégrée.</li>
 *   <li><b>{@code abstract}</b> : jamais instanciée directement ; elle n'existe
 *   que comme socle des vues concrètes.</li>
 *   <li><b>Séparation des responsabilités</b> : {@code VueFormulaire} = cycle de
 *   vie de la vue-onglet (cadre) ; {@code BarreOutils} = la barre d'outils ;
 *   {@code Champs} = les fabriques ; la vue concrète = uniquement sa logique
 *   métier (champs, lookups, navigation).</li>
 * </ul>
 */
public abstract class VueFormulaire extends Div {

    /** Action de fermeture de l'onglet hôte, injectée par {@code HomeView}. */
    private Runnable fermetureAction;

    protected VueFormulaire() {
        addClassName("orpv-body");
    }

    /**
     * Définit l'action de « Quitter » : la fermeture de l'onglet hôte. Injectée
     * par {@code HomeView} à l'ouverture de l'onglet.
     */
    public void setFermetureAction(Runnable fermetureAction) {
        this.fermetureAction = fermetureAction;
    }

    /**
     * « Quitter » : ferme l'onglet hôte s'il existe, sinon (vue ouverte seule via
     * sa route) revient à l'écran d'accueil.
     */
    protected void quitter() {
        if (fermetureAction != null) {
            fermetureAction.run();
        } else {
            getUI().ifPresent(ui -> ui.navigate(AcceuilView.class));
        }
    }

    /**
     * Met à jour la barre d'état partagée. Sans effet tant que la vue n'est pas
     * attachée à son {@link MainLayout}.
     */
    protected void majStatut(String message) {
        MainLayout.rechercher(this)
                .ifPresent(layout -> layout.getStatusBar().setMessage(message));
    }
}
