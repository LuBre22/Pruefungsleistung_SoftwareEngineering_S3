# Anforderungsmanagement – SOPHIST Master-Methode (Minimalversion)

## Sitzplatzverwaltung am Check-in-Schalter (Airbus A350-900)

**Version:** 2.3 (Minimal – Optimiert + Payment Flow + Transaktionskonsistenz)  
**Stand:** 2025-11-11  

---

## 1. Zusammenfassung

Dieses Dokument beschreibt die Anforderungen für ein Lernprojekt-System zur Sitzplatzverwaltung am Check-in-Schalter. Das System unterstützt Check-in-Mitarbeiter bei Zuweisung, Änderung und Upgrade von Sitzplätzen auf einem Airbus A350-900 (Klassen: Business, Premium Economy, Economy). Annahmen: Nur Check-in vor Ort; Passagierdaten vom Buchungssystem; Upgrade-Regeln mit Autorisierung, Bezahllogik und Kompensation; Sitzplan-Visualisierung als zentrale UI-Komponente; zeitgesteuerte Reservierung mit automatischer Freigabe bei Zahlungsfehlern; hybride Transaktionsstrategie (ACID für lokale Operationen, Saga-Pattern für verteilte Upgrade-Flows).

---

## 2. Template für Anforderungen (SOPHIST Master-Methode)

| **Attribut**              | **Beschreibung**                                                                 |
|---------------------------|----------------------------------------------------------------------------------|
| **ID**                    | Eindeutige Kennzeichnung (F-XX, N-XX)                                          |
| **Version**               | Versionsnummer                                                                   |
| **Anforderungsaussage**   | <System> <MUSS/SOLL/KANN> <Stakeholder> <Aktion> <Objekt> <Bedingung>          |
| **Rationale**             | Begründung                                                                       |
| **Priorität**             | Must-Have / Should-Have / Could-Have                                            |
| **Akzeptanzkriterien**    | Given/When/Then                                                                  |
| **Abhängigkeiten**        | Verweise auf andere Anforderungen                                               |

---

## 3. Funktionale Anforderungen

### 3.1 Use Case: Zuweisung

#### **F-01: Automatische Sitzplatzzuweisung**
- **ID:** F-01 | **Version:** v2.3  
- **Anforderungsaussage:** Das System MUSS beim Check-in einem Passagier automatisch einen verfügbaren Sitzplatz der gebuchten Klasse zuweisen, wenn kein Sitzplatz vorab zugewiesen wurde und keine manuelle Auswahl erfolgt.  
- **Rationale:** Schneller Check-in-Prozess.  
- **Priorität:** Must-Have  
- **Akzeptanzkriterien:**  
  - **Given:** Passagier mit Economy-Buchung ohne Sitzplatz  
  - **When:** Check-in abgeschlossen  
  - **Then:** System weist Economy-Sitzplatz zu und zeigt ihn auf Bordkarte  
- **Abhängigkeiten:** Buchungssystem-API  

---

#### **F-02: Manuelle Sitzplatzauswahl**
- **ID:** F-02 | **Version:** v2.3  
- **Anforderungsaussage:** Das System MUSS dem Check-in-Mitarbeiter ermöglichen, aus verfügbaren Sitzplätzen der gebuchten Klasse manuell einen Sitzplatz auszuwählen.  
- **Rationale:** Passagierwünsche (Gangplatz, Fenster).  
- **Priorität:** Must-Have  
- **Akzeptanzkriterien:**  
  - **Given:** Sitzplatzauswahl-Ansicht geöffnet  
  - **When:** Mitarbeiter wählt Sitzplatz aus  
  - **Then:** Sitzplatz zugewiesen, Bordkarte aktualisiert  
- **Abhängigkeiten:** F-01, N-05  

---

#### **F-03: Verhinderung von Doppelzuweisungen**
- **ID:** F-03 | **Version:** v2.3  
- **Anforderungsaussage:** Das System MUSS bereits zugewiesene Sitzplätze als nicht verfügbar markieren.  
- **Rationale:** Konfliktvermeidung.  
- **Priorität:** Must-Have  
- **Akzeptanzkriterien:**  
  - **Given:** Sitzplatz bereits zugewiesen  
  - **When:** Sitzplatzauswahl geöffnet  
  - **Then:** Besetzter Sitzplatz nicht auswählbar  
- **Abhängigkeiten:** N-05  

---

#### **F-04: Check-in ohne Sitzplatz (Überbuchung)**
- **ID:** F-04 | **Version:** v2.3  
- **Anforderungsaussage:** Das System MUSS Check-in ohne Sitzplatzzuweisung ermöglichen, wenn keine Sitzplätze verfügbar sind. Bordkarte erhält Vermerk "Sitzplatz am Gate".  
- **Rationale:** Handhabung von Überbuchung.  
- **Priorität:** Must-Have  
- **Akzeptanzkriterien:**  
  - **Given:** Keine Sitzplätze verfügbar  
  - **When:** Check-in bestätigt  
  - **Then:** Bordkarte mit "Sitzplatz am Gate" erstellt  
  - **And:** Passagier als "eingecheckt ohne Sitzplatz" markiert  
