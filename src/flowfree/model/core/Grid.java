package flowfree.model.core;

import java.io.Serializable;

public class Grid implements Serializable {
    private final int rows, cols;
    private char[][] grid;

    public Grid(int rows, int cols) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Rows and columns must be positive: rows=" + rows + ", cols=" + cols);
        }
        this.rows = rows;
        this.cols = cols;
        this.grid = new char[rows][cols];
        initializeGrid();
    }

    private void initializeGrid() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = '.';
            }
        }
    }

    public char[][] getGrid() {
        return grid;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public void setCell(int x, int y, char value) {
        if (x < 0 || x >= rows || y < 0 || y >= cols) {
            throw new IllegalArgumentException("Invalid grid position: x=" + x + ", y=" + y);
        }
        grid[x][y] = value;
    }

    public char getCell(int x, int y) {
        if (x < 0 || x >= rows || y < 0 || y >= cols) {
            throw new IllegalArgumentException("Invalid grid position: x=" + x + ", y=" + y);
        }
        return grid[x][y];
    }
}