# Architecture

> Technology stack and application structure. `pom.xml` is the source of truth for versions. Do not modify `pom.xml` or this file without asking.

---

## 1. Technology Stack

- **Java 25** (`<java.version>25</java.version>` in `pom.xml`)
- **Vaadin 25** — Flow, **server-side Java only** (no React/Hilla)
- **Spring Boot 4** (`@SpringComponent`, prototype `@Scope`)
- **Maven** (wrapper `mvnw` / `mvnw.cmd`), default goal `spring-boot:run`, port **8080**
- **No database**: list/grid data is simulated in code (`xxxSimules()`); persistence is a deliberate future wiring point.
- **No Java hot reload** (no `spring-boot-devtools`): a Java change requires a restart.
- Build/run happens in a **WSL Dev Container** (Compose, image `eclipse-temurin:25-jdk`, JDK 25 built in). On the Windows host, `JAVA_HOME` must point to Temurin JDK 25.

---

## 2. Application Structure

```
com.example/
  Application.java              — Spring Boot entry point + global @CssImport (app.css + themes)
  views/
    MainLayout.java             — root layout (content area + shared status bar)
    HomeView.java               — home menu; opens views as closable tabs; Help▸Theme menu
    AppHeader.java / StatusBar.java
    AcceuilView.java / LoginView.java
    Tcmgorg1MainView.java       — REFERENCE VIEW (the model to imitate)
    <NewView>.java              — one migrated Oracle Forms screen = one class
```

- **Styles** live in `src/main/frontend/styles/` — `app.css` (structure + `--orpv-*` tokens + default theme) and `themes/<name>.css` (one switchable palette each) — **bundled** via `@CssImport` on `Application`.
- **Toolbar icons** are static SVGs in `src/main/resources/META-INF/resources/icons/`, loaded by URL.

---

## 3. UI conventions

- A view is a **form body** (`extends Div`, class `orpv-body`), **not** a window; the window / title / close button are provided by `HomeView` when the view is opened as a tab.
- Reuse the reference **helpers and patterns** (toolbar, tabs, editable grid, lookup dialogs) documented in the migration guide.
- **Theme switching** uses an attribute-variant system (`html[theme~="..."]`, persisted in `localStorage`) — see [Design System](design-system.md) and migration guide §13.

---

## 4. Testing

- **JUnit 5** + Vaadin **Browserless tests** (Karibu, `browserless-test-junit6`): pure-logic unit tests and browserless UI tests. Tests are organized **per use case**, not per view — see `/use-case-tests`.

---

> The detailed, authoritative build specification (helpers, conventions, toolbar, tabs, dialogs, CSS & theme system) is the migration guide: [specification.fr.md](../src/main/java/com/example/views/specification.fr.md).
