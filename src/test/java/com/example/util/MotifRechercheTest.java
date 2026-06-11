package com.example.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test unitaire pur (sans Vaadin) du filtre de recherche {@link MotifRecherche}.
 * Couche 1 de la pyramide de tests : logique métier isolée, rapide, sans
 * dépendance à la base de données ni à l'interface.
 */
class MotifRechercheTest {

    @Test
    void motif_vide_null_ou_joker_seul_laisse_tout_passer() {
        assertTrue(MotifRecherche.correspond("Abidjan", ""));
        assertTrue(MotifRecherche.correspond("Abidjan", "   "));
        assertTrue(MotifRecherche.correspond("Abidjan", "%"));
        assertTrue(MotifRecherche.correspond("Abidjan", null));
    }

    @Test
    void comparaison_insensible_a_la_casse() {
        assertTrue(MotifRecherche.correspond("Abidjan", "abi"));
        assertTrue(MotifRecherche.correspond("ABIDJAN", "abi"));
        assertTrue(MotifRecherche.correspond("abidjan", "ABI"));
    }

    @Test
    void le_motif_est_recherche_comme_une_sous_chaine() {
        assertTrue(MotifRecherche.correspond("Abomey-Calavi", "Calavi"));
        assertFalse(MotifRecherche.correspond("Abidjan", "Calavi"));
    }

    @Test
    void le_pourcent_joue_le_role_de_joker() {
        assertTrue(MotifRecherche.correspond("Acton Vale", "Ac%ale"));
        assertTrue(MotifRecherche.correspond("Acton Vale", "%Vale"));
        assertFalse(MotifRecherche.correspond("Abidjan", "Ac%ale"));
    }

    @Test
    void un_texte_null_ne_correspond_pas_a_un_motif_concret() {
        assertFalse(MotifRecherche.correspond(null, "abc"));
    }
}
