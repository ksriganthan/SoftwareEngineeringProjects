package org.NavigationProject.Phase2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.NavigationProject.Phase1.MapData;


public class AStar {
	private static Map<String, ArrayList<MapData.Destination>> adjlist;
	private static Map<String, MapData.GPS> nodelist;
	private static int counter = 1;
	private static String start = "AllschwilBaslerstrasse1";
	private static String ziel = "BaselSpalenring1";
	
	private static int totalPathsChecked = 0;
	private static int rejectedBecauseOfCircle = 0;
	private static int totalNeighbourVisited = 0;

	public static void main(String[] args) {
		MapData mapData = null;

		try {
			mapData = new MapData();
		} catch (Exception e) {
			System.out.println("Error reading data");
		}
		adjlist = mapData.getAdjacencyList();
		nodelist = mapData.getNodes();

		long startzeit = System.nanoTime();
		ArrayList<String> path = AStar(start, ziel);
		long endzeit = System.nanoTime();

		long elapsedTime = endzeit - startzeit;

		double distanz = printPath(path);
		System.out.println("Status: A*-Search funktiniert wie erwartet!");
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

	private static double printPath(ArrayList<String> path) {
		System.out.println("Solution for A* Search from " + start + " to " + ziel + ":");
		System.out.println("--------------------------------------------------------------------------------------------");
		path.forEach(node -> System.out.println(counter++ + ". Stopp:\t" + node));
		System.out.println("=========================================");

		// Distanz berechnen
		double gesamtDistanz = searchInfo.get(ziel).distanceSoFar;
		return gesamtDistanz;
	}

	public static class NodeInfo {
		double distanceSoFar;
		double distanceToGoal;
		String previousNode;

		NodeInfo(double distanceSoFar, double distanceToGoal, String previousNode) {
			this.distanceSoFar = distanceSoFar;
			this.distanceToGoal = distanceToGoal;
			this.previousNode = previousNode;
		}
	}

	private static Map<String, NodeInfo> searchInfo = new HashMap<>();
	private static ArrayList<String> queue = new ArrayList<>();

	private static ArrayList<String> AStar(String start, String ziel) {

		searchInfo.put(start, new AStar.NodeInfo(0, 0, null));
		queue.add(start);

		String currentNode = findLowestCost();

		while (currentNode != null && !currentNode.equals(ziel)) {
			totalPathsChecked++; // zählt jeden überprüften Pfad
			
			ArrayList<MapData.Destination> neighbours = adjlist.get(currentNode);

			for (MapData.Destination neighbour : neighbours) {
				totalNeighbourVisited++; // zählt alle Nachbarn
				NodeInfo currentNodeInfo = searchInfo.get(currentNode);
				NodeInfo neighbourNodeInfo = searchInfo.get(neighbour.node());
				double distanceNow = currentNodeInfo.distanceSoFar +  neighbour.distance();
				if (searchInfo.keySet().contains(neighbour.node())) {
					if (distanceNow < neighbourNodeInfo.distanceSoFar) {
						neighbourNodeInfo.distanceSoFar = distanceNow;
						neighbourNodeInfo.previousNode = currentNode;
						queue.add(neighbour.node());
					}
					else {
						rejectedBecauseOfCircle++; //Wenn wir den Node schon mal besucht haben und es nicht mehr besser werden kann
					}
				} else {
					searchInfo.put(neighbour.node(),
							new NodeInfo(distanceNow, distanceBetween(neighbour.node(), ziel), currentNode));
					queue.add(neighbour.node());
				}
			}
			currentNode = findLowestCost();

		}

		if (currentNode == null) {
			return null;
		} else {
			return constructPath(start, ziel);
		}
	}

	private static ArrayList<String> constructPath(String start, String ziel) {
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

	private static String findLowestCost() {
		double lowestCost = Double.MAX_VALUE;
		String result = null;

		for (String node : queue) {
			NodeInfo nodeInfo = searchInfo.get(node);
			if (lowestCost > nodeInfo.distanceSoFar + nodeInfo.distanceToGoal) {
				lowestCost = nodeInfo.distanceSoFar + nodeInfo.distanceToGoal;
				result = node;
			}
		}
		if (result != null) {
			queue.remove(result);
		}
		return result;
	}

	private static double distanceBetween(String current, String ziel) {
		MapData.GPS lastPos = nodelist.get(current);
		MapData.GPS goalPos = nodelist.get(ziel);

		double xDiff = lastPos.east() - goalPos.east();
		double yDiff = lastPos.north() - goalPos.north();

		return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
	}

}
