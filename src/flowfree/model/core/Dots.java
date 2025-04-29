package flowfree.model.core;

import java.awt.*;
import java.io.Serializable;
import java.util.Map;

public class Dots implements Serializable {
    private final Map<Character, Point[]> dots;

    public Dots(Map<Character, Point[]> dots) {
        if (dots == null) {
            throw new IllegalArgumentException("Dots map cannot be null");
        }
        for (Map.Entry<Character, Point[]> entry : dots.entrySet()) {
            if (entry.getValue()[0] == null || entry.getValue()[1] == null) {
                throw new IllegalStateException("Dot points cannot be null for color: " + entry.getKey());
            }
        }
        this.dots = dots;
    }

    public Map<Character, Point[]> getDots() {
        return dots;
    }

    public boolean isDot(Point p, char color) {
        Point[] dotPair = dots.get(color);
        return (p.equals(dotPair[0]) || p.equals(dotPair[1]));
    }

    public void placeDotsOnGrid(Grid grid) {
        for (Map.Entry<Character, Point[]> entry : dots.entrySet()) {
            Point[] points = entry.getValue();
            grid.setCell(points[0].x, points[0].y, entry.getKey());
            grid.setCell(points[1].x, points[1].y, entry.getKey());
        }
    }
}