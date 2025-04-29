package flowfree.view;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.util.*;

import flowfree.FlowFreeGame;
import flowfree.view.components.CustomButton;

public class GameView extends JPanel {
    private flowfree.model.core.GameModel model;
    private FlowFreeGame game;
    private String difficulty;
    private int levelIndex;
    private Map<String, CustomButton> difficultyButtons;
    private static final int CELL_SIZE = 50;
    private static final int DOT_SIZE = 40;
    private static final int PIPE_WIDTH = 20;
    private static final Map<Character, Color> COLOR_MAP = new HashMap<>();
    private flowfree.controller.GameController controller;
    private RoundedProgressBar timerBar;
    private Timer uiTimer; // Để cập nhật giao diện thanh thời gian

    static {
        COLOR_MAP.put('R', new Color(255, 0, 0)); // Ferr (Red)
        COLOR_MAP.put('B', new Color(87, 167, 255)); // Geanglo (Blue)
        COLOR_MAP.put('G', Color.GREEN); // Not used in image, but keep for compatibility
        COLOR_MAP.put('Y', Color.YELLOW); // Gowtdernos (Yellow)
        COLOR_MAP.put('P', Color.PINK); // pink
        COLOR_MAP.put('O', Color.ORANGE); // Not used in image, but keep for compatibility
        COLOR_MAP.put('.', new Color(220, 200, 170)); // Beige for cells
        COLOR_MAP.put('A', new Color(0, 255, 255)); // aqua
        COLOR_MAP.put('T', new Color(255, 99, 71)); // tomato
        COLOR_MAP.put('K', Color.BLACK); // black
        COLOR_MAP.put('X', Color.GRAY); // gray
        COLOR_MAP.put('V', new Color(238, 130, 238)); // violet
        COLOR_MAP.put('U', new Color(128, 0, 128)); // purple
        COLOR_MAP.put('C', new Color(0, 139,139)); // darkcyan
        COLOR_MAP.put('E', new Color(0, 0, 139)); // darkblue
        COLOR_MAP.put('N', new Color(0, 100, 0)); //darkgreen
        COLOR_MAP.put('L', new Color(173, 255, 47)); // greenyellow
    }