- **Abhängigkeiten:** F-01  

---

### 3.2 Use Case: Änderung

#### **F-05: Sitzplatzänderung innerhalb Klasse**
- **ID:** F-05 | **Version:** v2.3  
- **Anforderungsaussage:** Das System MUSS dem Check-in-Mitarbeiter ermöglichen, einen zugewiesenen Sitzplatz durch einen anderen verfügbaren Sitzplatz derselben Klasse zu ersetzen.  
- **Rationale:** Nachträgliche Wünsche.  
- **Priorität:** Must-Have  
- **Akzeptanzkriterien:**  
  - **Given:** Passagier mit Sitzplatz 12A  
  - **When:** Mitarbeiter wählt Sitzplatz 15C  
  - **Then:** 12A freigegeben, 15C zugewiesen, Bordkarte aktualisiert  
- **Abhängigkeiten:** F-03, N-05  

---

#### **F-06: Freigabe vorheriger Sitzplatz**
- **ID:** F-06 | **Version:** v2.3  
- **Anforderungsaussage:** Das System MUSS den vorherigen Sitzplatz automatisch freigeben, sobald ein neuer Sitzplatz zugewiesen wurde.  
- **Rationale:** Ressourceneffizienz.  
- **Priorität:** Must-Have  
- **Akzeptanzkriterien:**  
  - **Given:** Sitzplatzwechsel von 12A zu 15C  
  - **When:** Neue Zuweisung bestätigt  
  - **Then:** 12A als verfügbar markiert  
- **Abhängigkeiten:** F-05  

---

### 3.3 Use Case: Upgrade

#### **F-07: Upgrade zwischen Klassen**
- **ID:** F-07 | **Version:** v2.3  
- **Anforderungsaussage:** Das System MUSS einem autorisierten Check-in-Mitarbeiter ermöglichen, für einen Passagier ein Upgrade in eine höhere Klasse gegen Bezahlung durchzuführen (Economy → Premium Economy, Premium Economy → Business), wenn Sitzplätze verfügbar sind.  
- **Rationale:** Umsatzsteigerung, Kundenzufriedenheit.  
- **Priorität:** Must-Have  
- **Akzeptanzkriterien:**  
  - **Given:** Economy-Passagier, Premium Economy-Sitzplätze verfügbar  
  - **And:** Mitarbeiter ist als "Supervisor" autorisiert (siehe N-06)  
  - **When:** Mitarbeiter wählt Premium Economy-Upgrade  
  - **And:** Bezahlung wird erfolgreich autorisiert (siehe F-08)  
  - **Then:** Premium Economy-Sitzplatz zugewiesen  
  - **And:** Economy-Sitzplatz freigegeben (siehe F-06)  
  - **And:** Bordkarte mit neuer Klasse aktualisiert  
- **Abhängigkeiten:** N-05, F-06, F-08, N-06, N-07, Zahlungssystem-Schnittstelle  

---

#### **F-08: Reserve-Payment-Confirm Flow für Upgrades**
- **ID:** F-08 | **Version:** v2.3  
- **Anforderungsaussage:** Das System MUSS bei Upgrades (F-07) den Ziel-Sitzplatz temporär reservieren (Time-To-Live: 120 Sekunden), die Bezahlung initiieren, und bei erfolgreicher Zahlung die Reservierung bestätigen. Bei Zahlungsfehlschlag oder Timeout MUSS das System die Reservierung automatisch freigeben und den ursprünglichen Sitzplatz wiederherstellen.  
- **Rationale:** Verhinderung von Race Conditions bei Zahlungen; Kompensation bei asynchronen Zahlungsfehlern.  
- **Priorität:** Must-Have  
- **Akzeptanzkriterien:**  
  - **Given:** Mitarbeiter initiiert Premium Economy-Upgrade für Economy-Passagier  
  - **When:** System reserviert Premium Economy-Sitzplatz 8A (TTL: 120s)  
  - **And:** Zahlungssystem wird kontaktiert  
  - **Then:** Sitzplatz 8A als "reserviert" markiert (nicht verfügbar für andere)  
  - **And:** Economy-Sitzplatz bleibt temporär zugewiesen  
  - **When:** Zahlung erfolgreich innerhalb 120s  
  - **Then:** Reservierung bestätigt → 8A endgültig zugewiesen, Economy-Sitzplatz freigegeben  
  - **When:** Zahlung fehlgeschlagen oder Timeout  
  - **Then:** Reservierung automatisch freigegeben → 8A verfügbar, Economy-Sitzplatz bleibt zugewiesen, Fehlermeldung an Mitarbeiter  
