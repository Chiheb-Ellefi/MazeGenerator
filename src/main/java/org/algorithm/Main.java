package org.algorithm;

import org.algorithm.dfs.MazeGenerator;
import org.algorithm.hexa_maze.HexaMaze;
import org.algorithm.prim_maze.PrimAlgorithm;

public class Main {
    public static void main(String[] args) {
     /*  MazeGenerator mazeGenerator=new MazeGenerator(10,10);
       mazeGenerator.generateMaze();
        PrimAlgorithm primAlgorithm=new PrimAlgorithm(10,10);
        primAlgorithm.generateMaze();
        primAlgorithm.setStartAndEnd();
        primAlgorithm.printMaze();*/
        HexaMaze mazeGenerator=new HexaMaze(10,10);
        mazeGenerator.generateMaze();
        mazeGenerator.printMaze();

    }
}