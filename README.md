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