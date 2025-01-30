package org.algorithm.bee_hive;

import org.algorithm.components.HexaNode;

import java.util.*;

public class BeeHive {
    private final int nbRow;
    private final int nbColumn;
    private HexaNode[][] hive;
    private HexaNode start;
    private HexaNode end;
    private Random random;

    public BeeHive(int nbRow, int nbColumn) {
        this.nbRow = nbRow;
        this.nbColumn = nbColumn;
        this.hive = new HexaNode[nbRow][nbColumn];
        this.random = new Random();

        // Initialize all cells with walls
        for (int i = 0; i < nbRow; i++) {
            for (int j = 0; j < nbColumn; j++) {
                hive[i][j] = new HexaNode(i, j);
                hive[i][j].setValue((char)(random.nextInt(26) + 'A'));


            }
        }

        this.start = hive[0][0];
        this.end = hive[nbRow-1][nbColumn-1];
    }

    public HexaNode[][] getHive() {
        return hive;
    }

    private List<HexaNode> getUnvisitedNeighbors(HexaNode current, boolean[][] visited) {
        List<HexaNode> neighbors = new ArrayList<>();
        int[][] directions = {
                {-1, 0, 1},    // top-right (dir0)
                {0, 1, 1},     // right (dir1)
                {1, 0, 1},     // bottom-right (dir2)
                {1, -1, 0},    // bottom-left (dir3)
                {0, -1, -1},   // left (dir4)
                {-1, -1, 0}    // top-left (dir5)
        };

        int currentRow = current.getRow();
        int currentCol = current.getColumn();
        boolean isEvenRow = currentRow % 2 == 0;

        for (int[] d : directions) {
            int newRow = currentRow + d[0];
            int newCol = currentCol + (isEvenRow ? d[1] : d[2]);

            if (newRow >= 0 && newRow < nbRow &&
                    newCol >= 0 && newCol < nbColumn &&
                    !visited[newRow][newCol]) {
                neighbors.add(hive[newRow][newCol]);
            }
        }
        return neighbors;
    }

    public void generateHive() {
        boolean[][] visited = new boolean[nbRow][nbColumn];
        Stack<HexaNode> stack = new Stack<>();

        // Start from the beginning cell
        HexaNode current = start;
        visited[current.getRow()][current.getColumn()] = true;
        stack.push(current);
int index=-1;
        while (!stack.isEmpty()) {
            index++;
            current = stack.peek();
            current.setValue((char)('A'+index));
            List<HexaNode> unvisitedNeighbors = getUnvisitedNeighbors(current, visited);

            if (!unvisitedNeighbors.isEmpty()) {
                // Choose a random unvisited neighbor
                HexaNode next = unvisitedNeighbors.get(random.nextInt(unvisitedNeighbors.size()));

                // Remove the wall between current and next
                removeWallBetween(hive[current.getRow()][current.getColumn()] , hive[next.getRow()][next.getColumn()] );

                // Mark as visited and push to stack
                visited[next.getRow()][next.getColumn()] = true;
                stack.push(next);
            } else {
                // Backtrack
                stack.pop();
            }
        }
    }

    private void removeWallBetween(HexaNode current, HexaNode next) {
        int currentRow = current.getRow();
        int currentCol = current.getColumn();
        int nextRow = next.getRow();
        int nextCol = next.getColumn();

        int deltaR = nextRow - currentRow;
        int deltaC = nextCol - currentCol;

        boolean isEvenRow = currentRow % 2 == 0;

        int[][] directions = {
                {-1, 0, 1},    // top-right (dir0)
                {0, 1, 1},     // right (dir1)
                {1, 0, 1},     // bottom-right (dir2)
                {1, -1, 0},    // bottom-left (dir3)
                {0, -1, -1},   // left (dir4)
                {-1, -1, 0}    // top-left (dir5)
        };

        int direction = -1;

        for (int dir = 0; dir < directions.length; dir++) {
            int[] d = directions[dir];
            int expectedDeltaR = d[0];
            int expectedDeltaC = isEvenRow ? d[1] : d[2];

            if (deltaR == expectedDeltaR && deltaC == expectedDeltaC) {
                direction = dir;
                break;
            }
        }

        if (direction != -1) {
            int oppositeDirection = (direction + 3) % 6;
            current.removeBorder(direction);
            next.removeBorder(oppositeDirection);
        }
    }

    public void printHive() {
        for (int i = 0; i < nbRow; i++) {
            for (int j = 0; j < nbColumn; j++) {
                System.out.print(hive[i][j].getValue()+" : "+ Arrays.toString(hive[i][j].getBorders()));
            }
            System.out.println();
        }
    }
}