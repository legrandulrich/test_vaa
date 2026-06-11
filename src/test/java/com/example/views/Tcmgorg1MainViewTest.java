package com.example.views;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.textfield.TextField;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test UI « browserless » (Karibu / Vaadin Browserless Testing) de la vue
 * {@link Tcmgorg1MainView}. Couche 2 de la pyramide : la vue est instanciée et
 * pilotée côté serveur, sans navigateur ni base de données — elle fonctionne
 * aujourd'hui sur ses données simulées.
 *
 * <p>{@code @SpringBootTest} charge le contexte Spring (la vue est un
 * {@code @SpringComponent}) et {@link SpringBrowserlessTest} fournit la session
 * Vaadin, l'{@code UI} et les méthodes {@code navigate} / {@code $} /
 * {@code fireShortcut}.</p>
 */
@SpringBootTest
class Tcmgorg1MainViewTest extends SpringBrowserlessTest {

    @Test
    void la_vue_saffiche_avec_ses_boutons_outils() {
        Tcmgorg1MainView vue = navigate(Tcmgorg1MainView.class);

        assertNotNull(vue);
        assertInstanceOf(Tcmgorg1MainView.class, getCurrentView());
        assertFalse($(Button.class).all().isEmpty(),
                "la barre d'outils doit comporter des boutons");
    }

    @Test
    void le_bouton_liste_du_code_orpv_est_desactive_par_defaut() {
        navigate(Tcmgorg1MainView.class);

        Button lookup = $(Button.class).id("code-orpv-lookup");

        assertFalse(lookup.isEnabled(),
                "hors mode interrogation, la liste de valeurs du code ORPV est désactivée");
    }

    @Test
    void le_mode_interrogation_F7_active_la_liste_du_code_orpv() {
        navigate(Tcmgorg1MainView.class);
        Button lookup = $(Button.class).id("code-orpv-lookup");
        assertFalse(lookup.isEnabled());

        // F7 bascule la barre d'outils en mode interrogation.
        fireShortcut(Key.F7);

        assertTrue(lookup.isEnabled(),
                "le mode interrogation doit activer la liste de valeurs du code ORPV");
    }

    @Test
    void selection_dans_la_liste_des_categories_reporte_code_et_libelle() {
        navigate(Tcmgorg1MainView.class);

        // Au départ, aucune fenêtre n'est ouverte.
        assertTrue($(Dialog.class).all().isEmpty());

        // Clic sur la loupe « catégorie » : la fenêtre de liste s'ouvre.
        test($(Button.class).id("categorie-lookup")).click();
        Dialog dialog = $(Dialog.class).single();
        assertTrue(dialog.isOpened());

        // La première ligne (« Compagnies » / « 11 ») est présélectionnée ;
        // « OK » la valide et referme la fenêtre.
        test($(Button.class).id("categorie-ok")).click();
        assertFalse(dialog.isOpened());

        // Le code et le libellé de la catégorie sont reportés dans les champs,
        // et le site WWW est réinitialisé.
        assertEquals("11", $(TextField.class).id("categorie-code").getValue());
        assertEquals("Compagnies", $(TextField.class).id("categorie-libelle").getValue());
        assertEquals("http://", $(TextField.class).id("site-www").getValue());
    }
}
