package org.algorithm.dfs;

import org.algorithm.data.MazePopulator;
import org.algorithm.components.Node;

import java.util.*;

public class MazeGenerator {
    private final int nbColumn;
    private final  int nbRow;
    private final  Node[][] maze;
    private final Random random;
    private   List<String> dictionary;
    private final MazePopulator mazePopulator;
    private Node start;
    private Node end;
    public MazeGenerator(int nbColumn, int nbRow) {
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
    void setStartAndEnd() {
        boolean isVertical = random.nextBoolean();
        if (isVertical) {
            start.setColumn(0);
            end.setColumn(nbColumn - 1);
            start.setRow(random.nextInt(nbRow));
            end.setRow(random.nextInt(nbRow));
        } else {
            start.setRow(0);
            end.setRow(nbRow - 1);
            start.setColumn(random.nextInt(nbColumn));
            end.setColumn(random.nextInt(nbColumn));
        }
        while (start.getRow() == end.getRow() && start.getColumn() == end.getColumn()) {
            if (isVertical) {
                end.setRow(random.nextInt(nbRow));
            } else {
                end.setColumn(random.nextInt(nbColumn));
            }
        }
    }
    private void initializeVisited(Boolean[][] visited) {
        for (int y = 0; y < nbRow; y++) {
            for (int x = 0; x < nbColumn; x++) {
                visited[y][x] = false;
            }
        }
    }

    private List<Node> getUnvisitedNeighbors(Boolean[][] visited, Node current) {
        List<Node> unvisitedNeighbors = new ArrayList<>();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int i = 0; i < directions.length; i++) {
            int newRow = current.incrementRow(directions[i][0]);
            int newCol = current.incrementColumn(directions[i][1]);

            if (newRow >= 0 && newRow < nbRow &&
                    newCol >= 0 && newCol < nbColumn &&
                    !visited[newRow][newCol]) {
                unvisitedNeighbors.add(maze[newRow][newCol]);
            }
        }
        return unvisitedNeighbors;
    }

    public void generateMaze() {
        setStartAndEnd();
        Stack<Node> stack = new Stack<>();
        Stack<Character> injected = new Stack<>();
        Stack<Character> toInject = new Stack<>();
        Boolean[][] visited = new Boolean[nbRow][nbColumn];
        initializeVisited(visited);
        int index = 0;
        int word = 0;
        String currentWord = dictionary.get(word);
        Node current;
        char currentChar;

        do {
            if (stack.isEmpty()) {
                current = maze[start.getRow()][start.getColumn()];
                currentChar = currentWord.charAt(index);
                injected.push(currentChar);
                current.setValue(currentChar);
                index++;
                stack.push(current);
                visited[start.getRow()][start.getColumn()] = true;
            } else {
                current = stack.peek();
            }

            List<Node> unvisitedNeighbors = getUnvisitedNeighbors(visited, current);
            if (!unvisitedNeighbors.isEmpty()) {
                Node nextCell = unvisitedNeighbors.get(random.nextInt(unvisitedNeighbors.size()));
                removeWallBetween(current, nextCell);
                stack.push(nextCell);
                visited[nextCell.getRow()][nextCell.getColumn()] = true;
                current = nextCell;

                // Inject characters from the current word
                if (toInject.isEmpty()) {
                    currentChar = currentWord.charAt(index);
                    index++;
                } else {
                    currentChar = toInject.pop();
                }
                injected.push(currentChar);
                current.setValue(currentChar);
            } else {
                current = stack.pop();
                currentChar = injected.pop();
                toInject.push(currentChar);
            }

            // Move to the next word if the current word is fully injected
            if(word < dictionary.size() - 1){
                if (index == currentWord.length()  ) {
                    word++;
                    currentWord = dictionary.get(word);
                    index = 0;
                }
            }else{
                dictionary=mazePopulator.getData();
            }

        } while (!stack.isEmpty());
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
    public void printMaze() {
        for (int y = 0; y < nbRow; y++) {
            for (int x = 0; x < nbColumn; x++) {
                System.out.print(maze[y][x].getValue());
            }
            System.out.println();
        }
    }

}