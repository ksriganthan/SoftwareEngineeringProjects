package org.NavigationProject.Phase2;

import org.NavigationProject.Phase1.MapData;

import java.util.ArrayList;
import java.util.List;
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

    public static void main(String[] args) {
        ArrayList<String> path = depthFirst("AllschwilBaslerstrasse1", "BaselSpalenring1");
        printPath(path);
    }

    private static ArrayList<String> depthFirst(String start, String end) {
        ArrayList<String> path = new ArrayList<>();
        path.add(start); // Startknoten hinzufügen
        return depthFirstRecursive(path, start, end);
    }

    private static ArrayList<String> depthFirstRecursive(ArrayList<String> path, String current, String end) {
        // Basisfall: Ziel erreicht
        if (current.equals(end)) {
            return path;
        }

        // Nachbarn durchlaufen
        ArrayList<MapData.Destination> neighbors = adjList.get(current);
        if (neighbors == null) return path;

        for (MapData.Destination dest : neighbors) {
            String next = dest.node();

            // Zyklus vermeiden
            if (!path.contains(next)) {
                path.add(next);
                depthFirstRecursive(path, next, end);

                // Falls Ziel gefunden wurde, Abbruch
                if (path.get(path.size() - 1).equals(end)) {
                    return path;
                }

                path.remove(path.size() - 1);
            }
        }
        return path;
    }

    private static void printPath(ArrayList<String> path) {
        if (path == null || path.isEmpty()) {
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