- **Abhängigkeiten:** F-07, N-05, N-07, Zahlungssystem-Schnittstelle  

---

## 4. Nicht-funktionale Anforderungen

#### **N-01: Antwortzeit**
- **ID:** N-01 | **Version:** v2.3  
- **Anforderungsaussage:** Das System MUSS Sitzplatzzuweisungen in maximal 2 Sekunden bestätigen (95. Perzentil bei 50 parallelen Check-ins).  
- **Rationale:** Wartezeiten minimieren.  
- **Priorität:** Must-Have  
- **Testmöglichkeit:** Lasttest mit 50 parallelen Transaktionen, APM-Messung  

---

#### **N-02: Datenkonsistenz bei lokalen Operationen**
- **ID:** N-02 | **Version:** v2.3  
- **Anforderungsaussage:** Das System MUSS bei parallelen Sitzplatzzuweisungen innerhalb derselben Datenbank-Instanz Doppelbuchungen durch ACID-Transaktionen verhindern. Dies gilt für Operationen, die keine externen Systeme einbeziehen (F-01, F-02, F-03, F-05, F-06).  
- **Rationale:** Datenintegrität bei lokalen Operationen; strikte Konsistenz ohne verteilte Koordination.  
- **Priorität:** Must-Have  
- **Testmöglichkeit:** 100 parallele Zuweisungen desselben Sitzplatzes innerhalb lokaler Datenbank → 0 Doppelbuchungen, Validierung mit Isolation Level SERIALIZABLE oder REPEATABLE READ  
- **Abhängigkeiten:** Datenbank-Transaktionsmanagement  

---

#### **N-03: Benutzerfreundlichkeit**
- **ID:** N-03 | **Version:** v2.3  
- **Anforderungsaussage:** Das System MUSS eine Sitzplatzzuweisung in maximal 30 Sekunden ermöglichen (inkl. Passagiersuche).  
- **Rationale:** Effizienz am Schalter.  
- **Priorität:** Should-Have  
- **Testmöglichkeit:** Usability-Test mit 10 Probanden, Zeitmessung  

---

#### **N-04: Datensicherheit**
- **ID:** N-04 | **Version:** v2.3  
- **Anforderungsaussage:** Das System MUSS Passagierdaten verschlüsselt übertragen und speichern (TLS 1.2+, AES-256 oder vergleichbar).  
- **Rationale:** DSGVO-Konformität.  
- **Priorität:** Must-Have  
- **Testmöglichkeit:** Security-Audit, Penetrationstest  

---

#### **N-05: Sitzplan-Visualisierung**
- **ID:** N-05 | **Version:** v2.3  
- **Anforderungsaussage:** Das System MUSS eine grafische Sitzplan-Ansicht bereitstellen, die verfügbare, belegte, blockierte und temporär reservierte Sitzplätze farblich unterscheidet (grün = verfügbar, rot = belegt, grau = blockiert, gelb = reserviert).  
- **Rationale:** Zentrale UI-Komponente für manuelle Auswahl (F-02), Änderung (F-05), Doppelzuweisungsprävention (F-03) und Reservierungsstatus (F-08).  
- **Priorität:** Must-Have  
- **Testmöglichkeit:** UI-Test mit Mockdaten, Farbkontrast-Validierung, Usability-Test  

---

#### **N-06: Zugriffskontrolle (RBAC)**
- **ID:** N-06 | **Version:** v2.3  
- **Anforderungsaussage:** Das System MUSS sicherstellen, dass Aktionen nur von autorisierten Benutzern durchgeführt werden können, basierend auf einem Rollenmodell (Role-Based Access Control). Upgrades (F-07) erfordern die Rolle "Supervisor".  
- **Rationale:** Schutz vor unbefugten Aktionen und Datenmanipulation (Segregation of Duties).  
- **Priorität:** Must-Have  
- **Akzeptanzkriterien:**  
  - **Given:** Benutzer mit Rolle "CheckInAgent" angemeldet  
  - **When:** Benutzer versucht, kostenpflichtiges Upgrade (F-07) durchzuführen  
  - **Then:** System verweigert Aktion mit Fehlermeldung "Supervisor-Autorisierung erforderlich"  
- **Testmöglichkeit:** Rollen-basierter Integrationstest, Negativtests für unberechtigte Zugriffe  

---

