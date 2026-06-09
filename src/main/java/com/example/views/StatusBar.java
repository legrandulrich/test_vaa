package com.example.views;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Barre d'état commune affichée en bas de toutes les vues (placée par
 * {@link MainLayout}). Permet d'écrire un message à gauche ; l'heure est
 * affichée à droite et rafraîchie à chaque changement de message.
 */
public class StatusBar extends Div {

    private static final DateTimeFormatter FORMAT_HEURE = DateTimeFormatter.ofPattern("HH:mm");

    private final Span messageLabel = new Span();
    private final Span heureLabel = new Span();

    public StatusBar() {
        addClassName("app-statusbar");
        messageLabel.addClassName("app-statusbar-message");
        heureLabel.addClassName("app-statusbar-info");
        add(messageLabel, heureLabel);
        setMessage("Prêt");
    }

    /** Définit le texte affiché à gauche et rafraîchit l'heure à droite. */
    public void setMessage(String message) {
        messageLabel.setText(message);
        heureLabel.setText(LocalTime.now().format(FORMAT_HEURE));
    }
}
