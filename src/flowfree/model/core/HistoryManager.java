package flowfree.model.core;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class HistoryManager implements Serializable {
    private Stack<Map<Character, List<Point>>> undoStack;
    private Stack<Map<Character, List<Point>>> redoStack;
    private final GameState gameState;

    public HistoryManager(GameState gameState) {
        if (gameState == null) {
            throw new IllegalArgumentException("GameState cannot be null");
        }
        this.gameState = gameState;
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
    }

    public void saveState() {
        Map<Character, List<Point>> pathsCopy = new HashMap<>();
        for (Map.Entry<Character, List<Point>> entry : gameState.getPaths().entrySet()) {
            pathsCopy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        undoStack.push(pathsCopy);
        redoStack.clear();
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            Map<Character, List<Point>> currentPaths = new HashMap<>();
            for (Map.Entry<Character, List<Point>> entry : gameState.getPaths().entrySet()) {
                currentPaths.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }
            redoStack.push(currentPaths);

            Map<Character, List<Point>> previousState = undoStack.pop();
            restoreState(previousState);
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            Map<Character, List<Point>> currentPaths = new HashMap<>();
            for (Map.Entry<Character, List<Point>> entry : gameState.getPaths().entrySet()) {
                currentPaths.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }
            undoStack.push(currentPaths);

            Map<Character, List<Point>> nextState = redoStack.pop();
            restoreState(nextState);
        }
    }

    private void restoreState(Map<Character, List<Point>> state) {
        // Reset grid
        char[][] gridArray = gameState.getGrid();
        for (int i = 0; i < gridArray.length; i++) {
            for (int j = 0; j < gridArray[0].length; j++) {
                gridArray[i][j] = '.';
            }
        }

        // Đặt lại các dots trên grid
        for (Map.Entry<Character, Point[]> entry : gameState.getDots().entrySet()) {
            Point[] points = entry.getValue();
            gridArray[points[0].x][points[0].y] = entry.getKey();
            gridArray[points[1].x][points[1].y] = entry.getKey();
        }

        // Khôi phục paths
        Map<Character, List<Point>> paths = gameState.getPaths();
        paths.clear();
        for (Map.Entry<Character, List<Point>> entry : state.entrySet()) {
            char color = entry.getKey();
            List<Point> path = new ArrayList<>(entry.getValue());
            paths.put(color, path);
            for (Point p : path) {
                if (!gameState.isDot(p, color)) {
                    gridArray[p.x][p.y] = color;
                }
            }
        }
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public Stack<Map<Character, List<Point>>> getUndoStack() {
        return undoStack;
    }

    public Stack<Map<Character, List<Point>>> getRedoStack() {
        return redoStack;
    }
}