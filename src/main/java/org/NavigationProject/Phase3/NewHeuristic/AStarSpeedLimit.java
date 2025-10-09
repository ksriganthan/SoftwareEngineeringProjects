package org.NavigationProject.Phase3.NewHeuristic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.NavigationProject.Phase1.MapData;
import org.NavigationProject.Phase1.MapData.Destination;

/*
 * Dieser A*-Algorithmus optimiert auf Zeit und nicht mehr auf Distanz
 */
public class AStarSpeedLimit {
	private static Map<String, ArrayList<MapData.Destination>> adjlist;
	private static Map<String, MapData.GPS> nodelist;
	private static int counter = 1;
	private static String start = "AllschwilBaslerstrasse1";
	private static String ziel = "BaselSpalenring1";
	
	private static int totalPathsChecked = 0;
	private static int rejectedBecauseOfCircle = 0;
	private static int totalNeighbourVisited = 0;

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

		long startzeit = System.nanoTime();
		ArrayList<String> path = AStar(start, ziel);
		long endzeit = System.nanoTime();

		long elapsedTime = endzeit - startzeit;

		double gesamtZeit = printPath(path); // Gesamtzeit berechnen
		double gesamtDistanz = totalDistance(path);
		
		
		System.out.println("Status: A*-Search mit Speed-Limits funktioniert wie erwartet!");
		System.out.println("---------------------------------------------------------");
		
		//System.out.println("Startzeit: " + startzeit);
		//System.out.println("Endzeit: " + endzeit);
		System.out.printf("Benötigte Zeit: %.3f Millisekunden%n", elapsedTime / 1_000_000.0);
		System.out.println("Pfadlänge: " + path.size());
		System.out.printf("Gesamtzeit: %.2f Minuten\n", gesamtZeit / 60 );
		System.out.printf("Distanz: %, .2f Meter%n", gesamtDistanz);
		System.out.println("Anzahl geprüfter Pfade: " + totalPathsChecked);
		System.out.println("Anzahl Nachbarn besucht: " + totalNeighbourVisited);
		System.out.println("Abgelehnte (wegen Cycle-Gefahr): " + rejectedBecauseOfCircle);
		System.out.println("---------------------------------------------------------");
		}
		catch (Exception e) {
			System.err.println("Search funktioniert nicht wie erwartet.");
		}

	}

	private static double totalDistance(ArrayList<String> path) {
	    double distance = 0.0;

	    for (int i = 0; i < path.size() - 1; i++) {
	        String currentNode = path.get(i);
	        String nextNode = path.get(i + 1);

	        for (MapData.Destination neighbour : adjlist.get(currentNode)) {
	            if (neighbour.node().equals(nextNode)) {
	                distance += neighbour.distance();
	                break;
	            }
	        }
	    }
	    return distance;
	}


	private static double printPath(ArrayList<String> path) {
		System.out.println("Solution for A* Search with the new heuristic 'speed limit' from " + start + " to " + ziel + ":");
		System.out.println("--------------------------------------------------------------------------------------------");
		path.forEach(node -> System.out.println(counter++ + ". Stopp:\t" + node));
		System.out.println("=========================================");

		// Gesamtzeit berechnen
		double gesamtZeit = searchInfo.get(ziel).timeSoFar;
		return gesamtZeit;
	}

	public static class NodeInfo {
		double timeSoFar; // Zeit verbraucht bisher
		double timeToGoal; // Zeit bis zum Ziel
		String previousNode;

		NodeInfo(double timeSoFar, double timeToGoal, String previousNode) {
			this.timeSoFar = timeSoFar;
			this.timeToGoal = timeToGoal;
			this.previousNode = previousNode;
		}
	}

	private static Map<String, NodeInfo> searchInfo = new HashMap<>();
	private static ArrayList<String> queue = new ArrayList<>();

	private static ArrayList<String> AStar(String start, String ziel) {

		searchInfo.put(start, new AStarSpeedLimit.NodeInfo(0, 0, null));
		queue.add(start);

		String currentNode = findLowestTime();

		while (currentNode != null && !currentNode.equals(ziel)) {
			totalPathsChecked++; // zählt jeden überprüften Pfad
			
			ArrayList<MapData.Destination> neighbours = adjlist.get(currentNode);

			for (MapData.Destination neighbour : neighbours) {
				totalNeighbourVisited++; // zählt alle Nachbarn
				NodeInfo currentNodeInfo = searchInfo.get(currentNode);
				NodeInfo neighbourNodeInfo = searchInfo.get(neighbour.node());
				double timeNow = currentNodeInfo.timeSoFar +  neighbour.distance() / (neighbour.speedLimit()/3.6); // Zeit in Sekunden
				if (searchInfo.keySet().contains(neighbour.node())) {
					if (timeNow < neighbourNodeInfo.timeSoFar) {
						neighbourNodeInfo.timeSoFar = timeNow;
						neighbourNodeInfo.previousNode = currentNode;
						queue.add(neighbour.node());
					}
					else {
						rejectedBecauseOfCircle++; //Wenn wir den Node schon mal besucht haben und es nicht mehr besser werden kann
					}
				} else {
					searchInfo.put(neighbour.node(), // Zeit in Sekunden
							new NodeInfo(timeNow, distanceBetween(neighbour.node(), ziel) / (neighbour.speedLimit() / 3.6), currentNode));
					queue.add(neighbour.node());
				}
			}
			currentNode = findLowestTime();

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

	private static String findLowestTime() {
		double lowestTime = Double.MAX_VALUE;
		String result = null;

		for (String node : queue) {
			NodeInfo nodeInfo = searchInfo.get(node);
			if (lowestTime > nodeInfo.timeSoFar + nodeInfo.timeToGoal) {
				lowestTime = nodeInfo.timeSoFar + nodeInfo.timeToGoal;
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

