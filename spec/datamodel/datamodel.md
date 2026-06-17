# Data Model

> Domain entities. **No database yet** — list/grid data is simulated in code (`xxxSimules()` / mock generators), so this describes the in-memory shapes used by the views and the future persistence target.

| Entity | Key fields | Notes |
|--------|-----------|-------|
| **OrganismePourvoyeur** (ORPV) | codeOrpv, siru, mess, acronyme, nom, autreNom, villeCode, villeLibelle, payeurFraisIndirect, comptabilisationFondsInternes, fondsDotation, adressePrincipale, adresseSecondaire, accreditations, remarque, categorieCode, categorieLibelle, siteWww, codeRevenuMeq, anneeFinActivite, moisFermeture | The reference screen (`Tcmgorg1MainView`). Generated in bulk by `OrganismesPourvoyeurs.generer(n)` |
| **Adresse** | ligne1, ligne2, ligne3, codePostal | Principal and secondary address of an ORPV |
| **Accreditation** | debut, fin, organisme, creation, modification | Editable accreditation grid rows of an ORPV |
| **Lieu** | nomLieu, code, type, paysAttache | Lookup list (ville) — `lieuxSimules()` |
| **Categorie** | description, code | Lookup list (catégorie d'organisme) — `categoriesSimulees()` |

## Simulation / persistence

- Mock generators live next to the views (`com.example.util.OrganismesPourvoyeurs`, and `xxxSimules()` methods inside the views).
- Each generator is commented as **temporary, to be replaced by real database queries**. That comment marks the single wiring point for future persistence; do not introduce a database without an explicit request.
