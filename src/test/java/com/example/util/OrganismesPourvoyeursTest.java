package com.example.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Test unitaire (couche 1 de la pyramide : logique pure, sans Vaadin) du
 * générateur d'enregistrements fictifs {@link OrganismesPourvoyeurs}.
 */
class OrganismesPourvoyeursTest {

    @Test
    void genere_le_nombre_demande() {
        assertEquals(2000, OrganismesPourvoyeurs.generer(2000).size());
    }

    @Test
    void un_nombre_negatif_donne_une_liste_vide() {
        assertEquals(List.of(), OrganismesPourvoyeurs.generer(-5));
    }

    @Test
    void la_generation_est_deterministe() {
        assertEquals(OrganismesPourvoyeurs.generer(100), OrganismesPourvoyeurs.generer(100));
    }

    @Test
    void le_code_orpv_suit_le_rang() {
        List<OrganismePourvoyeur> liste = OrganismesPourvoyeurs.generer(3);
        assertEquals("10000", liste.get(0).codeOrpv());
        assertEquals("10001", liste.get(1).codeOrpv());
        assertEquals("10002", liste.get(2).codeOrpv());
    }

    @Test
    void tous_les_champs_textuels_sont_non_nuls() {
        for (OrganismePourvoyeur o : OrganismesPourvoyeurs.generer(200)) {
            assertNotNull(o.siru());
            assertNotNull(o.acronyme());
            assertNotNull(o.autreNom());
            assertNotNull(o.villeLibelle());
            assertFalse(o.remarque().isBlank(), "chaque enregistrement a sa propre remarque");
            assertNotNull(o.anneeFinActivite());
            assertFalse(o.nom().isBlank(), "le nom ne doit jamais être vide");
            assertFalse(o.categorieCode().isBlank(), "la catégorie ne doit jamais être vide");

            assertNotNull(o.adressePrincipale());
            assertNotNull(o.adressePrincipale().ligne1());
            assertNotNull(o.adressePrincipale().codePostal());
            assertNotNull(o.adresseSecondaire());
            assertNotNull(o.adresseSecondaire().ligne1());
            assertNotNull(o.adresseSecondaire().codePostal());
        }
    }

    @Test
    void chaque_enregistrement_a_des_lignes_d_accreditation_renseignees() {
        for (OrganismePourvoyeur o : OrganismesPourvoyeurs.generer(200)) {
            assertFalse(o.accreditations().isEmpty(),
                    "chaque enregistrement doit avoir au moins une accréditation");
            for (OrganismePourvoyeur.Accreditation a : o.accreditations()) {
                assertFalse(a.debut().isBlank());
                assertFalse(a.fin().isBlank());
                assertFalse(a.organisme().isBlank());
                assertFalse(a.creation().isBlank());
            }
        }
    }
}
