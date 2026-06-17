# Oracle Forms → Java / Vaadin Migration Specification

> **Language note.** The French version, [specification.fr.md](specification.fr.md), is the
> **authoritative reference**. This English translation is provided as a courtesy for
> team members who prefer English; if the two ever diverge, the French version wins.
> Note that **all generated code stays in French** (labels, method names, variables,
> comments) — see §6.

> **Purpose of this document.** This file is the project's **reference template**. Any code
> generation — by a developer or by an AI given a **screenshot** of an Oracle Forms (ORAFORM)
> screen — **must** follow it to produce views consistent with the existing
> [Tcmgorg1MainView.java](Tcmgorg1MainView.java), which is the reference model.
>
> We are migrating Oracle Forms screens (SIRUL, Université Laval) to Vaadin Flow. We keep
> **the same theme and the same construction logic**. Several people work on this project:
> consistency matters more than inventiveness.

---

## Table of contents

1. [Tech stack & versions](#1-tech-stack--versions)
2. [Project layout: where things go](#2-project-layout-where-things-go)
3. [Screenshot conversion workflow](#3-screenshot-conversion-workflow)
4. [Oracle Forms → Vaadin mapping](#4-oracle-forms--vaadin-mapping)
5. [New-view skeleton (copy this)](#5-new-view-skeleton-copy-this)
6. [Naming conventions (French)](#6-naming-conventions-french)
7. [Catalog of reusable helpers](#7-catalog-of-reusable-helpers)
8. [Toolbar (smartbar)](#8-toolbar-smartbar)
9. [Tabs (TabSheet)](#9-tabs-tabsheet)
10. [Editable grid (multi-record block)](#10-editable-grid-multi-record-block)
11. [Modal windows (Dialog)](#11-modal-windows-dialog)
12. [SVG icons](#12-svg-icons)
13. [CSS, theme and theme system](#13-css-theme-and-theme-system)
14. [Simulated data (mock)](#14-simulated-data-mock)
15. [Read-only vs disabled](#15-read-only-vs-disabled)
16. [Keyboard shortcuts](#16-keyboard-shortcuts)
17. [Integration (route, layout, status bar, tab)](#17-integration-route-layout-status-bar-tab)
18. [Build / run / verify](#18-build--run--verify)
19. [Definition of Done (review checklist)](#19-definition-of-done-review-checklist)
20. [Rules for the AI — must follow](#20-rules-for-the-ai--must-follow)

---

## 1. Tech stack & versions

| Item | Value |
|---|---|
| Language | Java **25** (`<java.version>25</java.version>` in `pom.xml`) |
| UI framework | **Vaadin 25** (Flow, server-side, in Java — **no** React/Hilla) |
| Platform | **Spring Boot 4** (`@SpringComponent`, prototype `@Scope`) |
| Build | Maven wrapper (`mvnw` / `mvnw.cmd`), default goal `spring-boot:run`, port **8080** |
| Required JDK | Temurin **JDK 25** — `JAVA_HOME` must point to it before `mvnw` (see §18) |
| Hot reload | **None** (no `spring-boot-devtools`): a Java change requires a restart |

---

## 2. Project layout: where things go

```
src/main/java/com/example/
    Application.java           ← Spring Boot entry point + global @CssImport (app.css + themes)
    views/
        MainLayout.java        ← root layout (content + status bar) — DO NOT change without reason
        StatusBar.java         ← shared status bar
        AppHeader.java         ← white header (logo + title)
        HomeView.java          ← home menu; opens views as tabs; Help▸Theme menu
        AcceuilView.java / LoginView.java
        Tcmgorg1MainView.java  ← REFERENCE VIEW (to imitate)
        specification.fr.md    ← French reference / specification.en.md ← this file
        <NewView>.java         ← a migrated view = a new file here

src/main/frontend/styles/          ← CSS bundled by the build (Vite), via @CssImport
    app.css                    ← THE SINGLE STYLESHEET: structure + --orpv-* tokens + default theme
    themes/<name>.css          ← one theme = one attribute variant `html[theme~="<name>"]` (see §13)

src/main/resources/META-INF/resources/
    icons/<name>.svg           ← toolbar icons (static resources, served by URL)
```

**Placement rules:**
- One migrated view = **one class** in `com.example.views`.
- **All structural CSS lives in `app.css`** (never inline `<style>`, never an ad-hoc file); add a
  **numbered section** per feature (see §13). **Palettes** go in `styles/themes/<name>.css`, one per theme.
- `app.css` and themes are **bundled** (not served by URL): declared via `@CssImport` on `Application`
  (see §13). **Icons** stay in `META-INF/resources/icons/` because they are loaded **by URL**
  (`new Image("icons/…")`).

---

## 3. Screenshot conversion workflow

Given an Oracle Forms screenshot, proceed as follows:

1. **Inventory** the visible elements: titles, labels, fields, LOV buttons (magnifier/list),
   combos, checkboxes, multi-line text areas, tabs, tabular blocks, toolbar.
2. **Map** each element to its Vaadin equivalent via the table in §4.
3. **Reuse existing helpers** (§7) before writing new ones. If a new helper is needed, make it
   **generic** and reusable (cf. `creerFormulaireAdresse`).
4. **Structure the view** like `Tcmgorg1MainView`: a constructor assembling `creerXxx()` methods,
   one section per logical block.
5. **Name in French** (§6); comments in French.
6. **Simulate data** for lists/grids via an `xxxSimules()` method (§14).
7. **Wire LOV buttons** to an `ouvrirListeXxx()` modal (§11).
8. **Add CSS only when needed**, in a new section of `app.css`, reusing the `--orpv-*` tokens (§13).
9. **Create** any missing icons, respecting the style (§12).
10. **Compile and verify visually** (§18) before considering the task done (§19).

---

## 4. Oracle Forms → Vaadin mapping

| Oracle Forms | Vaadin (via reference helper) |
|---|---|
| Text Item (editable) | `TextField` → `champTexte(value)` |
| Display Item / non-editable item | `TextField` + `setReadOnly(true)` (readable) or `setEnabled(false)` (greyed) — see §15 |
| Item + LOV button | `champTexte()` + `boutonRecherche()` + `ouvrirListeXxx()` modal |
| List Item / Poplist / Combo box | `ComboBox<String>` + `setItems(...)` |
| Check Box | `Checkbox` → `checkboxAvecLibelle(label, checkbox)` (label on the left) |
| Radio Group | `RadioButtonGroup<>` *(not yet used; follow the same compact style)* |
| Multi-line Text Item | `TextArea` (+ `TextAreaVariant.LUMO_SMALL`) |
| Date Item | plain text field for now; prefer `DatePicker` (FR locale) for real interaction |
| Tab Canvas / Tab Pages | `TabSheet` (class `orpv-tabs`) |
| **Multi-record** block (tabular) | `Grid<>` with editable `addComponentColumn(...)` (§10) |
| **Single-record** block | `FormLayout` → `formulaireResponsive(columns)` |
| Push Button | `Button` + `ButtonVariant.LUMO_SMALL` |
| Smartbar / icon bar | `orpv-toolbar` container + `boutonOutil(...)` (§8) |
| Modal window | `Dialog` (§11) |
| Main window | `Div` (body only) opened as a **tab** by `HomeView` (§17) |
| Status line (bottom message) | `MainLayout.getStatusBar().setMessage(...)` (§17) |

---

## 5. New-view skeleton (copy this)

```java
package com.example.views;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
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
// No @StyleSheet here: app.css and the themes are imported globally via @CssImport
// on Application (see §13). A view declares no stylesheet of its own.
@Route(value = "<url-segment>", layout = MainLayout.class)
@PageTitle("<Page title>")
@SpringComponent
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class <ViewName> extends Div {

    /** Action de fermeture de l'onglet hôte, injectée par {@link HomeView}. */
    private Runnable fermetureAction;

    public <ViewName>() {
        addClassName("orpv-body");                 // shared compact density + theme
        add(creerToolBar(),
            creerFormulaire(),
            creerOnglets());                       // depending on the form
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        MainLayout.rechercher(this)
                .ifPresent(layout -> layout.getStatusBar()
                        .setMessage("<Title> | Prêt"));
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

    // private Component creerToolBar() { … }      // see §8 and Tcmgorg1MainView
    // private Component creerFormulaire() { … }    // see §7
}
```

> **Important**: a view is a **form body** (`extends Div`), **not** a window. The window, title and
> close button are provided by `HomeView` (the tab) — see §17. Note that even though the surrounding
> doc is in English, the **code identifiers and comments stay in French** (`creerToolBar`,
> `fermetureAction`, `quitter`, `"… | Prêt"`).
> To make a view openable from the menu, add an entry in `HomeView` (cf.
> `ouvrirOnglet(TITRE, new <ViewName>())`).

---

## 6. Naming conventions (French)

- **All** UI (labels, tooltips, titles) and **all** comments are **in French**, accents included.
- **Builder methods**: `creerXxx()` (return a `Component` / layout).
- **Modal-opening methods**: `ouvrirXxx()`.
- **Button factories**: `boutonXxx()`.
- **Fields (attributes)**: type suffix — `xxxField` (`TextField`), `xxxButton` (`Button`),
  `xxxCheckbox`, `xxxArea` (`TextArea`), `xxxCombo` (`ComboBox`).
- **Grid row models**: a `record` or nested class, singular (`Lieu`, `Categorie`, `Accreditation`).
- **Simulated data**: `xxxSimules()` / `xxxSimulees()`.
- **Local variables**: French (`barre`, `grille`, `contenu`, `ligne`, `rechercher`, `valider`).

---

## 7. Catalog of reusable helpers

These helpers live in [Tcmgorg1MainView.java](Tcmgorg1MainView.java). **Reuse them as-is** (extract
into a shared utility if mutualized across views). Signatures:

**Fields & labels**
- `TextField champTexte(String valeur)` — `TextField` + `TextFieldVariant.LUMO_SMALL`.
- `FormLayout formulaireResponsive(int colonnesMax)` — labels on the left (ASIDE), responsive
  (1 col mobile → 2 → `colonnesMax`), `labelWidth = 110px`. Use `form.setColspan(item, n)` for wide
  fields (name, address…).
- Checkbox via `HorizontalLayout checkboxAvecLibelle(String libelle, Checkbox checkbox)` — label
  (class `orpv-checkbox-label`) on the **left** of the box.

**LOV buttons (list of values)**
- `Button boutonRecherche()` — small "list" button (`VaadinIcon.LIST` icon, class
  `orpv-lookup-button`, tooltip "Liste de valeurs").
- `HorizontalLayout champAvecRecherche(TextField champ, Button bouton)` — code field + LOV button
  joined (gap 4px), field takes the space.

**Search / grid (modals)**
- `<T> void appliquerResultats(Grid<T> grille, List<T> resultats)` — `setItems` + preselect the
  first row.
- `boolean correspondMotif(String texte, String motif)` — SQL `LIKE`-style filter (`%` = wildcard,
  case-insensitive, empty or `%` pattern matches everything).

**Toolbar** — see §8.

> If a helper is missing, **make it generic** (parameterize the label/fields) rather than duplicate.
> Reference example: `creerFormulaireAdresse(libelle, ligne1, ligne2, ligne3, codePostal)` factors
> the "Adresse principale" and "Adresse secondaire" tabs.

---

## 8. Toolbar (smartbar)

A `Div` of class `orpv-toolbar` containing **icon buttons** grouped by function, separated by
`separateur()`. Each button carries a **tooltip** describing the function **and the shortcut** in
parentheses.

Reference helpers:
- `Button boutonOutil(String icone, String infoBulle)` — SVG icon `icons/<icone>.svg`
  (class `orpv-tool-icon`), tertiary/icon/small button, `tooltip` + `ariaLabel`.
- `Button boutonOutil(String icone, String infoBulle, Key touche, KeyModifier... mods)` — same +
  `addClickShortcut`.
- `Button boutonOutilAction(String icone, String infoBulle, Runnable action[, Key, KeyModifier...])`
  — same + click action.
- `Button boutonOutilNavigation(String icone, String infoBulle, Key touche)` — arrow shortcut with
  `.allowBrowserDefault()` (lets the caret move inside fields).
- `Span separateur()` — vertical separator (class `orpv-toolbar-sep`).

**Mode switching.** The toolbar is rebuilt dynamically: keep the `Div` in a field, expose
`afficherToolBarXxx()` doing `barre.removeAll(); barre.add(boutonsXxx());`. Recreate the buttons on
each switch (shortcuts re-register cleanly on attach). Model: the "Interroger" button switches to a
query toolbar; its "Annuler" button restores the main toolbar.

### Wired actions (print, clipboard, editor, quit)

Some icon buttons trigger a real action, often on the browser side via
`getElement().executeJs(...)` (keyboard shortcut and click do the same thing):

- **Print** (Shift+F8): `ui.getPage().executeJs("window.print()")`. The `@media print` sheet
  (§13 / app.css) hides the chrome so only the form prints.
- **Cut / Copy / Paste / Edit**: a client script installed **once** on the view element (in
  `onAttach`) tracks the **last focused field** and keeps an **internal clipboard**. The buttons call
  it (`__orpvCut/Copy/Paste`); "Edit" opens a pre-filled *Editor* modal that rewrites the field on *OK*.
  > ### ⚠ Light DOM gotcha (must know)
  > In Vaadin 24+/25, the `<input>`/`<textarea>` of a `vaadin-text-field` / `vaadin-text-area` lives in
  > the **light DOM** (not the shadow): get the field with `host.querySelector('input, textarea')`,
  > **not** `host.shadowRoot.querySelector(...)`. The `focusin` event targets the `<input>` itself →
  > resolve the host via `closest('vaadin-text-field, vaadin-text-area')`. To sync the value to the
  > server, set `input.value` then dispatch the `input` and `change` events.
- **Quit**: two variants. In a view's toolbar, "Quitter" closes the **host tab** (`fermetureAction`
  injected by `HomeView`, §17). In the **Fichier ▸ Quitter** menu of `HomeView`, the action closes the
  **browser tab** (`window.close()`, with an end screen as fallback — browsers only allow
  `window.close()` for a tab opened by script).

---

## 9. Tabs (TabSheet)

`TabSheet` of class `orpv-tabs`, full width. Three established tab-content patterns:

- **Address form**: label + 3 stacked lines (`VerticalLayout`, gap 6px) + postal code, via
  `creerFormulaireAdresse(...)`.
- **Editable tabular block**: `Grid` with component columns (§10).
- **Free text**: full-width `TextArea`, fixed height (e.g. `150px`), scrollable.

Do not leave an "empty" tab: reproduce the screenshot's content.

---

## 10. Editable grid (multi-record block)

For an Oracle Forms multi-row editable block:

- `Grid<Model>` with a dedicated class (e.g. `accreditation-grid`), full width, **fixed height** to
  reveal the scrollbar.
- **Component columns**: `grille.addComponentColumn(row -> …)` to make each cell editable.
- **Model = mutable nested class** `private static final class Modele { … }`. Being nested in the
  view, its private fields are accessible from the lambdas (no getters/setters needed).
- Cells:
  - editable: `TextField celluleEditable(String valeur, Consumer<String> maj)` — writes the input
    back into the model via a `ValueChangeListener` (persists across scrolling / re-render).
  - read-only: `TextField celluleLectureSeule(String valeur)` (creation/modification dates…).
- LOV button column: `addComponentColumn(row -> { Button b = boutonRecherche(); … })`. To fill a
  field of the row from the modal, keep a **transient reference** to the field on the model
  (cf. `Accreditation.organismeChamp`) and read it **inside the click listener** (i.e. after render).
- Data: `List<Model> xxxSimules()` with one filled row + a few blank rows.

---

## 11. Modal windows (Dialog)

Two families, both `Dialog` with `setDraggable(true).setResizable(true)`, title via
`setHeaderTitle(...)`, fixed dimensions.

**A. List of values (LOV)** — model `ouvrirListeLieux` / `ouvrirListeCategories`:
- class `lieux-dialog`; a "Rech" field at the top; a `Grid` in the middle (class `lieux-grid`,
  single selection); **three buttons** in the footer: `Rech.`, `OK` (class `orpv-dialog-ok`, green,
  primary), `Annuler` (tertiary, `addClickShortcut(Key.ESCAPE)`).
- Filtering via `correspondMotif`, populating via `appliquerResultats`.
- Validation by **double-click** on a row **or** the `OK` button; either way, write the values back
  into the target fields then `dialog.close()`.

**B. Data-entry form** — model `ouvrirGestionLieux`:
- title like "Système de la recherche - <Screen>"; a **close button** added to the header
  (`dialog.getHeader().add(boutonFermer)`, `VaadinIcon.CLOSE_SMALL` icon).
- a bold section title in the content; "label : field" rows via `ligneFormulaire(...)`; side-by-side
  component groups via `groupeChamps(...)`.

> ### ⚠ Shadow DOM pitfall (must know)
> A `Dialog`'s overlay **does not inherit** the CSS variables of `.orpv-body`. Therefore:
> - To restore the **compact density**, add a **class to the dialog** (`addClassName`) and
>   **re-declare the `--lumo-*`** variables on that class in `app.css` (cf. `.gestion-lieux-dialog`).
> - Rules scoped as **`.orpv-body …`** do **not** apply inside the modal.
> - **Global class selectors** (`.gestion-lieux-titre`, `.gestion-lieux-label`) apply to the
>   *slotted* content; `::part(content)` works on the overlay. So prefer classed `Span`s + explicit
>   rows over `vaadin-form-item::part(label)`.

---

## 12. SVG icons

- Location: `src/main/resources/META-INF/resources/icons/<function>.svg`.
- Referenced by `new Image("icons/" + nom + ".svg", infoBulle)` (cf. `boutonOutil`).
- Format: `viewBox="0 0 20 20"`, **20×20**, **retro pixelated** style faithful to Oracle Forms.
- Named **by function**, in French: `imprimer`, `couper`, `copier`, `coller`, `editer`, `executer`,
  `decompter`, `annuler`, `interroger`, etc.
- Reference palette (reuse):
  - yellow `#f3c100` / outline `#9c7e00`
  - red `#d12b2b` / outline `#8e1818`
  - cyan `#17b6d6`, blue `#1c4fd6`
- One icon = one function. Do not reuse an icon for a different action.

---

## 13. CSS, theme and theme system

### 13.1 Single stylesheet (`app.css`)

- **Location**: `src/main/frontend/styles/app.css` — **bundled by the build** (Vite), not served by
  URL. Declared **once**, globally, by `@CssImport("./styles/app.css")` on `Application` (which
  implements `AppShellConfigurator`). **A view carries no style annotation** (no more per-view
  `@StyleSheet`).
- Organized into **numbered sections** with a table of contents at the top. **Add a new numbered
  section** per feature (follow the existing numbering).
- **Tokens** in `:root` (prefix `--orpv-*`): surfaces, borders, text, actions, toolbar. **Reuse**
  these variables; do not hardcode colors. Some tokens **derive** from another (e.g. the toolbar
  reuses the status-bar tones by default: `--orpv-toolbar-bg: var(--orpv-statusbar-bg)`) so it follows
  the theme automatically.
- **Density**: the root class `orpv-body` carries the `--lumo-*` overrides (the project uses **Lumo**,
  not Aura, for density and components; `Aura.STYLESHEET` is imported only as a base). Every new view
  adds `addClassName("orpv-body")`.
- **Scoping**: prefix with `.orpv-body` when you need to win over the `display:block` rule on direct
  children (e.g. `.orpv-body .orpv-toolbar { display:flex; }`).
- Classes named by feature (`orpv-toolbar`, `orpv-tabs`, `lieux-grid`, `accreditation-grid`,
  `gestion-lieux-dialog`, `editeur-dialog`…). No inline style except the occasional
  `getStyle().set("gap", …)` for layout spacing.

### 13.2 Theme system (attribute variants)

- **One theme = one file** `src/main/frontend/styles/themes/<name>.css`, also declared via `@CssImport`
  on `Application` (so bundled). Current themes: `gris_vert`, `bleu_azur`, `vert_emeraude`,
  `terracotta`, `violet_amethyste`, `bleu_ardoise` (default), `gris`.
- Each theme **overrides only the `--orpv-*` tokens**, scoped under an **attribute variant**:
  `html[theme~="<slug>"] { --orpv-… }` (hyphenated slug, e.g. `bleu-ardoise`). Its specificity (0,1,1)
  beats the `:root` of `app.css` (0,1,0) → the theme wins, and since the tokens sit on `<html>` they
  propagate everywhere (including the derived toolbar).
- **Switching** (Help ▸ Theme menu in `HomeView`): set the attribute, remember the choice.
  ```java
  // HomeView: JS_APPLIQUER_THEME, parameter $0 = slug
  "document.documentElement.setAttribute('theme', $0);" +
  "window.localStorage.setItem('orpv-theme', $0);"
  ```
- **Restore on load** (`MainLayout.onAttach`, runs on every navigation): read the remembered slug,
  otherwise apply the default.
  ```java
  "const t = window.localStorage.getItem('orpv-theme') || 'bleu-ardoise';" +
  "document.documentElement.setAttribute('theme', t);"
  ```
- **Add a theme**: create `styles/themes/<name>.css` (a `html[theme~="<slug>"]` block setting the
  `--orpv-*`), add its `@CssImport` on `Application`, and an `appliquerTheme("<slug>")` entry in the
  `HomeView` menu.

---

## 14. Simulated data (mock)

Lists/grids are fed by `xxxSimules()` methods returning a `List.of(...)`, **always commented**:
"*liste temporaire …, simulant le résultat d'une requête en base. À remplacer ultérieurement par les
données réelles.*" This isolates the future database wiring point. Do not wire persistence until
asked.

---

## 15. Read-only vs disabled

- **`setReadOnly(true)`**: field is **readable**, not keyboard-editable, **not greyed**, and still
  **settable programmatically** (`setValue` works). Use for **LOV-sourced labels** (e.g.
  `villeLibelleField`, category label) that the user does not type but must remain readable.
- **`setEnabled(false)`**: field is **greyed / disabled**. Use when the screenshot shows a visually
  inactive (grey) field, or when a state requires it (e.g. LOV button enabled only in query mode:
  `codeOrpvLookupButton.setEnabled(false)` by default).

When in doubt, follow the screenshot's look: white readable field → `readOnly`; grey field →
`disabled`.

---

## 16. Keyboard shortcuts

- Reproduce the Oracle Forms shortcuts and **mention them in the tooltip** in parentheses
  (e.g. "Imprimer la fenêtre courante (Maj+F8)").
- `bouton.addClickShortcut(Key.X, KeyModifier.CONTROL)`; for navigation arrows, add
  `.allowBrowserDefault()`.
- In modals: `OK` → `Key.ENTER`; `Annuler` → `Key.ESCAPE`.

---

## 17. Integration (route, layout, status bar, tab)

- **Route**: `@Route(value = "<segment>", layout = MainLayout.class)`. `MainLayout` provides the
  content area + the persistent status bar.
- **Status bar**: in `onAttach`, `MainLayout.rechercher(this).ifPresent(l ->
  l.getStatusBar().setMessage("<Screen> | Prêt"))`.
- **Opening as a tab**: `HomeView` opens the view via `ouvrirOnglet(TITRE, new <ViewName>())`
  (closable tab, no duplicate). If the view must be able to close its tab, `HomeView` injects
  `setFermetureAction(Runnable)`; the view calls that action from its "Quitter" button.
  → **To make a new view reachable, add the matching menu entry in `HomeView`.**

---

## 18. Build / run / verify

**Recommended environment: WSL Dev Container** (Compose, image `eclipse-temurin:25-jdk`, JDK 25
built in — **no `JAVA_HOME` to set**). Inside the container:

```bash
./mvnw -q compile     # compile only (quick validation)
./mvnw                # dev (default goal spring-boot:run) → http://localhost:8080
```

**On the Windows host** (if needed), JDK 25 is required (the machine default won't do):

```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot"
.\mvnw.cmd -q compile
.\mvnw.cmd
```

- **Bundled CSS**: since `app.css` and the themes live under `src/main/frontend/styles/` (via
  `@CssImport`), a style change goes through the **frontend build** (Vite). Re-run `./mvnw` (goal
  `build-frontend`) to re-bundle if needed; a theme that fails to apply is almost always a stale bundle.
- **No Java hot reload**: after a Java change, **stop** the server, **recompile**, **restart**.
- Visual verification: open `http://localhost:8080/home`, open the view's tab, compare to the
  screenshot. (Playwright MCP can automate navigation/screenshots if available.)
- ⚠ The IDE (Java language server) may run on an older JDK and wrongly flag `Stream.toList()` as
  undefined: **false positive**, the Maven build (JDK 25) is the source of truth.

---

## 19. Definition of Done (review checklist)

- [ ] The view follows the §5 skeleton (`extends Div`, annotations, `onAttach` → status bar).
- [ ] `addClassName("orpv-body")` present.
- [ ] All screenshot components reproduced (labels, fields, combos, LOV, tabs, grids, toolbar).
- [ ] Existing helpers reused; new helpers generic and named in French.
- [ ] Labels/tooltips/comments in **French**; shortcuts in tooltips.
- [ ] LOVs wired to an `ouvrirListeXxx` modal; combos populated; read-only / disabled per §15.
- [ ] CSS added in a **new numbered section** of `app.css`, `--orpv-*` tokens reused; for modals,
      density re-declared on the dialog's class (§11).
- [ ] Missing icons created with the right format/palette (§12).
- [ ] Simulated data via `xxxSimules()` with an "à remplacer" comment.
- [ ] Menu entry added in `HomeView` if the view must be openable.
- [ ] `mvnw -q compile` **passes** (the Maven build, not just the IDE).
- [ ] Visual verification performed.

---

## 20. Rules for the AI — must follow

**DO**
- Take `Tcmgorg1MainView.java` as a **living model**: imitate its structure, helpers, style.
- Reuse the helpers (§7) and patterns (§8–§11) **before** inventing.
- Name/write everything **in French** (UI, methods, variables, comments).
- Factor common code into **generic**, reusable helpers.
- Put **structural** styling in `app.css` (`--orpv-*` tokens); **palettes** go in
  `styles/themes/<name>.css` (attribute variants, §13).
- Simulate data and mark the future wiring point (§14).
- Compile (`mvnw`) and fix until green; report state honestly (compiled / verified / untested).

**DON'T**
- ❌ Don't introduce React/Hilla or another UI framework.
- ❌ Don't create an **ad-hoc** CSS file or inline styles (except the occasional `gap`): structure goes
  in `app.css`, palettes in theme variants (§13). Don't reintroduce per-view `@StyleSheet`.
- ❌ Don't turn a view into a window/`Dialog`: a view is a `Div` **body** opened as a tab (unless the
  screenshot is explicitly a **modal window**).
- ❌ Don't wire persistence/database without an explicit request.
- ❌ Don't hardcode colors: use the tokens.
- ❌ Don't trust the IDE's false positives on `toList()` (§18).
- ❌ Don't name/comment in English.

**When the screenshot is ambiguous** (exact widget type, button behavior, combo values): implement a
faithful visual rendering, isolate behavior behind a helper/mock, and **ask** rather than guess
business behavior.
