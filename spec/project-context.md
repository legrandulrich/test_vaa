# Project Context

> High-level context for the project: the problem being solved, who it's for, what's in scope, and what constraints apply.

## 1. Vision

**SIRUL** (Système de la recherche, Université Laval) is being migrated from legacy **Oracle Forms** screens to a modern **Java / Vaadin Flow** web application. Each Oracle Forms screen is reproduced faithfully — same layout, same retro-administrative look, same interaction logic — on a maintainable server-side Java stack.

Several people work on the migration, so **consistency with the reference view and the migration guide matters more than inventiveness**. Success is achieved when each migrated screen matches its Oracle Forms screenshot and follows the shared conventions.

## 2. Users

Research-administration staff at Université Laval who manage providers (organismes pourvoyeurs), places (lieux), categories, accreditations and related research-funding data through the migrated screens.

## 3. Scope & constraints

- **In scope**: faithful migration of Oracle Forms screens to Vaadin Flow views; the shared chrome (toolbar, tabs, lookup dialogs, status bar) and a switchable visual theme system.
- **Server-side Vaadin Flow only** (Java) — no React/Hilla, no other UI framework.
- All UI text, method names, variables and comments are **in French**.
- **No persistence yet**: list/grid data is simulated (`xxxSimules()`), which isolates the future database wiring point. Do not wire a database without an explicit request.
- The **authoritative, detailed build specification** is the migration guide (helpers, conventions, patterns).

---

# Related Documents

- [Spec README](README.md) — process overview and workflow
- [Architecture](architecture.md) — technology stack and application structure
- [Design System](design-system.md) — theme, tokens, component usage
- [Use Case Template](use-cases/use-case-template.md) — template for feature specifications
- [Data Model](datamodel/datamodel.md) — domain entities (currently simulated)
- [Migration guide (authoritative)](../src/main/java/com/example/views/specification.fr.md) — the detailed build spec (FR; EN mirror alongside)