#### **N-07: Eventuelle Konsistenz bei verteilten Upgrade-Transaktionen**
- **ID:** N-07 | **Version:** v2.3  
- **Anforderungsaussage:** Das System MUSS bei Upgrade-Operationen mit externen Zahlungssystemen (F-08) eventuelle Konsistenz (BASE-Modell) über ein Saga-Pattern mit Kompensationslogik sicherstellen. Die maximale Inkonsistenzzeit (zwischen Reservierung und finaler Zuweisung oder Rollback) DARF 120 Sekunden (TTL) nicht überschreiten.  
- **Rationale:** Technische Unmöglichkeit von ACID-Transaktionen über verteilte Systeme (Zahlungssystem, Sitzplatzdatenbank); Saga-Pattern mit TTL-basierter Kompensation bietet akzeptable Konsistenz für das Geschäftsszenario.  
- **Priorität:** Must-Have  
- **Testmöglichkeit:**  
  - **Erfolgsfall:** Reservierung → Zahlung erfolgreich → Commit innerhalb 120s → finale Konsistenz erreicht  
  - **Fehlschlag:** Reservierung → Zahlung fehlgeschlagen → Rollback innerhalb 120s → ursprünglicher Zustand wiederhergestellt  
  - **Timeout:** Reservierung → keine Zahlungsantwort nach 120s → automatischer Rollback → ursprünglicher Zustand wiederhergestellt  
  - **Parallelität:** 10 parallele Upgrade-Versuche auf denselben Sitzplatz → 1 erfolgreich, 9 erhalten "bereits reserviert"-Fehler  
- **Abhängigkeiten:** F-08, Zahlungssystem-Schnittstelle, Scheduler/Timer-Mechanismus  

---

## 5. Traceability-Matrix

| **ID** | **Use Case** | **Transaktionsmodell** | **Abhängigkeiten**                     |
|--------|--------------|------------------------|----------------------------------------|
| F-01   | Zuweisung    | ACID (lokal)           | Buchungssystem-API, N-02               |
| F-02   | Zuweisung    | ACID (lokal)           | F-01, N-05, N-02                       |
| F-03   | Zuweisung    | ACID (lokal)           | N-05, N-02                             |
| F-04   | Zuweisung    | ACID (lokal)           | F-01, N-02                             |
| F-05   | Änderung     | ACID (lokal)           | F-03, N-05, N-02                       |
| F-06   | Änderung     | ACID (lokal)           | F-05, N-02                             |
| F-07   | Upgrade      | BASE (verteilt)        | N-05, F-06, F-08, N-06, N-07, Zahlungssystem-API |
| F-08   | Upgrade      | BASE (Saga)            | F-07, N-05, N-07, Zahlungssystem-API   |

---

## 6. SOPHIST-Schablonen (Anwendung)

**Funktional:** `<System> <MUSS/SOLL/KANN> <Stakeholder> <Aktion> <Objekt> <Bedingung>`  
**Nicht-funktional:** `<System> MUSS <Metrik> <unter Last> einhalten`  

**Beispiel N-07 (Eventuelle Konsistenz):**  
- System: "Das System"  
- MUSS  
- Metrik: "eventuelle Konsistenz (BASE-Modell) über Saga-Pattern"  
- Bedingung: "bei Upgrade-Operationen mit externen Zahlungssystemen, maximale Inkonsistenzzeit ≤ 120s"  

---

## 7. Vollständige SOPHIST-Schablonen aller Anforderungen

### Funktionale Anforderungen

#### F-01: Automatische Sitzplatzzuweisung

```
┌─────────────────────────────────────────────────────────────────────────┐
│ F-01: Automatische Sitzplatzzuweisung                   Version: v2.3   │
├─────────────────────────────────────────────────────────────────────────┤
│ Anforderung: Das System MUSS beim Check-in einem Passagier automatisch  │
│              einen verfügbaren Sitzplatz der gebuchten Klasse zuweisen, │
│              wenn kein Sitzplatz vorab zugewiesen wurde und keine       │
│              manuelle Auswahl erfolgt.                                  │
│ Rationale:   Schneller Check-in-Prozess.                                │
│ Priorität:   Must-Have                                                  │
│ Akzeptanz:   Given: Passagier mit Economy-Buchung ohne Sitzplatz        │
│              When:  Check-in abgeschlossen                              │
│              Then:  System weist Economy-Sitzplatz zu und zeigt ihn     │
│                     auf Bordkarte                                       │
│ Abhängig:    Buchungssystem-API, N-02 (ACID-Transaktionen)              │
└─────────────────────────────────────────────────────────────────────────┘
```

---

#### F-02: Manuelle Sitzplatzauswahl

