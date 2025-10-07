package org.NavigationProject.Phase2;

import org.NavigationProject.Phase1.MapData;

import java.util.*;

public class BreadthFirst {

    static MapData mapData;

    static {
        try {
            mapData = new MapData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static Map<String, ArrayList<MapData.Destination>> adjList = mapData.getAdjacencyList();

    public static void main(String[] args) {
        ArrayList<String> path = breadthFirst("AllschwilBaslerstrasse1", "BaselSpalenring1");
        printPath(path);
    }

    private static ArrayList<String> breadthFirst(String start, String end) {
        ArrayList<ArrayList<String>> paths = new ArrayList<>();
        ArrayList<String> startingPath = new ArrayList<>();
        startingPath.add(start);
        paths.add(startingPath);

        boolean solutionFound = false;

        while (!solutionFound && paths.size() > 0) {
            // Nimm den ersten Pfad aus der Liste
            ArrayList<String> oldPath = paths.remove(0);

            // Letzter Knoten in diesem Pfad
            String current = oldPath.get(oldPath.size() - 1);

            // Alle Nachbarn durchgehen
            ArrayList<MapData.Destination> neighbors = adjList.get(current);
            if (neighbors == null) continue; // keine Nachbarn

            for (MapData.Destination dest : neighbors) {
                String next = dest.node();

                // Nur hinzufügen, wenn noch nicht im Pfad (Vermeidung von Schleifen)
                if (!oldPath.contains(next)) {
                    ArrayList<String> newPath = (ArrayList<String>) oldPath.clone();
                    newPath.add(next);
                    paths.add(newPath);

                    // Prüfen, ob wir am Ziel sind
                    if (next.equals(end)) {
                        solutionFound = true;
                        break;
                    }
                }
            }
        }

        return (paths.size() == 0) ? null : paths.get(paths.size() - 1);
    }

    private static void printPath(ArrayList<String> path) {
        if (path == null) {
            System.out.println("Kein Pfad gefunden.");
            return;
        }
        System.out.print("Final solution: ");
        for (String node : path) {
            System.out.print(node + " ");
        }
        System.out.println();
    }
}
