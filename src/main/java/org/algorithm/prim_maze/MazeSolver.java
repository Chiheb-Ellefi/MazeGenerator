package org.algorithm.prim_maze;

import org.algorithm.components.Node;

public class MazeSolver {
    private final  Node[][] maze;
    private final  Node start ;
    private final  Node end;
    public MazeSolver(Node[][] maze,Node start,Node end){
     this.maze=maze;
     this.start=start;
     this.end=end;
    }
}
