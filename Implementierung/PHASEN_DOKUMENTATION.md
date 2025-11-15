# Projektdokumentation: Sitzplatzverwaltungssystem (Airbus A350-900)

## Übersicht
Diese Datei dokumentiert alle Implementierungsschritte, Designentscheidungen und Änderungen im Projektverlauf. Sie wird nach jeder Phase und jedem Schritt aktualisiert.

---

## Phase 1: Domänenmodell (Bottom-Up)

### Schritt 1.1: Erstellung der Enumerationen
**Datum:** 2025-11-15

**Ziel:** Alle Enums aus dem UML-Klassendiagramm v4 exakt und konsistent im Paket `com.sitzplatzverwaltung.domain.model` anlegen.

**Erstellte Enums:**
- `CabinClass` (Business, PremiumEconomy, Economy)
- `SeatStatus` (AVAILABLE, RESERVED, ASSIGNED, BLOCKED)
- `ReservationStatus` (PENDING, CONFIRMED, EXPIRED, COMPLETED)
- `AssignmentReason` (AUTOMATISCH, MANUELL, UPGRADE)
- `EmployeeRole` (CheckInAgent, Supervisor)
- `PaymentStatus` (PENDING, SUCCESS, FAILED)
- `SagaStatus` (STARTED, COMPENSATED, COMPLETED)
- `OrchestratorState` (IDLE, RESERVING, PAYING, COMMITTING, ROLLBACK_PENDING)

**Vorgehen:**
- Für jede Enum wurde eine eigene Datei im Zielpaket angelegt.
- Die Enum-Namen und Werte entsprechen exakt dem UML-Diagramm (Groß-/Kleinschreibung, Reihenfolge).
- Jede Enum enthält einen JavaDoc-Kommentar mit Verweis auf das UML-Diagramm und die jeweilige Bedeutung.

**Ergebnis:**
- Die Enums sind vollständig, konsistent und bereit für die weitere Modellierung der Entitäten.

---

### Nächster Schritt: 1.2 – Entitäten
Im nächsten Schritt wurden die Entitätsklassen gemäß Klassendiagramm v4 erstellt. Dabei wurden alle Attribute, Beziehungen und Invarianten exakt übernommen und mit Lombok annotiert.

---

### Schritt 1.2: Erstellung der Entitäten
**Datum:** 2025-11-15

**Ziel:** Alle Entitätsklassen aus dem UML-Klassendiagramm v4 exakt und konsistent im Paket `com.sitzplatzverwaltung.domain.model` anlegen.

**Erstellte Entitäten:**
- `Sitzplatz`: Sitzplatz im Flugzeug, inkl. Sitznummer, Kabinenklasse, Reihe, Spalte, Status, optionale Reservierungs-ID.
- `Zuweisung`: Zuweisung eines Sitzplatzes an einen Passagier, inkl. Audit- und Idempotenz-Attributen.
- `Reservierung`: Temporäre Reservierung eines Sitzplatzes (Upgrade-Flow), inkl. Status und Ablaufzeitpunkt.
- `Mitarbeiter`: Mitarbeiter-Entität für RBAC, inkl. Rolle.
- `SagaLog`: Audit-Log für Saga-Transaktionen (Upgrade-Flow).
- `Passagier`: Passagier-Entität (aus Buchungssystem).
- `Buchung`: Buchung-Entität (aus Buchungssystem), inkl. gebuchter Kabinenklasse.

**Vorgehen:**
- Für jede Entität wurde eine eigene Datei im Zielpaket angelegt.
- Die Attribute, Typen und Beziehungen entsprechen exakt dem UML-Diagramm.
- Jede Entität enthält einen JavaDoc-Kommentar mit Invariante und Traceability-Hinweis.
- Lombok-Annotationen (`@Data`, `@AllArgsConstructor`, `@NoArgsConstructor`, `@Builder`) wurden verwendet, um Boilerplate zu vermeiden.

**Ergebnis:**
- Die Entitäten sind vollständig, konsistent und fehlerfrei kompilierbar (Lombok ist via Maven eingebunden).
- Die Kompilierung wurde erfolgreich geprüft.

---

### Nächster Schritt: Phase 2 – Persistenzschicht (Repository-Interfaces)
Im nächsten Schritt werden die Repository-Interfaces gemäß Klassendiagramm und Sequenzdiagrammen erstellt. Diese Dokumentation wird nach jedem Schritt fortlaufend ergänzt.

---
