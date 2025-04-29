package flowfree.model.core;

import java.awt.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class GameModel implements Serializable {
    private final GameState gameState;
    private final TimerManager timerManager;
    private final HistoryManager historyManager;

    public GameModel(int rows, int cols, Map<Character, Point[]> dots, int timeLimit) {
        this.gameState = new GameState(rows, cols, dots);
        this.timerManager = new TimerManager(timeLimit);
        this.historyManager = new HistoryManager(gameState);
    }

    public void setOnTimeUp(Runnable onTimeUp) {
        timerManager.setOnTimeUp(onTimeUp);
    }

    public int getTimeRemaining() {
        return timerManager.getTimeRemaining();
    }

    public void startTimer() {
        timerManager.startTimer(gameState.isGameWon());
    }

    public void stopTimer() {
        timerManager.stopTimer();
    }

    public char[][] getGrid() {
        return gameState.getGrid();
    }

    public Map<Character, Point[]> getDots() {
        return gameState.getDots();
    }

    public Map<Character, List<Point>> getPaths() {
        return gameState.getPaths();
    }

    public char getCurrentColor() {
        return gameState.getCurrentColor();
    }

    public void setCurrentColor(char color) {
        gameState.setCurrentColor(color);
    }

    public boolean isValidMove(Point p, char color, boolean hasReachedEndDot) {
        return gameState.isValidMove(p, color, hasReachedEndDot);
    }

    public void addToPath(Point p, char color) {
        gameState.addToPath(p, color);
    }

    public void removeLastPoint(char color) {
        gameState.removeLastPoint(color);
    }

    public void clearPath(char color) {
        gameState.clearPath(color);
    }

    public boolean isPointOnPath(Point p, char color) {
        return gameState.isPointOnPath(p, color);
    }

    public boolean isDot(Point p, char color) {
        return gameState.isDot(p, color);
    }

    public boolean hasReachedOtherDot(char color) {
        return gameState.hasReachedOtherDot(color);
    }

    public boolean isGameWon() {
        return gameState.isGameWon();
    }

    public void saveStateToUndo() {
        historyManager.saveState();
    }

    public void undo() {
        historyManager.undo();
    }

    public void redo() {
        historyManager.redo();
    }

    public boolean canUndo() {
        return historyManager.canUndo();
    }

    public boolean canRedo() {
        return historyManager.canRedo();
    }

    public Stack<Map<Character, List<Point>>> getUndoStack() {
        return historyManager.getUndoStack();
    }

    public Stack<Map<Character, List<Point>>> getRedoStack() {
        return historyManager.getRedoStack();
    }
}