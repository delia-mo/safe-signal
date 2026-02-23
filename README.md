# SafeSignal

SafeSignal ist ein Android-MVP zur **lokalen, heuristischen Prüfung** sicherheitsrelevanter Geräteeinstellungen (z. B. Accessibility, Geräteadministrator, Standort/Berechtigungen) und bietet **geführte Maßnahmen (Guided Flows)** zur manuellen Überprüfung.

> **Wichtig:** SafeSignal liefert Hinweise (Heuristiken), keine forensischen Beweise.

---

## Features (MVP)

- Sicherheits-Check auf dem Gerät (lokal)
- Heuristische Findings mit Priorisierung (LOW / MED / HIGH)
- Guided Measures / Action Flows für manuelle Prüfung
- Quick Check (kurzer Risiko-Check)
- Safety Gate & Quick Exit
- Lokale Speicherung (keine Cloud)

---

## Dokumentation

Ausführliche Doku findest du im [`docs/`](./docs/) Ordner:

- [Architektur](./docs/architecture.md)
- [Scanner-Spezifikation](./docs/scanner-spec.md)
- [Action Flows](./docs/action-flows.md)
- [Threat Model & Safety](./docs/threat-model-and-safety.md)

---

## Voraussetzungen

- **Android Studio** (aktuelle stabile Version empfohlen)
- **Android SDK** (per Android Studio installiert)
- **USB-Kabel**
- **Android-Gerät** (empfohlen: Android 10+)
- **Entwickleroptionen + USB-Debugging** auf dem Gerät aktiviert

---

## Installation auf Android-Gerät (USB Debug)

### 1) Repository klonen

```bash
git clone <REPO-URL>
cd safe-signal
```
### 2) Projekt in Android Studio öffnen

- Android Studio starten
- Open wählen
- Projektordner auswählen (der Ordner mit settings.gradle.kts)

### 3) USB-Debugging auf dem Android-Gerät aktivieren
Auf dem Gerät:
1. Einstellungen → Über das Telefon
2. Mehrfach auf Build-Nummer tippen, bis Entwickleroptionen aktiviert sind
3. Zurück zu Einstellungen → Entwickleroptionen
4. USB-Debugging aktivieren
Je nach Hersteller können die Menünamen leicht abweichen.

### 4) Gerät per USB verbinden
- Gerät mit dem Rechner verbinden
- Auf dem Handy die Abfrage „USB-Debugging zulassen?“ bestätigen

### 5) Gerät in Android Studio auswählen
- Oben in Android Studio im Device-Dropdown dein Gerät auswählen
- Falls es nicht erscheint:
  - Kabel prüfen (Datenkabel)
  - USB-Modus am Handy prüfen (Dateiübertragung hilft oft)
  - Debugging-Abfrage am Handy erneut bestätigen

### 6) App im Debug-Modus starten
- In Android Studio sicherstellen, dass die ``app``-Konfiguration ausgewählt ist
- Auf Run ▶ klicken
Android Studio installiert die App auf dem Gerät und startet sie im Debug Build.

## Projektstruktur (kurz)
- app/ – Android App-Modul
- docs/ – Projektdokumentation
- gradle/ – Build-Konfiguration
