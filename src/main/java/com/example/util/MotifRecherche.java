package com.example.util;

import java.util.regex.Pattern;

/**
 * Filtre de recherche façon « LIKE » SQL : le caractère {@code %} y joue le rôle
 * de joker. Un motif vide, {@code null} ou réduit à {@code %} laisse tout passer.
 * La comparaison est insensible à la casse et le motif est recherché n'importe où
 * dans le texte (sous-chaîne).
 *
 * <p>Logique métier pure (sans dépendance Vaadin), extraite des vues pour être
 * testable directement par un test unitaire JUnit.</p>
 */
public final class MotifRecherche {

    private MotifRecherche() {
    }

    /**
     * Indique si {@code texte} correspond au motif de recherche {@code motif}.
     *
     * @param texte le texte à confronter (peut être {@code null})
     * @param motif le motif de recherche, joker {@code %} compris (peut être {@code null})
     * @return {@code true} si le texte correspond au motif
     */
    public static boolean correspond(String texte, String motif) {
        String m = (motif == null) ? "" : motif.trim();
        if (m.isEmpty() || m.equals("%")) {
            return true;
        }
        StringBuilder regex = new StringBuilder();
        for (char c : m.toCharArray()) {
            regex.append(c == '%' ? ".*" : Pattern.quote(String.valueOf(c)));
        }
        return Pattern.compile(regex.toString(), Pattern.CASE_INSENSITIVE)
                .matcher(texte == null ? "" : texte).find();
    }
}
