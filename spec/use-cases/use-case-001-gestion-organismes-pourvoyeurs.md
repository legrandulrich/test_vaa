# UC-001: Gestion des organismes pourvoyeurs (ORPV)

**As a** research-administration user, **I want to** view, navigate and edit organisme-pourvoyeur records through a faithful migration of the Oracle Forms screen **so that** I can manage provider data in the new Vaadin application with the same workflow as before.

**Status:** Implemented
**Date:** 2026-06-16

> This is the **reference screen** (`Tcmgorg1MainView`) that every other migration must imitate. See the migration guide for the conventions it embodies.

---

## Main Flow

- I open **Gestion des unités** (or **Pilotage ▸ Organisme**) from the home menu; the screen opens in a closable tab.
- I see the ORPV form: identification (code ORPV with LOV, SIRU, MESS, acronyme, nom, autre nom, ville with LOV), three option checkboxes, tabs (Adresse principale / Adresse secondaire / Accréditation / Remarque) and a lower block (catégorie with LOV, site WWW, code revenu MEQ, année fin activité, mois fermeture).
- I navigate records with the toolbar (premier / précédent / suivant / dernier); the status bar shows `Eng. n/2000`.
- I edit fields; I can **cut / copy / paste** between text fields, and use **Éditer** to edit a field's full content in an editor dialog (OK writes it back).
- I click a **LOV** (list) button to open a lookup dialog (lieux, catégories, organismes payeurs), filter with **Rech**, and pick a row (OK or double-click) to fill the target fields.
- I switch to **query mode** with **Interroger** (the form clears and the code-ORPV LOV becomes active); **Annuler** restores the main toolbar.
- I **print** the form (Maj+F8) — only the form prints, not the application chrome.

---

## Business Rules

| ID | Rule |
|----|------|
| BR-01 | The view is a form body (`extends Div`, class `orpv-body`), opened as a **closable tab** by `HomeView`; no duplicate tab for the same screen |
| BR-02 | LOV-filled labels (ville, catégorie) are `setReadOnly(true)` (readable, not typed); visually inactive fields are `setEnabled(false)` |
| BR-03 | List/grid data is **simulated** (`xxxSimules()` / mock generator) — no database |
| BR-04 | All UI text, method names and comments are **in French**; keyboard shortcuts are shown in tooltips |
| BR-05 | A single theme drives the look; the toolbar follows the active theme (see Design System) |

---

## Acceptance Criteria

- [ ] The screen reproduces every component of the Oracle Forms screenshot (labels, fields, combos, LOV buttons, tabs, editable accreditation grid, toolbar).
- [ ] Record navigation (premier / précédent / suivant / dernier) updates all fields and the status bar.
- [ ] LOV dialogs filter on the "Rech" criterion and fill the target fields on OK / double-click.
- [ ] Cut / copy / paste move text across form fields; **Éditer** opens the editor dialog pre-filled and writes the edited text back on OK.
- [ ] Query mode (Interroger) clears the form and enables the code-ORPV LOV; Annuler restores the main toolbar.

---

## Tests

> Browserless (Karibu) UI tests + pure-logic unit tests, per the architecture testing convention.

- [ ] `Tcmgorg1MainViewTest` — record navigation, LOV fill, query-mode toggle, read-only vs disabled behaviour.

---

## UI / Routes

- Reference view: **`Tcmgorg1MainView`** — the model all other Oracle Forms migrations imitate.

| Route | Access | Notes |
|-------|--------|-------|
| `/organismes-pourvoyeurs` | authenticated | Vaadin `@Route`, layout `MainLayout`; usually opened as a tab from `HomeView` |
