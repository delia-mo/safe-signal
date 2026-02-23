# Threat Model & Safety

## Zweck dieses Dokuments

Dieses Dokument beschreibt die Sicherheitsannahmen, Grenzen und UX-Entscheidungen von SafeSignal.

Die App adressiert ein sensibles Nutzungsszenario. Daher sind **technische Grenzen**, **Sicherheitskommunikation** und **risikobewusste UX** zentral.

## Kurzfassung

SafeSignal ist eine lokale Android-App, die Nutzer:innen dabei unterstützt,

- Hinweise auf mögliche missbräuchliche Geräteeinstellungen / App-Berechtigungen zu erkennen
- sichere, manuelle Maßnahmen durchzuführen (guided flows)

SafeSignal ist **kein Forensik-Tool** und liefert **keine Beweise**.

## Zielgruppe (MVP)

Zielgruppe:
- Nutzer:innen, die ihr Gerät auf auffällige Einstellungen / Berechtigungen prüfen möchten
- Beratungs-/Unterstützungskontexte (z. B. Anleitung durch Fachpersonal)

## Annahmen (Threat Model, vereinfacht)

### Was die App sinnvoll unterstützen kann
- Sichtbare Android-Einstellungen und Berechtigungszustände prüfen
- heuristische Hinweise liefern
- Nutzer:innen zu relevanten Settings führen
- Änderungen durch Re-Scan nachvollziehbar machen

### Was die App nicht sicher erkennen kann
- versteckte / hochentwickelte Spyware ohne sichtbare Indikatoren
- Netzwerkbasierte Überwachung (ohne Traffic-Analyse)
- forensische Beweislage
- kompromittierte Systeme außerhalb des App-Scope

## Nicht-Ziele (bewusst)

SafeSignal bietet im MVP **nicht**:
- Signaturbasierte Malware-Erkennung
- Netzwerk-/Traffic-Analyse
- automatische Systemänderungen
- Cloud-/Server-Backend
- forensische Beweissicherung

Diese Abgrenzung ist wichtig, um falsche Erwartungen zu vermeiden.

## Sicherheitsprinzipien in der UX

## 1) Heuristiken statt Gewissheit
Alle Ergebnisse sind als **Hinweise/Indikatoren** formuliert.

Warum?
- Android erlaubt nicht auf alle relevanten Informationen Zugriff
- dieselben Systemfunktionen können legitim oder missbräuchlich genutzt werden
- Fehlalarme und verpasste Fälle sind möglich

Konsequenz für die UX:
- keine definitive Sprache („überwacht“, „Spyware gefunden“)
- stattdessen: „Hinweis“, „prüfen“, „möglicherweise“, „kontextabhängig“

## 2) Safety Gate (Interlock)
Vor Scan und vor sensiblen Maßnahmen wird ein Safety Gate eingeblendet.

Ziel:
- Nutzer:innen daran erinnern, Änderungen nur in einer sicheren Situation vorzunehmen
- impulsive oder riskante Aktionen vermeiden

Beispiele für Hinweise:
- „Nur fortfahren, wenn du dich gerade sicher fühlst“
- „Falls unsicher: später fortsetzen“

## 3) Quick Exit
Die App bietet einen Quick Exit:
- App schließen
- neutrale Seite im Browser öffnen

Ziel:
- schnelle, unauffällige Unterbrechung der Nutzung

## 4) Guided statt automatisch
Die App führt Nutzer:innen durch Schritte, ändert aber nichts automatisch.

Warum?
- technische Einschränkungen (Android-Sicherheitsmodell)
- Nutzerkontrolle bleibt erhalten
- Risiko unbeabsichtigter Änderungen sinkt

## Risiken bei Nutzung (wichtig)

Auch eine Sicherheits-App kann Risiken erzeugen, wenn der Nutzungskontext unsicher ist.

Mögliche Risiken:
- eine andere Person bemerkt die Nutzung
- Änderungen an Einstellungen führen zu Rückfragen/Konflikten
- Nutzer:innen interpretieren Hinweise als Gewissheit

Daher:
- Safety Gate vor kritischen Schritten
- neutrale Formulierungen
- keine Alarm-/Paniksprache

## Datenschutz- und Datensicherheitsprinzipien (MVP)

### Lokal-first
Die App arbeitet lokal auf dem Gerät.

### Lokale Persistenz
Session-/Report-Daten werden lokal gespeichert (DataStore), damit:
- Baseline und letzter Scan vergleichbar sind
- Maßnahmenstatus erhalten bleibt

### Löschbarkeit
„Alles löschen“ (Hard delete) ist verfügbar, um lokal gespeicherte Daten zu entfernen.

## Grenzen & Unsicherheiten (transparent kommunizieren)

### Android-/OEM-Fragmentierung
Settings-Seiten und Deep-Links unterscheiden sich je Hersteller/Version.
Folge:
- Fallbacks nötig
- teils manuelle Navigation erforderlich

### Begrenzte API-Sichtbarkeit
Einige Informationen sind aus Sicherheitsgründen absichtlich nicht auslesbar.
Folge:
- unvollständige Sicht
- Heuristiken statt Vollerkennung

### Root-Erkennung ist heuristisch
Root kann verborgen oder verschleiert sein.
Folge:
- Root-Hinweise sind nie „Beweis“

## Tonalität & UX-Richtlinien

Empfohlene Formulierungen:
- „Hinweis“
- „Bitte prüfen“
- „möglicherweise“
- „kontextabhängig“
- „wenn du dich sicher fühlst“

Vermeiden:
- „Beweis“
- „sicher infiziert“
- „überwacht“
- unnötig alarmierende Sprache

## Zusammenfassung

SafeSignal ist bewusst als:
- **lokales**
- **heuristisches**
- **nutzungsorientiertes**
- **sicherheitsbewusstes**

MVP konzipiert.

Das zentrale Designziel ist nicht „forensische Sicherheit“, sondern:
**niedrigschwellige Unterstützung bei Prüfung und sicherer Konfiguration von Geräte-Einstellungen.**