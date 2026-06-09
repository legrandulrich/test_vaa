package com.example.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLayout;

import java.util.Optional;

/**
 * Mise en page racine commune à toutes les vues : une zone de contenu (où
 * s'affichent les vues routées) surmontant une barre d'état partagée.
 *
 * <p>L'instance est conservée d'une navigation à l'autre, de sorte que la
 * barre d'état persiste ; chaque vue met à jour son message à l'attachement.</p>
 */
@StyleSheet("styles/app.css")
public class MainLayout extends VerticalLayout implements RouterLayout {

    private final Div contenu = new Div();
    private final StatusBar statusBar = new StatusBar();

    public MainLayout() {
        addClassName("orpv-view");
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        contenu.addClassName("app-content");

        add(contenu, statusBar);
        setFlexGrow(1, contenu);
    }

    @Override
    public void showRouterLayoutContent(HasElement content) {
        contenu.getElement().appendChild(content.getElement());
    }

    @Override
    public void removeRouterLayoutContent(HasElement oldContent) {
        contenu.getElement().removeAllChildren();
    }

    /** Barre d'état partagée, que les vues mettent à jour. */
    public StatusBar getStatusBar() {
        return statusBar;
    }

    /**
     * Recherche le {@link MainLayout} ancêtre d'un composant, pour permettre
     * à une vue de mettre à jour la barre d'état (à appeler une fois attachée).
     */
    static Optional<MainLayout> rechercher(Component composant) {
        Component courant = composant;
        while (courant != null) {
            if (courant instanceof MainLayout) {
                return Optional.of((MainLayout) courant);
            }
            courant = courant.getParent().orElse(null);
        }
        return Optional.empty();
    }
}
