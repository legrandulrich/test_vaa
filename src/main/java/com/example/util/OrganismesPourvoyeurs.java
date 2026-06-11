package com.example.util;

import com.example.util.OrganismePourvoyeur.Accreditation;
import com.example.util.OrganismePourvoyeur.Adresse;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Fabrique d'enregistrements fictifs d'organismes pourvoyeurs, destinée à
 * alimenter le formulaire de la vue de gestion et à démontrer la navigation
 * (premier / précédent / suivant / dernier).
 *
 * <p>La génération est <strong>déterministe</strong> : chaque enregistrement est
 * tiré d'un {@link Random} ensemencé par son index, de sorte que
 * {@code generer(n)} renvoie toujours exactement la même liste. Logique purement
 * applicative (sans dépendance Vaadin), directement testable par un test unitaire
 * JUnit.</p>
 */
public final class OrganismesPourvoyeurs {

    private OrganismesPourvoyeurs() {
    }

    private static final String[] PREFIXES = {
            "Groupe", "Société", "Institut", "Fondation", "Laboratoires",
            "Centre", "Compagnie", "Agence", "Coopérative", "Consortium"
    };

    private static final String[] NOYAUX = {
            "Bélanger", "Nordique", "Atlantique", "Boréal", "Horizon",
            "Saint-Laurent", "Cartier", "Champlain", "Phénix", "Polaris",
            "Méridien", "Lavoie", "Tremblay", "Gauthier", "Optimum",
            "Synergie", "Lumina", "Vertex"
    };

    private static final String[] SUFFIXES = {
            "Inc.", "Ltée", "S.A.", "International", "Canada",
            "Québec", "Group", "Corp.", "Enr.", "et Associés"
    };

    /** Couples {@code {code, libellé}} de villes. */
    private static final String[][] VILLES = {
            {"MTL", "Montréal  Québec, Canada"},
            {"QUE", "Québec  Québec, Canada"},
            {"TOR", "Toronto  Ontario, Canada"},
            {"OTT", "Ottawa  Ontario, Canada"},
            {"VAN", "Vancouver  Colombie-Britannique, Canada"},
            {"SHE", "Sherbrooke  Québec, Canada"},
            {"PIT", "Pittsburgh  Pennsylvania, États-Unis"},
            {"NYC", "New York  New York, États-Unis"},
            {"BOS", "Boston  Massachusetts, États-Unis"},
            {"PAR", "Paris  Île-de-France, France"},
            {"LYO", "Lyon  Auvergne-Rhône-Alpes, France"},
            {"BRU", "Bruxelles  Belgique"},
            {"GEN", "Genève  Suisse"},
            {"LON", "Londres  Royaume-Uni"}
    };

    /** Couples {@code {code, libellé}} de catégories (cf. liste des catégories). */
    private static final String[][] CATEGORIES = {
            {"61", "Non canadien: compagnies"},
            {"11", "Compagnies"},
            {"31", "Établissements d'enseignement"},
            {"33", "Établissements de santé"},
            {"12", "Fondations, assoc., sociétés"},
            {"21", "Gouvernement du Canada"},
            {"23", "Gouvernement du Québec"},
            {"22", "Municipalités"},
            {"40", "Divers"},
            {"62", "Non canadien: fondations, assoc., sociétés"}
    };

    private static final String[] RUES = {
            "rue Sherbrooke", "boulevard René-Lévesque", "avenue du Parc",
            "rue Saint-Denis", "chemin Sainte-Foy", "rue University",
            "boulevard Charest", "avenue des Pins", "rue Notre-Dame",
            "chemin de la Côte-des-Neiges"
    };

    /** Organismes payeurs des frais indirects (lignes d'accréditation). */
    private static final String[] ORGANISMES_PAYEURS = {
            "Conseil de recherches en sciences naturelles et en génie",
            "Fondation canadienne pour l'innovation",
            "Fonds de recherche du Québec",
            "Instituts de recherche en santé du Canada",
            "Ministère de l'Économie et de l'Innovation",
            "Université Laval"
    };

    private static final String[] REMARQUES = {
            "Dossier à valider par le responsable.",
            "Coordonnées mises à jour récemment.",
            "Organisme partenaire de longue date.",
            "Vérifier le numéro SIRU avant transmission.",
            "Financement conditionnel à l'accréditation."
    };

    private static final String[] TLDS = {"com", "ca", "org", "net"};

    /** Lettres autorisées dans un code postal canadien. */
    private static final String LETTRES_POSTALES = "ABCEGHJKLMNPRSTVXY";

    /**
     * Génère {@code nombre} enregistrements fictifs déterministes.
     *
     * @param nombre nombre d'enregistrements souhaité (négatif traité comme 0)
     * @return liste immuable d'enregistrements
     */
    public static List<OrganismePourvoyeur> generer(int nombre) {
        List<OrganismePourvoyeur> liste = new ArrayList<>(Math.max(0, nombre));
        for (int index = 0; index < nombre; index++) {
            liste.add(creer(index));
        }
        return List.copyOf(liste);
    }