```
┌─────────────────────────────────────────────────────────────────────────┐
│ F-02: Manuelle Sitzplatzauswahl                         Version: v2.3   │
├─────────────────────────────────────────────────────────────────────────┤
│ Anforderung: Das System MUSS dem Check-in-Mitarbeiter ermöglichen,      │
│              aus verfügbaren Sitzplätzen der gebuchten Klasse manuell   │
│              einen Sitzplatz auszuwählen.                               │
│ Rationale:   Passagierwünsche (Gangplatz, Fenster).                     │
│ Priorität:   Must-Have                                                  │
│ Akzeptanz:   Given: Sitzplatzauswahl-Ansicht geöffnet                   │
│              When:  Mitarbeiter wählt Sitzplatz aus                     │
│              Then:  Sitzplatz zugewiesen, Bordkarte aktualisiert        │
│ Abhängig:    F-01 (Automatische Zuweisung),                             │
│              N-05 (Sitzplan-Visualisierung),                            │
│              N-02 (ACID-Transaktionen)                                  │
└─────────────────────────────────────────────────────────────────────────┘
```

---

#### F-03: Verhinderung von Doppelzuweisungen

```
┌─────────────────────────────────────────────────────────────────────────┐
│ F-03: Verhinderung von Doppelzuweisungen               Version: v2.3    │
├─────────────────────────────────────────────────────────────────────────┤
│ Anforderung: Das System MUSS bereits zugewiesene Sitzplätze als nicht   │
│              verfügbar markieren.                                       │
│ Rationale:   Konfliktvermeidung.                                        │
│ Priorität:   Must-Have                                                  │
│ Akzeptanz:   Given: Sitzplatz bereits zugewiesen                        │
│              When:  Sitzplatzauswahl geöffnet                           │
│              Then:  Besetzter Sitzplatz nicht auswählbar                │
│ Abhängig:    N-05 (Sitzplan-Visualisierung),                            │
│              N-02 (ACID-Transaktionen)                                  │
└─────────────────────────────────────────────────────────────────────────┘
```

---

#### F-04: Check-in ohne Sitzplatz (Überbuchung)

```
┌─────────────────────────────────────────────────────────────────────────┐
│ F-04: Check-in ohne Sitzplatz (Überbuchung)            Version: v2.3    │
├─────────────────────────────────────────────────────────────────────────┤
│ Anforderung: Das System MUSS Check-in ohne Sitzplatzzuweisung           │
│              ermöglichen, wenn keine Sitzplätze verfügbar sind.         │
│              Bordkarte erhält Vermerk "Sitzplatz am Gate".              │
│ Rationale:   Handhabung von Überbuchung.                                │
│ Priorität:   Must-Have                                                  │
│ Akzeptanz:   Given: Keine Sitzplätze verfügbar                          │
│              When:  Check-in bestätigt                                  │
│              Then:  Bordkarte mit "Sitzplatz am Gate" erstellt          │
│              And:   Passagier als "eingecheckt ohne Sitzplatz" markiert │
│ Abhängig:    F-01 (Automatische Sitzplatzzuweisung),                    │
│              N-02 (ACID-Transaktionen)                                  │
└─────────────────────────────────────────────────────────────────────────┘
```

---

#### F-05: Sitzplatzänderung innerhalb Klasse

```
┌─────────────────────────────────────────────────────────────────────────┐
│ F-05: Sitzplatzänderung innerhalb Klasse               Version: v2.3    │
├─────────────────────────────────────────────────────────────────────────┤
│ Anforderung: Das System MUSS dem Check-in-Mitarbeiter ermöglichen,      │
│              einen zugewiesenen Sitzplatz durch einen anderen           │
│              verfügbaren Sitzplatz derselben Klasse zu ersetzen.        │
│ Rationale:   Nachträgliche Wünsche.                                     │
│ Priorität:   Must-Have                                                  │
│ Akzeptanz:   Given: Passagier mit Sitzplatz 12A                         │
│              When:  Mitarbeiter wählt Sitzplatz 15C                     │
│              Then:  12A freigegeben, 15C zugewiesen,                    │
│                     Bordkarte aktualisiert                              │
│ Abhängig:    F-03 (Verhinderung von Doppelzuweisungen),                 │
│              N-05 (Sitzplan-Visualisierung),                            │
│              N-02 (ACID-Transaktionen)                                  │
└─────────────────────────────────────────────────────────────────────────┘
```

---

#### F-06: Freigabe vorheriger Sitzplatz

```
┌─────────────────────────────────────────────────────────────────────────┐
│ F-06: Freigabe vorheriger Sitzplatz                    Version: v2.3    │
├─────────────────────────────────────────────────────────────────────────┤
│ Anforderung: Das System MUSS den vorherigen Sitzplatz automatisch       │
│              freigeben, sobald ein neuer Sitzplatz zugewiesen wurde.    │
│ Rationale:   Ressourceneffizienz.                                       │
│ Priorität:   Must-Have                                                  │
│ Akzeptanz:   Given: Sitzplatzwechsel von 12A zu 15C                     │
│              When:  Neue Zuweisung bestätigt                            │
│              Then:  12A als verfügbar markiert                          │
│ Abhängig:    F-05 (Sitzplatzänderung innerhalb Klasse),                 │
│              N-02 (ACID-Transaktionen)                                  │
└─────────────────────────────────────────────────────────────────────────┘
```

