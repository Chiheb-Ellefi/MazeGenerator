package org.algorithm.bee_hive_v2;

import org.algorithm.components.HiveCell;

import java.util.*;

public class BeeHive {
    private int width;
    private int height;
    private HiveCell[][] grid;
    private HiveCell start;

    public BeeHive(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new HiveCell[width][height];
        initializeGrid();
        linkNeighbors();
        this.start = grid[0][0]; // Starting cell can be adjusted as needed
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public HiveCell[][] getGrid() {
        return grid;
    }

    private void initializeGrid() {
        for (int q = 0; q < width; q++) {
            for (int r = 0; r < height; r++) {
                grid[q][r] = new HiveCell();
            }
        }
    }

    private void linkNeighbors() {
        // Directions for axial coordinates (q, r)
        int[][] dirs = {
                {1, -1}, // top-right (0)
                {1, 0},  // right (1)
                {0, 1},  // bottom-right (2)
                {-1, 1}, // bottom-left (3)
                {-1, 0}, // left (4)
                {0, -1}  // top-left (5)
        };

        for (int q = 0; q < width; q++) {
            for (int r = 0; r < height; r++) {
                HiveCell cell = grid[q][r];
                HiveCell[] neighbors = new HiveCell[6];
                for (int d = 0; d < 6; d++) {
                    int dq = dirs[d][0];
                    int dr = dirs[d][1];
                    int nq = q + dq;
                    int nr = r + dr;

                    if (nq >= 0 && nq < width && nr >= 0 && nr < height) {
                        neighbors[d] = grid[nq][nr];
                    } else {
                        neighbors[d] = null;
                    }
                }
                cell.setNeighbors(neighbors);
            }
        }
    }

    public HiveCell getStartCell() {
        return start;
    }

    public void generateHive() {
        Stack<HiveCell> stack = new Stack<>();
        HiveCell current = start;
        Random random = new Random();
        int index = 0;

        current.setPartOfMaze(true);
        current.setValue((char) ('A' + index));
        index++;
        stack.push(current);

        while (!stack.isEmpty()) {
            current = stack.pop();
            List<HiveCell> neighbors = getNeighbors(current);

            if (!neighbors.isEmpty()) {
                stack.push(current);

                int rand = random.nextInt(neighbors.size());
                HiveCell next = neighbors.get(rand);

                // Find the direction index from current to next
                int nextIndex = -1;
                for (int i = 0; i < 6; i++) {
                    if (current.getNeighbors()[i] == next) {
                        nextIndex = i;
                        break;
                    }
                }

                // Find the opposite direction (from next to current)
                int oppositeIndex = (nextIndex + 3) % 6;

                // Remove walls between current and next
                Boolean[] currentPointsAt = current.getPointsAt();
                currentPointsAt[nextIndex] = true; // Current points to next
                current.setPointsAt(currentPointsAt);

                Boolean[] nextPointsAt = next.getPointsAt();
                nextPointsAt[oppositeIndex] = true; // Next points back to current
                next.setPointsAt(nextPointsAt);

                next.setPartOfMaze(true);
                next.setValue((char) ('A' + index));
                index++;

                if (index > 25) {
                    index = 0;
                }

                stack.push(next);
            }
        }
    }

    List<HiveCell> getNeighbors(HiveCell current) {
        List<HiveCell> neighbors = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            HiveCell neighbor = current.getNeighbors()[i];
            if (neighbor != null && !neighbor.isPartOfMaze()) {
                neighbors.add(neighbor);
            }
        }
        return neighbors;
    }
}