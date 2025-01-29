package org.algorithm.components;

public class HexaNode {
    private int row;
    private int column;
    private boolean[] borders; // Borders: top-right(0), right(1), bottom-right(2), bottom-left(3), left(4), top-left(5)
    private char value;
    private boolean partOfMaze;

    public HexaNode(int row, int column) {
        this.row = row;
        this.column = column;
        this.borders = new boolean[]{true, true, true, true, true, true};
        this.partOfMaze = false;
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

    public boolean isPartOfMaze() {
        return partOfMaze;
    }

    public void setPartOfMaze(boolean partOfMaze) {
        this.partOfMaze = partOfMaze;
    }
}