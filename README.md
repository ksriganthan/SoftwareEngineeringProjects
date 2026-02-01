# Navigation Project  
Software Engineering – Wegfindung im Raum Basel–Allschwil

Dieses Projekt entstand im Rahmen des Moduls **Software Engineering** (FHNW) und befasst sich mit der **Implementierung, Analyse und dem Vergleich klassischer und heuristischer Suchalgorithmen** zur Navigation auf einer selbst erstellten Kartendatenbasis.

Ziel des Projekts ist es, Suchalgorithmen nicht nur theoretisch zu verstehen, sondern deren **praktisches Verhalten, Effizienz und Ergebnisqualität** anhand realitätsnaher Daten zu untersuchen.

---

## Projektüberblick

- Thema: Routenplanung / Graphensuche  
- Gebiet: Raum Basel – Allschwil  
- Datengrundlage: Eigene manuell erstellte Kartendaten  
- Fokus:
  - Vergleich verschiedener Suchalgorithmen
  - Analyse von Laufzeit, Pfadlänge und Suchverhalten
  - Erweiterung klassischer Algorithmen um realitätsnahe Heuristiken

---

## Projektstruktur

Navigation/
- documentation/ – Ausführliche Projektdokumentation
- phase-1/ – Phase 1: Kartendaten erstellen
- phase-2/ – Phase 2: Klassische Suchalgorithmen
- phase-3/ – Phase 3: Erweiterte und angepasste Algorithmen
- material/ – Datensätze und KML-Dateien
- README.md

---

## Phase 1 – Kartendaten erstellen

In der ersten Phase wurde eine **eigene Kartendatenbasis** für einen Teil des Raums Allschwil–Basel erstellt.

Umsetzung:
- Manuelle Erfassung von Nodes (Kreuzungen, Wegpunkte)
- Manuelle Erfassung von Edges (Strassenverbindungen)
- Datengrundlage: map.geo.admin.ch
- Speicherung in Textdateien:
  - nodes.txt – Node-Name und GPS-Koordinaten
  - edges.txt – Startnode, Zielnode, Distanz

Ergebnis:
- 73 Nodes
- 224 Edges
- Aufbau einer Adjazenzliste für die Algorithmen

---

## Phase 2 – Klassische Suchalgorithmen

In Phase 2 wurden klassische Suchverfahren implementiert und miteinander verglichen.

Implementierte Algorithmen:
- Depth-First Search (DFS)
- Breadth-First Search (BFS)
- Best-First Search
- A* Search (distanzbasierte Heuristik)

Untersuchungskriterien:
- Laufzeit
- Anzahl expandierter Knoten
- Pfadlänge (Anzahl Knoten)
- Gesamtdistanz
- Suchverhalten (Breiten- vs. Tiefensuche)

Ziel:
Verständnis der Unterschiede zwischen uninformativer und heuristischer Suche sowie optimaler Pfadfindung.

---

## Phase 3 – Erweiterte und angepasste Algorithmen

In Phase 3 wurde das Projekt um realitätsnahe Aspekte erweitert.

Erweiterungen:
- Dijkstra-Algorithmus als Referenz für optimale Pfade ohne Heuristik
- Beam Search mit begrenztem Suchraum
- A* Search mit neuer Heuristik basierend auf Geschwindigkeitslimiten

A* mit Speed-Limit-Heuristik:
- Optimierung auf Fahrzeit statt Distanz
- Berücksichtigung realer Geschwindigkeitsbegrenzungen (20 / 30 / 50 km/h)
- Ziel: realistischere Routenberechnung

---

## Daten und Material

Der Ordner material/ enthält:
- Node- und Edge-Datensätze
- KML-Dateien zur Visualisierung der berechneten Routen
- Kartendaten zur Reproduzierbarkeit der Ergebnisse

Die KML-Dateien ermöglichen eine visuelle Analyse und den Vergleich der verschiedenen Suchalgorithmen.

---

## Ergebnisse und Erkenntnisse

- DFS und BFS sind sehr schnell, liefern jedoch oft suboptimale Routen
- BFS garantiert kürzeste Pfade gemessen an der Anzahl Kanten
- Best-First Search ist effizient, aber nicht optimal
- A* liefert zuverlässige und optimale Ergebnisse
- Dijkstra dient als Vergleichsstandard ohne Heuristik
- A* mit Speed-Limit-Heuristik erzeugt realistischere Routen bezüglich Fahrzeit
- Beam Search reduziert Rechenaufwand, kann jedoch optimale Lösungen verwerfen

---

## Ziel des Projekts

Dieses Projekt dient:
- dem praktischen Verständnis von Graph- und Suchalgorithmen
- der Anwendung theoretischer Konzepte auf reale Daten
- der Analyse von Heuristiken und deren Einfluss auf Suchergebnisse
- als Grundlage für weiterführende Projekte im Bereich Navigation und künstliche Intelligenz

---

## Autoren

- Kapischan Sriganthan  
- Mladen Radovanovic  

Studiengang Wirtschaftsinformatik  
Fachhochschule Nordwestschweiz (FHNW)

---

## Hinweis

Dieses Projekt ist ein **Lehr- und Analyseprojekt**.  
Der Fokus liegt auf **Algorithmik, Vergleichbarkeit und Verständnis**, nicht auf einem produktiven Navigationssystem.
