# Spécification de migration Oracle Forms → Java / Vaadin

> 🇬🇧 English version: [specification.en.md](specification.en.md) (traduction de courtoisie ;
> **cette version française fait foi**).

> **But de ce document.** Ce fichier est le **gabarit de référence** du projet. Toute
> génération de code — par un développeur ou par une IA à qui l'on fournit la **capture
> d'écran** d'un formulaire Oracle Forms (ORAFORM) — **doit** s'y conformer pour produire
> des vues homogènes avec l'existant ([Tcmgorg1MainView.java](Tcmgorg1MainView.java)), qui sert de modèle de
> référence.
>
> On migre des écrans Oracle Forms (SIRUL, Université Laval) vers Vaadin Flow. On conserve
> **le même thème et la même logique de construction**. Plusieurs personnes travaillent sur
> le projet : la cohérence prime sur l'inventivité.

---

## Sommaire

1. [Stack technique & versions](#1-stack-technique--versions)
2. [Arborescence : où mettre quoi](#2-arborescence--où-mettre-quoi)
3. [Workflow de conversion d'une capture](#3-workflow-de-conversion-dune-capture)
4. [Correspondance Oracle Forms → Vaadin](#4-correspondance-oracle-forms--vaadin)
5. [Squelette d'une nouvelle vue (à copier)](#5-squelette-dune-nouvelle-vue-à-copier)
6. [Conventions de nommage (français)](#6-conventions-de-nommage-français)
7. [Catalogue des helpers réutilisables](#7-catalogue-des-helpers-réutilisables)
8. [Barre d'outils (smartbar)](#8-barre-doutils-smartbar)
9. [Onglets (TabSheet)](#9-onglets-tabsheet)
10. [Grille éditable (bloc multi-enregistrements)](#10-grille-éditable-bloc-multi-enregistrements)
11. [Fenêtres modales (Dialog)](#11-fenêtres-modales-dialog)
12. [Icônes SVG](#12-icônes-svg)
13. [CSS / thème](#13-css--thème)
14. [Données simulées (mock)](#14-données-simulées-mock)
15. [Lecture seule vs désactivé](#15-lecture-seule-vs-désactivé)
16. [Raccourcis clavier](#16-raccourcis-clavier)
17. [Intégration (route, layout, barre d'état, onglet)](#17-intégration-route-layout-barre-détat-onglet)
18. [Build / exécution / vérification](#18-build--exécution--vérification)
19. [Definition of Done (checklist de revue)](#19-definition-of-done-checklist-de-revue)
20. [Règles pour l'IA — à respecter impérativement](#20-règles-pour-lia--à-respecter-impérativement)

---

## 1. Stack technique & versions

| Élément | Valeur |
|---|---|
| Langage | Java **25** (`<java.version>25</java.version>` dans `pom.xml`) |
| Framework UI | **Vaadin 25** (Flow, côté serveur, en Java — **pas** de React/Hilla) |
| Socle | **Spring Boot 4** (`@SpringComponent`, `@Scope` prototype) |
| Build | Maven wrapper (`mvnw` / `mvnw.cmd`), goal par défaut `spring-boot:run`, port **8080** |
| JDK requis | Temurin **JDK 25** — `JAVA_HOME` doit pointer dessus avant `mvnw` (voir §18) |
| Hot reload | **Aucun** (pas de `spring-boot-devtools`) : un changement Java impose un redémarrage |

---

## 2. Arborescence : où mettre quoi

```
src/main/java/com/example/views/
    MainLayout.java        ← layout racine (contenu + barre d'état) — NE PAS modifier sans raison
    StatusBar.java         ← barre d'état partagée
    AppHeader.java         ← en-tête blanc (logo + titre)
    HomeView.java          ← menu d'accueil ; ouvre les vues dans des onglets fermables
    AcceuilView.java / LoginView.java
    Tcmgorg1MainView.java  ← VUE DE RÉFÉRENCE (à imiter)
    specification.fr.md    ← ce document / specification.en.md ← version anglaise
    <NouvelleVue>.java     ← une nouvelle vue = un nouveau fichier ici

src/main/resources/META-INF/resources/
    styles/app.css         ← FEUILLE DE STYLE UNIQUE de toute l'application
    icons/<nom>.svg        ← icônes de la barre d'outils (une par fonction)
```

**Règles d'emplacement :**
- Une vue migrée = **une classe** dans `com.example.views`.
- **Tout le CSS va dans `app.css`** (jamais de `<style>` inline ni de fichier CSS supplémentaire). On y ajoute une **section numérotée** par fonctionnalité (voir §13).
- Les icônes bitmap-rétro vont dans `icons/`, en **SVG**, nommées d'après leur **fonction** (voir §12).

---

## 3. Workflow de conversion d'une capture

À partir d'une image de formulaire Oracle Forms, procéder ainsi :

1. **Inventorier** les éléments visibles : titres, libellés, champs, boutons LOV (loupe/liste),
   combos, cases à cocher, zones de texte multilignes, onglets, blocs tabulaires, barre d'outils.
2. **Mapper** chaque élément vers son équivalent Vaadin via la table du §4.
3. **Réutiliser les helpers existants** (§7) avant d'en écrire de nouveaux. Si un nouveau helper
   est nécessaire, le rendre **générique** et réutilisable (cf. `creerFormulaireAdresse`).
4. **Structurer la vue** comme `Tcmgorg1MainView` : constructeur qui assemble des méthodes `creerXxx()`,
   une section par bloc logique.
5. **Nommer en français** (§6), commentaires en français.
6. **Simuler les données** des listes/grilles via une méthode `xxxSimules()` (§14).
7. **Câbler les boutons LOV** vers une modale `ouvrirListeXxx()` (§11).
8. **Ajouter du CSS uniquement si nécessaire**, dans une nouvelle section d'`app.css`, en
   réutilisant les tokens `--orpv-*` (§13).
9. **Créer les icônes** manquantes en respectant le style (§12).
10. **Compiler et vérifier visuellement** (§18) avant de considérer la tâche terminée (§19).

---

## 4. Correspondance Oracle Forms → Vaadin

| Oracle Forms | Vaadin (via helper de référence) |
|---|---|
| Text Item (saisissable) | `TextField` → `champTexte(valeur)` |
| Display Item / Item non éditable | `TextField` + `setReadOnly(true)` (lisible) ou `setEnabled(false)` (grisé) — voir §15 |
| Item + bouton LOV | `champTexte()` + `boutonRecherche()` + modale `ouvrirListeXxx()` |
| List Item / Poplist / Combo box | `ComboBox<String>` + `setItems(...)` |
| Check Box | `Checkbox` → `checkboxAvecLibelle(libelle, checkbox)` (libellé à gauche) |
| Radio Group | `RadioButtonGroup<>` *(pas encore utilisé ; suivre le même style compact)* |
| Text Item multiligne | `TextArea` (+ `TextAreaVariant.LUMO_SMALL`) |
| Date Item | champ texte pour l'instant ; préférer `DatePicker` (locale FR) si interaction réelle |
| Tab Canvas / Tab Pages | `TabSheet` (classe `orpv-tabs`) |
| Bloc **multi-enregistrements** (tabulaire) | `Grid<>` avec `addComponentColumn(...)` éditable (§10) |
| Bloc **mono-enregistrement** | `FormLayout` → `formulaireResponsive(colonnes)` |
| Push Button | `Button` + `ButtonVariant.LUMO_SMALL` |
| Smartbar / barre d'icônes | conteneur `orpv-toolbar` + `boutonOutil(...)` (§8) |
| Fenêtre modale | `Dialog` (§11) |
| Fenêtre principale | `Div` (corps seul) ouvert en **onglet** par `HomeView` (§17) |
| Ligne d'état (message bas d'écran) | `MainLayout.getStatusBar().setMessage(...)` (§17) |

---

## 5. Squelette d'une nouvelle vue (à copier)

```java
package com.example.views;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;

/**
 * Vue de gestion de <…>. Corps de formulaire seul (ni fenêtre ni barre de titre),
 * destiné à être affiché dans un onglet par {@link HomeView}.
 */
@Route(value = "<segment-url>", layout = MainLayout.class)
@PageTitle("<Titre de la page>")
@StyleSheet("styles/app.css")
@SpringComponent
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class <NomVue> extends Div {

    /** Action de fermeture de l'onglet hôte, injectée par {@link HomeView}. */
    private Runnable fermetureAction;

    public <NomVue>() {
        addClassName("orpv-body");                 // densité + thème compact partagés
        add(creerToolBar(),
            creerFormulaire(),
            creerOnglets());                       // selon le formulaire
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        MainLayout.rechercher(this)
                .ifPresent(layout -> layout.getStatusBar()
                        .setMessage("<Titre> | Prêt"));
    }

    public void setFermetureAction(Runnable fermetureAction) {
        this.fermetureAction = fermetureAction;
    }

    private void quitter() {
        if (fermetureAction != null) {
            fermetureAction.run();
        } else {
            getUI().ifPresent(ui -> ui.navigate(AcceuilView.class));
        }
    }

    // private Component creerToolBar() { … }      // cf. §8 et Tcmgorg1MainView
    // private Component creerFormulaire() { … }    // cf. §7
}
```

> **Important** : une vue est un **corps de formulaire** (`extends Div`), **pas** une fenêtre.
> La fenêtre, le titre et la croix de fermeture sont fournis par `HomeView` (onglet) — voir §17.
> Pour qu'une vue soit ouvrable depuis le menu, ajouter une entrée dans `HomeView` (cf.
> `ouvrirOnglet(TITRE, new <NomVue>())`).

---

## 6. Conventions de nommage (français)

- **Toute** l'UI (libellés, info-bulles, titres) et **tous** les commentaires sont **en français**,
  accents inclus.
- **Méthodes de construction** : `creerXxx()` (renvoient un `Component` / layout).
- **Méthodes d'ouverture de modale** : `ouvrirXxx()`.
- **Fabriques de boutons** : `boutonXxx()`.
- **Champs (attributs)** : suffixe par type — `xxxField` (`TextField`), `xxxButton` (`Button`),
  `xxxCheckbox`, `xxxArea` (`TextArea`), `xxxCombo` (`ComboBox`).
- **Modèles de ligne de grille** : `record` ou classe imbriquée nommée au singulier (`Lieu`,
  `Categorie`, `Accreditation`).
- **Données simulées** : `xxxSimules()` / `xxxSimulees()`.
- **Variables locales** : français (`barre`, `grille`, `contenu`, `ligne`, `rechercher`, `valider`).

---

## 7. Catalogue des helpers réutilisables

Ces helpers existent dans [Tcmgorg1MainView.java](Tcmgorg1MainView.java). **Les réutiliser tels quels** (les
extraire en utilitaire partagé si on les mutualise entre vues). Signatures :

**Champs & libellés**
- `TextField champTexte(String valeur)` — `TextField` + `TextFieldVariant.LUMO_SMALL`.
- `FormLayout formulaireResponsive(int colonnesMax)` — labels à gauche (ASIDE), responsive
  (1 col mobile → 2 → `colonnesMax`), `labelWidth = 110px`. Utiliser `form.setColspan(item, n)`
  pour les champs larges (nom, adresse…).
- `Checkbox` via `HorizontalLayout checkboxAvecLibelle(String libelle, Checkbox checkbox)` —
  libellé (classe `orpv-checkbox-label`) à **gauche** de la case.

**Boutons LOV (liste de valeurs)**
- `Button boutonRecherche()` — petit bouton « liste » (icône `VaadinIcon.LIST`, classe
  `orpv-lookup-button`, info-bulle « Liste de valeurs »).
- `HorizontalLayout champAvecRecherche(TextField champ, Button bouton)` — champ-code + bouton LOV
  collés (gap 4px), le champ prend l'espace.

**Recherche / grille (modales)**
- `<T> void appliquerResultats(Grid<T> grille, List<T> resultats)` — `setItems` + présélection
  de la 1ʳᵉ ligne.
- `boolean correspondMotif(String texte, String motif)` — filtre façon `LIKE` SQL (`%` = joker,
  insensible à la casse, motif vide ou `%` = tout passe).

**Barre d'outils** — voir §8.

> Si un helper manque, **le créer générique** (paramétrer le libellé/les champs) plutôt que de
> dupliquer. Exemple de référence : `creerFormulaireAdresse(libelle, ligne1, ligne2, ligne3,
> codePostal)` factorise les onglets « Adresse principale » et « Adresse secondaire ».

---

## 8. Barre d'outils (smartbar)

Conteneur `Div` de classe `orpv-toolbar` contenant des **boutons-icônes** groupés par fonction,
séparés par `separateur()`. Chaque bouton porte une **info-bulle** décrivant la fonction **et le
raccourci** entre parenthèses.

Helpers de référence :
- `Button boutonOutil(String icone, String infoBulle)` — icône SVG `icons/<icone>.svg`
  (classe `orpv-tool-icon`), bouton tertiaire/icône/small, `tooltip` + `ariaLabel`.
- `Button boutonOutil(String icone, String infoBulle, Key touche, KeyModifier... mods)` — idem +
  `addClickShortcut`.
- `Button boutonOutilAction(String icone, String infoBulle, Runnable action[, Key, KeyModifier...])`
  — idem + action au clic.
- `Button boutonOutilNavigation(String icone, String infoBulle, Key touche)` — raccourci flèche
  avec `.allowBrowserDefault()` (laisse le curseur se déplacer dans les champs).
- `Span separateur()` — séparateur vertical (classe `orpv-toolbar-sep`).

**Bascule de mode.** La barre est reconstruite dynamiquement : conserver la `Div` dans un champ,
exposer `afficherToolBarXxx()` qui fait `barre.removeAll(); barre.add(boutonsXxx());`. Recréer les
boutons à chaque bascule (les raccourcis se ré-enregistrent proprement à l'attachement). Modèle :
le bouton « Interroger » bascule vers une barre d'interrogation ; son bouton « Annuler » rétablit
la barre principale.

---

## 9. Onglets (TabSheet)

`TabSheet` de classe `orpv-tabs`, pleine largeur. Trois patterns de contenu d'onglet déjà établis :

- **Formulaire d'adresse** : libellé + 3 lignes empilées (`VerticalLayout`, gap 6px) + code postal,
  via `creerFormulaireAdresse(...)`.
- **Bloc tabulaire éditable** : `Grid` à colonnes-composants (§10).
- **Texte libre** : `TextArea` pleine largeur, hauteur fixe (ex. `150px`), défilante.

Ne pas laisser d'onglet « vide » : reproduire le contenu de la capture.

---

## 10. Grille éditable (bloc multi-enregistrements)

Pour un bloc Oracle Forms multi-lignes saisissable :

- `Grid<Modele>` de classe dédiée (ex. `accreditation-grid`), largeur pleine, **hauteur fixe** pour
  faire apparaître l'ascenseur.
- **Colonnes-composants** : `grille.addComponentColumn(ligne -> …)` pour rendre chaque cellule
  éditable.
- **Modèle = classe imbriquée mutable** `private static final class Modele { … }`. Comme elle est
  imbriquée dans la vue, ses champs privés sont accessibles depuis les lambdas (pas besoin de
  getters/setters).
- Cellules :
  - éditable : `TextField celluleEditable(String valeur, Consumer<String> maj)` — reporte la
    saisie dans le modèle via un `ValueChangeListener` (persiste au défilement / re-rendu).
  - lecture seule : `TextField celluleLectureSeule(String valeur)` (dates création/modification…).
- Bouton LOV en colonne : `addComponentColumn(ligne -> { Button b = boutonRecherche(); … })`. Pour
  remplir un champ de la ligne depuis la modale, conserver une **référence transitoire** au champ
  sur le modèle (cf. `Accreditation.organismeChamp`) et la lire **dans le listener du clic** (donc
  après rendu).
- Données : `List<Modele> xxxSimules()` avec une ligne renseignée + quelques lignes vierges.

---

## 11. Fenêtres modales (Dialog)

Deux familles, toutes deux `Dialog` `setDraggable(true).setResizable(true)`, titre via
`setHeaderTitle(...)`, dimensions fixes.

**A. Liste de valeurs (LOV)** — modèle `ouvrirListeLieux` / `ouvrirListeCategories` :
- classe `lieux-dialog` ; en haut un champ « Rech » ; au centre un `Grid` (classe `lieux-grid`,
  sélection simple) ; en pied **trois boutons** : `Rech.`, `OK` (classe `orpv-dialog-ok`, vert,
  primaire), `Annuler` (tertiaire, `addClickShortcut(Key.ESCAPE)`).
- Filtrage via `correspondMotif`, remplissage via `appliquerResultats`.
- Validation par **double-clic** sur une ligne **ou** bouton `OK` ; au choix, on reporte les valeurs
  dans les champs cibles puis `dialog.close()`.

**B. Formulaire de saisie** — modèle `ouvrirGestionLieux` :
- titre type « Système de la recherche - <Écran> » ; **croix de fermeture** ajoutée dans l'en-tête
  (`dialog.getHeader().add(boutonFermer)`, icône `VaadinIcon.CLOSE_SMALL`).
- titre de section en gras dans le contenu ; lignes « libellé : champ » via `ligneFormulaire(...)` ;
  groupes de composants côte à côte via `groupeChamps(...)`.

> ### ⚠ Piège shadow DOM (à connaître impérativement)
> L'overlay d'un `Dialog` **n'hérite pas** des variables CSS de `.orpv-body`. Donc :
> - Pour retrouver la **densité compacte**, ajouter une **classe au dialog** (`addClassName`) et
>   **redéclarer les `--lumo-*`** sur cette classe dans `app.css` (cf. `.gestion-lieux-dialog`).
> - Les règles **scopées `.orpv-body …`** ne s'appliquent pas dans la modale.
> - Les **sélecteurs de classe globaux** (`.gestion-lieux-titre`, `.gestion-lieux-label`)
>   s'appliquent au contenu *slotté* ; `::part(content)` fonctionne sur l'overlay. Préférer donc
>   des `Span` à classe + des lignes explicites plutôt que `vaadin-form-item::part(label)`.

---

## 12. Icônes SVG

- Emplacement : `src/main/resources/META-INF/resources/icons/<fonction>.svg`.
- Référencées par `new Image("icons/" + nom + ".svg", infoBulle)` (cf. `boutonOutil`).
- Format : `viewBox="0 0 20 20"`, **20×20**, style **rétro pixelisé** fidèle à Oracle Forms.
- Nommage **par fonction**, en français : `imprimer`, `couper`, `copier`, `coller`, `editer`,
  `executer`, `decompter`, `annuler`, `interroger`, etc.
- Palette de référence (réutiliser) :
  - jaune `#f3c100` / contour `#9c7e00`
  - rouge `#d12b2b` / contour `#8e1818`
  - cyan `#17b6d6`, bleu `#1c4fd6`
- Une icône = une fonction. Ne pas réutiliser une icône pour une action différente.

---

## 13. CSS / thème

- **Fichier unique** `app.css`, importé par `@StyleSheet("styles/app.css")` sur chaque vue.
- Organisé en **sections numérotées** avec un sommaire en tête. **Ajouter une nouvelle section
  numérotée** par fonctionnalité (suivre la numérotation existante).
- **Tokens** dans `:root` (préfixe `--orpv-*`) : surfaces, bordures, texte, actions. **Réutiliser**
  ces variables, ne pas coder les couleurs en dur.
- **Densité** : la classe racine `orpv-body` porte les surcharges `--lumo-*` (taille de police,
  hauteur des champs, rayons, fond blanc des champs bordurés). Toute nouvelle vue ajoute
  `addClassName("orpv-body")`.
- **Scoping** : préfixer par `.orpv-body` quand il faut l'emporter sur la règle `display:block`
  des enfants directs (ex. `.orpv-body .orpv-toolbar { display:flex; }`).
- Classes nommées par fonctionnalité (`orpv-toolbar`, `orpv-tabs`, `lieux-grid`,
  `accreditation-grid`, `gestion-lieux-dialog`…). Pas de style inline sauf `getStyle().set("gap", …)`
  ponctuel pour l'espacement des layouts.

---

## 14. Données simulées (mock)

Les listes/grilles sont alimentées par des méthodes `xxxSimules()` renvoyant un `List.of(...)`,
**toujours commentées** : « *liste temporaire …, simulant le résultat d'une requête en base. À
remplacer ultérieurement par les données réelles.* » Cela isole le point de branchement futur sur
la base de données. Ne pas câbler de persistance tant que ce n'est pas demandé.

---

## 15. Lecture seule vs désactivé

- **`setReadOnly(true)`** : champ **lisible**, non éditable au clavier, **non grisé**, et
  **renseignable par programme** (`setValue` fonctionne). À utiliser pour les **libellés issus d'un
  LOV** (ex. `villeLibelleField`, libellé de catégorie) que l'utilisateur ne tape pas mais qui
  doivent rester lisibles.
- **`setEnabled(false)`** : champ **grisé / désactivé**. À utiliser quand la capture montre un champ
  visuellement inactif (gris), ou quand un état le requiert (ex. bouton LOV activé seulement en mode
  interrogation : `codeOrpvLookupButton.setEnabled(false)` par défaut).

En cas de doute, se fier à l'apparence de la capture : champ blanc lisible → `readOnly` ; champ gris
→ `disabled`.

---

## 16. Raccourcis clavier

- Reproduire les raccourcis Oracle Forms et **les mentionner dans l'info-bulle** entre parenthèses
  (ex. « Imprimer la fenêtre courante (Maj+F8) »).
- `bouton.addClickShortcut(Key.X, KeyModifier.CONTROL)` ; pour les flèches de navigation, ajouter
  `.allowBrowserDefault()`.
- Dans les modales : `OK` → `Key.ENTER` ; `Annuler` → `Key.ESCAPE`.

---

## 17. Intégration (route, layout, barre d'état, onglet)

- **Route** : `@Route(value = "<segment>", layout = MainLayout.class)`. `MainLayout` fournit la zone
  de contenu + la barre d'état persistante.
- **Barre d'état** : dans `onAttach`, `MainLayout.rechercher(this).ifPresent(l ->
  l.getStatusBar().setMessage("<Écran> | Prêt"))`.
- **Ouverture en onglet** : `HomeView` ouvre la vue via `ouvrirOnglet(TITRE, new <NomVue>())`
  (onglet fermable, pas de doublon). Si la vue doit pouvoir fermer son onglet, `HomeView` lui injecte
  `setFermetureAction(Runnable)` ; la vue appelle cette action depuis son bouton « Quitter ».
  → **Pour rendre une nouvelle vue accessible, ajouter l'entrée de menu correspondante dans
  `HomeView`.**

---

## 18. Build / exécution / vérification

```powershell
# JDK 25 obligatoire (la valeur par défaut de la machine ne convient pas)
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot"

.\mvnw.cmd -q compile            # compilation seule (validation rapide)
.\mvnw.cmd compile spring-boot:run   # recompile puis lance (http://localhost:8080)
```

- **Pas de hot reload** : après un changement Java, **arrêter** le serveur, **recompiler**, **relancer**.
- Vérification visuelle : ouvrir `http://localhost:8080/home`, ouvrir l'onglet de la vue, comparer à
  la capture. (Playwright MCP peut automatiser la navigation/capture si disponible.)
- ⚠ L'IDE (serveur de langage Java) peut tourner sur un JDK plus ancien et signaler à tort
  `Stream.toList()` comme indéfini : **faux positif**, la compilation Maven (JDK 25) fait foi.

---

## 19. Definition of Done (checklist de revue)

- [ ] La vue suit le squelette du §5 (`extends Div`, annotations, `onAttach` → barre d'état).
- [ ] `addClassName("orpv-body")` présent.
- [ ] Tous les composants de la capture sont reproduits (libellés, champs, combos, LOV, onglets,
      grilles, barre d'outils).
- [ ] Helpers existants réutilisés ; nouveaux helpers génériques et nommés en français.
- [ ] Libellés/info-bulles/commentaires en **français** ; raccourcis dans les info-bulles.
- [ ] LOV câblés vers une modale `ouvrirListeXxx` ; combos remplis ; lecture seule / désactivé
      conformes à §15.
- [ ] CSS ajouté dans une **nouvelle section numérotée** d'`app.css`, tokens `--orpv-*` réutilisés ;
      pour les modales, densité re-déclarée sur la classe du dialog (§11).
- [ ] Icônes manquantes créées au bon format/palette (§12).
- [ ] Données simulées via `xxxSimules()` avec commentaire « à remplacer ».
- [ ] Entrée de menu ajoutée dans `HomeView` si la vue doit être ouvrable.
- [ ] `mvnw -q compile` **passe** (la compilation Maven, pas seulement l'IDE).
- [ ] Vérification visuelle effectuée.

---

## 20. Règles pour l'IA — à respecter impérativement

**À FAIRE**
- Prendre `Tcmgorg1MainView.java` comme **modèle vivant** : imiter sa structure, ses helpers, son style.
- Réutiliser les helpers (§7) et les patterns (§8–§11) **avant** d'inventer.
- Tout nommer/écrire **en français** (UI, méthodes, variables, commentaires).
- Factoriser le code commun en helpers **génériques** et réutilisables.
- Mettre tout le style dans `app.css`, avec les tokens `--orpv-*`.
- Simuler les données et marquer le point de branchement futur (§14).
- Compiler (`mvnw`) et corriger jusqu'au vert ; signaler honnêtement l'état (compilé / vérifié / non
  testé).

**À NE PAS FAIRE**
- ❌ Ne pas introduire React/Hilla ni un autre framework UI.
- ❌ Ne pas créer de second fichier CSS ni de style inline (hors `gap` ponctuel).
- ❌ Ne pas transformer une vue en fenêtre/`Dialog` : une vue est un **corps** `Div` ouvert en onglet
  (sauf si la capture est explicitement une **fenêtre modale**).
- ❌ Ne pas câbler de persistance/base de données sans demande explicite.
- ❌ Ne pas coder les couleurs en dur : utiliser les tokens.
- ❌ Ne pas se fier aux faux positifs de l'IDE sur `toList()` (§18).
- ❌ Ne pas nommer/commenter en anglais.

**En cas d'ambiguïté de la capture** (type exact d'un widget, comportement d'un bouton, valeurs d'un
combo) : implémenter le rendu visuel fidèle, isoler le comportement derrière un helper/mock, et
**poser la question** plutôt que de deviner un comportement métier.
