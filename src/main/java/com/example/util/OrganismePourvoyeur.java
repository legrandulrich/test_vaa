package com.example.util;

import java.util.List;

/**
 * Enregistrement fictif d'un organisme pourvoyeur (ORPV), servant à alimenter le
 * formulaire de la vue de gestion. Toutes les valeurs sont des chaînes non nulles
 * (chaîne vide pour un champ non renseigné), donc directement injectables dans
 * les champs Vaadin via {@code setValue}.
 *
 * <p>Donnée purement applicative, sans dépendance Vaadin : générée en masse par
 * {@link OrganismesPourvoyeurs} et testable par un test unitaire JUnit.</p>
 */
public record OrganismePourvoyeur(
        String codeOrpv,
        String siru,
        String mess,
        String acronyme,
        String nom,
        String autreNom,
        String villeCode,
        String villeLibelle,
        boolean payeurFraisIndirect,
        boolean comptabilisationFondsInternes,
        boolean fondsDotation,
        Adresse adressePrincipale,
        Adresse adresseSecondaire,
        List<Accreditation> accreditations,
        String remarque,
        String categorieCode,
        String categorieLibelle,
        String siteWww,
        String codeRevenuMeq,
        String anneeFinActivite,
        String moisFermeture) {

    /** Adresse postale : trois lignes de saisie et un code postal. */
    public record Adresse(String ligne1, String ligne2, String ligne3, String codePostal) {

        /** Adresse entièrement vierge (aucun champ renseigné). */
        public static Adresse vide() {
            return new Adresse("", "", "", "");
        }
    }

    /**
     * Ligne d'accréditation : période (début / fin), organisme payeur des frais
     * indirects, et dates de création / modification (en lecture seule).
     */
    public record Accreditation(String debut, String fin, String organisme,
            String creation, String modification) {
    }
}
