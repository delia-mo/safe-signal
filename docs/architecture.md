# Architektur

## Überblick

SafeSignal ist als **Layered Architecture + MVVM** (Jetpack Compose) aufgebaut.

Ziel der Architektur:

- klare Trennung von UI, Logik und Android-spezifischer Implementierung
- gute Testbarkeit von Regeln und State-Logik
- einfache Erweiterbarkeit (weitere Checks, weitere Flows)

## Layer

### 1) `ui/`
Verantwortung:
- Compose Screens
- UI-Komponenten
- Navigation
- Darstellung von Findings, Flows und Report

Beispiele:
- Start / Scan / Finding / Actions / Report / SafetyGate Screens
- wiederverwendbare Cards, List-Items, Footer, Scaffold

### 2) `presentation/`
Verantwortung:
- ViewModels (StateFlow)
- UI-State
- Orchestrierung von User-Events (Scan starten, Flow fortsetzen, Re-Scan, Report aktualisieren)

Beispiele:
- `ScanViewModel`
- `QuickCheckViewModel`
- `ReportViewModel`

### 3) `domain/`
Verantwortung:
- Datenmodelle
- Regeln / Scoring
- Safety-Gate-Definitionen
- Abstrakte Aktions- und Flow-Konzepte

Beispiele:
- Scan-/Finding-Modelle
- QuickCheck-Fragen + Scoring
- Actions / NextBestAction
- Safety Gate Specs

### 4) `data/`
Verantwortung:
- Android-spezifische Scanner-Implementierung
- Persistenz (DataStore)
- Mapping von Android-Rohdaten in Domain-Modelle

Beispiele:
- AndroidScanner
- Root Checks (heuristisch)
- SessionStore / SessionService
- Report-/Session-Mapping

## Datenfluss

Typischer Datenfluss in der App:

1. UI-Event (z. B. „Scan starten“)
2. ViewModel verarbeitet Event
3. ViewModel ruft Scanner / Store an
4. Ergebnis wird in UI-State überführt
5. `StateFlow` sendet Update
6. Compose recomposed UI

Kurzform:

`UI Event -> ViewModel -> (Scanner / SessionStore) -> StateFlow Update -> UI`

## Architekturentscheidungen

### MVVM + StateFlow
- Passt gut zu Jetpack Compose
- UI-State ist explizit und beobachtbar
- UI bleibt „dumm“, Logik liegt im ViewModel

### Layered Architecture
- Android-spezifische Details bleiben im `data/`-Layer
- Fachlogik (Scoring, Priorisierung, Flow-Regeln) ist entkoppelt
- Erleichtert spätere Änderungen (z. B. neue Checks, neue Datenquellen)

### Lokale Persistenz (DataStore)
- Einfach und zuverlässig für MVP
- geeignet für Session-/Report-Status
- unterstützt den Privacy-Ansatz („lokal auf dem Gerät“)

## Zentrale Domänenkonzepte

### Finding
Ein einzelner Hinweis / Befund aus dem Scanner (heuristisch), inkl.:
- Typ/Kategorie
- Beschreibung
- Severity (LOW / MED / HIGH)
- optional betroffene App(s) / Systemsetting(s)

### Quick Check
Kurzer Fragebogen mit Scoring für eine grobe Risiko-Einschätzung:
- Low / Medium / High
- liefert „Nächste Schritte“ als Orientierung

### Guided Measure / Action Flow
Geführter Maßnahmenablauf pro Finding:
- Erklärung
- passende Systemeinstellung öffnen
- ggf. App-Info öffnen
- Re-Scan
- Ergebnis prüfen

### Report / Session
Lokale Ablage von:
- Baseline / letzter Scan
- Findings-Verlauf
- Maßnahmenstatus (offen / in Bearbeitung / erledigt)

## Erweiterungspunkte

### Neue Scanner-Checks hinzufügen
- Android-spezifischen Check in `data/scanner` ergänzen
- Domain-Modell/Mapping ergänzen
- Severity-/Priorisierungsregel definieren
- UI/Finding-Details ergänzen
- optional Guided Flow zuordnen

### Neue Guided Flows hinzufügen
- neuen `FlowSpec` definieren
- Steps zusammenstellen
- passenden Finding-Typ zuordnen
- Re-Scan-/Ergebnislogik integrieren

## Grenzen der Architektur (bewusst)

- Keine forensische Analyse
- Keine Signatur-/Netzwerk-basierte Erkennung
- Keine automatischen Systemänderungen
- Android-/OEM-Fragmentierung erfordert Fallbacks bei Settings-Links