---

#### F-07: Upgrade zwischen Klassen

```
┌─────────────────────────────────────────────────────────────────────────┐
│ F-07: Upgrade zwischen Klassen                         Version: v2.3    │
├─────────────────────────────────────────────────────────────────────────┤
│ Anforderung: Das System MUSS einem autorisierten Check-in-Mitarbeiter   │
│              ermöglichen, für einen Passagier ein Upgrade in eine höhere│
│              Klasse gegen Bezahlung durchzuführen (Economy → Premium    │
│              Economy, Premium Economy → Business), wenn Sitzplätze      │
│              verfügbar sind.                                            │
│ Rationale:   Umsatzsteigerung, Kundenzufriedenheit.                     │
│ Priorität:   Must-Have                                                  │
│ Akzeptanz:   Given: Economy-Passagier, Premium Economy-Sitzplätze       │
│                     verfügbar                                           │
│              And:   Mitarbeiter ist als "Supervisor" autorisiert        │
│                     (siehe N-06)                                        │
│              When:  Mitarbeiter wählt Premium Economy-Upgrade           │
│              And:   Bezahlung wird erfolgreich autorisiert (siehe F-08) │
│              Then:  Premium Economy-Sitzplatz zugewiesen                │
│              And:   Economy-Sitzplatz freigegeben (siehe F-06)          │
│              And:   Bordkarte mit neuer Klasse aktualisiert             │
│ Abhängig:    N-05 (Sitzplan-Visualisierung),                            │
│              F-06 (Freigabe vorheriger Sitzplatz),                      │
│              F-08 (Reserve-Payment-Confirm Flow),                       │
│              N-06 (Zugriffskontrolle),                                  │
│              N-07 (Eventuelle Konsistenz bei verteilten Transaktionen), │
│              Zahlungssystem-Schnittstelle                               │
└─────────────────────────────────────────────────────────────────────────┘
```

---

#### F-08: Reserve-Payment-Confirm Flow für Upgrades

```
┌─────────────────────────────────────────────────────────────────────────┐
│ F-08: Reserve-Payment-Confirm Flow für Upgrades        Version: v2.3    │
├─────────────────────────────────────────────────────────────────────────┤
│ Anforderung: Das System MUSS bei Upgrades (F-07) den Ziel-Sitzplatz     │
│              temporär reservieren (Time-To-Live: 120 Sekunden), die     │
│              Bezahlung initiieren, und bei erfolgreicher Zahlung die    │
│              Reservierung bestätigen. Bei Zahlungsfehlschlag oder       │
│              Timeout MUSS das System die Reservierung automatisch       │
│              freigeben und den ursprünglichen Sitzplatz wiederherstellen│
│ Rationale:   Verhinderung von Race Conditions bei Zahlungen;            │
│              Kompensation bei asynchronen Zahlungsfehlern.              │
│ Priorität:   Must-Have                                                  │
│ Akzeptanz:   Given: Mitarbeiter initiiert Premium Economy-Upgrade für   │
│                     Economy-Passagier                                   │
│              When:  System reserviert Premium Economy-Sitzplatz 8A      │
│                     (TTL: 120s)                                         │
│              And:   Zahlungssystem wird kontaktiert                     │
│              Then:  Sitzplatz 8A als "reserviert" markiert (nicht       │
│                     verfügbar für andere)                               │
│              And:   Economy-Sitzplatz bleibt temporär zugewiesen        │
│              When:  Zahlung erfolgreich innerhalb 120s                  │
│              Then:  Reservierung bestätigt → 8A endgültig zugewiesen,   │
│                     Economy-Sitzplatz freigegeben                       │
│              When:  Zahlung fehlgeschlagen oder Timeout                 │
│              Then:  Reservierung automatisch freigegeben → 8A verfügbar,│
│                     Economy-Sitzplatz bleibt zugewiesen,                │
│                     Fehlermeldung an Mitarbeiter                        │
│ Abhängig:    F-07 (Upgrade zwischen Klassen),                           │
│              N-05 (Sitzplan-Visualisierung - Reservierungsstatus),      │
│              N-07 (Eventuelle Konsistenz bei verteilten Transaktionen), │
│              Zahlungssystem-Schnittstelle                               │
└─────────────────────────────────────────────────────────────────────────┘
```

---

### Nicht-funktionale Anforderungen

#### N-01: Antwortzeit

