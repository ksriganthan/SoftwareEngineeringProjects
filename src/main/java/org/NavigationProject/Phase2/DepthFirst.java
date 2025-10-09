package org.NavigationProject.Phase2;

import org.NavigationProject.Phase1.MapData;

import java.util.ArrayList;
import java.util.Map;

public class DepthFirst {

    static MapData mapData;
    static {
        try {
            mapData = new MapData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static Map<String, ArrayList<MapData.Destination>> adjList = mapData.getAdjacencyList();

    // Statistik-Variablen
    static long tStart;
    static long tEnd;
    static int pathsChecked = 0;     // Anzahl expandierter Knoten (Rekursionsaufrufe)
    static int neighborsVisited = 0; // Anzahl tatsächlich besuchter Nachbarn (rekursiv betreten)
    static int rejectedCycles = 0;   // abgelehnt wegen Zyklus (bereits im aktuellen Pfad)

    public static void main(String[] args) {
        tStart = System.nanoTime();

        ArrayList<String> path = depthFirst("AllschwilBaslerstrasse1", "BaselSpalenring1");

        tEnd = System.nanoTime();
        printPath(path);
        printStatistics(path);
    }

    private static ArrayList<String> depthFirst(String start, String end) {
        ArrayList<String> path = new ArrayList<>();
        path.add(start);
        return depthFirstRecursive(path, start, end);
    }

    private static ArrayList<String> depthFirstRecursive(ArrayList<String> path, String current, String end) {
        // Jeder Aufruf = ein geprüfter Pfad (Expansion dieses Knotens)
        pathsChecked++;

        // Basisfall: Ziel erreicht -> aktuellen Pfad zurückgeben
        if (current.equals(end)) {
            return new ArrayList<>(path);
        }

        ArrayList<MapData.Destination> neighbors = adjList.get(current);
        if (neighbors == null) {
            return null; // Sackgasse -> kein Pfad über diesen Ast
        }

        for (MapData.Destination dest : neighbors) {
            String next = dest.node();

            // Cycle vermeiden: Knoten schon im aktuellen Pfad?
            if (path.contains(next)) {
                rejectedCycles++;
                continue;
            }

            // Knoten betreten
            path.add(next);
            neighborsVisited++;

            ArrayList<String> result = depthFirstRecursive(path, next, end);
            if (result != null) {
                return result; // Lösung gefunden -> direkt nach oben durchreichen
            }

            // Backtracking
            path.remove(path.size() - 1);
        }

        // Keine Lösung in diesem Teilbaum
        return null;
    }

    private static double computePathDistance(ArrayList<String> path) {
        if (path == null || path.size() < 2) return 0.0;
        double sum = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            String a = path.get(i);
            String b = path.get(i + 1);
            double w = edgeWeight(a, b);
            sum += w;
        }
        return sum;
    }

    // Holt die Kantenlänge a->b aus der Adjazenzliste (0.0, falls nicht vorhanden)
    private static double edgeWeight(String a, String b) {
        ArrayList<MapData.Destination> nbs = adjList.get(a);
        if (nbs == null) return 0.0;
        for (MapData.Destination d : nbs) {
            if (b.equals(d.node())) {
                return d.distance();
            }
        }
        return 0.0;
    }

    private static void printPath(ArrayList<String> path) {
        if (path == null || path.isEmpty()) {
            System.out.println("Kein Pfad gefunden.");
            System.out.println("=========================================");
            System.out.println("Status: Depth-First-Search hat keinen Pfad gefunden.");
            System.out.println("---------------------------------------------------------");
            return;
        }
        for (int i = 0; i < path.size(); i++) {
            System.out.printf("%d. Stopp:\t%s%n", i + 1, path.get(i));
        }
        System.out.println("=========================================");
        System.out.println("Status: Depth-First-Search funktioniert wie erwartet!");
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
}
