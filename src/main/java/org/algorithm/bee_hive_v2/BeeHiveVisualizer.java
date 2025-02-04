package org.algorithm.bee_hive_v2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.algorithm.components.HiveCell;

public class BeeHiveVisualizer extends Application {
    private static final double HEX_SIZE = 30;
    private BeeHive beeHive;

    @Override
    public void start(Stage primaryStage) {
        int width = 5;
        int height = 5;
        beeHive = new BeeHive(width, height);

        Pane root = new Pane();
        beeHive.generateHive();
        drawHive(root);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Bee Hive Visualization");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void drawHive(Pane root) {
        for (int q = 0; q < beeHive.getWidth(); q++) {
            for (int r = 0; r < beeHive.getHeight(); r++) {
                HiveCell cell = beeHive.getGrid()[q][r];
                // Convert axial coordinates (q, r) to screen coordinates
                double x = HEX_SIZE * (3.0 / 2 * q);
                double y = HEX_SIZE * (Math.sqrt(3) * (r + 0.5 * q));
                char value = cell.getValue();

                drawHexagonWalls(root, x, y, cell);
                Label label = createLabel(x, y, value);
                root.getChildren().add(label);
            }
        }
    }

    private void drawHexagonWalls(Pane root, double centerX, double centerY, HiveCell cell) {
        double[] angles = new double[6];
        double[] xPoints = new double[6];
        double[] yPoints = new double[6];

        // Calculate the points of the hexagon
        for (int i = 0; i < 6; i++) {
            angles[i] = 2 * Math.PI / 6 * i;
            xPoints[i] = centerX + HEX_SIZE * Math.cos(angles[i]);
            yPoints[i] = centerY + HEX_SIZE * Math.sin(angles[i]);
        }

        // Mapping from hexagon edge index to HiveCell direction
        int[] edgeToHiveDirection = {0, 5, 4, 3, 2, 1};

        for (int edge = 0; edge < 6; edge++) {
            int hiveDir = edgeToHiveDirection[edge];
            boolean shouldDrawWall = !cell.getPointsAt()[hiveDir];

            if (shouldDrawWall) {
                Line wall = new Line(
                        xPoints[edge],
                        yPoints[edge],
                        xPoints[(edge + 1) % 6],
                        yPoints[(edge + 1) % 6]
                );
                wall.setStroke(Color.BLACK);
                wall.setStrokeWidth(1.5);
                root.getChildren().add(wall);
            }
        }
    }

    private Label createLabel(double x, double y, char value) {
        Label label = new Label(String.valueOf(value));
        label.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        label.setTextFill(Color.BLACK);
        label.setLayoutX(x - 10);
        label.setLayoutY(y - 10);
        return label;
    }

    public static void main(String[] args) {
        launch(args);
    }
}