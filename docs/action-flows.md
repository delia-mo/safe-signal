# Action Flows (Guided Measures)

## Zweck

Action Flows führen Nutzer:innen Schritt für Schritt durch eine sichere, nachvollziehbare Maßnahme zu einem Finding.

Beispiele:
- Geräteadministrator prüfen/deaktivieren
- Accessibility-Dienst prüfen/deaktivieren
- App-Info öffnen und Berechtigungen prüfen
- Re-Scan durchführen

Die App nimmt **keine automatischen Systemänderungen** vor, sondern unterstützt die Nutzer:innen bei der manuellen Durchführung.

## Grundprinzip

Ein Flow ist ein konfigurierbarer Ablauf (FlowSpec) aus mehreren Steps.

Typischer Ablauf:

1. Erklärung / Kontext
2. Safety Gate (falls erforderlich)
3. passende Systemeinstellung öffnen
4. ggf. App-Info öffnen
5. Nutzeraktion bestätigen
6. Re-Scan
7. Ergebnis anzeigen

## Warum FlowSpecs?

FlowSpecs machen Maßnahmen:
- wiederverwendbar
- konsistent
- leichter erweiterbar
- besser testbar

Statt Logik in jeder Screen-Komponente zu duplizieren, wird der Ablauf als Spezifikation beschrieben.

## Zentrale Konzepte

### `FlowSpec`
Beschreibt einen kompletten Maßnahmen-Flow:
- `id`
- `title`
- `description`
- Liste von `steps`
- optional Bedingungen / Fallbacks
- optional Zuordnung zu Finding-Typen

### `Step`
Ein einzelner Flow-Schritt, z. B.:
- Erklärung anzeigen
- Setting öffnen
- App-Info öffnen
- Bestätigung abfragen
- Re-Scan auslösen
- Ergebnis anzeigen

### `SettingsKind`
Abstraktion für verschiedene Android-Settings-Ziele, z. B.:
- Accessibility
- Device Admin
- Location
- App Details / App Permissions

### `NextBestAction`
Nächste empfohlene Maßnahme aus einem Finding oder aus dem aktuellen Report-Kontext.

## Beispiel-Step-Typen (konzeptionell)

> Die konkreten Klassen/Typen können im Code anders heißen.

### 1) InfoStep
Zeigt eine Erklärung:
- Warum ist das relevant?
- Was soll geprüft werden?
- Was ist ein legitimer Fall?

### 2) SafetyGateStep
Interlock vor sensitiven Maßnahmen:
- Erinnerung: nur in sicherer Situation fortfahren
- optional Abbruch / später fortsetzen

### 3) OpenSettingsStep
Öffnet eine Android-Systemeinstellung:
- via Intent / Deep Link
- mit Fallbacks bei OEM-/Version-Unterschieden

### 4) OpenAppInfoStep
Öffnet App-Info einer konkreten App:
- Paketname aus Finding/Evidence
- Nutzer:innen können Berechtigungen / Deinstallation / Zwangsstopp prüfen

### 5) ConfirmStep
Bestätigungs-/Fortschrittsschritt:
- „Ich habe geprüft“

### 6) RescanStep
Startet einen erneuten Scan:
- aktualisiert Finding-Status
- speichert Ergebnis im Report/Session

### 7) ResultStep
Zeigt Ergebnis:
- behoben / unverändert / teilweise behoben
- ggf. nächste empfohlene Maßnahme

## Beispiel-Flow: Geräteadministrator prüfen

### Ziel
Aktive Geräteadministrator-Einträge prüfen und ggf. deaktivieren.

### Flow (Beispiel)
1. **SafetyGateStep**  
   Hinweis, Änderungen nur in sicherer Situation vorzunehmen.

2. **InfoStep**  
   Erklärung, was Geräteadministrator-Rechte sind und warum sie relevant sein können.

3. **OpenSettingsStep(DeviceAdmin)**  
   Android-Seite für Geräteadministrator öffnen.

4. **DeinstallationStep**  
   Nutzer:in bestätigt kann gefundene Apps deinstallieren.

5. **Rescan- und ResultStep**  
   Scan erneut ausführen.

## Beispiel-Flow: Accessibility-Dienst prüfen

### Ziel
Aktive Accessibility-Dienste prüfen und ggf. deaktivieren.

### Flow (Beispiel)
1. SafetyGateStep
2. InfoStep (was ist Accessibility / legitime vs. kritische Nutzung)
3. OpenSettingsStep(Accessibility)
4. optional OpenAppInfoStep (wenn betroffene App bekannt)
5. Rescan-/ResultStep

## Fallbacks & OEM-Fragmentierung

Android-Settings-Deep-Links verhalten sich je nach Hersteller/Version unterschiedlich.

Daher sollte jeder OpenSettings-Step:
- Primär-Intent verwenden
- Fallback-Intent(s) haben
- im UI eine manuelle Anleitung anzeigen, falls Öffnen fehlschlägt

Beispiel-Fallback-Strategie:
1. spezifischer Intent
2. allgemeine Settings-Seite
3. textuelle Anleitung („Suche nach …“)

## Fehlerfälle (UX)

Flows sollten robuste UX für Abweichungen bieten:

- **Setting nicht gefunden**
    - alternative Anleitung anzeigen
- **App-Info nicht öffnbar**
    - Paketname anzeigen + manuelle Navigation erklären
- **Nutzer:in bricht ab**
    - Status „in Bearbeitung“ speichern
- **Re-Scan liefert unverändertes Ergebnis**
    - nicht als Fehler labeln, sondern „noch aktiv / nicht geändert“

## Statusmodell für Maßnahmen

Empfohlenes Statusmodell:
- `OPEN`
- `DONE`

Dieser Status fließt in den Report ein.

## Designprinzipien für gute Flows

1. **Kurz und klar**
    - maximal wenige Schritte
    - einfache Sprache

2. **Sicherheitsorientiert**
    - Safety Gate vor sensitiven Schritten
    - neutrale, nicht-alarmistische Formulierungen

3. **Ergebnisorientiert**
    - immer Re-Scan + sichtbares Ergebnis

4. **Fehlertolerant**
    - Fallbacks bei OEM-/Android-Unterschieden

5. **Nachvollziehbar**
    - Schrittstatus im Report sichtbar