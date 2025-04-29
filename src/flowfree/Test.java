package flowfree;


// Main class to run the game
//public class FlowFreeGame {
//    private JFrame frame;
//    private CardLayout cardLayout;
//    private JPanel mainPanel;
//    private GameModel model;
//    private GameView gameView;
//    private GameController controller;
//    private MenuView menuView;
//    private Map<String, java.util.List<LevelConfig>> difficultyLevels;
//    private java.util.List<String> difficultyOrder;
//    private Set<String> completedLevels;
//    private static final String PROGRESS_FILE = "progress.txt";
//    private static final String LEVELS_DIR = "src/resources/";
//    private String currentDifficulty;
//    private int currentLevelIndex;
//
//    public FlowFreeGame() {
//        frame = new JFrame("Flow Free");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        cardLayout = new CardLayout();
//        mainPanel = new JPanel(cardLayout);
//
//        difficultyLevels = new LinkedHashMap<>();
//        difficultyOrder = Arrays.asList("easy", "medium", "hard", "superhard");
//        completedLevels = new HashSet<>();
//        loadLevelsFromFiles();
//        loadProgress();
//
//        menuView = new MenuView(this);
//        mainPanel.add(menuView, "Menu");
//
//        if (currentDifficulty != null && currentLevelIndex >= 0) {
//            startLevel(currentDifficulty, currentLevelIndex);
//        } else {
//            cardLayout.show(mainPanel, "Menu");
//        }
//
//        frame.add(mainPanel);
//        frame.pack();
//        frame.setVisible(true);
//    }
//
//    private void loadLevelsFromFiles() {
//        for (String difficulty : difficultyOrder) {
//            File difficultyDir = new File(LEVELS_DIR + difficulty);
//            if (!difficultyDir.exists() || !difficultyDir.isDirectory()) {
//                throw new IllegalStateException("Difficulty directory not found: " + difficultyDir.getPath());
//            }
//
//            String fileName = difficulty.equals("superhard") ? "SuperHard.txt" : capitalize(difficulty) + ".txt";
//            File levelFile = new File(difficultyDir, fileName);
//
//            if (!levelFile.exists() || !levelFile.isFile()) {
//                throw new IllegalStateException(fileName + " file not found in " + difficultyDir.getPath());
//            }
//
//            java.util.List<LevelConfig> levels;
//            try {
//                levels = LevelConfig.fromSingleFile(levelFile, difficulty);
//                if (levels.isEmpty()) {
//                    throw new IllegalStateException("No valid levels found in " + fileName);
//                }
//                difficultyLevels.put(difficulty, levels);
//            } catch (IOException e) {
//                throw new IllegalStateException("Error reading " + fileName + ": " + e.getMessage(), e);
//            }
//        }
//
//        if (difficultyLevels.isEmpty()) {
//            throw new IllegalStateException("No valid levels found in " + LEVELS_DIR);
//        }
//    }
//
//    private void loadProgress() {
//        File progressFile = new File(PROGRESS_FILE);
//        if (!progressFile.exists()) {
//            return;
//        }
//
//        try (BufferedReader reader = new BufferedReader(new FileReader(progressFile))) {
//            String line = reader.readLine();
//            if (line != null && !line.isEmpty()) {
//                String[] completed = line.split(",");
//                for (String level : completed) {
//                    completedLevels.add(level.trim());
//                }
//            }
//        } catch (IOException e) {
//            throw new IllegalStateException("Error reading progress file: " + e.getMessage(), e);
//        }
//    }
//
//    public void saveProgress(String difficulty, int levelIndex) {
//        if (difficulty == null) {
//            throw new IllegalArgumentException("Difficulty cannot be null");
//        }
//        String levelKey = difficulty + "_" + levelIndex;
//        boolean wasLocked = !isDifficultyUnlocked(difficultyOrder.get(difficultyOrder.indexOf(difficulty) + 1 < difficultyOrder.size() ? difficultyOrder.indexOf(difficulty) + 1 : difficultyOrder.size() - 1));
//        completedLevels.add(levelKey);
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PROGRESS_FILE))) {
//            StringBuilder sb = new StringBuilder();
//            for (String level : completedLevels) {
//                sb.append(level).append(",");
//            }
//            if (sb.length() > 0) {
//                sb.deleteCharAt(sb.length() - 1);
//            }
//            writer.write(sb.toString());
//        } catch (IOException e) {
//            throw new IllegalStateException("Error saving progress: " + e.getMessage(), e);
//        }
//        boolean isNowUnlocked = isDifficultyUnlocked(difficultyOrder.get(difficultyOrder.indexOf(difficulty) + 1 < difficultyOrder.size() ? difficultyOrder.indexOf(difficulty) + 1 : difficultyOrder.size() - 1));
//        if (wasLocked && isNowUnlocked) {
//            menuView.notifyDifficultyUnlocked(difficultyOrder.get(difficultyOrder.indexOf(difficulty) + 1));
//        }
//    }
//
//    public void startLevel(String difficulty, int levelIndex) {
//        if (difficulty == null) {
//            throw new IllegalArgumentException("Difficulty cannot be null");
//        }
//        java.util.List<LevelConfig> levels = difficultyLevels.get(difficulty);
//        if (levels == null || levelIndex < 0 || levelIndex >= levels.size()) {
//            throw new IllegalArgumentException("Invalid level: Difficulty=" + difficulty + ", LevelIndex=" + levelIndex);
//        }
//        currentDifficulty = difficulty;
//        currentLevelIndex = levelIndex;
//        LevelConfig level = levels.get(levelIndex);
//
//        // Tính thời gian tối đa dựa trên độ khó và levelIndex
//        int baseTime;
//        switch (difficulty) {
//            case "easy":
//                baseTime = 10;
//                break;
//            case "medium":
//                baseTime = 15;
//                break;
//            case "hard":
//                baseTime = 30;
//                break;
//            case "superhard":
//                baseTime = 50;
//                break;
//            default:
//                throw new IllegalArgumentException("Invalid difficulty: " + difficulty);
//        }
//        int timeLimit = baseTime + levelIndex * 3; // Tăng 3 giây mỗi level
//
//        model = new GameModel(level.getRows(), level.getCols(), level.getDots(), timeLimit);
//        gameView = new GameView(model, this, difficulty, levelIndex);
//        controller = new GameController(model, gameView, this, difficulty, levelIndex);
//        mainPanel.add(gameView, "Game");
//        cardLayout.show(mainPanel, "Game");
//        frame.pack();
//    }
//
//    public void showMenu() {
//        currentDifficulty = null;
//        currentLevelIndex = -1;
//        model = null;
//        gameView = null;
//        controller = null;
//        cardLayout.show(mainPanel, "Menu");
//        frame.pack();
//    }
//
//    public Map<String, java.util.List<LevelConfig>> getDifficultyLevels() {
//        return difficultyLevels;
//    }
//
//    public java.util.List<String> getDifficultyOrder() {
//        return difficultyOrder;
//    }
//
//    public Set<String> getCompletedLevels() {
//        return completedLevels;
//    }
//
//    public boolean isDifficultyUnlocked(String difficulty) {
//        int difficultyIndex = difficultyOrder.indexOf(difficulty);
//        if (difficultyIndex == 0) return true;
//        String prevDifficulty = difficultyOrder.get(difficultyIndex - 1);
//        java.util.List<LevelConfig> prevLevels = difficultyLevels.get(prevDifficulty);
//        if (prevLevels == null) return false;
//        for (int i = 0; i < prevLevels.size(); i++) {
//            if (!completedLevels.contains(prevDifficulty + "_" + i)) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    public boolean isLevelUnlocked(String difficulty, int levelIndex) {
//        return isDifficultyUnlocked(difficulty);
//    }
//
//    public boolean isDifficultyCompleted(String difficulty) {
//        List<LevelConfig> levels = difficultyLevels.get(difficulty);
//        if (levels == null) return false;
//        for (int i = 0; i < levels.size(); i++) {
//            if (!completedLevels.contains(difficulty + "_" + i)) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    private String capitalize(String str) {
//        if (str == null || str.isEmpty()) return str;
//        return str.substring(0, 1).toUpperCase() + str.substring(1);
//    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            try {
//                new FlowFreeGame();
//            } catch (Exception e) {
//                JOptionPane.showMessageDialog(null, "Error starting game: " + e.getMessage());
//                System.exit(1);
//            }
//        });
//    }
//
//}
//
//class LevelConfig {
//    private int rows, cols;
//    private Map<Character, Point[]> dots;
//    private String fileName;
//    private String difficulty;
//
//    public LevelConfig(int rows, int cols, Map<Character, Point[]> dots, String fileName, String difficulty) {
//        this.rows = rows;
//        this.cols = cols;
//        this.dots = dots;
//        this.fileName = fileName;
//        this.difficulty = difficulty;
//    }
//
//    public static flowfree.model.config.LevelConfig fromFile(File file, String difficulty) throws IOException {
//        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
//            String[] dimensions = reader.readLine().trim().split("\\s+");
//            if (dimensions.length != 2) {
//                throw new IOException("Invalid dimensions in " + file.getName());
//            }
//
//            int rows, cols;
//            try {
//                rows = Integer.parseInt(dimensions[0]);
//                cols = Integer.parseInt(dimensions[1]);
//            } catch (NumberFormatException e) {
//                throw new IOException("Invalid dimensions format in " + file.getName());
//            }
//
//            Map<Character, Point[]> dots = new HashMap<>();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                String[] parts = line.trim().split("\\s+");
//                if (parts.length != 5) {
//                    throw new IOException("Invalid dot format in " + file.getName());
//                }
//
//                char color;
//                int x1, y1, x2, y2;
//                try {
//                    color = parts[0].charAt(0);
//                    x1 = Integer.parseInt(parts[1]);
//                    y1 = Integer.parseInt(parts[2]);
//                    x2 = Integer.parseInt(parts[3]);
//                    y2 = Integer.parseInt(parts[4]);
//                } catch (NumberFormatException e) {
//                    throw new IOException("Invalid number format in " + file.getName());
//                }
//
//                if (x1 < 0 || x1 >= rows || y1 < 0 || y1 >= cols ||
//                        x2 < 0 || x2 >= rows || y2 < 0 || y2 >= cols) {
//                    throw new IOException("Dot coordinates out of bounds in " + file.getName());
//                }
//
//                dots.put(color, new Point[]{new Point(x1, y1), new Point(x2, y2)});
//            }
//
//            return new flowfree.model.config.LevelConfig(rows, cols, dots, file.getName(), difficulty);
//        }
//    }
//
//    // For reading multiple levels from a single file (used for easy)
//    public static List<flowfree.model.config.LevelConfig> fromSingleFile(File file, String difficulty) throws IOException {
//        List<flowfree.model.config.LevelConfig> levels = new ArrayList<>();
//        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
//            String line;
//            int levelIndex = 0;
//            int rows = 0, cols = 0;
//            Map<Character, Point[]> dots = null;
//
//            while ((line = reader.readLine()) != null) {
//                line = line.trim();
//                if (line.isEmpty()) continue;
//
//                if (line.equals("---")) {
//                    // End of a level, add to list if we have data
//                    if (dots != null && rows > 0 && cols > 0) {
//                        levels.add(new flowfree.model.config.LevelConfig(rows, cols, dots, "easy_" + levelIndex + ".txt", difficulty));
//                        levelIndex++;
//                    }
//                    // Reset for the next level
//                    rows = 0;
//                    cols = 0;
//                    dots = null;
//                    continue;
//                }
//
//                if (rows == 0 && cols == 0) {
//                    // First line of a level: dimensions
//                    String[] dimensions = line.split("\\s+");
//                    if (dimensions.length != 2) {
//                        throw new IOException("Invalid dimensions in " + file.getName() + " at level " + (levelIndex + 1));
//                    }
//                    try {
//                        rows = Integer.parseInt(dimensions[0]);
//                        cols = Integer.parseInt(dimensions[1]);
//                    } catch (NumberFormatException e) {
//                        throw new IOException("Invalid dimensions format in " + file.getName() + " at level " + (levelIndex + 1));
//                    }
//                    dots = new HashMap<>();
//                } else {
//                    // Dot data
//                    String[] parts = line.split("\\s+");
//                    if (parts.length != 5) {
//                        throw new IOException("Invalid dot format in " + file.getName() + " at level " + (levelIndex + 1));
//                    }
//
//                    char color;
//                    int x1, y1, x2, y2;
//                    try {
//                        color = parts[0].charAt(0);
//                        x1 = Integer.parseInt(parts[1]);
//                        y1 = Integer.parseInt(parts[2]);
//                        x2 = Integer.parseInt(parts[3]);
//                        y2 = Integer.parseInt(parts[4]);
//                    } catch (NumberFormatException e) {
//                        throw new IOException("Invalid number format in " + file.getName() + " at level " + (levelIndex + 1));
//                    }
//
//                    if (x1 < 0 || x1 >= rows || y1 < 0 || y1 >= cols ||
//                            x2 < 0 || x2 >= rows || y2 < 0 || y2 >= cols) {
//                        throw new IOException("Dot coordinates out of bounds in " + file.getName() + " at level " + (levelIndex + 1));
//                    }
//
//                    dots.put(color, new Point[]{new Point(x1, y1), new Point(x2, y2)});
//                }
//            }
//
//            // Add the last level if it exists
//            if (dots != null && rows > 0 && cols > 0) {
//                levels.add(new flowfree.model.config.LevelConfig(rows, cols, dots, "easy_" + levelIndex + ".txt", difficulty));
//            }
//        }
//
//        if (levels.isEmpty()) {
//            throw new IOException("No valid levels found in " + file.getName());
//        }
//
//        return levels;
//    }
//
//    public int getRows() {
//        return rows;
//    }
//
//    public int getCols() {
//        return cols;
//    }
//
//    public Map<Character, Point[]> getDots() {
//        return dots;
//    }
//
//    public String getFileName() {
//        return fileName;
//    }
//
//    public String getDifficulty() {
//        return difficulty;
//    }
//}
//
//
//class GameModel implements Serializable {
//    private final GameState gameState;
//    private final TimerManager timerManager;
//    private final HistoryManager historyManager;
//
//    public GameModel(int rows, int cols, Map<Character, Point[]> dots, int timeLimit) {
//        this.gameState = new GameState(rows, cols, dots);
//        this.timerManager = new TimerManager(timeLimit);
//        this.historyManager = new HistoryManager(gameState);
//    }
//
//    public void setOnTimeUp(Runnable onTimeUp) {
//        timerManager.setOnTimeUp(onTimeUp);
//    }
//
//    public int getTimeRemaining() {
//        return timerManager.getTimeRemaining();
//    }
//
//    public void startTimer() {
//        timerManager.startTimer(gameState.isGameWon());
//    }
//
//    public void stopTimer() {
//        timerManager.stopTimer();
//    }
//
//    public char[][] getGrid() {
//        return gameState.getGrid();
//    }
//
//    public Map<Character, Point[]> getDots() {
//        return gameState.getDots();
//    }
//
//    public Map<Character, List<Point>> getPaths() {
//        return gameState.getPaths();
//    }
//
//    public char getCurrentColor() {
//        return gameState.getCurrentColor();
//    }
//
//    public void setCurrentColor(char color) {
//        gameState.setCurrentColor(color);
//    }
//
//    public boolean isValidMove(Point p, char color, boolean hasReachedEndDot) {
//        return gameState.isValidMove(p, color, hasReachedEndDot);
//    }
//
//    public void addToPath(Point p, char color) {
//        gameState.addToPath(p, color);
//    }
//
//    public void removeLastPoint(char color) {
//        gameState.removeLastPoint(color);
//    }
//
//    public void clearPath(char color) {
//        gameState.clearPath(color);
//    }
//
//    public boolean isPointOnPath(Point p, char color) {
//        return gameState.isPointOnPath(p, color);
//    }
//
//    public boolean isDot(Point p, char color) {
//        return gameState.isDot(p, color);
//    }
//
//    public boolean hasReachedOtherDot(char color) {
//        return gameState.hasReachedOtherDot(color);
//    }
//
//    public boolean isGameWon() {
//        return gameState.isGameWon();
//    }
//
//    public void saveStateToUndo() {
//        historyManager.saveState();
//    }
//
//    public void undo() {
//        historyManager.undo();
//    }
//
//    public void redo() {
//        historyManager.redo();
//    }
//
//    public boolean canUndo() {
//        return historyManager.canUndo();
//    }
//
//    public boolean canRedo() {
//        return historyManager.canRedo();
//    }
//
//    public Stack<Map<Character, List<Point>>> getUndoStack() {
//        return historyManager.getUndoStack();
//    }
//
//    public Stack<Map<Character, List<Point>>> getRedoStack() {
//        return historyManager.getRedoStack();
//    }
//}
//
//class Dots implements Serializable {
//    private final Map<Character, Point[]> dots;
//
//    public Dots(Map<Character, Point[]> dots) {
//        if (dots == null) {
//            throw new IllegalArgumentException("Dots map cannot be null");
//        }
//        for (Map.Entry<Character, Point[]> entry : dots.entrySet()) {
//            if (entry.getValue()[0] == null || entry.getValue()[1] == null) {
//                throw new IllegalStateException("Dot points cannot be null for color: " + entry.getKey());
//            }
//        }
//        this.dots = dots;
//    }
//
//    public Map<Character, Point[]> getDots() {
//        return dots;
//    }
//
//    public boolean isDot(Point p, char color) {
//        Point[] dotPair = dots.get(color);
//        return (p.equals(dotPair[0]) || p.equals(dotPair[1]));
//    }
//
//    public void placeDotsOnGrid(Grid grid) {
//        for (Map.Entry<Character, Point[]> entry : dots.entrySet()) {
//            Point[] points = entry.getValue();
//            grid.setCell(points[0].x, points[0].y, entry.getKey());
//            grid.setCell(points[1].x, points[1].y, entry.getKey());
//        }
//    }
//}
//
//class Grid implements Serializable {
//    private final int rows, cols;
//    private char[][] grid;
//
//    public Grid(int rows, int cols) {
//        if (rows <= 0 || cols <= 0) {
//            throw new IllegalArgumentException("Rows and columns must be positive: rows=" + rows + ", cols=" + cols);
//        }
//        this.rows = rows;
//        this.cols = cols;
//        this.grid = new char[rows][cols];
//        initializeGrid();
//    }
//
//    private void initializeGrid() {
//        for (int i = 0; i < rows; i++) {
//            for (int j = 0; j < cols; j++) {
//                grid[i][j] = '.';
//            }
//        }
//    }
//
//    public char[][] getGrid() {
//        return grid;
//    }
//
//    public int getRows() {
//        return rows;
//    }
//
//    public int getCols() {
//        return cols;
//    }
//
//    public void setCell(int x, int y, char value) {
//        if (x < 0 || x >= rows || y < 0 || y >= cols) {
//            throw new IllegalArgumentException("Invalid grid position: x=" + x + ", y=" + y);
//        }
//        grid[x][y] = value;
//    }
//
//    public char getCell(int x, int y) {
//        if (x < 0 || x >= rows || y < 0 || y >= cols) {
//            throw new IllegalArgumentException("Invalid grid position: x=" + x + ", y=" + y);
//        }
//        return grid[x][y];
//    }
//}
//
//class Paths implements Serializable {
//    private final Map<Character, List<Point>> paths;
//    private final flowfree.model.core.Dots dots;
//    private final flowfree.model.core.Grid grid;
//
//    public Paths(flowfree.model.core.Dots dots, flowfree.model.core.Grid grid) {
//        if (dots == null || grid == null) {
//            throw new IllegalArgumentException("Dots and Grid cannot be null");
//        }
//        this.dots = dots;
//        this.grid = grid;
//        this.paths = new HashMap<>();
//        initializePaths();
//    }
//
//    private void initializePaths() {
//        for (Character color : dots.getDots().keySet()) {
//            paths.put(color, new ArrayList<>());
//        }
//    }
//
//    public Map<Character, List<Point>> getPaths() {
//        return paths;
//    }
//
//    public void addToPath(Point p, char color, boolean isValid) {
//        if (isValid) {
//            grid.setCell(p.x, p.y, color);
//            paths.get(color).add(p);
//        }
//    }
//
//    public void removeLastPoint(char color) {
//        List<Point> path = paths.get(color);
//        if (path != null && path.size() > 1) {
//            Point lastPoint = path.get(path.size() - 1);
//            if (!dots.isDot(lastPoint, color)) {
//                path.remove(path.size() - 1);
//                grid.setCell(lastPoint.x, lastPoint.y, '.');
//            }
//        }
//    }
//
//    public void clearPath(char color) {
//        for (Point p : paths.get(color)) {
//            if (!dots.isDot(p, color)) {
//                grid.setCell(p.x, p.y, '.');
//            }
//        }
//        paths.get(color).clear();
//    }
//
//    public boolean isPointOnPath(Point p, char color) {
//        List<Point> path = paths.get(color);
//        if (path == null || path.isEmpty()) return false;
//        for (int i = 0; i < path.size() - 1; i++) {
//            if (path.get(i).equals(p)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public boolean hasReachedOtherDot(char color) {
//        List<Point> path = paths.get(color);
//        if (path == null || path.isEmpty()) return false;
//        Point firstPoint = path.get(0);
//        Point lastPoint = path.get(path.size() - 1);
//        Point[] dotPair = dots.getDots().get(color);
//        return (firstPoint.equals(dotPair[0]) && lastPoint.equals(dotPair[1])) ||
//                (firstPoint.equals(dotPair[1]) && lastPoint.equals(dotPair[0]));
//    }
//}
//
//class GameState implements Serializable {
//    private final flowfree.model.core.Grid grid;
//    private final flowfree.model.core.Dots dots;
//    private final flowfree.model.core.Paths paths;
//    private char currentColor;
//
//    public GameState(int rows, int cols, Map<Character, Point[]> dotsMap) {
//        this.grid = new flowfree.model.core.Grid(rows, cols);
//        this.dots = new flowfree.model.core.Dots(dotsMap);
//        this.paths = new flowfree.model.core.Paths(this.dots, this.grid);
//        this.currentColor = '\0';
//        this.dots.placeDotsOnGrid(this.grid);
//    }
//
//    public char[][] getGrid() {
//        return grid.getGrid();
//    }
//
//    public Map<Character, Point[]> getDots() {
//        return dots.getDots();
//    }
//
//    public Map<Character, List<Point>> getPaths() {
//        return paths.getPaths();
//    }
//
//    public char getCurrentColor() {
//        return currentColor;
//    }
//
//    public void setCurrentColor(char color) {
//        this.currentColor = color;
//    }
//
//    public boolean isValidMove(Point p, char color, boolean hasReachedEndDot) {
//        if (p == null) {
//            throw new IllegalArgumentException("Point cannot be null");
//        }
//        if (p.x < 0 || p.x >= grid.getRows() || p.y < 0 || p.y >= grid.getCols()) {
//            return false;
//        }
//
//        char cell = grid.getCell(p.x, p.y);
//        if (hasReachedEndDot) {
//            return false;
//        }
//
//        if (cell == '.' || cell == color) {
//            for (Map.Entry<Character, List<Point>> entry : paths.getPaths().entrySet()) {
//                char otherColor = entry.getKey();
//                if (otherColor == color) continue;
//                List<Point> path = entry.getValue();
//                if (path.contains(p)) {
//                    Point[] otherDots = dots.getDots().get(otherColor);
//                    if (p.equals(otherDots[0]) || p.equals(otherDots[1])) {
//                        continue;
//                    }
//                    return false;
//                }
//            }
//            return true;
//        }
//
//        Point[] dotPair = dots.getDots().get(color);
//        if (dotPair != null) {
//            return p.equals(dotPair[0]) || p.equals(dotPair[1]);
//        }
//
//        return false;
//    }
//
//    public void addToPath(Point p, char color) {
//        paths.addToPath(p, color, isValidMove(p, color, paths.hasReachedOtherDot(color)));
//    }
//
//    public void removeLastPoint(char color) {
//        paths.removeLastPoint(color);
//    }
//
//    public void clearPath(char color) {
//        paths.clearPath(color);
//    }
//
//    public boolean isPointOnPath(Point p, char color) {
//        return paths.isPointOnPath(p, color);
//    }
//
//    public boolean isDot(Point p, char color) {
//        return dots.isDot(p, color);
//    }
//
//    public boolean hasReachedOtherDot(char color) {
//        return paths.hasReachedOtherDot(color);
//    }
//
//    public boolean isGameWon() {
//        char[][] gridArray = grid.getGrid();
//        for (int i = 0; i < grid.getRows(); i++) {
//            for (int j = 0; j < grid.getCols(); j++) {
//                if (gridArray[i][j] == '.') {
//                    return false;
//                }
//            }
//        }
//
//        for (Map.Entry<Character, Point[]> entry : dots.getDots().entrySet()) {
//            char color = entry.getKey();
//            Point[] dotPair = entry.getValue();
//            List<Point> path = paths.getPaths().get(color);
//
//            if (path == null || path.isEmpty()) {
//                return false;
//            }
//
//            Point start = path.get(0);
//            Point end = path.get(path.size() - 1);
//
//            boolean startMatches = start.equals(dotPair[0]) || start.equals(dotPair[1]);
//            boolean endMatches = end.equals(dotPair[0]) || end.equals(dotPair[1]);
//            boolean differentDots = !start.equals(end);
//
//            if (!startMatches || !endMatches || !differentDots) {
//                return false;
//            }
//        }
//
//        return true;
//    }
//}
//
//class HistoryManager implements Serializable {
//    private Stack<Map<Character, List<Point>>> undoStack;
//    private Stack<Map<Character, List<Point>>> redoStack;
//    private final flowfree.model.core.GameState gameState;
//
//    public HistoryManager(flowfree.model.core.GameState gameState) {
//        if (gameState == null) {
//            throw new IllegalArgumentException("GameState cannot be null");
//        }
//        this.gameState = gameState;
//        this.undoStack = new Stack<>();
//        this.redoStack = new Stack<>();
//    }
//
//    public void saveState() {
//        Map<Character, List<Point>> pathsCopy = new HashMap<>();
//        for (Map.Entry<Character, List<Point>> entry : gameState.getPaths().entrySet()) {
//            pathsCopy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
//        }
//        undoStack.push(pathsCopy);
//        redoStack.clear();
//    }
//
//    public void undo() {
//        if (!undoStack.isEmpty()) {
//            Map<Character, List<Point>> currentPaths = new HashMap<>();
//            for (Map.Entry<Character, List<Point>> entry : gameState.getPaths().entrySet()) {
//                currentPaths.put(entry.getKey(), new ArrayList<>(entry.getValue()));
//            }
//            redoStack.push(currentPaths);
//
//            Map<Character, List<Point>> previousState = undoStack.pop();
//            restoreState(previousState);
//        }
//    }
//
//    public void redo() {
//        if (!redoStack.isEmpty()) {
//            Map<Character, List<Point>> currentPaths = new HashMap<>();
//            for (Map.Entry<Character, List<Point>> entry : gameState.getPaths().entrySet()) {
//                currentPaths.put(entry.getKey(), new ArrayList<>(entry.getValue()));
//            }
//            undoStack.push(currentPaths);
//
//            Map<Character, List<Point>> nextState = redoStack.pop();
//            restoreState(nextState);
//        }
//    }
//
//    private void restoreState(Map<Character, List<Point>> state) {
//        // Reset grid
//        char[][] gridArray = gameState.getGrid();
//        for (int i = 0; i < gridArray.length; i++) {
//            for (int j = 0; j < gridArray[0].length; j++) {
//                gridArray[i][j] = '.';
//            }
//        }
//
//        // Đặt lại các dots trên grid
//        for (Map.Entry<Character, Point[]> entry : gameState.getDots().entrySet()) {
//            Point[] points = entry.getValue();
//            gridArray[points[0].x][points[0].y] = entry.getKey();
//            gridArray[points[1].x][points[1].y] = entry.getKey();
//        }
//
//        // Khôi phục paths
//        Map<Character, List<Point>> paths = gameState.getPaths();
//        paths.clear();
//        for (Map.Entry<Character, List<Point>> entry : state.entrySet()) {
//            char color = entry.getKey();
//            List<Point> path = new ArrayList<>(entry.getValue());
//            paths.put(color, path);
//            for (Point p : path) {
//                if (!gameState.isDot(p, color)) {
//                    gridArray[p.x][p.y] = color;
//                }
//            }
//        }
//    }
//
//    public boolean canUndo() {
//        return !undoStack.isEmpty();
//    }
//
//    public boolean canRedo() {
//        return !redoStack.isEmpty();
//    }
//
//    public Stack<Map<Character, List<Point>>> getUndoStack() {
//        return undoStack;
//    }
//
//    public Stack<Map<Character, List<Point>>> getRedoStack() {
//        return redoStack;
//    }
//}
//
//class TimerManager implements Serializable {
//    private int timeLimit;
//    private int timeRemaining;
//    private transient Timer timer; // transient vì Timer không thể serialize
//    private transient Runnable onTimeUp; // Callback khi hết thời gian
//
//    public TimerManager(int timeLimit) {
//        if (timeLimit <= 0) {
//            throw new IllegalArgumentException("Time limit must be positive: " + timeLimit);
//        }
//        this.timeLimit = timeLimit;
//        this.timeRemaining = timeLimit;
//    }
//
//    public void setOnTimeUp(Runnable onTimeUp) {
//        this.onTimeUp = onTimeUp;
//    }
//
//    public int getTimeRemaining() {
//        return timeRemaining;
//    }
//
//    public void startTimer(boolean isGameWon) {
//        if (timer == null) {
//            timer = new Timer(1000, e -> {
//                if (timeRemaining > 0) {
//                    timeRemaining--;
//                }
//                if (timeRemaining <= 0) {
//                    timer.stop();
//                    if (onTimeUp != null && !isGameWon) {
//                        onTimeUp.run();
//                    }
//                }
//            });
//            timer.start();
//        }
//    }
//
//    public void stopTimer() {
//        if (timer != null) {
//            timer.stop();
//            timer = null;
//        }
//    }
//}
//
//class GameController {
//    private flowfree.model.core.GameModel model;
//    private GameView view;
//    private FlowFreeGame game;
//    private String currentDifficulty;
//    private int currentLevelIndex;
//    private Point lastPoint;
//    private char drawingColor;
//
//    public GameController(flowfree.model.core.GameModel model, GameView view, FlowFreeGame game, String difficulty, int levelIndex) {
//        if (model == null || view == null || game == null || difficulty == null) {
//            throw new IllegalArgumentException("Model, view, game, and difficulty cannot be null");
//        }
//        this.model = model;
//        this.view = view;
//        this.game = game;
//        this.currentDifficulty = difficulty;
//        this.currentLevelIndex = levelIndex;
//        this.drawingColor = '\0';
//        setupMouseListeners();
//        view.setController(this);
//    }
//
//    private void setupMouseListeners() {
//        JPanel gridPanel = view.getGridPanel();
//        gridPanel.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mousePressed(MouseEvent e) {
//                Point gridPoint = getGridPoint(e.getPoint());
//                char cell = model.getGrid()[gridPoint.x][gridPoint.y];
//                if (model.getDots().containsKey(cell)) {
//                    drawingColor = cell;
//                    model.saveStateToUndo();
//                    model.clearPath(drawingColor);
//                    model.setCurrentColor(drawingColor);
//                    model.addToPath(gridPoint, drawingColor);
//                    lastPoint = gridPoint;
//                    gridPanel.repaint();
//                    updateButtons();
//                }
//            }
//
//            @Override
//            public void mouseReleased(MouseEvent e) {
//                drawingColor = '\0';
//                model.setCurrentColor('\0');
//                if (model.isGameWon()) {
//                    model.stopTimer();
//                    game.saveProgress(currentDifficulty, currentLevelIndex);
//                    List<LevelConfig> levels = game.getDifficultyLevels().get(currentDifficulty);
//                    int nextLevelIndex = currentLevelIndex + 1;
//                    String message;
//                    if (nextLevelIndex < levels.size()) {
//                        message = "Bạn đã thắng! Hoàn thành cấp độ " + (currentLevelIndex + 1) + " trong "  + capitalize(currentDifficulty)  + " Chơi tiếp cấp độ " + (nextLevelIndex + 1) + "...";
//                        JOptionPane.showMessageDialog(view, message);
//                        game.startLevel(currentDifficulty, nextLevelIndex);
//                    } else if (game.isDifficultyCompleted(currentDifficulty)) {
//                        int currentDifficultyIndex = game.getDifficultyOrder().indexOf(currentDifficulty);
//                        if (currentDifficultyIndex + 1 < game.getDifficultyOrder().size()) {
//                            String nextDifficulty = game.getDifficultyOrder().get(currentDifficultyIndex + 1);
//                            message = "Chúc mừng! bạn đã hoàn thành tất cả các cấp độ trong " + capitalize(currentDifficulty) + "! Mở khóa cấp độ " + capitalize(nextDifficulty);
//                            JOptionPane.showMessageDialog(view, message);
//                            game.showMenu();
//                        } else {
//                            message = "Chúc mừng! Bạn đã hoàn thành tất cả các cấp độ trong trò chơi";
//                            JOptionPane.showMessageDialog(view, message);
//                            game.showMenu();
//                        }
//                    }
//                }
//                updateButtons();
//            }
//        });
//
//        gridPanel.addMouseMotionListener(new MouseMotionAdapter() {
//            @Override
//            public void mouseDragged(MouseEvent e) {
//                if (drawingColor != '\0') {
//                    Point gridPoint = getGridPoint(e.getPoint());
//                    List<Point> currentPath = model.getPaths().get(drawingColor);
//                    if (currentPath.isEmpty()) return;
//
//                    Point pathEnd = currentPath.get(currentPath.size() - 1);
//
//                    if (!gridPoint.equals(pathEnd) && isOrthogonalMove(pathEnd, gridPoint)) {
//                        if (currentPath.size() >= 2 && gridPoint.equals(currentPath.get(currentPath.size() - 2))) {
//                            model.saveStateToUndo();
//                            model.removeLastPoint(drawingColor);
//                            lastPoint = currentPath.get(currentPath.size() - 1);
//                            gridPanel.repaint();
//                        } else if (!model.isPointOnPath(gridPoint, drawingColor)) {
//                            if (isValidMove(gridPoint)) {
//                                model.saveStateToUndo();
//                                model.addToPath(gridPoint, drawingColor);
//                                lastPoint = currentPath.get(currentPath.size() - 1);
//                                gridPanel.repaint();
//                            }
//                        }
//                    }
//                    updateButtons();
//                }
//            }
//        });
//    }
//
//    private Point getGridPoint(Point p) {
//        if (p == null) {
//            throw new IllegalArgumentException("Điểm không thể rỗng");
//        }
//        return new Point(p.y / 50, p.x / 50);
//    }
//
//    private boolean isValidMove(Point p) {
//        boolean hasReachedEndDot = model.hasReachedOtherDot(drawingColor);
//        return model.isValidMove(p, drawingColor, hasReachedEndDot);
//    }
//
//    private boolean isOrthogonalMove(Point from, Point to) {
//        if (from == null || to == null) {
//            throw new IllegalArgumentException("Điểm không thể rỗng");
//        }
//        int dx = Math.abs(to.x - from.x);
//        int dy = Math.abs(to.y - from.y);
//        return (dx == 1 && dy == 0) || (dx == 0 && dy == 1);
//    }
//
//    private String capitalize(String str) {
//        if (str == null || str.isEmpty()) return str;
//        return str.substring(0, 1).toUpperCase() + str.substring(1);
//    }
//
//    public void undo() {
//        model.undo();
//        view.getGridPanel().repaint();
//        updateButtons();
//    }
//
//    public void redo() {
//        model.redo();
//        view.getGridPanel().repaint();
//        updateButtons();
//    }
//
//    private void updateButtons() {
//        view.updateButtonStates(model.canUndo(), model.canRedo());
//    }
//}
//
//class GameView extends JPanel {
//    private flowfree.model.core.GameModel model;
//    private FlowFreeGame game;
//    private String difficulty;
//    private int levelIndex;
//    private Map<String, CustomButton> difficultyButtons;
//    private static final int CELL_SIZE = 50;
//    private static final int DOT_SIZE = 40;
//    private static final int PIPE_WIDTH = 20;
//    private static final Map<Character, Color> COLOR_MAP = new HashMap<>();
//    private flowfree.controller.GameController controller;
//    private flowfree.view.GameView.RoundedProgressBar timerBar;
//
//    static {
//        COLOR_MAP.put('R', new Color(255, 0, 0)); // Ferr (Red)
//        COLOR_MAP.put('B', new Color(87, 167, 255)); // Geanglo (Blue)
//        COLOR_MAP.put('G', Color.GREEN); // Not used in image, but keep for compatibility
//        COLOR_MAP.put('Y', Color.YELLOW); // Gowtdernos (Yellow)
//        COLOR_MAP.put('P', Color.PINK); // pink
//        COLOR_MAP.put('O', Color.ORANGE); // Not used in image, but keep for compatibility
//        COLOR_MAP.put('.', new Color(220, 200, 170)); // Beige for cells
//        COLOR_MAP.put('A', new Color(0, 255, 255)); // aqua
//        COLOR_MAP.put('T', new Color(255, 99, 71)); // tomato
//        COLOR_MAP.put('K', Color.BLACK); // black
//        COLOR_MAP.put('X', Color.GRAY); // gray
//        COLOR_MAP.put('V', new Color(238, 130, 238)); // violet
//        COLOR_MAP.put('U', new Color(128, 0, 128)); // purple
//        COLOR_MAP.put('C', new Color(0, 139,139)); // darkcyan
//        COLOR_MAP.put('E', new Color(0, 0, 139)); // darkblue
//        COLOR_MAP.put('N', new Color(0, 100, 0)); //darkgreen
//        COLOR_MAP.put('L', new Color(173, 255, 47)); // greenyellow
//    }
//
//    private static final Color BACKGROUND_COLOR = new Color(100, 200, 200);
//    private static final Color BORDER_COLOR = new Color(180, 160, 130);
//    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 50);
//    private static final Color EXIT_COLOR = new Color(74, 74, 74);
//
//    private JPanel gridPanel;
//    private CustomButton undoButton;
//    private CustomButton redoButton;
//
//    // Lớp con của JProgressBar để bo góc
//    private static class RoundedProgressBar extends JProgressBar {
//        private static final int ARC_RADIUS = 20; // Bán kính bo góc
//
//        public RoundedProgressBar(int min, int max) {
//            super(min, max);
//            setOpaque(false);
//            setBorderPainted(false);
//            setStringPainted(true);
//            setString("⏱️");
//        }
//
//        @Override
//        protected void paintComponent(Graphics g) {
//            Graphics2D g2d = (Graphics2D) g.create();
//            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//            int width = getWidth();
//            int height = getHeight();
//            int value = getValue();
//            int max = getMaximum();
//
//            // Vẽ nền của thanh tiến trình (màu xám nhạt)
//            g2d.setColor(new Color(200, 200, 200));
//            g2d.fillRoundRect(0, 0, width - 1, height - 1, ARC_RADIUS, ARC_RADIUS);
//
//            // Tính chiều dài của phần tiến trình
//            double ratio = (double) value / max;
//            int progressWidth = (int) (width * ratio);
//
//            // Vẽ phần tiến trình với màu phù hợp
//            g2d.setColor(getForeground());
//            g2d.fillRoundRect(0, 0, progressWidth - 1, height - 1, ARC_RADIUS, ARC_RADIUS);
//
//            // Vẽ viền
//            g2d.setColor(Color.DARK_GRAY);
//            g2d.drawRoundRect(0, 0, width - 1, height - 1, ARC_RADIUS, ARC_RADIUS);
//
//            // Vẽ chuỗi (text) ở giữa
//            String text = getString();
//            if (text != null) {
//                FontMetrics fm = g2d.getFontMetrics();
//                int textWidth = fm.stringWidth(text);
//                int textHeight = fm.getAscent();
//                int x = (width - textWidth);
//                int y = (height + textHeight) / 2 - 2;
//                g2d.setColor(Color.BLACK);
//                g2d.drawString(text, x, y);
//            }
//
//            g2d.dispose();
//        }
//    }
//
//    public GameView(flowfree.model.core.GameModel model, FlowFreeGame game, String difficulty, int levelIndex) {
//        if (model == null || game == null || difficulty == null) {
//            throw new IllegalArgumentException("Model, game, and difficulty cannot be null");
//        }
//        this.model = model;
//        this.game = game;
//        this.difficulty = difficulty;
//        this.levelIndex = levelIndex;
//        this.difficultyButtons = new HashMap<>();
//        setLayout(new BorderLayout());
//        setBackground(BACKGROUND_COLOR);
//
//        model.setOnTimeUp(() -> {
//            int option = JOptionPane.showOptionDialog(
//                    this,
//                    "Time's up! You lost. Do you want to retry?",
//                    "Game Over",
//                    JOptionPane.YES_NO_OPTION,
//                    JOptionPane.INFORMATION_MESSAGE,
//                    null,
//                    new String[]{"Retry", "Back to Menu"},
//                    "Retry"
//            );
//            if (option == JOptionPane.YES_OPTION) {
//                game.startLevel(difficulty, levelIndex);
//            } else {
//                game.showMenu();
//            }
//        });
//
//        gridPanel = new JPanel() {
//            @Override
//            protected void paintComponent(Graphics g) {
//                super.paintComponent(g);
//                Graphics2D g2d = (Graphics2D) g;
//                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//                if (model == null || model.getGrid() == null) {
//                    g.setColor(Color.RED);
//                    g.drawString("Error: Game model or grid is null", 10, 20);
//                    return;
//                }
//
//                char[][] grid = model.getGrid();
//                Map<Character, java.util.List<Point>> paths = model.getPaths();
//
//                for (int i = 0; i < grid.length; i++) {
//                    for (int j = 0; j < grid[0].length; j++) {
//                        int x = j * CELL_SIZE;
//                        int y = i * CELL_SIZE;
//
//                        g2d.setColor(COLOR_MAP.get('.'));
//                        g2d.fillRoundRect(x + 5, y + 5, CELL_SIZE - 10, CELL_SIZE - 10, 10, 10);
//
//                        g2d.setColor(BORDER_COLOR);
//                        g2d.setStroke(new BasicStroke(2));
//                        g2d.drawRoundRect(x + 5, y + 5, CELL_SIZE - 10, CELL_SIZE - 10, 10, 10);
//                    }
//                }
//
//                g2d.setStroke(new BasicStroke(PIPE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
//                for (Map.Entry<Character, java.util.List<Point>> entry : paths.entrySet()) {
//                    char color = entry.getKey();
//                    java.util.List<Point> path = entry.getValue();
//                    if (path != null && path.size() > 1) {
//                        g2d.setColor(SHADOW_COLOR);
//                        for (int i = 1; i < path.size(); i++) {
//                            Point p1 = path.get(i - 1);
//                            Point p2 = path.get(i);
//                            int x1 = p1.y * CELL_SIZE + CELL_SIZE / 2 + 3;
//                            int y1 = p1.x * CELL_SIZE + CELL_SIZE / 2 + 3;
//                            int x2 = p2.y * CELL_SIZE + CELL_SIZE / 2 + 3;
//                            int y2 = p2.x * CELL_SIZE + CELL_SIZE / 2 + 3;
//                            g2d.drawLine(x1, y1, x2, y2);
//                        }
//
//                        g2d.setColor(COLOR_MAP.getOrDefault(color, Color.WHITE));
//                        for (int i = 1; i < path.size(); i++) {
//                            Point p1 = path.get(i - 1);
//                            Point p2 = path.get(i);
//                            int x1 = p1.y * CELL_SIZE + CELL_SIZE / 2;
//                            int y1 = p1.x * CELL_SIZE + CELL_SIZE / 2;
//                            int x2 = p2.y * CELL_SIZE + CELL_SIZE / 2;
//                            int y2 = p2.x * CELL_SIZE + CELL_SIZE / 2;
//                            g2d.drawLine(x1, y1, x2, y2);
//                        }
//                    }
//                }
//
//                for (Map.Entry<Character, Point[]> entry : model.getDots().entrySet()) {
//                    char color = entry.getKey();
//                    Point[] dots = entry.getValue();
//                    if (dots != null) {
//                        for (Point dot : dots) {
//                            if (dot != null) {
//                                int x = dot.y * CELL_SIZE + (CELL_SIZE - DOT_SIZE) / 2;
//                                int y = dot.x * CELL_SIZE + (CELL_SIZE - DOT_SIZE) / 2;
//
//                                g2d.setColor(SHADOW_COLOR);
//                                g2d.fillOval(x + 3, y + 3, DOT_SIZE, DOT_SIZE);
//
//                                g2d.setColor(COLOR_MAP.getOrDefault(color, Color.WHITE));
//                                g2d.fillOval(x, y, DOT_SIZE, DOT_SIZE);
//
//                                g2d.setColor(Color.WHITE);
//                                g2d.fillOval(x + 10, y + 10, DOT_SIZE / 3, DOT_SIZE / 3);
//                            }
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public Dimension getPreferredSize() {
//                return new Dimension(model.getGrid()[0].length * CELL_SIZE, model.getGrid().length * CELL_SIZE);
//            }
//        };
//        gridPanel.setBackground(BACKGROUND_COLOR);
//
//        JPanel centeringPanel = new JPanel(new GridBagLayout());
//        centeringPanel.setBackground(BACKGROUND_COLOR);
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        gbc.anchor = GridBagConstraints.CENTER;
//        centeringPanel.add(gridPanel, gbc);
//        add(centeringPanel, BorderLayout.CENTER);
//
//        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
//        topPanel.setBackground(BACKGROUND_COLOR);
//
//        // Khởi tạo RoundedProgressBar
//        timerBar = new flowfree.view.GameView.RoundedProgressBar(0, model.getTimeRemaining());
//        timerBar.setValue(model.getTimeRemaining());
//        timerBar.setPreferredSize(new Dimension(200, 30));
////        timerBar.setString("Time: " + model.getTimeRemaining());
//        timerBar.setForeground(Color.GREEN); // Màu mặc định ban đầu là xanh lá
//
//        Timer uiTimer = new Timer(1000, e -> {
//            int timeRemaining = model.getTimeRemaining();
//            int initialTime = timerBar.getMaximum();
//            timerBar.setValue(timeRemaining);
//
//            // Tính tỷ lệ thời gian còn lại
//            double timeRatio = (double) timeRemaining / initialTime;
//
//            // Thay đổi màu dựa trên tỷ lệ thời gian
//            if (timeRatio > 2.0 / 3.0) {
//                timerBar.setForeground(Color.GREEN);
//            } else if (timeRatio > 1.0 / 3.0) {
//                timerBar.setForeground(Color.YELLOW);
//            } else {
//                timerBar.setForeground(Color.RED);
//            }
//
//            timerBar.repaint();
//        });
//        uiTimer.start();
//
//        topPanel.add(timerBar);
//        add(topPanel, BorderLayout.NORTH);
//
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//        buttonPanel.setBackground(BACKGROUND_COLOR);
//
//        undoButton = new CustomButton("Undo", EXIT_COLOR, false, false);
//        undoButton.setEnabled(false);
//        undoButton.addActionListener(e -> controller.undo());
//        buttonPanel.add(undoButton);
//
//        redoButton = new CustomButton("Redo", EXIT_COLOR, false, false);
//        redoButton.setEnabled(false);
//        redoButton.addActionListener(e -> controller.redo());
//        buttonPanel.add(redoButton);
//
//        CustomButton exitButton = new CustomButton("Exit", EXIT_COLOR, true, false);
//        exitButton.addActionListener(e -> {
//            model.stopTimer();
//            game.showMenu();
//        });
//        buttonPanel.add(exitButton);
//
//        add(buttonPanel, BorderLayout.SOUTH);
//    }
//
//    public JPanel getGridPanel() {
//        return gridPanel;
//    }
//
//    public void setController(flowfree.controller.GameController controller) {
//        if (controller == null) {
//            throw new IllegalArgumentException("Controller cannot be null");
//        }
//        this.controller = controller;
//    }
//
//    public void updateButtonStates(boolean canUndo, boolean canRedo) {
//        undoButton.setEnabled(canUndo);
//        redoButton.setEnabled(canRedo);
//    }
//}
//
//class MenuView extends JPanel {
//    private FlowFreeGame game;
//    private String currentDifficulty;
//    private JLabel title;
//    private Map<String, CustomButton> difficultyButtons;
//
//    private static final Color EASY_COLOR = new Color(74, 144, 226);
//    private static final Color MEDIUM_COLOR = new Color(80, 200, 120);
//    private static final Color HARD_COLOR = new Color(245, 166, 35);
//    private static final Color SUPERHARD_COLOR = new Color(233, 78, 119);
//    private static final Color EXIT_COLOR = new Color(74, 74, 74);
//
//    public MenuView(FlowFreeGame game) {
//        if (game == null) {
//            throw new IllegalArgumentException("Game cannot be null");
//        }
//        this.game = game;
//        this.currentDifficulty = null;
//        this.difficultyButtons = new HashMap<>();
//        setLayout(new GridBagLayout());
//        setPreferredSize(new Dimension(600, 700));
//        showDifficulties();
//    }
//
//    @Override
//    protected void paintComponent(Graphics g) {
//        super.paintComponent(g);
//        Graphics2D g2d = (Graphics2D) g;
//        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//        RadialGradientPaint radialGradient = new RadialGradientPaint(
//                getWidth() / 2f, getHeight() / 2f, getWidth(),
//                new float[]{0f, 1f},
//                new Color[]{new Color(205, 255, 216), new Color(148, 185, 255)}
//        );
//        g2d.setPaint(radialGradient);
//        g2d.fillRect(0, 0, getWidth(), getHeight());
//    }
//
//    public void refresh() {
//        if (currentDifficulty == null) {
//            showDifficulties();
//        } else {
//            showLevels(currentDifficulty);
//        }
//    }
//
//    public void notifyDifficultyUnlocked(String difficulty) {
//        if (currentDifficulty == null) {
//            CustomButton button = difficultyButtons.get(difficulty);
//            if (button != null) {
//                button.setLocked(false);
//                button.startUnlockAnimation();
//            }
//        }
//    }
//
//    private void showDifficulties() {
//        removeAll();
//        currentDifficulty = null;
//
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.gridwidth = GridBagConstraints.REMAINDER;
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//        gbc.insets = new Insets(15, 15, 15, 15);
//
//        title = new JLabel("Flow Free", SwingConstants.CENTER) {
//            @Override
//            protected void paintComponent(Graphics g) {
//                Graphics2D g2d = (Graphics2D) g;
//                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//                String text = getText();
//                Font font = new Font("VNM Sans Display Bold", Font.BOLD, 48);
//                g2d.setFont(font);
//                FontMetrics fm = g2d.getFontMetrics();
//
//                java.text.AttributedString attributedString = new java.text.AttributedString(text);
//                attributedString.addAttribute(java.awt.font.TextAttribute.FONT, font);
//
//                GradientPaint gradient = new GradientPaint(
//                        0, 0, new Color(255, 255, 255),
//                        0, fm.getHeight(), new Color(200, 200, 200)
//                );
//                attributedString.addAttribute(java.awt.font.TextAttribute.FOREGROUND, gradient, 0, text.length());
//
//                attributedString.addAttribute(java.awt.font.TextAttribute.FOREGROUND, new Color(74, 144, 226), 0, 1);
//                attributedString.addAttribute(java.awt.font.TextAttribute.FOREGROUND, new Color(80, 200, 120), 1, 2);
//                attributedString.addAttribute(java.awt.font.TextAttribute.FOREGROUND, new Color(245, 166, 35), 2, 3);
//                attributedString.addAttribute(java.awt.font.TextAttribute.FOREGROUND, new Color(233, 78, 119), 3, 4);
//                attributedString.addAttribute(java.awt.font.TextAttribute.FOREGROUND, new Color(74, 144, 226), 5, 6);
//                attributedString.addAttribute(java.awt.font.TextAttribute.FOREGROUND, new Color(80, 200, 120), 6, 7);
//                attributedString.addAttribute(java.awt.font.TextAttribute.FOREGROUND, new Color(245, 166, 35), 7, 8);
//                attributedString.addAttribute(java.awt.font.TextAttribute.FOREGROUND, new Color(233, 78, 119), 8, 9);
//
//                g2d.setPaint(gradient);
//                g2d.drawString(attributedString.getIterator(), 0, fm.getHeight());
//            }
//        };
//        title.setPreferredSize(new Dimension(400, 100));
//        add(title, gbc);
//
//        CustomButton easyButton = new CustomButton("Easy", EASY_COLOR, false, false);
//        easyButton.setLocked(!game.isDifficultyUnlocked("easy"));
//        easyButton.addActionListener(e -> showLevels("easy"));
//        difficultyButtons.put("easy", easyButton);
//        add(easyButton, gbc);
//
//        CustomButton mediumButton = new CustomButton("Medium", MEDIUM_COLOR, false, false);
//        mediumButton.setLocked(!game.isDifficultyUnlocked("medium"));
//        mediumButton.addActionListener(e -> showLevels("medium"));
//        difficultyButtons.put("medium", mediumButton);
//        add(mediumButton, gbc);
//
//        CustomButton hardButton = new CustomButton("Hard", HARD_COLOR, false, false);
//        hardButton.setLocked(!game.isDifficultyUnlocked("hard"));
//        hardButton.addActionListener(e -> showLevels("hard"));
//        difficultyButtons.put("hard", hardButton);
//        add(hardButton, gbc);
//
//        CustomButton superhardButton = new CustomButton("Superhard", SUPERHARD_COLOR, false, false);
//        superhardButton.setLocked(!game.isDifficultyUnlocked("superhard"));
//        superhardButton.addActionListener(e -> showLevels("superhard"));
//        difficultyButtons.put("superhard", superhardButton);
//        add(superhardButton, gbc);
//
//        CustomButton exitButton = new CustomButton("Exit", EXIT_COLOR, false, false);
//        exitButton.addActionListener(e -> System.exit(0));
//        add(exitButton, gbc);
//
//        revalidate();
//        repaint();
//    }
//
//    private void showLevels(String difficulty) {
//        if (difficulty == null) {
//            throw new IllegalArgumentException("Difficulty cannot be null");
//        }
//        removeAll();
//        currentDifficulty = difficulty;
//
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.gridwidth = GridBagConstraints.REMAINDER;
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//        gbc.insets = new Insets(15, 15, 15, 15);
//
//        String displayDifficulty = difficulty.equals("superhard") ? "Superhard" : capitalize(difficulty);
//        Color titleColor;
//        switch (difficulty) {
//            case "easy":
//                titleColor = EASY_COLOR;
//                break;
//            case "medium":
//                titleColor = MEDIUM_COLOR;
//                break;
//            case "hard":
//                titleColor = HARD_COLOR;
//                break;
//            case "superhard":
//                titleColor = SUPERHARD_COLOR;
//                break;
//            default:
//                throw new IllegalArgumentException("Invalid difficulty: " + difficulty);
//        }
//        title = new JLabel(displayDifficulty + " Levels", SwingConstants.CENTER) {
//            @Override
//            protected void paintComponent(Graphics g) {
//                Graphics2D g2d = (Graphics2D) g;
//                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//                String text = getText();
//                Font font = new Font("VNM Sans Display Bold", Font.BOLD, 36);
//                g2d.setFont(font);
//                FontMetrics fm = g2d.getFontMetrics();
//
//                g2d.setColor(new Color(20, 20, 50, 100));
//                g2d.drawString(text, 3, fm.getHeight() + 2);
//
//                GradientPaint gradient = new GradientPaint(
//                        0, 0, titleColor,
//                        0, fm.getHeight() + 20, new Color(255, 255, 255)
//                );
//                g2d.setPaint(gradient);
//                g2d.drawString(text, 0, fm.getHeight());
//            }
//        };
//        title.setPreferredSize(new Dimension(400, 80));
//        gbc.gridy = 0;
//        add(title, gbc);
//
//        List<LevelConfig> levels = game.getDifficultyLevels().get(difficulty);
//        if (levels == null || levels.isEmpty()) {
//            JLabel errorLabel = new JLabel("No levels found for " + displayDifficulty, SwingConstants.CENTER);
//            errorLabel.setForeground(Color.RED);
//            errorLabel.setFont(new Font("VNM Sans Display Bold", Font.BOLD, 20));
//            gbc.gridy = 1;
//            add(errorLabel, gbc);
//        } else {
//            boolean isUnlocked = game.isDifficultyUnlocked(difficulty);
//            Color levelColor;
//            switch (difficulty) {
//                case "easy":
//                    levelColor = new Color(74, 144, 226, 180);
//                    break;
//                case "medium":
//                    levelColor = new Color(80, 200, 120, 180);
//                    break;
//                case "hard":
//                    levelColor = new Color(245, 166, 35, 180);
//                    break;
//                case "superhard":
//                    levelColor = new Color(233, 78, 119, 180);
//                    break;
//                default:
//                    throw new IllegalArgumentException("Invalid difficulty: " + difficulty);
//            }
//
//            final int BUTTONS_PER_ROW = 4;
//            int numRows = (int) Math.ceil((double) levels.size() / BUTTONS_PER_ROW);
//            JPanel buttonGridPanel = new JPanel(new GridLayout(numRows, BUTTONS_PER_ROW, 10, 10));
//            buttonGridPanel.setOpaque(false);
//            buttonGridPanel.setPreferredSize(new Dimension(BUTTONS_PER_ROW * 70, numRows * 70));
//
//            for (int i = 0; i < levels.size(); i++) {
//                String buttonText = String.valueOf(i + 1);
//                if (game.getCompletedLevels().contains(difficulty + "_" + i)) {
//                    buttonText += " ✓";
//                }
//                CustomButton levelButton = new CustomButton(buttonText, levelColor, false, true);
//                levelButton.setLocked(!isUnlocked);
//                final int levelIndex = i;
//                levelButton.addActionListener(e -> game.startLevel(difficulty, levelIndex));
//                buttonGridPanel.add(levelButton);
//            }
//
//            int remainingSlots = (numRows * BUTTONS_PER_ROW) - levels.size();
//            for (int i = 0; i < remainingSlots; i++) {
//                buttonGridPanel.add(new JLabel());
//            }
//
//            gbc.gridy = 1;
//            gbc.fill = GridBagConstraints.NONE;
//            gbc.anchor = GridBagConstraints.CENTER;
//            add(buttonGridPanel, gbc);
//
//            CustomButton backButton = new CustomButton("Back", EXIT_COLOR, true, false);
//            backButton.addActionListener(e -> showDifficulties());
//            gbc.gridy = 2;
//            gbc.fill = GridBagConstraints.HORIZONTAL;
//            gbc.anchor = GridBagConstraints.CENTER;
//            add(backButton, gbc);
//        }
//
//        revalidate();
//        repaint();
//    }
//
//    private String capitalize(String str) {
//        if (str == null || str.isEmpty()) return str;
//        return str.substring(0, 1).toUpperCase() + str.substring(1);
//    }
//}
//
//
//
//class CustomButton extends JButton {
//    private Color baseColor;
//    private Color hoverColor;
//    private Color lockedColor;
//    private boolean isBackButton;
//    private boolean isLevelButton;
//    private boolean isHovered;
//    private boolean isLocked;
//    private boolean animateUnlock; // For unlock animation
//    private float animationAlpha; // For glow effect
//    private Timer animationTimer;
//
//
//    public CustomButton(String text, Color baseColor, boolean isBackButton, boolean isLevelButton) {
//        super(text);
//        this.baseColor = baseColor;
//        this.isBackButton = isBackButton;
//        this.isLevelButton = isLevelButton;
//        this.isHovered = false;
//        this.isLocked = !isEnabled(); // Initially locked if disabled
//        this.animateUnlock = false;
//        this.animationAlpha = 0f;
//
//        // Calculate hover color (lighter version of base color)
//        int r = Math.min(255, baseColor.getRed() + 50);
//        int g = Math.min(255, baseColor.getGreen() + 50);
//        int b = Math.min(255, baseColor.getBlue() + 50);
//        this.hoverColor = new Color(r, g, b);
//
//        // Locked color (grayed out version of base color)
//        this.lockedColor = new Color(100, 100, 100);
//
//        setContentAreaFilled(false);
//        setFocusPainted(false);
//        setBorderPainted(false);
//        if (isLevelButton) {
//            setPreferredSize(new Dimension(60, 60)); // Circular button for levels
//        } else {
//            setPreferredSize(new Dimension(100, 50)); // Rectangular button for difficulties and Back
//        }
////        setPreferredSize(new Dimension(280, 50));
//        setFont(new Font("VNM Sans Display", Font.BOLD, 20));
//
//        // Add hover effect
//        addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseEntered(MouseEvent e) {
//                if (isEnabled()) {
//                    isHovered = true;
//                    repaint();
//                }
//            }
//
//            @Override
//            public void mouseExited(MouseEvent e) {
//                isHovered = false;
//                repaint();
//            }
//        });
//    }
//
//    public void setLocked(boolean locked) {
//        this.isLocked = locked;
//        setEnabled(!locked);
//        repaint();
//    }
//
//    public void startUnlockAnimation() {
//        if (!isLocked) {
//            animateUnlock = true;
//            animationAlpha = 0f;
//            if (animationTimer != null && animationTimer.isRunning()) {
//                animationTimer.stop();
//            }
//            animationTimer = new Timer(30, e -> {
//                animationAlpha += 0.05f;
//                if (animationAlpha >= 1f) {
//                    animationAlpha = 0f;
//                    animateUnlock = false;
//                    ((Timer)e.getSource()).stop();
//                }
//                repaint();
//            });
//            animationTimer.start();
//        }
//    }
//
//    @Override
//    protected void paintComponent(Graphics g) {
//        Graphics2D g2d = (Graphics2D) g;
//        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//        // Determine the base color based on state
//        Color currentBaseColor = isLocked ? lockedColor : baseColor;
//        Color currentHoverColor = isLocked ? lockedColor : hoverColor;
//
//        if (isLevelButton) {
//            // Circular button for level selection
//            int diameter = Math.min(getWidth(), getHeight()) - 10;
//            int x = (getWidth() - diameter) / 2;
//            int y = (getHeight() - diameter) / 2;
//
//            // Draw shadow
//            g2d.setColor(new Color(0, 0, 0, isHovered ? 100 : 50));
//            g2d.fillOval(x + 5, y + 5, diameter, diameter);
//
//            // Draw button background with gradient
//            Color startColor = isHovered ? currentHoverColor : currentBaseColor;
//            Color endColor = new Color(
//                    Math.max(0, startColor.getRed() - 30),
//                    Math.max(0, startColor.getGreen() - 30),
//                    Math.max(0, startColor.getBlue() - 30)
//            );
//            GradientPaint gradient = new GradientPaint(
//                    x, y, startColor,
//                    x, y + diameter, endColor
//            );
//            g2d.setPaint(gradient);
//            g2d.fillOval(x, y, diameter, diameter);
//
//            // Draw unlock animation (glow effect)
//            if (animateUnlock) {
//                g2d.setColor(new Color(255, 255, 255, (int)(animationAlpha * 200)));
//                g2d.setStroke(new BasicStroke(4));
//                g2d.drawOval(x, y, diameter, diameter);
//            }
//
//            // Draw glow effect (white border)
//            g2d.setColor(new Color(255, 255, 255, isHovered ? 200 : 100));
//            g2d.setStroke(new BasicStroke(2));
//            g2d.drawOval(x, y, diameter, diameter);
//
//            // Draw lock icon if locked
//            if (isLocked) {
//                g2d.setColor(Color.WHITE);
//                int lockSize = 16;
//                int lockX = x + (diameter - lockSize) / 2;
//                int lockY = y + (diameter - lockSize) / 2;
//
//                // Draw lock body
//                g2d.fillRoundRect(lockX + 5, lockY + 5, lockSize - 10, lockSize - 5, 5, 5);
//
//                // Draw lock shackle
//                g2d.setStroke(new BasicStroke(3));
//                g2d.drawArc(lockX + 3, lockY - 5, lockSize - 6, 10, 0, 180);
//            }
//
//            // Draw text (number for level buttons)
//            String text = getText();
//            g2d.setFont(getFont());
//            FontMetrics fm = g2d.getFontMetrics();
//            int textWidth = fm.stringWidth(text);
//            int textHeight = fm.getHeight();
//            int textX = x + (diameter - textWidth) / 2;
//            int textY = y + (diameter + textHeight) / 2 - fm.getDescent();
//
//            // Draw text shadow
//            g2d.setColor(new Color(0, 0, 0, 50));
//            g2d.drawString(text, textX + 1, textY + 1);
//
//            // Draw text
//            g2d.setColor(Color.WHITE);
//            g2d.drawString(text, textX, textY);
//
//
//        } else {
//            // Rectangular button for difficulties and Back
//            // Draw shadow
//            g2d.setColor(new Color(0, 0, 0, isHovered ? 100 : 50));
//            g2d.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 30, 30);
//
//            // Draw button background with gradient
//            Color startColor = isHovered ? currentHoverColor : currentBaseColor;
//            Color endColor = new Color(
//                    Math.max(0, startColor.getRed() - 30),
//                    Math.max(0, startColor.getGreen() - 30),
//                    Math.max(0, startColor.getBlue() - 30)
//            );
//            GradientPaint gradient = new GradientPaint(
//                    0, 0, startColor,
//                    0, getHeight(), endColor
//            );
//            g2d.setPaint(gradient);
//            g2d.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 30, 30);
//
//            // Draw unlock animation (glow effect)
//            if (animateUnlock) {
//                g2d.setColor(new Color(255, 255, 255, (int)(animationAlpha * 200)));
//                g2d.setStroke(new BasicStroke(4));
//                g2d.drawRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 30, 30);
//            }
//
//            // Draw glow effect (white border)
//            g2d.setColor(new Color(255, 255, 255, isHovered ? 200 : 100));
//            g2d.setStroke(new BasicStroke(2));
//            g2d.drawRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 30, 30);
//
//            // Draw lock icon if locked
//            if (isLocked) {
//                g2d.setColor(Color.WHITE);
//                int lockSize = 20;
//                int lockX = 20;
//                int lockY = (getHeight() - lockSize) / 2;
//
//                // Draw lock body
//                g2d.fillRoundRect(lockX + 5, lockY + 5, lockSize - 10, lockSize - 5, 5, 5);
//
//                // Draw lock shackle
//                g2d.setStroke(new BasicStroke(3));
//                g2d.drawArc(lockX + 3, lockY - 5, lockSize - 6, 10, 0, 180);
//            }
//
//            // Draw text
//            String text = getText();
//            g2d.setFont(getFont());
//            FontMetrics fm = g2d.getFontMetrics();
//            int textWidth = fm.stringWidth(text);
//            int textHeight = fm.getHeight();
//            int x = (getWidth() - textWidth) / 2;
//            int y = (getHeight() + textHeight) / 2 - fm.getDescent() - 5;
//
//            // Draw text shadow
//            g2d.setColor(new Color(0, 0, 0, 50));
//            g2d.drawString(text, x + 1, y + 1);
//
//            // Draw text
//            g2d.setColor(Color.WHITE);
//            g2d.drawString(text, x, y);
//
//        }
//    }
//}