```
┌─────────────────────────────────────────────────────────────────────────┐
│ N-01: Antwortzeit                                       Version: v2.3   │
├─────────────────────────────────────────────────────────────────────────┤
│ Anforderung: Das System MUSS Sitzplatzzuweisungen in maximal 2 Sekunden │
│              bestätigen (95. Perzentil bei 50 parallelen Check-ins).    │
│ Rationale:   Wartezeiten minimieren.                                    │
│ Priorität:   Must-Have                                                  │
│ Metrik:      - Durchschnittliche Antwortzeit ≤ 1,5 Sek.                 │
│              - 95. Perzentil ≤ 2,0 Sek.                                 │
│              - 99. Perzentil ≤ 3,0 Sek.                                 │
│              - Messung bei 50 parallelen Check-ins                      │
│ Test:        Lasttest mit JMeter/Gatling, 50 parallele Transaktionen,   │
│              APM-Messung über 1000 Transaktionen                        │
│ Abhängig:    Infrastruktur, Datenbank-Performance                       │
└─────────────────────────────────────────────────────────────────────────┘
```

---

#### N-02: Datenkonsistenz bei lokalen Operationen

```
┌─────────────────────────────────────────────────────────────────────────┐
│ N-02: Datenkonsistenz bei lokalen Operationen          Version: v2.3    │
├─────────────────────────────────────────────────────────────────────────┤
│ Anforderung: Das System MUSS bei parallelen Sitzplatzzuweisungen        │
│              innerhalb derselben Datenbank-Instanz Doppelbuchungen durch│
│              ACID-Transaktionen verhindern. Dies gilt für Operationen,  │
│              die keine externen Systeme einbeziehen (F-01, F-02, F-03,  │
│              F-05, F-06).                                               │
│ Rationale:   Datenintegrität bei lokalen Operationen; strikte Konsistenz│
│              ohne verteilte Koordination.                               │
│ Priorität:   Must-Have                                                  │
│ Metrik:      0 Doppelzuweisungen bei 100 parallelen Transaktionen       │
│ Test:        Parallelitätstest mit 100 gleichzeitigen Zuweisungen       │
│              desselben Sitzplatzes innerhalb lokaler Datenbank,         │
│              Validierung mit Isolation Level SERIALIZABLE oder          │
│              REPEATABLE READ, keine Doppelbuchung                       │
│ Abhängig:    Datenbank-Transaktionsmanagement                           │
└─────────────────────────────────────────────────────────────────────────┘
```

---

#### N-03: Benutzerfreundlichkeit

```
┌─────────────────────────────────────────────────────────────────────────┐
│ N-03: Benutzerfreundlichkeit                           Version: v2.3    │
├─────────────────────────────────────────────────────────────────────────┤
│ Anforderung: Das System MUSS eine Sitzplatzzuweisung in maximal         │
│              30 Sekunden ermöglichen (inkl. Passagiersuche).            │
│ Rationale:   Effizienz am Schalter.                                     │
│ Priorität:   Should-Have                                                │
│ Metrik:      - Durchschnittliche Task-Zeit ≤ 30 Sek.                    │
│              - System Usability Scale (SUS) Score ≥ 75                  │
│ Test:        Usability-Test mit 10 geschulten Probanden, Zeitmessung,   │
│              SUS-Fragebogen, Standard-Arbeitsplatzbedingungen           │
│              (Netzwerk-Latenz < 100 ms)                                 │
│ Abhängig:    UI/UX-Design                                               │
└─────────────────────────────────────────────────────────────────────────┘
```

---

#### N-04: Datensicherheit

```
┌─────────────────────────────────────────────────────────────────────────┐
│ N-04: Datensicherheit                                   Version: v2.3   │
├─────────────────────────────────────────────────────────────────────────┤
│ Anforderung: Das System MUSS Passagierdaten verschlüsselt übertragen    │
│              und speichern (TLS 1.2+ für Transport, AES-256 für         │
│              Speicherung oder vergleichbar).                            │
│ Rationale:   DSGVO-Konformität.                                         │
│ Priorität:   Must-Have                                                  │
│ Metrik:      - 100 % der Datenübertragungen verschlüsselt               │
│              - 100 % der gespeicherten personenbezogenen Daten          │
│                verschlüsselt                                            │
│ Test:        Security-Audit, Penetrationstest, Code-Review,             │
│              Netzwerk-Traffic-Analyse                                   │
│ Abhängig:    Infrastruktur, Datenbank-Konfiguration                     │
└─────────────────────────────────────────────────────────────────────────┘
```

---

#### N-05: Sitzplan-Visualisierung

