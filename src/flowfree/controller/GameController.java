package flowfree.controller;

import flowfree.FlowFreeGame;
import flowfree.model.config.LevelConfig;
import flowfree.model.core.GameModel;
import flowfree.view.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;


public class GameController {
    private flowfree.model.core.GameModel model;
    private GameView view;
    private FlowFreeGame game;
    private String currentDifficulty;
    private int currentLevelIndex;
    private Point lastPoint;
    private char drawingColor;

    public GameController(GameModel model, GameView view, FlowFreeGame game, String difficulty, int levelIndex) {
        if (model == null || view == null || game == null || difficulty == null) {
            throw new IllegalArgumentException("Model, view, game, and difficulty cannot be null");
        }
        this.model = model;
        this.view = view;
        this.game = game;
        this.currentDifficulty = difficulty;
        this.currentLevelIndex = levelIndex;
        this.drawingColor = '\0';
        setupMouseListeners();
        view.setController(this);
    }

    private void setupMouseListeners() {
        JPanel gridPanel = view.getGridPanel();
        gridPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point gridPoint = getGridPoint(e.getPoint());
                char cell = model.getGrid()[gridPoint.x][gridPoint.y];
                if (model.getDots().containsKey(cell)) {
                    drawingColor = cell;
                    model.saveStateToUndo();
                    model.clearPath(drawingColor);
                    model.setCurrentColor(drawingColor);
                    model.addToPath(gridPoint, drawingColor);
                    lastPoint = gridPoint;
                    gridPanel.repaint();
                    updateButtons();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                drawingColor = '\0';
                model.setCurrentColor('\0');
                if (model.isGameWon()) {
                    model.stopTimer();
                    view.stopUITimer(); // Dừng timer giao diện
                    game.saveProgress(currentDifficulty, currentLevelIndex);
                    List<LevelConfig> levels = game.getDifficultyLevels().get(currentDifficulty);
                    int nextLevelIndex = currentLevelIndex + 1;
                    String message;
                    if (nextLevelIndex < levels.size()) {
                        message = "Bạn đã thắng! Hoàn thành cấp độ " + (currentLevelIndex + 1) + " trong "  + capitalize(currentDifficulty)  + " Chơi tiếp cấp độ " + (nextLevelIndex + 1) + "...";
                        JOptionPane.showMessageDialog(view, message);
                        game.startLevel(currentDifficulty, nextLevelIndex);
                    } else if (game.isDifficultyCompleted(currentDifficulty)) {
                        int currentDifficultyIndex = game.getDifficultyOrder().indexOf(currentDifficulty);
                        if (currentDifficultyIndex + 1 < game.getDifficultyOrder().size()) {
                            String nextDifficulty = game.getDifficultyOrder().get(currentDifficultyIndex + 1);
                            message = "Chúc mừng! bạn đã hoàn thành tất cả các cấp độ trong " + capitalize(currentDifficulty) + "! Mở khóa cấp độ " + capitalize(nextDifficulty);
                            JOptionPane.showMessageDialog(view, message);
                            game.showMenu();
                        } else {
                            message = "Chúc mừng! Bạn đã hoàn thành tất cả các cấp độ trong trò chơi";
                            JOptionPane.showMessageDialog(view, message);
                            game.showMenu();
                        }
                    }
                }
                updateButtons();
            }
        });

        gridPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (drawingColor != '\0') {
                    Point gridPoint = getGridPoint(e.getPoint());
                    List<Point> currentPath = model.getPaths().get(drawingColor);
                    if (currentPath.isEmpty()) return;

                    Point pathEnd = currentPath.get(currentPath.size() - 1);

                    if (!gridPoint.equals(pathEnd) && isOrthogonalMove(pathEnd, gridPoint)) {
                        if (currentPath.size() >= 2 && gridPoint.equals(currentPath.get(currentPath.size() - 2))) {
                            model.saveStateToUndo();
                            model.removeLastPoint(drawingColor);
                            lastPoint = currentPath.get(currentPath.size() - 1);
                            gridPanel.repaint();
                        } else if (!model.isPointOnPath(gridPoint, drawingColor)) {
                            if (isValidMove(gridPoint)) {
                                model.saveStateToUndo();
                                model.addToPath(gridPoint, drawingColor);
                                lastPoint = currentPath.get(currentPath.size() - 1);
                                gridPanel.repaint();
                            }
                        }
                    }
                    updateButtons();
                }
            }
        });
    }

    private Point getGridPoint(Point p) {
        if (p == null) {
            throw new IllegalArgumentException("Điểm không thể rỗng");
        }
        return new Point(p.y / 50, p.x / 50);
    }

    private boolean isValidMove(Point p) {
        boolean hasReachedEndDot = model.hasReachedOtherDot(drawingColor);
        return model.isValidMove(p, drawingColor, hasReachedEndDot);
    }

    private boolean isOrthogonalMove(Point from, Point to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Điểm không thể rỗng");
        }
        int dx = Math.abs(to.x - from.x);
        int dy = Math.abs(to.y - from.y);
        return (dx == 1 && dy == 0) || (dx == 0 && dy == 1);
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public void undo() {
        model.undo();
        view.getGridPanel().repaint();
        updateButtons();
    }

    public void redo() {
        model.redo();
        view.getGridPanel().repaint();
        updateButtons();
    }

    private void updateButtons() {
        view.updateButtonStates(model.canUndo(), model.canRedo());
    }
}
