package com.example.testlibrary;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

// A Java program for Dijkstra's
// single source shortest path
// algorithm. The program is for
// adjacency matrix representation
// of the graph.

import java.util.ArrayList;

public class DijkstraAlgo {

    public DijkstraAlgo() {
    }

    private final int NO_PARENT = -1;

    // Function that implements Dijkstra's
    // single source shortest path
    // algorithm for a graph represented
    // using adjacency matrix
    // representation
    public ArrayList dijkstra(float[][] adjacencyMatrix,
                         int startVertex) {
        int nVertices = adjacencyMatrix[0].length;

        // shortestDistances[i] will hold the
        // shortest distance from src to i
        float[] shortestDistances = new float[nVertices];

        // added[i] will true if vertex i is
        // included / in shortest path tree
        // or shortest distance from src to
        // i is finalized
        boolean[] added = new boolean[nVertices];

        // Initialize all distances as
        // INFINITE and added[] as false
        for (int vertexIndex = 0; vertexIndex < nVertices;
             vertexIndex++) {
            shortestDistances[vertexIndex] = Float.MAX_VALUE;
            added[vertexIndex] = false;
        }

        // Distance of source vertex from
        // itself is always 0
        shortestDistances[startVertex] = 0;

        // Parent array to store shortest
        // path tree
        int[] parents = new int[nVertices];

        // The starting vertex does not
        // have a parent
        parents[startVertex] = NO_PARENT;

        // Find shortest path for all
        // vertices
        for (int i = 1; i < nVertices; i++) {

            // Pick the minimum distance vertex
            // from the set of vertices not yet
            // processed. nearestVertex is
            // always equal to startNode in
            // first iteration.
            int nearestVertex = -1;
            float shortestDistance = Float.MAX_VALUE;
            for (int vertexIndex = 0;
                 vertexIndex < nVertices;
                 vertexIndex++) {
                if (!added[vertexIndex] &&
                        shortestDistances[vertexIndex] <
                                shortestDistance) {
                    nearestVertex = vertexIndex;
                    shortestDistance = shortestDistances[vertexIndex];
                }
            }

            // Mark the picked vertex as
            // processed
            added[nearestVertex] = true;

            // Update dist value of the
            // adjacent vertices of the
            // picked vertex.
            for (int vertexIndex = 0;
                 vertexIndex < nVertices;
                 vertexIndex++) {
                float edgeDistance = adjacencyMatrix[nearestVertex][vertexIndex];

                if (edgeDistance > 0
                        && ((shortestDistance + edgeDistance) <
                        shortestDistances[vertexIndex])) {
                    parents[vertexIndex] = nearestVertex;
                    shortestDistances[vertexIndex] = shortestDistance +
                            edgeDistance;
                }
            }
        }

//        printSolution(startVertex, shortestDistances, parents);
        ArrayList list = printSolution1(startVertex, 5, shortestDistances, parents);
        return list;
    }


    // A utility function to print
    // the constructed distances
    // array and shortest paths
    private void printSolution(int startVertex,
                               float[] distances,
                               int[] parents) {
        int nVertices = distances.length;
        System.out.print("Vertex\t Distance\tPath");

        for (int vertexIndex = 0;
             vertexIndex < nVertices;
             vertexIndex++) {
            if (vertexIndex != startVertex) {
                System.out.print("\n" + startVertex + " -> ");
                System.out.print(vertexIndex + " \t\t ");
                System.out.print(distances[vertexIndex] + "\t\t");
                printPath(vertexIndex, parents);
            }
        }
    }

    private ArrayList printSolution1(int startVertex,
                                int endVertex,
                                float[] distances,
                                int[] parents) {

        ArrayList<Integer> arr = returnPath(endVertex, parents, new ArrayList<Integer>());
        return arr;
    }

    // Function to print shortest path
    // from source to currentVertex
    private void printPath(int currentVertex,
                           int[] parents) {

        // Base case : Source node has
        // been processed
        if (currentVertex == NO_PARENT) {
            return;
        }
        printPath(parents[currentVertex], parents);

        System.out.print(currentVertex + " ");
    }
    // Function to print shortest path
    // from source to currentVertex
    private ArrayList<Integer> returnPath(int currentVertex,
                                          int[] parents, ArrayList<Integer> list) {

        // Base case : Source node has
        // been processed
        if (currentVertex == NO_PARENT) {
            return list;
        }
        returnPath(parents[currentVertex], parents, list);
        list.add(currentVertex);

        return list;
    }
}

