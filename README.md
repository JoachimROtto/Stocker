# Stocker

Stocker ist eine Anwendung zur Verwaltung und Darstellung von Aktienkursen. Anlass: Programmierpraktikum 2021

## Features

Basierend auf der Auswahl von Aktien aus dem Angebot eines Datenlieferanten (finnhub.io) wird bereitgestellt

- Anzeige aktueller Kurse (Basis: Pushnachrichten per Websocket)
- Darstellung des Kursverlaufs (Basis: Abfrage einer REST-API) mit Forschreibung durch aktuelle Werte
- Anzeige statistischer Kenngrössen (Bollinger Band, gleitender Durchschnitt)
- Alarmfunktionen basierend auf Kenngrössen

## Einschränkungen
Mittlerweile ist der Zugang zu historischen Kursdaten im Free-Account von finnhub nicht mehr inkludiert. Die graphische Darstellung ist also auf zahlende Nutzende beschränkt.
Seit Java 17 ist es Gson nicht mehr möglich auf bestimmte Felder per Reflection zuzugreifen. Das Speichern von Farbwerten ist deshalb nicht mehr möglich.

## Installation
- finnhub-Account anlegen und eine API-Key erstellen. Diesen in den Einstellungen (Kontextmenu Anbieter) eintragen.
- Sourcecode forken oder Herunterladen.
- Benötigte Bibliotheken einbinden (Siehe: - Benötigte Bibliotheken einbinden (Siehe: [Stocker.iml](Stocker.iml))
