package org.NavigationProject.Phase2;

import org.NavigationProject.Phase1.MapData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BreadthFirst {

    // MapData einmalig laden
    static MapData mapData;
    static {
        try {
            mapData = new MapData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Graph
    static Map<String, ArrayList<MapData.Destination>> adjList = mapData.getAdjacencyList();

    // Statistik-Variablen
    static long tStart;
    static long tEnd;
    static int pathsChecked = 0;       // Dequeued Pfade
    static int neighborsVisited = 0;   // Enqueued Nachbarn
    static int rejectedCycles = 0;     // Verworfen (Cycle/Visited)

    public static void main(String[] args) {
        String start = "AllschwilBaslerstrasse1";
        String end   = "BaselSpalenring1";

        tStart = System.nanoTime();
        ArrayList<String> path = breadthFirst(start, end);
        tEnd = System.nanoTime();

        printSolution("Breadth-First Search", start, end, path);
        printStatistics(path);
    }

    private static ArrayList<String> breadthFirst(String start, String end) {
        if (start == null || end == null) return null;
        if (!adjList.containsKey(start)) {
            System.out.println("Start node not in adjacency list: " + start);
            return null;
        }

        // FIFO-Struktur mit Pfaden (wie in deiner Vorlage)
        ArrayList<ArrayList<String>> paths = new ArrayList<>();
        ArrayList<String> startingPath = new ArrayList<>();
        startingPath.add(start);
        paths.add(startingPath);

        // Globale Besuchsmarkierung (verhindert Endlosschleifen)
        Set<String> visited = new HashSet<>();
        visited.add(start);

        while (!paths.isEmpty()) {
            ArrayList<String> oldPath = paths.remove(0);
            pathsChecked++;

            String current = oldPath.get(oldPath.size() - 1);

            // Ziel erreicht?
            if (current.equals(end)) return oldPath;

            // Nachbarn holen
            ArrayList<MapData.Destination> connected = adjList.get(current);
            if (connected == null) continue;

            for (MapData.Destination dest : connected) {
                String next = dest.node();

                // Zyklusbremse: nicht erneut im selben Pfad + nicht erneut global expandieren
                if (oldPath.contains(next)) {
                    rejectedCycles++;
                    continue;
                }
                if (visited.contains(next)) {
                    rejectedCycles++;
                    continue;
                }

                ArrayList<String> newPath = (ArrayList<String>) oldPath.clone();
                newPath.add(next);
                paths.add(newPath);
                visited.add(next);
                neighborsVisited++;

                // Optional: sofort zurück, falls Ziel gerade gefunden
                if (next.equals(end)) return newPath;
            }
        }
        return null; // kein Pfad gefunden
    }

    private static void printSolution(String algoName, String start, String end, ArrayList<String> path) {
        System.out.println("Solution for " + algoName + " from " + start + " to " + end + ":");
        System.out.println("--------------------------------------------------------------------------------------------");
        if (path == null || path.isEmpty()) {
            System.out.println("No path found.");
            System.out.println("=========================================");
            System.out.println("Status: " + algoName + " hat keinen Pfad gefunden.");
            System.out.println("---------------------------------------------------------");
            return;
        }
        for (int i = 0; i < path.size(); i++) {
            System.out.printf("%d. Stopp:\t%s%n", i + 1, path.get(i));
        }
        System.out.println("=========================================");
        System.out.println("Status: " + algoName + " funktioniert wie erwartet!");
        System.out.println("---------------------------------------------------------");
    }

    private static void printStatistics(ArrayList<String> path) {
        System.out.printf("Benötigte Zeit: %.3f Millisekunden%n", (tEnd - tStart) / 1_000_000.0);
        System.out.printf("Pfadlänge: %d%n", (path == null ? 0 : path.size()));
        System.out.printf("Distanz: %.2f Meter%n", computePathDistance(path));
        System.out.printf("Anzahl geprüfter Pfade: %d%n", pathsChecked);
        System.out.printf("Anzahl Nachbarn besucht: %d%n", neighborsVisited);
        System.out.printf("Abgelehnte (wegen Cycle-Gefahr): %d%n", rejectedCycles);
        System.out.println("---------------------------------------------------------");
    }

    private static double computePathDistance(ArrayList<String> path) {
        if (path == null || path.size() < 2) return 0.0;
        double sum = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            String a = path.get(i);
            String b = path.get(i + 1);
            Double w = edgeWeight(a, b);
            if (w != null) sum += w;
        }
        return sum;
    }

    // Holt die Kantenlänge a->b aus der Adjazenzliste (null, falls nicht vorhanden)
    private static Double edgeWeight(String a, String b) {
        ArrayList<MapData.Destination> nbs = adjList.get(a);
        if (nbs == null) return null;
        for (MapData.Destination d : nbs) {
            if (b.equals(d.node())) {
                return d.distance();
            }
        }
        return null;
    }
}
