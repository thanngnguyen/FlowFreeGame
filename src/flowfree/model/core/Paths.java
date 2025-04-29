package flowfree.model.core;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class Paths implements Serializable {
    private final Map<Character, List<Point>> paths;
    private final Dots dots;
    private final Grid grid;

    public Paths(Dots dots, Grid grid) {
        if (dots == null || grid == null) {
            throw new IllegalArgumentException("Dots and Grid cannot be null");
        }
        this.dots = dots;
        this.grid = grid;
        this.paths = new HashMap<>();
        initializePaths();
    }

    private void initializePaths() {
        for (Character color : dots.getDots().keySet()) {
            paths.put(color, new ArrayList<>());
        }
    }

    public Map<Character, List<Point>> getPaths() {
        return paths;
    }

    public void addToPath(Point p, char color, boolean isValid) {
        if (isValid) {
            grid.setCell(p.x, p.y, color);
            paths.get(color).add(p);
        }
    }

    public void removeLastPoint(char color) {
        List<Point> path = paths.get(color);
        if (path != null && path.size() > 1) {
            Point lastPoint = path.get(path.size() - 1);
            if (!dots.isDot(lastPoint, color)) {
                path.remove(path.size() - 1);
                grid.setCell(lastPoint.x, lastPoint.y, '.');
            }
        }
    }

    public void clearPath(char color) {
        for (Point p : paths.get(color)) {
            if (!dots.isDot(p, color)) {
                grid.setCell(p.x, p.y, '.');
            }
        }
        paths.get(color).clear();
    }

    public boolean isPointOnPath(Point p, char color) {
        List<Point> path = paths.get(color);
        if (path == null || path.isEmpty()) return false;
        for (int i = 0; i < path.size() - 1; i++) {
            if (path.get(i).equals(p)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasReachedOtherDot(char color) {
        List<Point> path = paths.get(color);
        if (path == null || path.isEmpty()) return false;
        Point firstPoint = path.get(0);
        Point lastPoint = path.get(path.size() - 1);
        Point[] dotPair = dots.getDots().get(color);
        return (firstPoint.equals(dotPair[0]) && lastPoint.equals(dotPair[1])) ||
                (firstPoint.equals(dotPair[1]) && lastPoint.equals(dotPair[0]));
    }
}