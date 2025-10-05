package org.NavigationProject.Phase1;

import java.util.ArrayList;
import java.util.Map;

public class Test {

	public static void main(String[] args) throws Exception {
		MapData mapData = new MapData();
		
		Map<String, MapData.GPS> nodes = mapData.getNodes();
		int sumNode = 0;
		System.out.println("Alle Nodes mit den Koordinaten: \n".toUpperCase());
		for (String node : nodes.keySet()) {
			MapData.GPS gps = nodes.get(node);
			sumNode++;
			System.out.println(node + ": (" + gps.north() + "," + gps.east() + ")");
		}
		System.out.println("==========================================================");
		System.out.println("ANZAHL NODES: " + sumNode);
		System.out.println("==========================================================");
		System.out.println();
		System.out.println();
		
		
		Map<String, ArrayList<MapData.Destination>>edges = mapData.getAdjacencyList();
		
		int sumEdges = 0;
		System.out.println("Alle Edges mit den Distanzen: \n".toUpperCase());
		for(String node: edges.keySet()) {
			for(MapData.Destination neighbour : edges.get(node)) {
				System.out.println(node + " -> " + neighbour.node() + " (" + neighbour.distance() + ")");
				sumEdges++;
			}
		}
		System.out.println("==========================================================");
		System.out.println("ANZAHL Edges: " + sumEdges);
		System.out.println("==========================================================");
		System.out.println();
		System.out.println();


	}

}
