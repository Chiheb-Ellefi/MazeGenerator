package org.algorithm.dfs;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.algorithm.components.Node;

import java.util.*;

public class MazeVisualizer extends Application {

    // Liste des noeuds du chemin visité
    private List<Node> currentPathNodes = new ArrayList<>();
    // Gestion du score via la classe Score
    private Score scoreCalculator;
    // Liste des noeuds à remplacer lorsqu’un mot est trouvé
    private List<Node> pendingNodesReplacement = new ArrayList<>();
    // Indique si le jeu est actif (si false, aucun déplacement n'est autorisé)
    private boolean isGameActive = false;

    private int moveCount;
    private Label moveCountLabel;
    private HBox controlBox;
    private int shortestPathLength = -1;

    private static final int CELL_SIZE = 35;
    private static final int MAZE_WIDTH = 25;
    private static final int MAZE_HEIGHT = 15;

    private Pane mazePane;
    private MazeGenerator mazeGenerator;
    private Circle player;
    private int playerRow;
    private int playerCol;
    private Node startNode;
    private Node endNode;
    private Label[][] valueLabels;
    // Nouveau tableau pour stocker les rectangles de fond de chaque case
    private Rectangle[][] cellRectangles;
    private Label scoreLabel;
    private Label timeLabel;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #2b2b2b;");

        controlBox = new HBox(15);
        controlBox.setAlignment(Pos.CENTER);
        controlBox.setPadding(new Insets(15));
        controlBox.setStyle("-fx-background-color: #3c3f41;");

        mazePane = new Pane();
        mazePane.setBackground(new Background(new BackgroundFill(Color.web("#3c3f41"), CornerRadii.EMPTY, Insets.EMPTY)));

        moveCountLabel = new Label("Moves: 0 (Shortest: ?)");
        moveCountLabel.setTextFill(Color.WHITE);
        moveCountLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        scoreLabel = new Label("Score: 0");
        scoreLabel.setTextFill(Color.WHITE);
        scoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        timeLabel = new Label("Time: 60");
        timeLabel.setTextFill(Color.WHITE);
        timeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        // Création des boutons de déplacement et du bouton "New Game"
        Button[] buttons = {
                createStyledButton("↑", "-fx-base: #4CAF50;", -1, 0),
                createStyledButton("↓", "-fx-base: #4CAF50;", 1, 0),
                createStyledButton("←", "-fx-base: #2196F3;", 0, -1),
                createStyledButton("→", "-fx-base: #2196F3;", 0, 1),
                createStyledButton("↖", "-fx-base: #9C27B0;", -1, -1),
                createStyledButton("↗", "-fx-base: #9C27B0;", -1, 1),
                createStyledButton("↙", "-fx-base: #9C27B0;", 1, -1),
                createStyledButton("↘", "-fx-base: #9C27B0;", 1, 1),
                createStyledButton("New Game", "-fx-base: #FF5722;", 0, 0)
        };

        // Le bouton "New Game" appelle la régénération du labyrinthe
        buttons[8].setOnAction(event -> regenerateMaze());

        controlBox.getChildren().addAll(buttons);
        controlBox.getChildren().addAll(moveCountLabel, scoreLabel, timeLabel);

        root.setCenter(mazePane);
        root.setBottom(controlBox);

        regenerateMaze();

        Scene scene = new Scene(root, MAZE_WIDTH * CELL_SIZE + 40, MAZE_HEIGHT * CELL_SIZE + 120);
        primaryStage.setTitle("Mystery Maze");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createStyledButton(String text, String style, int deltaRow, int deltaCol) {
        Button button = new Button(text);
        button.setStyle(style + " -fx-text-fill: white; -fx-font-weight: bold;");
        button.setFont(Font.font("Arial", 16));
        button.setMinSize(50, 50);
        if (!text.equals("New Game")) {
            button.setOnAction(e -> movePlayer(deltaRow, deltaCol));
        }
        return button;
    }

