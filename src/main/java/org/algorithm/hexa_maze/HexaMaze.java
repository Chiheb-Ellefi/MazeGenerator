package org.algorithm.hexa_maze;

import org.algorithm.data.MazePopulator;
import org.algorithm.components.HexaNode;

import java.util.*;

public class HexaMaze {
    private final int nbColumn;
    private final int nbRow;
    private final HexaNode[][] maze;
    private final Random random;
    private List<String> dictionary;
    private final MazePopulator mazePopulator;
    private HexaNode start;
    private HexaNode end;

    public HexaMaze(int nbColumn, int nbRow) {
        this.nbColumn = nbColumn;
        this.nbRow = nbRow;
        this.maze = new HexaNode[nbRow][nbColumn];
        this.random = new Random();
        this.mazePopulator = new MazePopulator();

        // Initialize maze in rhombus shape
        for (int i = 0; i < nbRow; i++) {
            int distanceFromEdge = Math.min(i, nbRow - 1 - i);
            int numColumnsInRow = 1 + 2 * distanceFromEdge;
            int startColumn = (nbColumn - numColumnsInRow) / 2;

            for (int j = startColumn; j < startColumn + numColumnsInRow; j++) {
                if (j >= 0 && j < nbColumn) {
                    maze[i][j] = new HexaNode(i, j);
                }
            }
        }

        // Initialize start and end to top and bottom center nodes
        int middleCol = (nbColumn - 1) / 2;
        this.start = maze[0][middleCol];
        this.end = maze[nbRow - 1][middleCol];

        this.dictionary = mazePopulator.getData();
    }

    public HexaNode getStart() {
        return start;
    }

    public HexaNode getEnd() {
        return end;
    }

    public HexaNode[][] getMaze() {
        return maze;
    }

    void setStartAndEnd() {
        List<HexaNode> nodes = new ArrayList<>();
        for (HexaNode[] row : maze) {
            for (HexaNode node : row) {
                if (node != null) {
                    nodes.add(node);
                }
            }
        }

        if (nodes.size() < 2) {
            throw new IllegalStateException("Not enough nodes to set start and end");
        }

        // Ensure start and end are different
        int startIdx = random.nextInt(nodes.size());
        start = nodes.get(startIdx);

        int endIdx;
        do {
            endIdx = random.nextInt(nodes.size());
        } while (endIdx == startIdx);
        end = nodes.get(endIdx);
    }

    private List<HexaNode> getNeighbors(HexaNode current) {
        List<HexaNode> neighbors = new ArrayList<>();
        int[][] directions = {
                {-1, 0}, {-1, 1}, {0, 1},
                {1, 0}, {0, -1}, {-1, -1}
        };

        for (int[] dir : directions) {
            int newRow = current.getRow() + dir[0];
            int newCol = current.getColumn() + dir[1];

            if (newRow >= 0 && newRow < nbRow && newCol >= 0 && newCol < nbColumn) {
                HexaNode neighbor = maze[newRow][newCol];
                if (neighbor != null) {
                    neighbors.add(neighbor);
                }
            }
        }
        return neighbors;
    }

    private void removeWallBetween(HexaNode current, HexaNode next) {
        int rowDiff = next.getRow() - current.getRow();
        int colDiff = next.getColumn() - current.getColumn();

        int[][] directions = {
                {-1, 0},   // North (0)
                {-1, 1},   // Northeast (1)
                {0, 1},    // Southeast (2)
                {1, 0},    // South (3)
                {0, -1},   // Southwest (4)
                {-1, -1}   // Northwest (5)
        };

        int directionIndex = -1;
        for (int i = 0; i < directions.length; i++) {
            int[] dir = directions[i];
            if (dir[0] == rowDiff && dir[1] == colDiff) {
                directionIndex = i;
                break;
            }
        }

        if (directionIndex != -1) {
            current.removeBorder(directionIndex);
            next.removeBorder((directionIndex + 3) % 6);
        }
    }

    private List<HexaNode> getVisitedNeighbors(List<HexaNode> neighbors) {
        List<HexaNode> visitedNeighbors = new ArrayList<>();
        for (HexaNode node : neighbors) {
            if (node.isPartOfMaze()) {
                visitedNeighbors.add(node);
            }
        }
        return visitedNeighbors;
    }

    public void generateMaze() {
        setStartAndEnd();
        List<HexaNode> frontier = new ArrayList<>();
        HexaNode current = start;
        current.setPartOfMaze(true);
        int nb = 0;
        int index = 0;
        String word = dictionary.get(nb);
        frontier.add(current);

        do {
            current = frontier.get(random.nextInt(frontier.size()));
            maze[current.getRow()][current.getColumn()].setValue(word.charAt(index));
            index++;

            // Mark current node as visited
            maze[current.getRow()][current.getColumn()].setPartOfMaze(true);

            // Get the visited neighbors
            List<HexaNode> visitedNeighbors = getVisitedNeighbors(getNeighbors(maze[current.getRow()][current.getColumn()]));
            if (!visitedNeighbors.isEmpty()) {
                // Choose random visited neighbor
                HexaNode neighbor = visitedNeighbors.get(random.nextInt(visitedNeighbors.size()));
                removeWallBetween(maze[current.getRow()][current.getColumn()], maze[neighbor.getRow()][neighbor.getColumn()]);
            }

            // Add unvisited neighbors to the frontier set
            for (HexaNode neighbor : getNeighbors(maze[current.getRow()][current.getColumn()])) {
                if (!neighbor.isPartOfMaze() && !frontier.contains(neighbor)) {
                    frontier.add(neighbor);
                }
            }

            if (nb < dictionary.size() - 1) {
                if (index == word.length()) {
                    nb++;
                    word = dictionary.get(nb);
                    index = 0;
                }
            } else {
                dictionary = mazePopulator.getData();
            }

            frontier.remove(current);
        } while (!frontier.isEmpty());
    }

    public void printMaze() {
        for (int i = 0; i < nbRow; i++) {
            int distanceFromEdge = Math.min(i, nbRow - 1 - i);
            int numColumnsInRow = 1 + 2 * distanceFromEdge;
            int startColumn = (nbColumn - numColumnsInRow) / 2;

            // Add leading spaces for rhombus shape
            StringBuilder rowStr = new StringBuilder();
            for (int s = 0; s < startColumn; s++) {
                rowStr.append("  "); // Changed from four spaces to two
            }

            for (int j = startColumn; j < startColumn + numColumnsInRow; j++) {
                if (j >= 0 && j < nbColumn && maze[i][j] != null) {
                    rowStr.append(maze[i][j].getValue()).append(" ");
                } else {
                    rowStr.append("   "); // Adjust trailing spaces for alignment
                }
            }
            System.out.println(rowStr.toString().trim());
        }
    }
}