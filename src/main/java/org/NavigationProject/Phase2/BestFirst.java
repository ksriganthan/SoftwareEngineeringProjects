package org.NavigationProject.Phase2;

import java.util.ArrayList;
import java.util.Map;

import org.NavigationProject.Phase1.MapData;



public class BestFirst {
	private static Map<String, ArrayList<MapData.Destination>> adjlist;
	private static Map<String, MapData.GPS> nodelist;
	private static String start = "AllschwilBaslerstrasse1";
	private static String ziel = "BaselSpalenring1";
	private static int counter = 1;
	
	private static int totalPathsChecked = 0;
	private static int rejectedBecauseOfCircle = 0;
	private static int totalNeighbourVisited = 0;
	
	
	public record Path(ArrayList<String> nodes, double distanceToGoal) {
	}

	public static void main(String[] args) throws Exception {
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
		Path path = bestFirst(start, ziel);
		long endzeit = System.nanoTime();

		long elapsedTime = endzeit - startzeit;

		double distanz = printPath(path.nodes);
		System.out.println("Status: BestFirst-Search funktioniert wie erwartet!");
		System.out.println("---------------------------------------------------------");
		
		//System.out.println("Startzeit: " + startzeit);
		//System.out.println("Endzeit: " + endzeit);
		System.out.printf("Benötigte Zeit: %.3f Millisekunden%n", elapsedTime / 1_000_000.0);
		System.out.println("Pfadlänge: " + path.nodes().size());
		System.out.printf("Distanz: %, .2f Meter%n", distanz);
		System.out.println("Anzahl geprüfter Pfade: " + totalPathsChecked);
		System.out.println("Anzahl Nachbarn besucht: " + totalNeighbourVisited);
		System.out.println("Abgelehnte (wegen Cycle-Gefahr): " + rejectedBecauseOfCircle);
		System.out.println("---------------------------------------------------------");
		}
		catch (Exception e) {
			System.err.println("Search funktioniert nicht wie erwartet.");
		}

	}

	private static double printPath(ArrayList<String> nodes) {
		System.out.println("Solution for Best First Search from " + start + " to " + ziel + ":");
		System.out.println("--------------------------------------------------------------------------------------------");
		nodes.forEach(node -> System.out.println(counter++ + ". Stopp:\t" + node));
		System.out.println("=========================================");

		// Distanz berechnen
		double gesamtDistanz = 0.0;

		for (int i = 0; i < nodes.size() - 1; i++) {
			String from = nodes.get(i);
			String to = nodes.get(i + 1);

			for (MapData.Destination neighbour : adjlist.get(from)) {
				if (neighbour.node().equals(to)) {
					gesamtDistanz += neighbour.distance();
					break;
				}
			}
		}

		return gesamtDistanz;

	}

	private static Path bestFirst(String start, String ziel) {
		ArrayList<Path> queue = new ArrayList<>();
		ArrayList<String> startingNode = new ArrayList<>();
		startingNode.add(start);
		Path startPath = new Path(startingNode, distanceBetween(start, ziel));
		queue.add(startPath);
		boolean solutionFound = false;

		while (!solutionFound && !queue.isEmpty()) {
			int bestIndex = bestPath(queue);
			Path oldPath = queue.remove(bestIndex);
			ArrayList<String> oldnodes = oldPath.nodes;
			String lastNode = oldnodes.get(oldnodes.size() - 1);
			
			totalPathsChecked++; // zählt jeden überprüften Pfad
			
			ArrayList<MapData.Destination> neighbours = adjlist.get(lastNode);

			for (MapData.Destination neighbour : neighbours) {
				if (!oldnodes.contains(neighbour.node())) {
					totalNeighbourVisited++; // zählt alle Nachbarn
					ArrayList<String> newNodes = (ArrayList<String>) oldnodes.clone();
					newNodes.add(neighbour.node());
					queue.add(new Path(newNodes, distanceBetween(neighbour.node(), ziel)));

					if (neighbour.node().equals(ziel)) {
						solutionFound = true;
						break;
					}
				}
				else {
					rejectedBecauseOfCircle++;
				}

			}

		}

		return queue.size() == 0 ? null : queue.get(queue.size() - 1);
	}

	private static int bestPath(ArrayList<Path> queue) {
		int bestPath = 0;
		double smallestDistance = queue.get(0).distanceToGoal;

		for (int i = 1; i < queue.size(); i++) {
			if (smallestDistance > queue.get(i).distanceToGoal) {
				bestPath = i;
				smallestDistance = queue.get(i).distanceToGoal;
			}
		}
		return bestPath;
	}

	private static double distanceBetween(String current, String ziel) {
		//Überpfüfung, ob alle Daten richtig erfasst wurden
		if (!nodelist.containsKey(current)) {
		    System.out.println("❌ Node not found: " + current);
		}
		if (!nodelist.containsKey(ziel)) {
		    System.out.println("❌ Goal not found: " + ziel);
		}
		
		MapData.GPS lastPos = nodelist.get(current);
		MapData.GPS goalPos = nodelist.get(ziel);

		double xDiff = lastPos.east() - goalPos.east();
		double yDiff = lastPos.north() - goalPos.north();

		return xDiff * xDiff + yDiff * yDiff;
	}

}
