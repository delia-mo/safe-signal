# SafeSignal (Android): MVP für BA-Projekt

**SafeSignal** ist eine Android-App (Jetpack Compose), die Nutzer:innen dabei unterstützt, **Hinweise auf mögliche Spyware/Dual-Use-Apps** zu erkennen und **sicherere Einstellungen** vorzunehmen.  
Die App arbeitet **lokal auf dem Gerät** und bietet **geführte Maßnahmen-Flows** (z.B. für Geräteadministration und Bedienungshilfen).

**Wichtiger Hinweis (Disclaimer):**  
> Diese App liefert **keine sicheren Beweise** für Überwachung und ersetzt **keine forensische Analyse**.  
> Die Ergebnisse sind **Indikatoren/Heuristiken** und können Fehlalarme oder nicht erkannte Fälle beinhalten.

---

## Features (MVP)

### Safety & UX
- **Safety Gate** vor Scan und vor Maßnahmen (Interlock): erinnert daran, Änderungen nur in sicherer Situation vorzunehmen.
- **Quick Exit**: schließt die App und öffnet eine neutrale Seite im Browser.

### Quick Check
- Kurzer Fragebogen mit Scoring (Low/Medium/High Risiko-Einschätzung).
- Ergebnis-Screen mit „Nächsten Schritten“.

### Scanner (lokal)
Scan prüft **Einstellungen und App-Berechtigungen**, die häufig missbraucht werden:
- **Bedienungshilfen/Accessibility** aktiv + welche Dienste aktiv sind
- **Geräteadministrator-Rechte** aktiv + welche Einträge aktiv sind
- **Standort** aktiv
- **Background Location (Android 10+)**: Apps mit Zugriff auf Standort „im Hintergrund“
- **Root-Indikatoren** + Root-Manager Apps (heuristisch)

### Findings & Priorisierung
- Findings werden als Liste angezeigt (Severity: LOW/MED/HIGH).
- „Wichtigstes zuerst“ + „Mehr anzeigen“ für weniger kritische Hinweise.
- Detailansicht pro Finding.

### Maßnahmen (Guided Removal Flows)
- Klickbarer End-to-End Flow pro Finding:
  - Erklärung → Einstellungen öffnen → ggf. App-Infos öffnen → Re-Scan → Ergebnis
- Flows sind als **FlowSpecs** konfiguriert (wiederverwendbare Steps).

### Report (in-app)
- Report zeigt:
  - Zeitstempel (Baseline/Letzter Scan)
  - Findings (Baseline/aktueller Stand)
  - Status von Maßnahmen (offen/in Bearbeitung/erledigt)
- **Persistenz** via DataStore (Session/Report wird lokal gespeichert)
- **Alles löschen** (Hard delete) verfügbar

---

## Nicht-Ziele / Out of Scope
- Keine Erkennung durch Signaturen, keine Netzwerk-/Traffic-Analyse, keine forensische Beweissicherung.
- Keine automatischen Systemänderungen (die App führt Änderungen nicht selbst aus, sondern leitet in Einstellungen).
- Keine Server- oder Cloud-Komponente.

---

## Tech Stack
- **Kotlin**
- **Jetpack Compose** + **Material 3**
- **Navigation Compose**
- **ViewModel + StateFlow**
- **DataStore (Preferences)** für Persistenz

---

## Architektur (Kurz)
Die App ist als **Layered Architecture + MVVM (Compose)** umgesetzt:

- `ui/` — Compose Screens, Components, Navigation, Action Flows UI
- `presentation/` — ViewModels (StateFlow), UI-State, Orchestrierung
- `domain/` — Modelle & Regeln (Actions, Scoring, Safety Gate Specs)
- `data/` — Android-spezifischer Scanner + Persistenz (DataStore)

Datenfluss: **UI Event → ViewModel → (Scanner/SessionStore) → StateFlow Update → UI Recompose**

---

## Projektstruktur (Auszug)

data/
scanner/ AndroidScanner, Root checks, Policy
session/ SessionStore (DataStore), SessionService, Mappers
domain/
model/ Scan/QuickCheck/Action Models
actions/ NextBestAction, SettingsKind, MeasureItem
quickcheck/ Questions, Scoring
safetygate/ Specs
presentation/
scan/ ScanViewModel
quickcheck/ QuickCheckViewModel
report/ ReportViewModel
ui/
screens/ start, scan, finding, actions, report, safetygate, ...
actions/ flows (specs/steps/components/util)
components/ AppScaffold, Cards, FindingListItem, FooterBar, ...


---

## Build & Run

### Voraussetzungen
- Android Studio (aktuelle stabile Version empfohlen)
- Android SDK (minSdk **26**), Testgerät/Emulator (z.B. Android 11)

### Start
1. Repo klonen
2. In Android Studio öffnen
3. Gradle Sync ausführen
4. App starten (Run)

CLI (optional):
```bash
./gradlew :android:app:assembleDebug
./gradlew :android:app:installDebug
```

# Limitierungen

- Android/OEM Fragmentierung: Settings-Links funktionieren je nach Hersteller/Version unterschiedlich → Fallbacks + Anleitung.
- Einige Informationen sind absichtlich nicht zugänglich (Android Security Model) → Heuristiken statt Beweise.
- Root-Erkennung ist heuristisch und umgehbar.
- System-App-Komponenten (z.B. Google Play Services) bündeln mehrere Funktionen → nicht immer eindeutig als einzelne App ansteuerbar.
