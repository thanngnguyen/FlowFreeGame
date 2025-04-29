package flowfree.model.core;

import java.awt.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class GameState implements Serializable {
    private final Grid grid;
    private final Dots dots;
    private final Paths paths;
    private char currentColor;

    public GameState(int rows, int cols, Map<Character, Point[]> dotsMap) {
        this.grid = new Grid(rows, cols);
        this.dots = new Dots(dotsMap);
        this.paths = new Paths(this.dots, this.grid);
        this.currentColor = '\0';
        this.dots.placeDotsOnGrid(this.grid);
    }

    public char[][] getGrid() {
        return grid.getGrid();
    }

    public Map<Character, Point[]> getDots() {
        return dots.getDots();
    }

    public Map<Character, List<Point>> getPaths() {
        return paths.getPaths();
    }

    public char getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(char color) {
        this.currentColor = color;
    }

    public boolean isValidMove(Point p, char color, boolean hasReachedEndDot) {
        if (p == null) {
            throw new IllegalArgumentException("Point cannot be null");
        }
        if (p.x < 0 || p.x >= grid.getRows() || p.y < 0 || p.y >= grid.getCols()) {
            return false;
        }

        char cell = grid.getCell(p.x, p.y);
        if (hasReachedEndDot) {
            return false;
        }

        if (cell == '.' || cell == color) {
            for (Map.Entry<Character, List<Point>> entry : paths.getPaths().entrySet()) {
                char otherColor = entry.getKey();
                if (otherColor == color) continue;
                List<Point> path = entry.getValue();
                if (path.contains(p)) {
                    Point[] otherDots = dots.getDots().get(otherColor);
                    if (p.equals(otherDots[0]) || p.equals(otherDots[1])) {
                        continue;
                    }
                    return false;
                }
            }
            return true;
        }

        Point[] dotPair = dots.getDots().get(color);
        if (dotPair != null) {
            return p.equals(dotPair[0]) || p.equals(dotPair[1]);
        }

        return false;
    }

    public void addToPath(Point p, char color) {
        paths.addToPath(p, color, isValidMove(p, color, paths.hasReachedOtherDot(color)));
    }

    public void removeLastPoint(char color) {
        paths.removeLastPoint(color);
    }

    public void clearPath(char color) {
        paths.clearPath(color);
    }

    public boolean isPointOnPath(Point p, char color) {
        return paths.isPointOnPath(p, color);
    }

    public boolean isDot(Point p, char color) {
        return dots.isDot(p, color);
    }

    public boolean hasReachedOtherDot(char color) {
        return paths.hasReachedOtherDot(color);
    }

    public boolean isGameWon() {
        char[][] gridArray = grid.getGrid();
        for (int i = 0; i < grid.getRows(); i++) {
            for (int j = 0; j < grid.getCols(); j++) {
                if (gridArray[i][j] == '.') {
                    return false;
                }
            }
        }

        for (Map.Entry<Character, Point[]> entry : dots.getDots().entrySet()) {
            char color = entry.getKey();
            Point[] dotPair = entry.getValue();
            List<Point> path = paths.getPaths().get(color);

            if (path == null || path.isEmpty()) {
                return false;
            }

            Point start = path.get(0);
            Point end = path.get(path.size() - 1);

            boolean startMatches = start.equals(dotPair[0]) || start.equals(dotPair[1]);
            boolean endMatches = end.equals(dotPair[0]) || end.equals(dotPair[1]);
            boolean differentDots = !start.equals(end);

            if (!startMatches || !endMatches || !differentDots) {
                return false;
            }
        }

        return true;
    }
}