package org.algorithm.dfs;

import org.algorithm.components.Node;

import java.util.*;

public class Dijkstra {
    private final Node[][] maze;
    private final int rows;
    private final int cols;
    private final Node start;
    private final Node end;

    public Dijkstra(Node[][] maze, int rows, int cols, Node start, Node end) {
        this.maze = maze;
        this.rows = rows;
        this.cols = cols;
        this.start = start;
        this.end = end;
    }

    public int calculateShortestPath() {
        int[][] dist = new int[rows][cols];
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> dist[n.getRow()][n.getColumn()]));

        for(int[] row : dist) Arrays.fill(row, Integer.MAX_VALUE);
        dist[start.getRow()][start.getColumn()] = 0;
        pq.add(start);

        while(!pq.isEmpty()) {
            Node current = pq.poll();
            int currentRow = current.getRow();
            int currentCol = current.getColumn();
            int currentDist = dist[currentRow][currentCol];

            if(current.equals(end)) return currentDist + 1;
            if(currentDist > dist[currentRow][currentCol]) continue;

            for(Node neighbor : getNeighbors(current)) {
                int newDist = currentDist + 1;
                int neighborRow = neighbor.getRow();
                int neighborCol = neighbor.getColumn();

                if(newDist < dist[neighborRow][neighborCol]) {
                    dist[neighborRow][neighborCol] = newDist;
                    pq.add(neighbor);
                }
            }
        }
        return -1;
    }

    private List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();
        int row = node.getRow();
        int col = node.getColumn();
        boolean[] borders = node.getBorders();

        if(!borders[0]) neighbors.add(maze[row - 1][col]); // Nord
        if(!borders[1]) neighbors.add(maze[row][col + 1]); // Est
        if(!borders[2]) neighbors.add(maze[row + 1][col]); // Sud
        if(!borders[3]) neighbors.add(maze[row][col - 1]); // Ouest

        if(!borders[0] && !borders[1]) neighbors.add(maze[row - 1][col + 1]); // NE
        if(!borders[0] && !borders[3]) neighbors.add(maze[row - 1][col - 1]); // NO
        if(!borders[2] && !borders[1]) neighbors.add(maze[row + 1][col + 1]); // SE
        if(!borders[2] && !borders[3]) neighbors.add(maze[row + 1][col - 1]); // SO

        return neighbors;
    }
}