    /** Construit l'enregistrement de rang {@code index} (déterministe via la graine). */
    private static OrganismePourvoyeur creer(int index) {
        Random alea = new Random(index);

        String prefixe = choisir(alea, PREFIXES);
        String noyau = choisir(alea, NOYAUX);
        String suffixe = choisir(alea, SUFFIXES);
        String nom = prefixe + " " + noyau + " " + suffixe;
        String acronyme = (prefixe.charAt(0) + noyau.substring(0, 2)).toUpperCase(Locale.ROOT);

        String[] ville = VILLES[alea.nextInt(VILLES.length)];
        String[] categorie = CATEGORIES[alea.nextInt(CATEGORIES.length)];

        String autreNom = alea.nextInt(5) == 0 ? "Anciennement " + choisir(alea, NOYAUX) : "";
        String siteWww = "http://www." + slug(noyau) + "." + choisir(alea, TLDS) + "/";

        Adresse adressePrincipale = new Adresse(
                noCivique(alea) + ", " + choisir(alea, RUES),
                alea.nextInt(3) == 0 ? "Bureau " + (100 + alea.nextInt(900)) : "",
                "",
                codePostal(alea));

        // La plupart des enregistrements ont une adresse secondaire complète ;
        // une minorité n'en a pas (l'onglet affiche alors « (0) »).
        Adresse adresseSecondaire = alea.nextInt(5) == 0
                ? Adresse.vide()
                : new Adresse(
                        noCivique(alea) + ", " + choisir(alea, RUES),
                        alea.nextInt(3) == 0 ? "Bureau " + (100 + alea.nextInt(900)) : "",
                        "",
                        codePostal(alea));

        return new OrganismePourvoyeur(
                String.valueOf(10000 + index),
                cinqChiffres(alea),
                cinqChiffres(alea),
                acronyme,
                nom,
                autreNom,
                ville[0],
                ville[1],
                alea.nextBoolean(),
                alea.nextBoolean(),
                alea.nextBoolean(),
                adressePrincipale,
                adresseSecondaire,
                accreditations(alea),
                remarque(alea),
                categorie[0],
                categorie[1],
                siteWww,
                String.valueOf(400 + alea.nextInt(100)),
                alea.nextInt(6) == 0 ? String.valueOf(2018 + alea.nextInt(8)) : "",
                String.format(Locale.ROOT, "%02d", 1 + alea.nextInt(12)));
    }

    private static String choisir(Random alea, String[] valeurs) {
        return valeurs[alea.nextInt(valeurs.length)];
    }

    /** Une à trois lignes d'accréditation renseignées (périodes et organismes payeurs). */
    private static List<Accreditation> accreditations(Random alea) {
        int nbLignes = 1 + alea.nextInt(3);
        List<Accreditation> lignes = new ArrayList<>(nbLignes);
        for (int i = 0; i < nbLignes; i++) {
            int debut = 2008 + alea.nextInt(15);
            int fin = debut + 1 + alea.nextInt(6);
            String date = dateIso(alea);
            lignes.add(new Accreditation(
                    String.valueOf(debut),
                    String.valueOf(fin),
                    choisir(alea, ORGANISMES_PAYEURS),
                    date,
                    date));
        }
        return List.copyOf(lignes);
    }

    /** Remarque propre à chaque enregistrement : une à deux lignes. */
    private static String remarque(Random alea) {
        String premiere = choisir(alea, REMARQUES);
        return alea.nextBoolean() ? premiere + "\n" + choisir(alea, REMARQUES) : premiere;
    }

    /** Date au format ISO {@code AAAA-MM-JJ} entre 2015 et 2025. */
    private static String dateIso(Random alea) {
        return String.format(Locale.ROOT, "%04d-%02d-%02d",
                2015 + alea.nextInt(11), 1 + alea.nextInt(12), 1 + alea.nextInt(28));
    }

    private static String noCivique(Random alea) {
        return String.valueOf(100 + alea.nextInt(9900));
    }

    private static String cinqChiffres(Random alea) {
        return String.format(Locale.ROOT, "%05d", alea.nextInt(100000));
    }

    /** Code postal canadien du type {@code H3A 1B2}. */
    private static String codePostal(Random alea) {
        return "" + lettrePostale(alea) + alea.nextInt(10) + lettrePostale(alea)
                + " " + alea.nextInt(10) + lettrePostale(alea) + alea.nextInt(10);
    }

    private static char lettrePostale(Random alea) {
        return LETTRES_POSTALES.charAt(alea.nextInt(LETTRES_POSTALES.length()));
    }

    /** Réduit un libellé à des lettres/chiffres minuscules sans accent (pour une URL). */
    private static String slug(String texte) {
        String sansAccent = Normalizer.normalize(texte, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sansAccent.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }
}