```
┌─────────────────────────────────────────────────────────────────────────┐
│ N-05: Sitzplan-Visualisierung                          Version: v2.3    │
├─────────────────────────────────────────────────────────────────────────┤
│ Anforderung: Das System MUSS eine grafische Sitzplan-Ansicht            │
│              bereitstellen, die verfügbare, belegte, blockierte und     │
│              temporär reservierte Sitzplätze farblich unterscheidet     │
│              (grün = verfügbar, rot = belegt, grau = blockiert,         │
│              gelb = reserviert).                                        │
│ Rationale:   Zentrale UI-Komponente für manuelle Auswahl (F-02),        │
│              Änderung (F-05), Doppelzuweisungsprävention (F-03) und     │
│              Reservierungsstatus (F-08).                                │
│ Priorität:   Must-Have                                                  │
│ Metrik:      - Farbkontrast nach WCAG 2.1 AA                            │
│              - Renderzeit < 500 ms für vollständigen Sitzplan           │
│              - Aktualisierung Reservierungsstatus < 200 ms              │
│ Test:        UI-Test mit Mockdaten, Farbkontrast-Validierung,           │
│              Usability-Test, Performance-Test, Reservierungs-Lifecycle- │
│              Test                                                       │
│ Abhängig:    UI/UX-Design                                               │
└─────────────────────────────────────────────────────────────────────────┘
```

---

#### N-06: Zugriffskontrolle (RBAC)

```
┌─────────────────────────────────────────────────────────────────────────┐
│ N-06: Zugriffskontrolle (RBAC)                         Version: v2.3    │
├─────────────────────────────────────────────────────────────────────────┤
│ Anforderung: Das System MUSS sicherstellen, dass Aktionen nur von       │
│              autorisierten Benutzern durchgeführt werden können,        │
│              basierend auf einem Rollenmodell (Role-Based Access        │
│              Control). Upgrades (F-07) erfordern die Rolle "Supervisor".│
│ Rationale:   Schutz vor unbefugten Aktionen und Datenmanipulation       │
│              (Segregation of Duties).                                   │
│ Priorität:   Must-Have                                                  │
│ Akzeptanz:   Given: Benutzer mit Rolle "CheckInAgent" angemeldet        │
│              When:  Benutzer versucht, kostenpflichtiges Upgrade (F-07) │
│                     durchzuführen                                       │
│              Then:  System verweigert Aktion mit Fehlermeldung          │
│                     "Supervisor-Autorisierung erforderlich"             │
│ Test:        Rollen-basierter Integrationstest, Negativtests für        │
│              unberechtigte Zugriffe                                     │
│ Abhängig:    Authentifizierungs-/Autorisierungssystem                   │
└─────────────────────────────────────────────────────────────────────────┘
```

---

#### N-07: Eventuelle Konsistenz bei verteilten Upgrade-Transaktionen

```
┌─────────────────────────────────────────────────────────────────────────┐
│ N-07: Eventuelle Konsistenz bei verteilten Upgrade-    Version: v2.3    │
│       Transaktionen                                                     │
├─────────────────────────────────────────────────────────────────────────┤
│ Anforderung: Das System MUSS bei Upgrade-Operationen mit externen       │
│              Zahlungssystemen (F-08) eventuelle Konsistenz (BASE-Modell)│
│              über ein Saga-Pattern mit Kompensationslogik sicherstellen.│
│              Die maximale Inkonsistenzzeit (zwischen Reservierung und   │
│              finaler Zuweisung oder Rollback) DARF 120 Sekunden (TTL)   │
│              nicht überschreiten.                                       │
│ Rationale:   Technische Unmöglichkeit von ACID-Transaktionen über       │
│              verteilte Systeme (Zahlungssystem, Sitzplatzdatenbank);    │
│              Saga-Pattern mit TTL-basierter Kompensation bietet         │
│              akzeptable Konsistenz für das Geschäftsszenario.           │
│ Priorität:   Must-Have                                                  │
│ Metrik:      - Maximale Inkonsistenzzeit ≤ 120 Sekunden                 │
│              - Erfolgsrate Rollback bei Zahlungsfehlern: 100 %          │
│              - Erfolgsrate Timeout-Rollback: 100 %                      │
│ Test:        - Erfolgsfall: Reservierung → Zahlung erfolgreich →        │
│                Commit innerhalb 120s → finale Konsistenz erreicht       │
│              - Fehlschlag: Reservierung → Zahlung fehlgeschlagen →      │
│                Rollback innerhalb 120s → ursprünglicher Zustand         │
│                wiederhergestellt                                        │
│              - Timeout: Reservierung → keine Zahlungsantwort nach 120s  │
│                → automatischer Rollback → ursprünglicher Zustand        │
│                wiederhergestellt                                        │
│              - Parallelität: 10 parallele Upgrade-Versuche auf denselben│
│                Sitzplatz → 1 erfolgreich, 9 erhalten "bereits           │
│                reserviert"-Fehler                                       │
│ Abhängig:    F-08 (Reserve-Payment-Confirm Flow),                       │
│              Zahlungssystem-Schnittstelle,                              │
│              Scheduler/Timer-Mechanismus                                │
└─────────────────────────────────────────────────────────────────────────┘
```

---