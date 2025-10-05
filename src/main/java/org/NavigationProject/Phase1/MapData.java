package org.NavigationProject.Phase1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapData {
	private static final String EdgeFile = "/material/edges.txt";
	private static final String NodeFile = "/material/nodes.txt";
	private static final Map<String, ArrayList<Destination>> adjacencyList = new HashMap<>();
	private static final Map<String, GPS> nodes = new HashMap<>();

	public record GPS(Double east, Double north) {
	};

	public record Destination(String node, double distance) {
	};

	public MapData() throws Exception {
		createNodes();
		createAdjacencyList();
	}

	public Map<String, GPS> getNodes() {
		return nodes;
	}

	public Map<String, ArrayList<Destination>> getAdjacencyList() {
		return adjacencyList;
	}

	private void createNodes() throws Exception {

		try (InputStream in = getClass().getResourceAsStream(NodeFile);
				BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			reader.lines().map(line -> line.split(";"))
					.forEach(a -> nodes.put(a[0], new GPS(Double.parseDouble(a[1]), Double.parseDouble(a[2]))));
		} catch (IOException e) {
			e.printStackTrace();

		}
	}

	private void createAdjacencyList() throws Exception {

		try (InputStream in = getClass().getResourceAsStream(EdgeFile);
				BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			reader.lines().map(String::trim) // Leerzeichen weg
					.filter(line -> !line.isEmpty()) // Leere Zeilen überspringen
					.map(line -> line.split(";")).forEach(a -> {
						addDestination(a[0], a[1], a[2]);
						addDestination(a[1], a[0], a[2]);
					});
		} catch (IOException e) {
			e.printStackTrace();

		}
	}

	private void addDestination(String startNode, String endNode, String distance) {
		ArrayList<Destination> destinations = adjacencyList.get(startNode);

		if (destinations == null) {
			destinations = new ArrayList<Destination>();
			adjacencyList.put(startNode, destinations);
		}
		destinations.add(new Destination(endNode, Double.parseDouble(distance)));

	}

}
