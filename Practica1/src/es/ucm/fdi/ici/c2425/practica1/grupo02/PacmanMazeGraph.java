package es.ucm.fdi.ici.c2425.practica1.grupo02;

import org.jgrapht.graph.SimpleWeightedGraph;


public class PacmanMazeGraph {
    private SimpleWeightedGraph<Integer, PathEdge> mazeGraph;

    public PacmanMazeGraph() {
         // Create a Supplier for PathEdge
         // Explicitly specify vertex type (Integer) and use edge supplier
         mazeGraph = new SimpleWeightedGraph<>(PathEdge.class); 
    }

    public void addNode(int decisionPoint) {
        mazeGraph.addVertex(decisionPoint);
    }

    public void addPath(int from, int to, double distance) {
        PathEdge edge = mazeGraph.addEdge(from, to);
        mazeGraph.setEdgeWeight(edge, distance);
    }

    public double getEdgeWeight(int from, int to) {
        PathEdge edge = mazeGraph.getEdge(from, to);
        return edge != null ? mazeGraph.getEdgeWeight(edge) : -1; // Using graph's getEdgeWeight method
    }


    public static void main(String[] args) {
        PacmanMazeGraph maze = new PacmanMazeGraph();

        // Add nodes (decision points)
        maze.addNode(1);
        maze.addNode(2);
        maze.addNode(3);

        // Add edges (paths) with distances
        maze.addPath(1,2, 5.0);
        maze.addPath(2,3,3.0);

        System.out.println("Edge Weight 1-2: " + maze.getEdgeWeight(2,1));

        // Print the graph
        System.out.println(maze.mazeGraph);
    }
}