    private static final Color BACKGROUND_COLOR = new Color(100, 200, 200);
    private static final Color BORDER_COLOR = new Color(180, 160, 130);
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 50);
    private static final Color EXIT_COLOR = new Color(74, 74, 74);

    private JPanel gridPanel;
    private CustomButton undoButton;
    private CustomButton redoButton;

    // Lớp con của JProgressBar để bo góc
    private static class RoundedProgressBar extends JProgressBar {
        private static final int ARC_RADIUS = 20; // Bán kính bo góc

        public RoundedProgressBar(int min, int max) {
            super(min, max);
            setOpaque(false);
            setBorderPainted(false);
            setStringPainted(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int value = getValue();
            int max = getMaximum();

            // Vẽ nền của thanh tiến trình (màu xám nhạt)
            g2d.setColor(new Color(200, 200, 200));
            g2d.fillRoundRect(0, 0, width - 1, height - 1, ARC_RADIUS, ARC_RADIUS);

            // Tính chiều dài của phần tiến trình
            double ratio = (double) value / max;
            int progressWidth = (int) (width * ratio);

            // Vẽ phần tiến trình với màu phù hợp
            g2d.setColor(getForeground());
            g2d.fillRoundRect(0, 0, progressWidth - 1, height - 1, ARC_RADIUS, ARC_RADIUS);

            // Vẽ viền
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawRoundRect(0, 0, width - 1, height - 1, ARC_RADIUS, ARC_RADIUS);

            // Vẽ chuỗi (text) ở giữa
            String text = getString();
            if (text != null) {
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getAscent();
                int x = (width - textWidth);
                int y = (height + textHeight) / 2 - 2;
                g2d.setColor(Color.BLACK);
                g2d.drawString(text, x, y);
            }

            g2d.dispose();
        }
    }

    public GameView(flowfree.model.core.GameModel model, FlowFreeGame game, String difficulty, int levelIndex) {
        if (model == null || game == null || difficulty == null) {
            throw new IllegalArgumentException("Model, game, and difficulty cannot be null");
        }
        this.model = model;
        this.game = game;
        this.difficulty = difficulty;
        this.levelIndex = levelIndex;
        this.difficultyButtons = new HashMap<>();
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        model.setOnTimeUp(() -> {
            if (uiTimer != null) {
                uiTimer.stop(); // Dừng timer giao diện
            }
            int option = JOptionPane.showOptionDialog(
                    this,
                    "Time's up! You lost. Do you want to retry?",
                    "Game Over",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new String[]{"Retry", "Back to Menu"},
                    "Retry"
            );
            if (option == JOptionPane.YES_OPTION) {
                game.startLevel(difficulty, levelIndex);
            } else {
                game.showMenu();
            }
        });

        gridPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (model == null || model.getGrid() == null) {
                    g.setColor(Color.RED);
                    g.drawString("Error: Game model or grid is null", 10, 20);
                    return;
                }

                char[][] grid = model.getGrid();
                Map<Character, java.util.List<Point>> paths = model.getPaths();

                for (int i = 0; i < grid.length; i++) {
                    for (int j = 0; j < grid[0].length; j++) {
                        int x = j * CELL_SIZE;
                        int y = i * CELL_SIZE;

                        g2d.setColor(COLOR_MAP.get('.'));
                        g2d.fillRoundRect(x + 5, y + 5, CELL_SIZE - 10, CELL_SIZE - 10, 10, 10);

                        g2d.setColor(BORDER_COLOR);
                        g2d.setStroke(new BasicStroke(2));
                        g2d.drawRoundRect(x + 5, y + 5, CELL_SIZE - 10, CELL_SIZE - 10, 10, 10);
                    }
                }

                g2d.setStroke(new BasicStroke(PIPE_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                for (Map.Entry<Character, java.util.List<Point>> entry : paths.entrySet()) {
                    char color = entry.getKey();
                    java.util.List<Point> path = entry.getValue();
                    if (path != null && path.size() > 1) {
                        g2d.setColor(SHADOW_COLOR);
                        for (int i = 1; i < path.size(); i++) {
                            Point p1 = path.get(i - 1);
                            Point p2 = path.get(i);
                            int x1 = p1.y * CELL_SIZE + CELL_SIZE / 2 + 3;
                            int y1 = p1.x * CELL_SIZE + CELL_SIZE / 2 + 3;
                            int x2 = p2.y * CELL_SIZE + CELL_SIZE / 2 + 3;
                            int y2 = p2.x * CELL_SIZE + CELL_SIZE / 2 + 3;
                            g2d.drawLine(x1, y1, x2, y2);
                        }

                        g2d.setColor(COLOR_MAP.getOrDefault(color, Color.WHITE));
                        for (int i = 1; i < path.size(); i++) {
                            Point p1 = path.get(i - 1);
                            Point p2 = path.get(i);
                            int x1 = p1.y * CELL_SIZE + CELL_SIZE / 2;
                            int y1 = p1.x * CELL_SIZE + CELL_SIZE / 2;
                            int x2 = p2.y * CELL_SIZE + CELL_SIZE / 2;
                            int y2 = p2.x * CELL_SIZE + CELL_SIZE / 2;
                            g2d.drawLine(x1, y1, x2, y2);
                        }
                    }
                }

                for (Map.Entry<Character, Point[]> entry : model.getDots().entrySet()) {
                    char color = entry.getKey();
                    Point[] dots = entry.getValue();
                    if (dots != null) {
                        for (Point dot : dots) {
                            if (dot != null) {
                                int x = dot.y * CELL_SIZE + (CELL_SIZE - DOT_SIZE) / 2;
                                int y = dot.x * CELL_SIZE + (CELL_SIZE - DOT_SIZE) / 2;

                                g2d.setColor(SHADOW_COLOR);
                                g2d.fillOval(x + 3, y + 3, DOT_SIZE, DOT_SIZE);

                                g2d.setColor(COLOR_MAP.getOrDefault(color, Color.WHITE));
                                g2d.fillOval(x, y, DOT_SIZE, DOT_SIZE);

                                g2d.setColor(Color.WHITE);
                                g2d.fillOval(x + 10, y + 10, DOT_SIZE / 3, DOT_SIZE / 3);
                            }
                        }
                    }
                }
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(model.getGrid()[0].length * CELL_SIZE, model.getGrid().length * CELL_SIZE);
            }
        };
        gridPanel.setBackground(BACKGROUND_COLOR);

        JPanel centeringPanel = new JPanel(new GridBagLayout());
        centeringPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        centeringPanel.add(gridPanel, gbc);
        add(centeringPanel, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.setBackground(BACKGROUND_COLOR);

        // Khởi tạo RoundedProgressBar
        int initialTime = model.getTimeRemaining();
        timerBar = new RoundedProgressBar(0, initialTime);
        timerBar.setValue(initialTime);
        timerBar.setPreferredSize(new Dimension(200, 30));
        timerBar.setForeground(Color.GREEN); // Màu mặc định ban đầu là xanh lá
        timerBar.setString(" ⏱️ ");

        uiTimer = new Timer(1000, e -> {
            int timeRemaining = model.getTimeRemaining();
            timerBar.setValue(timeRemaining);

            // Tính tỷ lệ thời gian còn lại
            double timeRatio = (double) timeRemaining / initialTime;

            // Thay đổi màu dựa trên tỷ lệ thời gian
            if (timeRatio > 2.0 / 3.0) {
                timerBar.setForeground(Color.GREEN);
            } else if (timeRatio > 1.0 / 3.0) {
                timerBar.setForeground(Color.YELLOW);
            } else {
                timerBar.setForeground(Color.RED);
            }

            timerBar.repaint(); // Đảm bảo giao diện được làm mới
        });
        uiTimer.start();

        topPanel.add(timerBar);
        add(topPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        undoButton = new CustomButton("Undo", EXIT_COLOR, false, false);
        undoButton.setEnabled(false);
        undoButton.addActionListener(e -> controller.undo());
        buttonPanel.add(undoButton);

        redoButton = new CustomButton("Redo", EXIT_COLOR, false, false);
        redoButton.setEnabled(false);
        redoButton.addActionListener(e -> controller.redo());
        buttonPanel.add(redoButton);

        CustomButton exitButton = new CustomButton("Exit", EXIT_COLOR, true, false);
        exitButton.addActionListener(e -> {
            model.stopTimer();
            if (uiTimer != null) {
                uiTimer.stop(); // Dừng timer giao diện
            }
            game.showMenu();
        });
        buttonPanel.add(exitButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Bắt đầu timer của trò chơi
        model.startTimer();
    }

    public JPanel getGridPanel() {
        return gridPanel;
    }

    public void setController(flowfree.controller.GameController controller) {
        if (controller == null) {
            throw new IllegalArgumentException("Controller cannot be null");
        }
        this.controller = controller;
    }

    public void updateButtonStates(boolean canUndo, boolean canRedo) {
        undoButton.setEnabled(canUndo);
        redoButton.setEnabled(canRedo);
    }

    // Phương thức để dừng timer giao diện khi trò chơi kết thúc
    public void stopUITimer() {
        if (uiTimer != null) {
            uiTimer.stop();
        }
    }
}