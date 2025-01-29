package org.algorithm.bee_hive;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.algorithm.bee_hive.BeeHive;
import org.algorithm.components.HexaNode;

public class BeeHiveVisualizer extends Application {
    private static final double RADIUS = 30; // Increased radius for better visibility
    private static final double HORIZONTAL_SPACING = RADIUS * 2; // Increased for proper cell spacing
    private static final double VERTICAL_SPACING = RADIUS * Math.sqrt(3); // Correct vertical spacing
    private static final double PADDING = 50; // Add padding around the visualization
    private BeeHive beeHive;

    @Override
    public void start(Stage primaryStage) {
        beeHive = new BeeHive(10, 10);
        beeHive.generateHive();
        Group root = new Group();
        drawHive(root);
        Scene scene = new Scene(root, 1000, 800, Color.WHITE);
        primaryStage.setTitle("Hexagonal Bee Hive Maze");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void drawHive(Group root) {
        HexaNode[][] hive = beeHive.getHive();

        for (int row = 0; row < hive.length; row++) {
            for (int col = 0; col < hive[row].length; col++) {
                HexaNode node = hive[row][col];
                if (node == null) continue;

                // Calculate center position with padding
                double centerX = PADDING + col * HORIZONTAL_SPACING + (row % 2) * (HORIZONTAL_SPACING / 2);
                double centerY = PADDING + row * VERTICAL_SPACING;

                // Draw the cell value
                char value = node.getValue();
                Label label = new Label(String.valueOf(value));
                label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                label.setTextFill(Color.BLACK);

                // Center the label
                label.setLayoutX(centerX - label.getWidth() / 2);
                label.setLayoutY(centerY - label.getHeight() / 2);
                root.getChildren().add(label);

                // Calculate hexagon points
                double[][] points = calculateHexagonPoints(centerX, centerY);

                // Draw walls
                boolean[] borders = node.getBorders();
                for (int dir = 0; dir < 6; dir++) {
                    if (borders[dir]) {
                        int nextDir = (dir + 1) % 6;
                        Line wall = new Line(
                                points[dir][0], points[dir][1],
                                points[nextDir][0], points[nextDir][1]
                        );
                        wall.setStroke(Color.BLACK);
                        wall.setStrokeWidth(2);
                        root.getChildren().add(wall);
                    }
                }
            }
        }
    }

    private double[][] calculateHexagonPoints(double centerX, double centerY) {
        double[][] points = new double[6][2];
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i - 30);
            points[i][0] = centerX + RADIUS * Math.cos(angle);
            points[i][1] = centerY + RADIUS * Math.sin(angle);
        }
        return points;
    }

    public static void main(String[] args) {
        launch(args);
    }
}