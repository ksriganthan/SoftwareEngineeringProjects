package org.NavigationProject.Phase3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.NavigationProject.Phase1.MapData;


public class DijkstraAlgorithm {

	public static Map<String, ArrayList<MapData.Destination>> adjlist;
	public static Map<String, MapData.GPS> nodelist;
	public static String start = "AllschwilBaslerstrasse1";
	public static String ziel = "BaselSpalenring1";
	public static Integer counter = 1;
	
	private static int totalPathsChecked = 0;
	private static int rejectedBecauseOfCircle = 0;
	private static int totalNeighbourVisited = 0;

	public static class NodeInfo {
		double distanceSoFar;
		String previousNode;

		NodeInfo(double distanceSoFar, String previousNode) {
			this.distanceSoFar = distanceSoFar;
			this.previousNode = previousNode;
		}
	}

	public static Map<String, NodeInfo> searchInfo = new HashMap<>();

	public static ArrayList<String> queue = new ArrayList<>();

	public static void main(String[] args) {
		try {
		MapData mapData = null;

		try {
			mapData = new MapData();
		} catch (Exception e) {
			System.out.println("Error reading data");
		}
		adjlist = mapData.getAdjacencyList();
		nodelist = mapData.getNodes();

		for (String node : nodelist.keySet()) {
			if (node.equals(start)) {
				searchInfo.put(node, new NodeInfo(0, null));
			} else {
				searchInfo.put(node, new NodeInfo(Double.MAX_VALUE, null));
			}
		}

		long startzeit = System.nanoTime();
		ArrayList<String> path = dijkstra(start, ziel);
		long endzeit = System.nanoTime();

		long elapsedTime = endzeit - startzeit;

		double distanz = printPath(path);
		System.out.println("Status: Dijkstra-Algorithmus funktioniert wie erwartet!");
		System.out.println("---------------------------------------------------------");
		
		//System.out.println("Startzeit: " + startzeit);
		//System.out.println("Endzeit: " + endzeit);
		System.out.printf("Benötigte Zeit: %.3f Millisekunden%n", elapsedTime / 1_000_000.0);
		System.out.println("Pfadlänge: " + path.size());
		System.out.println("Distanz: " + distanz + " Meter");
		System.out.println("Anzahl geprüfter Pfade: " + totalPathsChecked);
		System.out.println("Anzahl Nachbarn besucht: " + totalNeighbourVisited);
		System.out.println("Abgelehnte (wegen Cycle-Gefahr): " + rejectedBecauseOfCircle);
		System.out.println("---------------------------------------------------------");
		}
		catch (Exception e) {
			System.err.println("Search funktioniert nicht wie erwartet.");
		}
	}

	private static double printPath(ArrayList<String> path) {
		System.out.println("Solution for Dijkstra-Algorithm from " + start + " to " + ziel);
		System.out.println("--------------------------------------------------------------------------------------------");
		path.forEach(node -> System.out.println(counter++ + ". Stopp:\t" + node));
		System.out.println("=========================================");
		
		//Distanz berechnen
		double gesamtDistanz = searchInfo.get(ziel).distanceSoFar;
		return gesamtDistanz;
	}

	private static ArrayList<String> dijkstra(String start, String ziel) {
		HashSet<String> visited = new HashSet<>();
		queue.add(start);

		String currentNode = findLowestDistanceSoFar();

		while (currentNode != null && !currentNode.equals(ziel)) {
			visited.add(currentNode);
			totalPathsChecked++; // zählt jeden überprüften Pfad
			ArrayList<MapData.Destination> neighbours = adjlist.get(currentNode);
			for (MapData.Destination neighbour : neighbours) {
				if (!visited.contains(neighbour.node())) {
					totalNeighbourVisited++; // zählt alle Nachbarn
					NodeInfo currentNodeInfo = searchInfo.get(currentNode);
					NodeInfo neighbourNodeInfo = searchInfo.get(neighbour.node());
					double distance = currentNodeInfo.distanceSoFar + neighbour.distance();

					if (neighbourNodeInfo.distanceSoFar > distance) {
						neighbourNodeInfo.distanceSoFar = distance;
						neighbourNodeInfo.previousNode = currentNode;
						queue.add(neighbour.node());
					}
				}
				else {
					rejectedBecauseOfCircle++;
				}
			}
			currentNode = findLowestDistanceSoFar();
		}

		if (currentNode == null) {
			return null;
		} else {
			return reconstructPath(start, ziel);
		}
	}

	private static ArrayList<String> reconstructPath(String start, String ziel) {
		ArrayList<String> path = new ArrayList<>();
		path.add(ziel);
		String current = ziel;

		while (!current.equals(start)) {
			NodeInfo nodeInfo = searchInfo.get(current);
			current = nodeInfo.previousNode;
			path.add(current);
		}
		Collections.reverse(path);
		return path;
	}

	private static String findLowestDistanceSoFar() {
		double lowestDistance = Double.MAX_VALUE;
		String result = null;

		for (String node : queue) {
			if (lowestDistance > searchInfo.get(node).distanceSoFar) {
				lowestDistance = searchInfo.get(node).distanceSoFar;
				result = node;
			}
		}
		if (result != null) {
			queue.remove(result);
		}
		return result;
	}

}
