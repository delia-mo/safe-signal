# Scanner-Spezifikation

## Zweck

Der Scanner arbeitet **lokal auf dem Gerät** und prüft Hinweise (Indikatoren), die häufig in Zusammenhang mit missbräuchlicher Nutzung von Gerätefunktionen stehen.

Wichtig:
- Der Scanner liefert **Heuristiken**, keine Beweise.
- Es sind **False Positives** und **False Negatives** möglich.
- Ergebnisse müssen immer im Kontext interpretiert werden.

## Scope des MVP

Der MVP-Scanner prüft insbesondere:

1. **Accessibility / Bedienungshilfen**
2. **Geräteadministrator-Rechte**
3. **Standort aktiv**
4. **Background Location (Android 10+)**
5. **Root-Indikatoren / Root-Manager-Apps (heuristisch)**

## Ergebnisformat (konzeptionell)

Ein Scan erzeugt eine Liste von `Findings`.

Jedes Finding enthält mindestens:
- `id`
- `type`
- `title`
- `description`
- `severity` (`LOW`, `MED`, `HIGH`)
- optional `evidence` (z. B. Dienstname, Paketname)
- optional `recommendedAction` / `flowSpecRef`

## Checks im Detail

---

## 1) Accessibility / Bedienungshilfen

### Ziel
Prüfen, ob Accessibility-Dienste aktiv sind und welche Dienste aktiv sind.

### Warum relevant?
Accessibility ist eine legitime Android-Funktion, kann aber missbraucht werden, um:
- Eingaben mitzulesen
- UI zu beobachten
- Aktionen automatisiert auszuführen

### Scanner-Verhalten
- Ermittelt, ob Accessibility global aktiv ist
- Listet aktive Accessibility-Dienste
- Erstellt Findings je nach Zustand (z. B. aktiv / verdächtige Dienste manuell prüfen)

### Mögliche Findings
- **HIGH**: Accessibility aktiv

### False Positives
- Viele legitime Apps nutzen Accessibility

### False Negatives
- OEM-spezifische Darstellungen / eingeschränkte Lesbarkeit von Details

---

## 2) Geräteadministrator-Rechte

### Ziel
Prüfen, ob Geräteadministrator-Rechte aktiv sind und welche Einträge aktiv sind.

### Warum relevant?
Geräteadministrator-Rechte können missbraucht werden, um:
- Deinstallation zu erschweren
- Gerätesteuerung zu erweitern
- Schutzmechanismen zu umgehen

### Scanner-Verhalten
- Prüft Status der Geräteadministrator-Funktion
- Ermittelt aktive Admin-Komponenten / Einträge
- Erzeugt Findings inkl. Hinweis auf manuelle Prüfung

### Mögliche Findings
- **HIGH**: Aktive Geräteadministrator-Einträge

### False Positives
- Unternehmensgeräte / MDM-Lösungen
- legitime Sicherheits- oder Device-Management-Apps

### False Negatives
- eingeschränkte Sichtbarkeit einzelner Komponenten je Android-Version/OEM

---

## 3) Standort aktiv

### Ziel
Prüfen, ob Standortdienste auf dem Gerät aktiv sind.

### Warum relevant?
Aktiver Standort allein ist nicht verdächtig, aber als Kontextsignal wichtig:
- ermöglicht Standortzugriffe durch Apps (je nach Berechtigung)
- relevant in Kombination mit Findings zu Background Location

### Scanner-Verhalten
- Prüft, ob Standort global aktiviert ist
- erstellt Hinweis-Finding (kontextabhängig meist `LOW`)

### Mögliche Findings
- **LOW**: Standort ist aktiv (Hinweis zur Einordnung)

### False Positives
- Standort ist im Alltag oft legitim aktiv (Maps, Wetter, Gerätefunktionen)

### False Negatives
- keine relevanten (Status i. d. R. zuverlässig lesbar), aber OEM-/UI-Darstellung kann variieren

---

## 4) Background Location (Android 10+)

### Ziel
Apps identifizieren, die Standortzugriff „immer“ / im Hintergrund haben.

### Warum relevant?
Hintergrund-Standortzugriff kann missbraucht werden für:
- kontinuierliches Tracking
- Bewegungsprofile

### Scanner-Verhalten
- prüft auf Android 10+ Apps mit Hintergrund-Standortberechtigung
- listet betroffene Apps
- erzeugt pro App oder gesammelt Findings (Implementationsentscheidung)

### Mögliche Findings
- **MED**: App mit Background Location (insbesondere unbekannte App)

### False Positives
- legitime Apps: Navigation, Familienfreigabe, Sicherheits-Apps, Fitness-Tracker

### False Negatives
- Android-API-/OEM-Einschränkungen bei Berechtigungsauflösung
- App nutzt andere Signale statt Standort

---

## 5) Root-Indikatoren / Root-Manager-Apps (heuristisch)

### Ziel
Hinweise auf ein gerootetes Gerät oder Root-Management erkennen.

### Warum relevant?
Root kann legitime Gründe haben, erhöht aber das Missbrauchsrisiko:
- tiefere Systemeingriffe möglich
- Schutzmechanismen können umgangen werden

### Scanner-Verhalten (heuristisch)
- Prüft bekannte Root-Indikatoren (Dateien/Binaries/Umgebung)
- Prüft auf bekannte Root-Manager-Apps (Paketnamen/Apps, heuristisch)
- kombiniert Signale zu einem oder mehreren Findings

### Mögliche Findings
- **HIGH**: starke Root-Indikatoren / Root-Manager erkannt
- **MED**: schwache oder uneindeutige Root-Hinweise

### False Positives
- Custom ROMs, Entwicklergeräte, legitime Root-Setups
- veraltete Root-Spuren ohne aktive Root-Nutzung

### False Negatives
- verschleierte Root-Lösungen
- Umgehungen von Standardindikatoren
- bewusst versteckte Root-Manager

## Severity-Regeln

Severity ist eine **Priorisierungshilfe**, kein Urteil.

### HIGH
Für Hinweise mit erhöhtem Risiko-/Missbrauchspotenzial, z. B.:
- unerwartete Geräteadministrator-Rechte
- starke Root-Indikatoren
- unbekannte App mit Background Location + weiterer Auffälligkeit

### MED
Für relevante, aber kontextabhängige Hinweise:
- aktive Accessibility-Dienste
- einzelne Background-Location-Apps
- uneindeutige Root-Hinweise

### LOW
Für Kontext-/Informationshinweise:
- Standort global aktiv
- allgemeine Hinweise ohne klare Missbrauchsanzeichen

## Priorisierung der Findings

Die UI zeigt „Wichtigstes zuerst“:
1. Severity HIGH
2. Severity MED
3. Severity LOW

Zusätzlich möglich:
- betroffene App-Namen anzeigen, damit Nutzer:innen gezielt prüfen können

## Re-Scan-Verhalten

Nach einer Maßnahme (Guided Flow) sollte ein Re-Scan ausgelöst werden, um:
- Statusänderungen sichtbar zu machen
- Report zu aktualisieren
- Nutzer:innen direktes Feedback zu geben

## Einschränkungen (wichtig)

- Android-/OEM-Fragmentierung kann Zugriff auf Settings-Deep-Links und Detailinfos beeinflussen
- Nicht alle sicherheitsrelevanten Informationen sind über Android-APIs zugänglich
- Keine Netzwerk-/Traffic-Analyse
- Keine Signaturprüfung / Malware-Erkennung
- Keine forensische Beweissicherung