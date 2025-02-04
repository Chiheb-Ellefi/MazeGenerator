package org.algorithm.dfs;

import org.algorithm.components.Node;
import org.algorithm.data.MazePopulator;

import java.util.*;

public class MazeGenerator {
    private Set<String> dictionaryLower = new HashSet<>();
    private final int nbColumn;

    public int getNbRow() {
        return nbRow;
    }

    private final  int nbRow;
    private final  Node[][] maze;
    private final Random random;
    private   List<String> dictionary;
    private final MazePopulator mazePopulator;
    private Node start;
    private Node end;

    public int getNbColumn() {
        return nbColumn;
    }

    public MazeGenerator(int nbColumn, int nbRow) {
        this.nbColumn = nbColumn;
        this.nbRow = nbRow;
        this.maze = new Node[nbRow][nbColumn];
        this.start = new Node(0, 0);
        this.end = new Node(nbRow - 1, nbColumn - 1);
        this.random = new Random();
        this.mazePopulator = new MazePopulator();

        // Initialiser le labyrinthe
        for (int i = 0; i < nbRow; i++) {
            for (int j = 0; j < nbColumn; j++) {
                maze[i][j] = new Node(i, j);
            }
        }

        this.dictionary = mazePopulator.getData();

        if (this.dictionary == null || this.dictionary.isEmpty()) {
            this.dictionary = List.of("default");
        }

        for (String word : this.dictionary) {
            dictionaryLower.add(word.toLowerCase());
        }
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

    //check word
    public boolean containsWord(String word) {
        return dictionaryLower.contains(word.toLowerCase());
    }

    void setStartAndEnd() {
        boolean isVertical = random.nextBoolean();
        int startRow, startCol, endRow, endCol;

        if (isVertical) {
            startCol = 0;
            endCol = nbColumn - 1;
            startRow = random.nextInt(nbRow);
            endRow = random.nextInt(nbRow);
        } else {
            startRow = 0;
            endRow = nbRow - 1;
            startCol = random.nextInt(nbColumn);
            endCol = random.nextInt(nbColumn);
        }

        // Éviter les positions identiques
        while (startRow == endRow && startCol == endCol) {
            if (isVertical) endRow = random.nextInt(nbRow);
            else endCol = random.nextInt(nbColumn);
        }

        // Pointer vers les nœuds existants dans le labyrinthe
        this.start = maze[startRow][startCol];
        this.end = maze[endRow][endCol];
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
        // Les 8 directions : 4 cardinales + 4 diagonales
        int[][] directions = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1},
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };

        for (int[] direction : directions) {
            int newRow = current.incrementRow(direction[0]);
            int newCol = current.incrementColumn(direction[1]);

            // Vérifier que la nouvelle position est dans le labyrinthe
            if (newRow >= 0 && newRow < nbRow &&
                    newCol >= 0 && newCol < nbColumn &&
                    !visited[newRow][newCol]) {

                // Pour un déplacement diagonal, vérifier que les deux cellules
                // cardinales adjacentes sont disponibles (non visitées)
                if (Math.abs(direction[0]) == 1 && Math.abs(direction[1]) == 1) {
                    int adjacentRow1 = current.getRow() + direction[0];
                    int adjacentCol1 = current.getColumn();
                    int adjacentRow2 = current.getRow();
                    int adjacentCol2 = current.getColumn() + direction[1];

                    if (adjacentRow1 < 0 || adjacentRow1 >= nbRow ||
                            adjacentCol2 < 0 || adjacentCol2 >= nbColumn) {
                        continue; // hors bornes
                    }

                    if (!visited[adjacentRow1][adjacentCol1] || !visited[adjacentRow2][adjacentCol2]) {
                        // On n’autorise le déplacement diagonal que si les deux cellules adjacentes
                        // ont déjà été visitées (ce qui permet de "lier" la diagonale via leurs passages)
                        // Vous pouvez inverser la condition si vous préférez ne creuser la diagonale que
                        // lorsque les deux passages existent.
                        continue;
                    }
                }
                unvisitedNeighbors.add(maze[newRow][newCol]);
            }
        }
        return unvisitedNeighbors;
    }

    private void removeWallBetweenDiagonal(Node current, Node next) {
        int rowDiff = next.getRow() - current.getRow();
        int colDiff = next.getColumn() - current.getColumn();

        // Déterminer les deux cellules cardinales intermédiaires
        // Par exemple, pour un déplacement Nord-Est (rowDiff = -1, colDiff = 1):
        // - adjacent1 est la cellule à l'Est de current
        // - adjacent2 est la cellule au Nord de current
        Node adjacent1 = maze[current.getRow()][current.getColumn() + colDiff]; // même ligne, colonne décalée
        Node adjacent2 = maze[current.getRow() + rowDiff][current.getColumn()]; // même colonne, ligne décalée

        // On supprime les murs entre current et adjacent1 puis entre adjacent1 et next
        removeWallBetween(current, adjacent1);
        removeWallBetween(adjacent1, next);

        // On supprime également les murs entre current et adjacent2 puis entre adjacent2 et next
        removeWallBetween(current, adjacent2);
        removeWallBetween(adjacent2, next);
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
                // Vérifier si le mouvement est diagonal
                int rowDiff = nextCell.getRow() - current.getRow();
                int colDiff = nextCell.getColumn() - current.getColumn();
                if (Math.abs(rowDiff) == 1 && Math.abs(colDiff) == 1) {
                    removeWallBetweenDiagonal(current, nextCell);
                } else {
                    removeWallBetween(current, nextCell);
                }
                stack.push(nextCell);
                visited[nextCell.getRow()][nextCell.getColumn()] = true;
                current = nextCell;
                // Injection du caractère (inchangé)
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
    public void createLoops() {
        int numberOfLoops=(int)(nbRow*nbColumn*0.3 );
        int loopsCreated = 0;
        while (loopsCreated < numberOfLoops) {
            int col = random.nextInt(nbColumn);
            int row = random.nextInt(nbRow);
            int border = random.nextInt(4);
            if (isBorderValid(row, col, border)) {
                maze[row][col].removeBorder(border);

                // Determine the neighboring cell
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
            case 0: // North
                return row > 0;
            case 1: // East
                return col < nbColumn - 1;
            case 2: // South
                return row < nbRow - 1;
            case 3: // West
                return col > 0;
            default:
                return false;
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

    public void replaceWordNodes(List<Node> nodes) {
        Random rand = new Random();
        for (Node node : nodes) {
            char randomChar = (char) (rand.nextInt(26) + 'A'); // Lettre majuscule aléatoire
            node.setValue(randomChar);
        }
    }



    public MazePopulator getMazePopulator() {
        return mazePopulator;
    }

    public char getRandomChar() {
        return (char) ('a' + random.nextInt(26));
    }

    public boolean isEndNode(Node node) {
        return node.getRow() == end.getRow() && node.getColumn() == end.getColumn();
    }


}