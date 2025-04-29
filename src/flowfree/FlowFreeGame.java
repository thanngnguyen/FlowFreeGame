package flowfree;

import flowfree.controller.GameController;
import flowfree.model.core.GameModel;
import flowfree.model.config.LevelConfig;
import flowfree.view.GameView;
import flowfree.view.MenuView;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.util.List;

// Main class to run the game
public class FlowFreeGame {
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private GameModel model;
    private GameView gameView;
    private GameController controller;
    private MenuView menuView;
    private Map<String, java.util.List<LevelConfig>> difficultyLevels;
    private java.util.List<String> difficultyOrder;
    private Set<String> completedLevels;
    private static final String PROGRESS_FILE = "progress/progress.txt";
    private static final String LEVELS_DIR = "src/resources/";
    private String currentDifficulty;
    private int currentLevelIndex;

    public FlowFreeGame() {
        frame = new JFrame("Flow Free");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        difficultyLevels = new LinkedHashMap<>();
        difficultyOrder = Arrays.asList("easy", "medium", "hard", "superhard");
        completedLevels = new HashSet<>();
        loadLevelsFromFiles();
        loadProgress();

        menuView = new MenuView(this);
        mainPanel.add(menuView, "Menu");

        if (currentDifficulty != null && currentLevelIndex >= 0) {
            startLevel(currentDifficulty, currentLevelIndex);
        } else {
            cardLayout.show(mainPanel, "Menu");
        }

        frame.add(mainPanel);
        frame.pack();
        frame.setVisible(true);
    }

    private void loadLevelsFromFiles() {
        for (String difficulty : difficultyOrder) {
            File difficultyDir = new File(LEVELS_DIR + difficulty);
            if (!difficultyDir.exists() || !difficultyDir.isDirectory()) {
                throw new IllegalStateException("Difficulty directory not found: " + difficultyDir.getPath());
            }

            String fileName = difficulty.equals("superhard") ? "SuperHard.txt" : capitalize(difficulty) + ".txt";
            File levelFile = new File(difficultyDir, fileName);

            if (!levelFile.exists() || !levelFile.isFile()) {
                throw new IllegalStateException(fileName + " file not found in " + difficultyDir.getPath());
            }

            java.util.List<LevelConfig> levels;
            try {
                levels = LevelConfig.fromSingleFile(levelFile, difficulty);
                if (levels.isEmpty()) {
                    throw new IllegalStateException("No valid levels found in " + fileName);
                }
                difficultyLevels.put(difficulty, levels);
            } catch (IOException e) {
                throw new IllegalStateException("Error reading " + fileName + ": " + e.getMessage(), e);
            }
        }

        if (difficultyLevels.isEmpty()) {
            throw new IllegalStateException("No valid levels found in " + LEVELS_DIR);
        }
    }

    private void loadProgress() {
        File progressFile = new File(PROGRESS_FILE);
        if (!progressFile.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(progressFile))) {
            String line = reader.readLine();
            if (line != null && !line.isEmpty()) {
                String[] completed = line.split(",");
                for (String level : completed) {
                    completedLevels.add(level.trim());
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error reading progress file: " + e.getMessage(), e);
        }
    }

    public void saveProgress(String difficulty, int levelIndex) {
        if (difficulty == null) {
            throw new IllegalArgumentException("Difficulty cannot be null");
        }
        String levelKey = difficulty + "_" + levelIndex;
        boolean wasLocked = !isDifficultyUnlocked(difficultyOrder.get(difficultyOrder.indexOf(difficulty) + 1 < difficultyOrder.size() ? difficultyOrder.indexOf(difficulty) + 1 : difficultyOrder.size() - 1));
        completedLevels.add(levelKey);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PROGRESS_FILE))) {
            StringBuilder sb = new StringBuilder();
            for (String level : completedLevels) {
                sb.append(level).append(",");
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            writer.write(sb.toString());
        } catch (IOException e) {
            throw new IllegalStateException("Error saving progress: " + e.getMessage(), e);
        }
        boolean isNowUnlocked = isDifficultyUnlocked(difficultyOrder.get(difficultyOrder.indexOf(difficulty) + 1 < difficultyOrder.size() ? difficultyOrder.indexOf(difficulty) + 1 : difficultyOrder.size() - 1));
        if (wasLocked && isNowUnlocked) {
            menuView.notifyDifficultyUnlocked(difficultyOrder.get(difficultyOrder.indexOf(difficulty) + 1));
        }
    }

    public void startLevel(String difficulty, int levelIndex) {
        if (difficulty == null) {
            throw new IllegalArgumentException("Difficulty cannot be null");
        }
        java.util.List<LevelConfig> levels = difficultyLevels.get(difficulty);
        if (levels == null || levelIndex < 0 || levelIndex >= levels.size()) {
            throw new IllegalArgumentException("Invalid level: Difficulty=" + difficulty + ", LevelIndex=" + levelIndex);
        }
        currentDifficulty = difficulty;
        currentLevelIndex = levelIndex;
        LevelConfig level = levels.get(levelIndex);

        // Tính thời gian tối đa dựa trên độ khó và levelIndex
        int baseTime;
        switch (difficulty) {
            case "easy":
                baseTime = 10;
                break;
            case "medium":
                baseTime = 15;
                break;
            case "hard":
                baseTime = 30;
                break;
            case "superhard":
                baseTime = 50;
                break;
            default:
                throw new IllegalArgumentException("Invalid difficulty: " + difficulty);
        }
        int timeLimit = baseTime + levelIndex * 3; // Tăng 3 giây mỗi level

        model = new GameModel(level.getRows(), level.getCols(), level.getDots(), timeLimit);
        gameView = new GameView(model, this, difficulty, levelIndex);
        controller = new GameController(model, gameView, this, difficulty, levelIndex);
        mainPanel.add(gameView, "Game");
        cardLayout.show(mainPanel, "Game");
        frame.pack();
    }

    public void showMenu() {
        if (gameView != null) {
            gameView.stopUITimer(); // Dừng timer giao diện
        }
        currentDifficulty = null;
        currentLevelIndex = -1;
        model = null;
        gameView = null;
        controller = null;
        cardLayout.show(mainPanel, "Menu");
        frame.pack();
    }

    public Map<String, java.util.List<LevelConfig>> getDifficultyLevels() {
        return difficultyLevels;
    }

    public java.util.List<String> getDifficultyOrder() {
        return difficultyOrder;
    }

    public Set<String> getCompletedLevels() {
        return completedLevels;
    }

    public boolean isDifficultyUnlocked(String difficulty) {
        int difficultyIndex = difficultyOrder.indexOf(difficulty);
        if (difficultyIndex == 0) return true;
        String prevDifficulty = difficultyOrder.get(difficultyIndex - 1);
        java.util.List<LevelConfig> prevLevels = difficultyLevels.get(prevDifficulty);
        if (prevLevels == null) return false;
        for (int i = 0; i < prevLevels.size(); i++) {
            if (!completedLevels.contains(prevDifficulty + "_" + i)) {
                return false;
            }
        }
        return true;
    }

    public boolean isLevelUnlocked(String difficulty, int levelIndex) {
        return isDifficultyUnlocked(difficulty);
    }

    public boolean isDifficultyCompleted(String difficulty) {
        List<LevelConfig> levels = difficultyLevels.get(difficulty);
        if (levels == null) return false;
        for (int i = 0; i < levels.size(); i++) {
            if (!completedLevels.contains(difficulty + "_" + i)) {
                return false;
            }
        }
        return true;
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}