    private void movePlayer(int deltaRow, int deltaCol) {
        if (!isGameActive) return;

        if (!pendingNodesReplacement.isEmpty()) {
            mazeGenerator.replaceWordNodes(pendingNodesReplacement);
            updateMazeDisplay(pendingNodesReplacement);
            pendingNodesReplacement.clear();
        }

        int newRow = playerRow + deltaRow;
        int newCol = playerCol + deltaCol;

        if (isValidMove(playerRow, playerCol, newRow, newCol)) {
            updatePlayerPosition(newRow, newCol);

            Node currentNode = mazeGenerator.getMaze()[newRow][newCol];
            currentPathNodes.add(currentNode);

            List<Node> nodesToReplace = scoreCalculator.updateScore(currentPathNodes, mazeGenerator);
            if (!nodesToReplace.isEmpty()) {
                pendingNodesReplacement.addAll(nodesToReplace);
            }
            scoreLabel.setText("Score: " + scoreCalculator.getScore());

            // Colorie immédiatement la case visitée
            cellRectangles[newRow][newCol].setFill(Color.LIGHTBLUE);

            // Si le joueur atteint la case finale, on désactive le jeu et on affiche l'alerte de victoire.
            if (playerRow == endNode.getRow() && playerCol == endNode.getColumn()) {
                isGameActive = false;
                scoreCalculator.victory();
            }
        }
    }

    private boolean isValidMove(int oldRow, int oldCol, int newRow, int newCol) {
        if (newRow < 0 || newRow >= MAZE_HEIGHT || newCol < 0 || newCol >= MAZE_WIDTH) return false;

        Node currentNode = mazeGenerator.getMaze()[oldRow][oldCol];
        int rowDiff = newRow - oldRow;
        int colDiff = newCol - oldCol;

        if (Math.abs(rowDiff) == 1 && Math.abs(colDiff) == 1) {
            boolean verticalWallCheck = rowDiff == -1 ? !currentNode.getBorders()[0] : !currentNode.getBorders()[2];
            boolean horizontalWallCheck = colDiff == 1 ? !currentNode.getBorders()[1] : !currentNode.getBorders()[3];
            return verticalWallCheck && horizontalWallCheck;
        } else {
            int border = getBorderDirection(oldRow, oldCol, newRow, newCol);
            return !currentNode.getBorders()[border];
        }
    }

    private int getBorderDirection(int oldRow, int oldCol, int newRow, int newCol) {
        if (newRow < oldRow) return 0;
        if (newRow > oldRow) return 2;
        if (newCol > oldCol) return 1;
        return 3;
    }

    private void updatePlayerPosition(int newRow, int newCol) {
        playerRow = newRow;
        playerCol = newCol;
        player.setCenterX(newCol * CELL_SIZE + CELL_SIZE / 2);
        player.setCenterY(newRow * CELL_SIZE + CELL_SIZE / 2);
        moveCount++;
        updateMoveCounter();
    }

    private void updateMoveCounter() {
        String shortest = shortestPathLength != -1 ? String.valueOf(shortestPathLength) : "?";
        moveCountLabel.setText("Moves: " + moveCount + " (Shortest: " + shortest + ")");
    }

