# Design System

> Theme, tokens, and component usage. The **detailed, authoritative** reference is the migration guide — §6–§16 (conventions, helpers, toolbar, tabs, dialogs) and §13 (CSS & themes): [specification.fr.md](../src/main/java/com/example/views/specification.fr.md). This file is a summary.

---

## 1. Look & feel

- The UI reproduces the **retro Oracle Forms** administrative look: compact density, framed white data fields, gray chrome.
- Components and density use **Lumo** (`--lumo-*` overrides on the `.orpv-body` root class). `Aura.STYLESHEET` is imported on `Application` only as a base — **do not build the UI on Aura `--aura-*` tokens**; the project's palette is the **`--orpv-*`** tokens.

## 2. Palette tokens

- Defined in `:root` in `app.css` (prefix `--orpv-*`): surfaces, borders, text, actions, and toolbar. **Reuse them; never hardcode hex colors.**
- Some tokens **derive** from others so the UI stays coherent — e.g. the toolbar reuses the status-bar tones by default (`--orpv-toolbar-bg: var(--orpv-statusbar-bg)`), so it follows any theme automatically.

## 3. Theme system (switchable)

- Each theme is a file `src/main/frontend/styles/themes/<name>.css` that overrides the `--orpv-*` tokens under an **attribute variant** `html[theme~="<slug>"]`.
- Switched via the **Aide ▸ Thème** menu in `HomeView`: it sets the `theme` attribute on `<html>` and persists the slug in `localStorage`; `MainLayout` restores it on load (default `bleu-ardoise`). See migration guide §13.2.
- Current themes: `gris_vert`, `bleu_azur`, `vert_emeraude`, `terracotta`, `violet_amethyste`, **`bleu_ardoise` (default)**, `gris`.

## 4. CSS organization

- Single structural stylesheet `app.css` (bundled via `@CssImport` on `Application`), organized into **numbered sections** with a table of contents. Classes named by feature (`orpv-toolbar`, `orpv-tabs`, `lieux-grid`, `accreditation-grid`, `gestion-lieux-dialog`, `editeur-dialog`…). No inline styles except the occasional layout `gap`.

## 5. Component standards

| Component | When to use | Notes |
|-----------|-------------|-------|
| `TextField` (`LUMO_SMALL`) | Form fields | via `champTexte(...)`; `setReadOnly(true)` (readable, LOV-filled) vs `setEnabled(false)` (greyed) — guide §15 |
| `Button` | Actions / toolbar | `boutonOutil(...)` for icon toolbar buttons; `LUMO_SMALL` elsewhere |
| `Grid` | Multi-record blocks | editable component columns — guide §10 |
| `Dialog` | Lookups & sub-forms | draggable/resizable; density re-declared on a dialog class — guide §11 |
| `ComboBox` | Poplists / combos | `setItems(...)` |
| `TabSheet` | Tab canvases | class `orpv-tabs` |
| `Notification` | Lightweight feedback | e.g. "Texte introuvable" |
