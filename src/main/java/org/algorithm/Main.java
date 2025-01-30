package org.algorithm;


import org.algorithm.bee_hive_v2.BeeHive;
import org.algorithm.components.HiveCell;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
     /*  MazeGenerator mazeGenerator=new MazeGenerator(10,10);
       mazeGenerator.generateMaze();
        PrimAlgorithm primAlgorithm=new PrimAlgorithm(10,10);
        primAlgorithm.generateMaze();
        primAlgorithm.setStartAndEnd();
        primAlgorithm.printMaze();*/
        BeeHive hive = new BeeHive(3, 3);
        hive.generateHive();

        // Print the grid to verify the maze generation
        for (int q = 0; q < hive.getWidth(); q++) {
            for (int r = 0; r < hive.getHeight(); r++) {
                HiveCell cell = hive.getGrid()[q][r];
                HiveCell[] neighbors = cell.getNeighbors();

                System.out.print(cell.isPartOfMaze()
                        ? cell.getValue() + " : " + Arrays.toString(cell.getPointsAt())
                        : ". ");
            }
            System.out.println();
        }

    }
}