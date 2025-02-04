package org.algorithm.components;

public class Node {
    private int row;
    private int column;
    private boolean[] borders;
    private char value;
    private boolean partOfMaze; // New field to track if the node is part of the maze
    private boolean onCurrentPath;
    private boolean visited;

    public Node(int row, int column) {
        this.row = row;
        this.column = column;
        this.borders = new boolean[]{true, true, true, true,true,true,true,true};
        this.partOfMaze = false; // Initially, the node is not part of the maze
        this.onCurrentPath = false;
        this.visited=false;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public void setValue(char value) {
        this.value = value;
    }

    public char getValue() {
        return value;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public boolean[] getBorders() {
        return borders;
    }

    public void setBorders(boolean[] borders) {
        this.borders = borders;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public void removeBorder(int direction) {
        borders[direction] = false;
    }

    public int incrementRow(int increment) {
        return row + increment;
    }

    public int incrementColumn(int increment) {
        return column + increment;
    }

    // New method to check if the node is part of the maze
    public boolean isPartOfMaze() {
        return partOfMaze;
    }

    // New method to set whether the node is part of the maze
    public void setPartOfMaze(boolean partOfMaze) {
        this.partOfMaze = partOfMaze;
    }
    public void setOnCurrentPath(boolean onPath) {
        this.onCurrentPath = onPath;
    }
    public boolean hasBorder(int direction) {
        return borders[direction];
    }

    public boolean isOnCurrentPath() { return onCurrentPath; }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return row == node.row && column == node.column;
    }

}

