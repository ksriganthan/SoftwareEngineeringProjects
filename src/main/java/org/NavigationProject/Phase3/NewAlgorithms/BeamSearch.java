package org.NavigationProject.Phase3.NewAlgorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.NavigationProject.Phase1.MapData;

//k = 1 -> Hill Climbing
public class BeamSearch {

	private static Map<String, ArrayList<MapData.Destination>> adjList;
	private static Map<String, MapData.GPS> nodesList;
	private static Map<String, Double> heuristics = new HashMap<>();
	private static String start = "AllschwilBaslerstrasse1";
	private static String ziel = "BaselSpalenring1";
	private static int beamWidth = 3; // Beam-Breite
	private static int counter = 1;
	
	private static int totalPathsChecked = 0;
	private static int rejectedBecauseOfCircle = 0;
	private static int totalNeighbourVisited = 0;
	private static int rejectedPaths = 0;

	public static void main(String[] args) {
		try {
		MapData mapData = null;
		try {
			mapData = new MapData();
		} catch (Exception e) {
			System.out.println("Error reading data");
		}
		adjList = mapData.getAdjacencyList();
		nodesList = mapData.getNodes();

		erstelleHeuristics(ziel);
		
		long startzeit = System.nanoTime();
		ArrayList<String> path = beamSearch(start, ziel, beamWidth);
		long endzeit = System.nanoTime();

		long elapsedTime = endzeit - startzeit;

		double distanz = printPath(path);
		System.out.println("Status: Beam-Search mit eine Beam-Widht von " + beamWidth + " funktioniert wie erwartet!");
		System.out.println("---------------------------------------------------------");
		
		//System.out.println("Startzeit: " + startzeit);
		//System.out.println("Endzeit: " + endzeit);
		System.out.printf("Benötigte Zeit: %.3f Millisekunden%n", elapsedTime / 1_000_000.0);
		System.out.println("Pfadlänge: " + path.size());
		System.out.println("Distanz: " + distanz + " Meter");
		System.out.println("Anzahl geprüfter Pfade: " + totalPathsChecked);
	    System.out.println("Anzahl Nachbarn besucht: " + totalNeighbourVisited);
		System.out.println("Abgelehnte (wegen Cycle-Gefahr): " + rejectedBecauseOfCircle);
		System.out.println("Weggeworfene Paths aufgrund Beam-Width(" + beamWidth +"): " + rejectedPaths);
		System.out.println("---------------------------------------------------------");
		}
		catch (Exception e) {
			System.err.println("Search funktioniert nicht wie erwartet");
		}
	}

	private static void erstelleHeuristics(String ziel) {
		for (String node : nodesList.keySet()) {
			heuristics.put(node, distanceBetween(node, ziel));
		}

	}

	private static Double distanceBetween(String node, String ziel) {
		MapData.GPS lastPos = nodesList.get(node);
		MapData.GPS goalPos = nodesList.get(ziel);

		double xDiff = lastPos.east() - goalPos.east();
		double yDiff = lastPos.north() - goalPos.north();

		return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
	}

	private static double printPath(ArrayList<String> path) {
		System.out.println(
				"Solution for Beam Search with a beam width of " + beamWidth + " from " + start + " to " + ziel + ":");
		path.forEach(node -> System.out.println(counter++ + ". Stopp: " + node));
		System.out.println("=========================================");
		
		//Distanz berechnen
				double gesamtDistanz = 0.0;
				
				for(int i = 0; i < path.size()-1; i++) {
					String from = path.get(i);
					String to = path.get(i +1);
					
					for(MapData.Destination neighbour : adjList.get(from)) {
						if(neighbour.node().equals(to)) {
							gesamtDistanz += neighbour.distance();
							break;
						}
					}
				}
				
				return gesamtDistanz;

			}

	private static ArrayList<String> beamSearch(String start, String ziel, int beamWidth) {
		ArrayList<ArrayList<String>> queue = new ArrayList<>();
		ArrayList<String> startingPath = new ArrayList<>();
		startingPath.add(start);
		queue.add(startingPath);

		
		while (!queue.isEmpty()) {
			ArrayList<ArrayList<String>> allcandidates = new ArrayList<>();
			
			for(ArrayList<String>oldPath : queue) {
			totalPathsChecked++; // zählt jeden überprüften Pfad
			String current = oldPath.get(oldPath.size()-1);
			
			ArrayList<MapData.Destination> neighbours = adjList.get(current);
			
			for (MapData.Destination neighbour : neighbours) {
				if (!oldPath.contains(neighbour.node())) {
					totalNeighbourVisited++;
					ArrayList<String> newpath = new ArrayList<>(oldPath);
					newpath.add(neighbour.node());
					allcandidates.add(newpath);
				}
				else {
					rejectedBecauseOfCircle++;
				}
				if(neighbour.node().equals(ziel)) {
					return allcandidates.get(allcandidates.size()-1);
				}
			}
			}

			// Kandidanten nach Heuristik-Wert sortieren
			allcandidates.sort((p1, p2) ->
			Double.compare(
					heuristics.get(p1.get(p1.size() - 1)), 
					heuristics.get(p2.get(p2.size() - 1))
					)
			);
			
			queue.clear();

			for (int i = 0; i < Math.min(beamWidth, allcandidates.size()); i++) {
				queue.add(allcandidates.get(i));
			}
			rejectedPaths = rejectedPaths + allcandidates.size() - queue.size();
		}

		return new ArrayList<>(); //Ziel nicht gefunden
	}

}

