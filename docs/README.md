# Dokumentation

Diese Doku ergänzt die README des Repos und beschreibt Architektur, Scanner-Heuristiken, Guided Measures (Flows) sowie Sicherheits- und Risikoannahmen im Detail.

## Inhalte

- [Architektur](./architecture.md)
- [Scanner-Spezifikation](./scanner-spec.md)
- [Action Flows / Guided Measures](./action-flows.md)
- [Threat Model & Safety](./threat-model-and-safety.md)

## Ziel der Doku

Die App ist ein **MVP** für ein BA-Projekt. Ziel ist eine nachvollziehbare Dokumentation für:

- Entwicklung / Wartung
- Bewertung im Hochschulkontext
- spätere Erweiterung (z. B. weitere Checks / Flows)

## Wichtige Grundsätze

- **Heuristiken statt Beweise**: Die App liefert Indikatoren, keine forensischen Beweise.
- **Lokal statt Cloud**: Keine Server-/Cloud-Komponente.
- **Geführte Maßnahmen statt Automatisierung**: Die App führt Nutzer:innen durch Systemeinstellungen, nimmt aber keine automatischen Systemänderungen vor.