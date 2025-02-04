package org.algorithm.components;

public class HiveCell {
    private HiveCell[] neighbors; //  (top-right, right, bottom-right, bottom-left, left, top-left)
    private char value;
    private boolean partOfMaze;
    private Boolean[] pointsAt;      // Cells this cell points to

    public HiveCell() {
        this.neighbors = new HiveCell[6];
        this.partOfMaze = false;
        this.pointsAt = new Boolean[]{false, false, false, false, false, false};
    }

    public void setPointsAt(Boolean[] pointsAt) {
        this.pointsAt = pointsAt;
    }

    public Boolean[] getPointsAt() {
        return pointsAt;
    }

    public void setValue(char value) {
        this.value = value;
    }

    public char getValue() {
        return value;
    }

    public HiveCell[] getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(HiveCell[] neighbors) {
        this.neighbors = neighbors;
    }

    public boolean isPartOfMaze() {
        return partOfMaze;
    }

    public void setPartOfMaze(boolean partOfMaze) {
        this.partOfMaze = partOfMaze;
    }
}