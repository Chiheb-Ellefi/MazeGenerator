package org.algorithm.hexa_maze;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.algorithm.components.HexaNode;
import org.algorithm.hexa_maze.HexaMaze;

public class HexMazeVisualizer extends Application {

    private static final int CELL_SIZE = 30; // Size of each cell in pixels
    private static final int MAZE_WIDTH = 4; // Number of columns in the maze
    private static final int MAZE_HEIGHT = 4; // Number of rows in the maze

    private Pane mazePane; // Pane to hold the maze cells
    private HexaMaze mazeGenerator; // Maze generator instance

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        mazePane = new Pane();
        root.setCenter(mazePane);

        Button regenerateButton = new Button("Regenerate Maze");
        regenerateButton.setOnAction(event -> regenerateMaze());
        root.setBottom(regenerateButton);
        BorderPane.setAlignment(regenerateButton, Pos.CENTER);

        regenerateMaze();

        Scene scene = new Scene(root, MAZE_WIDTH * CELL_SIZE * 1.5, MAZE_HEIGHT * CELL_SIZE * 1.5 + 40);
        primaryStage.setTitle("Hex Maze Visualizer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void regenerateMaze() {
        mazePane.getChildren().clear();
        mazePane.setPadding(new Insets(100, 100, 100, 100));

        mazeGenerator = new HexaMaze(MAZE_WIDTH, MAZE_HEIGHT);
        mazeGenerator.generateMaze();
     mazeGenerator.printMaze() ;


        HexaNode startNode = mazeGenerator.getStart();
        HexaNode endNode = mazeGenerator.getEnd();

        for (int y = 0; y < MAZE_HEIGHT; y++) {
            for (int x = 0; x < MAZE_WIDTH; x++) {
                HexaNode node = mazeGenerator.getMaze()[y][x];
                boolean[] borders = node.getBorders();
                char value = node.getValue();

                double centerX = x * CELL_SIZE * 1.5 + CELL_SIZE;
                double centerY = y * CELL_SIZE * Math.sqrt(3) + CELL_SIZE;

                if (x % 2 == 1) {
                    centerY += CELL_SIZE * Math.sqrt(3) / 2;
                }

                double[] xPoints = new double[6];
                double[] yPoints = new double[6];
                for (int i = 0; i < 6; i++) {
                    double angle = 2 * Math.PI / 6 * i;
                    xPoints[i] = centerX + CELL_SIZE * Math.cos(angle);
                    yPoints[i] = centerY + CELL_SIZE * Math.sin(angle);
                }

                for (int i = 0; i < 6; i++) {
                    if (borders[i]) {
                        int nextIndex = (i + 1) % 6;
                        mazePane.getChildren().add(new Line(xPoints[i], yPoints[i], xPoints[nextIndex], yPoints[nextIndex]));
                    }
                }

                Label label = new Label(String.valueOf(value));
                label.setFont(Font.font("Arial", FontWeight.BOLD, 20));
                label.setTextFill(Color.BLACK);
                label.setLayoutX(centerX - CELL_SIZE / 2);
                label.setLayoutY(centerY - CELL_SIZE / 2);
                label.setAlignment(Pos.CENTER);

                if (node.getRow() == startNode.getRow() && node.getColumn() == startNode.getColumn()) {
                    label.setTextFill(Color.GREEN);
                } else if (node.getRow() == endNode.getRow() && node.getColumn() == endNode.getColumn()) {
                    label.setTextFill(Color.RED);
                }

                mazePane.getChildren().add(label);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}