    /**
     * Régénère le labyrinthe et réinitialise toutes les variables associées.
     */
    private void regenerateMaze() {
        if (scoreCalculator != null) {
            scoreCalculator.reset();
        }

        isGameActive = true;
        mazePane.getChildren().clear();
        moveCount = 0;

        mazeGenerator = new MazeGenerator(MAZE_WIDTH, MAZE_HEIGHT);
        mazeGenerator.generateMaze();
        mazeGenerator.createLoops();

        startNode = mazeGenerator.getStart();
        endNode = mazeGenerator.getEnd();
        playerRow = startNode.getRow();
        playerCol = startNode.getColumn();

        currentPathNodes.clear();
        currentPathNodes.add(startNode);
        scoreCalculator = new Score();
        scoreCalculator.setGameEndCallback(() -> Platform.runLater(() -> regenerateMaze()));
        scoreLabel.setText("Score: 0");
        pendingNodesReplacement.clear();

        // Initialisation des tableaux pour les labels et les backgrounds
        valueLabels = new Label[MAZE_HEIGHT][MAZE_WIDTH];
        cellRectangles = new Rectangle[MAZE_HEIGHT][MAZE_WIDTH];

        // Calcul du plus court chemin à l'aide de Dijkstra
        Dijkstra dijkstra = new Dijkstra(mazeGenerator.getMaze(), MAZE_HEIGHT, MAZE_WIDTH, startNode, endNode);
        shortestPathLength = dijkstra.calculateShortestPath();
        updateMoveCounter();

        // Pour chaque cellule, on crée un rectangle de fond, on dessine les murs et on ajoute le label (ou l'affichage spécial)
        for (int y = 0; y < MAZE_HEIGHT; y++) {
            for (int x = 0; x < MAZE_WIDTH; x++) {
                Node node = mazeGenerator.getMaze()[y][x];
                double startX = x * CELL_SIZE;
                double startY = y * CELL_SIZE;

                // Crée et ajoute le rectangle de fond (initialement transparent)
                Rectangle bgRect = new Rectangle(startX, startY, CELL_SIZE, CELL_SIZE);
                bgRect.setFill(Color.TRANSPARENT);
                mazePane.getChildren().add(bgRect);
                cellRectangles[y][x] = bgRect;

                // Dessine les murs de la cellule
                drawWalls(startX, startY, node.getBorders());

                // Affiche la case spéciale de départ ou d'arrivée, ou sinon la valeur
                if (node.getRow() == startNode.getRow() && node.getColumn() == startNode.getColumn()) {
                    drawSpecialCell(startX, startY, String.valueOf(node.getValue()), "#4CAF50", "🚩");
                } else if (node.getRow() == endNode.getRow() && node.getColumn() == endNode.getColumn()) {
                    drawSpecialCell(startX, startY, String.valueOf(node.getValue()), "#FF5722", "🏁");
                } else {
                    createValueLabel(startX, startY, node.getValue(), y, x);
                }
            }
        }

        createPlayer();
        // Démarre le timer : en cas d'expiration, désactive le jeu et appelle gameOver.
        scoreCalculator.startTimer(timeLabel, () -> {
            isGameActive = false;
            scoreCalculator.gameOver(moveCount, shortestPathLength);
        });
    }

    private void drawWalls(double x, double y, boolean[] borders) {
        Line[] walls = {
                new Line(x, y, x + CELL_SIZE, y),
                new Line(x + CELL_SIZE, y, x + CELL_SIZE, y + CELL_SIZE),
                new Line(x, y + CELL_SIZE, x + CELL_SIZE, y + CELL_SIZE),
                new Line(x, y, x, y + CELL_SIZE)
        };

        for (int i = 0; i < 4; i++) {
            if (borders[i]) {
                walls[i].setStroke(Color.web("#616161"));
                walls[i].setStrokeWidth(3);
                mazePane.getChildren().add(walls[i]);
            }
        }
    }

    private void createValueLabel(double x, double y, char value, int row, int col) {
        Label label = new Label(String.valueOf(value));
        label.setStyle("-fx-text-fill: #E0E0E0; -fx-effect: dropshadow(one-pass-box, black, 2, 0.5, 0, 0);");
        label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        label.setLayoutX(x + 12);
        label.setLayoutY(y + 10);
        mazePane.getChildren().add(label);
        valueLabels[row][col] = label;
    }

    private void drawSpecialCell(double x, double y, String value, String color, String emoji) {
        Rectangle rect = new Rectangle(x + 2, y + 2, CELL_SIZE - 4, CELL_SIZE - 4);
        rect.setFill(Color.web(color + "80"));
        rect.setStroke(Color.web(color));
        rect.setStrokeWidth(3);
        rect.setArcWidth(10);
        rect.setArcHeight(10);

        Label label = new Label(emoji + " " + value);
        label.setStyle("-fx-font-family: 'Segoe UI Emoji'; -fx-font-size: 14; -fx-text-fill: " + color + "; -fx-font-weight: bold;");
        label.setLayoutX(x + 5);
        label.setLayoutY(y + 8);

        mazePane.getChildren().addAll(rect, label);
    }

    private void createPlayer() {
        player = new Circle(CELL_SIZE / 3);
        player.setFill(Color.TRANSPARENT);
        player.setStroke(Color.web("#2196F3"));
        player.setStrokeWidth(2);
        updatePlayerPosition(playerRow, playerCol);
        mazePane.getChildren().add(player);
    }

    private void updateMazeDisplay(List<Node> nodes) {
        for (Node node : nodes) {
            int row = node.getRow();
            int col = node.getColumn();
            if (valueLabels[row][col] != null) {
                valueLabels[row][col].setText(String.valueOf(node.getValue()));
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
