package org.algorithm.prim_maze;

import org.algorithm.components.Node;
import org.algorithm.data.MazePopulator;

import java.util.*;

public class PrimAlgorithm {
    private final int nbColumn;
    private final  int nbRow;
    private final  Node[][] maze;
    private final Random random;
    private List<String> dictionary;
    private final MazePopulator mazePopulator;
    private Node start;
    private Node end;
   public  PrimAlgorithm(int nbColumn, int nbRow){
       this.nbColumn = nbColumn;
       this.nbRow = nbRow;
       this.maze = new Node[nbRow][nbColumn];
       this.start=new Node(0,0);
       this.end=new Node(nbRow-1,nbColumn-1);
       this.random = new Random();
       this.mazePopulator=new MazePopulator();
       for (int i = 0; i < nbRow; i++) {
           for (int j = 0; j < nbColumn; j++) {
               maze[i][j] = new Node(i, j);
           }
       }
       this.dictionary=mazePopulator.getData();
    }
    public Node getStart() {
        return start;
    }

    public Node getEnd() {
        return end;
    }
    public Node[][] getMaze() {
        return maze;
    }
   public  void setStartAndEnd() {
        boolean isVertical = random.nextBoolean();
        if (isVertical) {
            start.setColumn(0);
            end.setColumn(nbColumn - 1);
            start.setRow(random.nextInt(nbRow));

            end.setRow(start.getRow()<nbRow/2?random.nextInt(nbRow/2,nbRow):random.nextInt(0,nbRow/2));
        } else {
            start.setRow(0);
            end.setRow(nbRow - 1);
            start.setColumn(random.nextInt(nbColumn));
            end.setColumn(start.getColumn()<nbColumn/2?random.nextInt(nbColumn/2,nbColumn):random.nextInt(0,nbColumn/2));
        }
        while (start.getRow() == end.getRow() && start.getColumn() == end.getColumn()) {
            if (isVertical) {
                end.setRow(random.nextInt(nbRow));
            } else {
                end.setColumn(random.nextInt(nbColumn));
            }
        }
    }

    private List<Node> getNeighbors( Node current) {
        List<Node> neighbors = new ArrayList<>();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] direction : directions) {
            int newRow = current.incrementRow(direction[0]);
            int newCol = current.incrementColumn(direction[1]);

            if (newRow >= 0 && newRow < nbRow &&
                    newCol >= 0 && newCol < nbColumn) {
                neighbors.add(maze[newRow][newCol]);
            }
        }
        return neighbors;
    }
    private List<Node> getVisitedNeighbors(List<Node> neighbors) {
        List<Node> visitedNeighbors = new ArrayList<>();
        for (Node node : neighbors) {
            if (node.isPartOfMaze()) {
                visitedNeighbors.add(node);
            }
        }
        return visitedNeighbors;
    }
    private void removeWallBetween(Node current, Node next) {
        int rowDiff = next.getRow() - current.getRow();
        int colDiff = next.getColumn() - current.getColumn();

        if (rowDiff == -1) {
            current.removeBorder(0);
            next.removeBorder(2);
        } else if (rowDiff == 1) {
            current.removeBorder(2);
            next.removeBorder(0);
        } else if (colDiff == -1) {
            current.removeBorder(3);
            next.removeBorder(1);
        } else if (colDiff == 1) {
            current.removeBorder(1);
            next.removeBorder(3);
        }
    }

    public void generateMaze() {
        Node current = start;
        List<Node> frontier = new ArrayList<>();
        current.setPartOfMaze(true);
        int nb=0;
        int index=0;
        String word=dictionary.get(nb);
        frontier.add(current);
        do {
            current = frontier.get(random.nextInt(frontier.size()));
            maze[current.getRow()][current.getColumn()].setValue(word.charAt(index));
            index++;
            // mark current node as visited
            maze[current.getRow()][current.getColumn()].setPartOfMaze(true);
            // get the visited neighbors
            List<Node> visitedNeighbors = getVisitedNeighbors(getNeighbors(current));
            if (!visitedNeighbors.isEmpty()) {
                // choose random visited neighbor
                Node neighbor = visitedNeighbors.get(random.nextInt(visitedNeighbors.size()));
                removeWallBetween(maze[current.getRow()][current.getColumn()], maze[neighbor.getRow()][neighbor.getColumn()]);
            }
            //add unvisited neighbors to the frontier set
            for (Node neighbor : getNeighbors(current)) {
                    if (!neighbor.isPartOfMaze() && !frontier.contains(neighbor)) {
                        frontier.add(neighbor);
                    }
                }
                if(nb < dictionary.size() - 1){
                    if (index == word.length()  ) {
                        nb++;
                        word = dictionary.get(nb);
                        index = 0;
                    }
                }else{
                    dictionary=mazePopulator.getData();
                }
                frontier.remove(current);
            }while(!frontier.isEmpty());
        }
    public void createLoops() {
        int numberOfLoops=(int)(nbRow*nbColumn*0.3 );
        int loopsCreated = 0;
        while (loopsCreated < numberOfLoops) {
            int col = random.nextInt(nbColumn);
            int row = random.nextInt(nbRow);
            int border = random.nextInt(4);
            if (isBorderValid(row, col, border)) {
                maze[row][col].removeBorder(border);
                int neighborRow = row;
                int neighborCol = col;
                switch (border) {
                    case 0:
                        neighborRow--;
                        break;
                    case 1:
                        neighborCol++;
                        break;
                    case 2:
                        neighborRow++;
                        break;
                    case 3:
                        neighborCol--;
                        break;
                }

                int oppositeBorder = (border + 2) % 4;
                maze[neighborRow][neighborCol].removeBorder(oppositeBorder);

                loopsCreated++;
            }
        }
    }
    private boolean isBorderValid(int row, int col, int border) {
        switch (border) {
            case 0:
                return row > 0;
            case 1:
                return col < nbColumn - 1;
            case 2:
                return row < nbRow - 1;
            case 3:
                return col > 0;
            default:
                return false;
        }
    }
        public void printMaze() {
            for (int i = 0; i < nbRow; i++) {
                for (int j = 0; j < nbColumn; j++) {
                System.out.print(maze[i][j].isPartOfMaze() ? maze[i][j].getValue() : "#");
            }
            System.out.println();
        }
    }


}
