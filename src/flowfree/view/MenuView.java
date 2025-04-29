package flowfree.view;

import flowfree.FlowFreeGame;
import flowfree.model.config.LevelConfig;
import flowfree.view.components.CustomButton;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuView extends JPanel {
    private FlowFreeGame game;
    private String currentDifficulty;
    private JLabel title;
    private Map<String, CustomButton> difficultyButtons;

    private static final Color EASY_COLOR = new Color(74, 144, 226);
    private static final Color MEDIUM_COLOR = new Color(80, 200, 120);
    private static final Color HARD_COLOR = new Color(245, 166, 35);
    private static final Color SUPERHARD_COLOR = new Color(233, 78, 119);
    private static final Color EXIT_COLOR = new Color(74, 74, 74);

    public MenuView(FlowFreeGame game) {
        if (game == null) {
            throw new IllegalArgumentException("Game cannot be null");
        }
        this.game = game;
        this.currentDifficulty = null;
        this.difficultyButtons = new HashMap<>();
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(600, 700));
        showDifficulties();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        RadialGradientPaint radialGradient = new RadialGradientPaint(
                getWidth() / 2f, getHeight() / 2f, getWidth(),
                new float[]{0f, 1f},
                new Color[]{new Color(205, 255, 216), new Color(148, 185, 255)}
        );
        g2d.setPaint(radialGradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }

    public void refresh() {
        if (currentDifficulty == null) {
            showDifficulties();
        } else {
            showLevels(currentDifficulty);
        }
    }

    public void notifyDifficultyUnlocked(String difficulty) {
        if (currentDifficulty == null) {
            CustomButton button = difficultyButtons.get(difficulty);
            if (button != null) {
                button.setLocked(false);
                button.startUnlockAnimation();
            }
        }
    }

    private void showDifficulties() {
        removeAll();
        currentDifficulty = null;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 15, 15, 15);

        title = new JLabel("Flow Free", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                String text = getText();
                Font font = new Font("VNM Sans Display Bold", Font.BOLD, 48);
                g2d.setFont(font);
                FontMetrics fm = g2d.getFontMetrics();

                java.text.AttributedString attributedString = new java.text.AttributedString(text);
                attributedString.addAttribute(java.awt.font.TextAttribute.FONT, font);

                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(255, 255, 255),
                        0, fm.getHeight(), new Color(200, 200, 200)
                );
                attributedString.addAttribute(java.awt.font.TextAttribute.FOREGROUND, gradient, 0, text.length());

                attributedString.addAttribute(java.awt.font.TextAttribute.FOREGROUND, new Color(74, 144, 226), 0, 1);
                attributedString.addAttribute(java.awt.font.TextAttribute.FOREGROUND, new Color(80, 200, 120), 1, 2);
                attributedString.addAttribute(java.awt.font.TextAttribute.FOREGROUND, new Color(245, 166, 35), 2, 3);
                attributedString.addAttribute(java.awt.font.TextAttribute.FOREGROUND, new Color(233, 78, 119), 3, 4);
                attributedString.addAttribute(java.awt.font.TextAttribute.FOREGROUND, new Color(74, 144, 226), 5, 6);
                attributedString.addAttribute(java.awt.font.TextAttribute.FOREGROUND, new Color(80, 200, 120), 6, 7);
                attributedString.addAttribute(java.awt.font.TextAttribute.FOREGROUND, new Color(245, 166, 35), 7, 8);
                attributedString.addAttribute(java.awt.font.TextAttribute.FOREGROUND, new Color(233, 78, 119), 8, 9);

                g2d.setPaint(gradient);
                g2d.drawString(attributedString.getIterator(), 0, fm.getHeight());
            }
        };
        title.setPreferredSize(new Dimension(400, 100));
        add(title, gbc);

        CustomButton easyButton = new CustomButton("Easy", EASY_COLOR, false, false);
        easyButton.setLocked(!game.isDifficultyUnlocked("easy"));
        easyButton.addActionListener(e -> showLevels("easy"));
        difficultyButtons.put("easy", easyButton);
        add(easyButton, gbc);

        CustomButton mediumButton = new CustomButton("Medium", MEDIUM_COLOR, false, false);
        mediumButton.setLocked(!game.isDifficultyUnlocked("medium"));
        mediumButton.addActionListener(e -> showLevels("medium"));
        difficultyButtons.put("medium", mediumButton);
        add(mediumButton, gbc);

        CustomButton hardButton = new CustomButton("Hard", HARD_COLOR, false, false);
        hardButton.setLocked(!game.isDifficultyUnlocked("hard"));
        hardButton.addActionListener(e -> showLevels("hard"));
        difficultyButtons.put("hard", hardButton);
        add(hardButton, gbc);

        CustomButton superhardButton = new CustomButton("Superhard", SUPERHARD_COLOR, false, false);
        superhardButton.setLocked(!game.isDifficultyUnlocked("superhard"));
        superhardButton.addActionListener(e -> showLevels("superhard"));
        difficultyButtons.put("superhard", superhardButton);
        add(superhardButton, gbc);

        CustomButton exitButton = new CustomButton("Exit", EXIT_COLOR, false, false);
        exitButton.addActionListener(e -> System.exit(0));
        add(exitButton, gbc);

        revalidate();
        repaint();
    }

    private void showLevels(String difficulty) {
        if (difficulty == null) {
            throw new IllegalArgumentException("Difficulty cannot be null");
        }
        removeAll();
        currentDifficulty = difficulty;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 15, 15, 15);

        String displayDifficulty = difficulty.equals("superhard") ? "Superhard" : capitalize(difficulty);
        Color titleColor;
        switch (difficulty) {
            case "easy":
                titleColor = EASY_COLOR;
                break;
            case "medium":
                titleColor = MEDIUM_COLOR;
                break;
            case "hard":
                titleColor = HARD_COLOR;
                break;
            case "superhard":
                titleColor = SUPERHARD_COLOR;
                break;
            default:
                throw new IllegalArgumentException("Invalid difficulty: " + difficulty);
        }
        title = new JLabel(displayDifficulty + " Levels", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                String text = getText();
                Font font = new Font("VNM Sans Display Bold", Font.BOLD, 36);
                g2d.setFont(font);
                FontMetrics fm = g2d.getFontMetrics();

                g2d.setColor(new Color(20, 20, 50, 100));
                g2d.drawString(text, 3, fm.getHeight() + 2);

                GradientPaint gradient = new GradientPaint(
                        0, 0, titleColor,
                        0, fm.getHeight() + 20, new Color(255, 255, 255)
                );
                g2d.setPaint(gradient);
                g2d.drawString(text, 0, fm.getHeight());
            }
        };
        title.setPreferredSize(new Dimension(400, 80));
        gbc.gridy = 0;
        add(title, gbc);

        List<LevelConfig> levels = game.getDifficultyLevels().get(difficulty);
        if (levels == null || levels.isEmpty()) {
            JLabel errorLabel = new JLabel("No levels found for " + displayDifficulty, SwingConstants.CENTER);
            errorLabel.setForeground(Color.RED);
            errorLabel.setFont(new Font("VNM Sans Display Bold", Font.BOLD, 20));
            gbc.gridy = 1;
            add(errorLabel, gbc);
        } else {
            boolean isUnlocked = game.isDifficultyUnlocked(difficulty);
            Color levelColor;
            switch (difficulty) {
                case "easy":
                    levelColor = new Color(74, 144, 226, 180);
                    break;
                case "medium":
                    levelColor = new Color(80, 200, 120, 180);
                    break;
                case "hard":
                    levelColor = new Color(245, 166, 35, 180);
                    break;
                case "superhard":
                    levelColor = new Color(233, 78, 119, 180);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid difficulty: " + difficulty);
            }

            final int BUTTONS_PER_ROW = 4;
            int numRows = (int) Math.ceil((double) levels.size() / BUTTONS_PER_ROW);
            JPanel buttonGridPanel = new JPanel(new GridLayout(numRows, BUTTONS_PER_ROW, 10, 10));
            buttonGridPanel.setOpaque(false);
            buttonGridPanel.setPreferredSize(new Dimension(BUTTONS_PER_ROW * 70, numRows * 70));

            for (int i = 0; i < levels.size(); i++) {
                String buttonText = String.valueOf(i + 1);
                if (game.getCompletedLevels().contains(difficulty + "_" + i)) {
                    buttonText += " ✓";
                }
                CustomButton levelButton = new CustomButton(buttonText, levelColor, false, true);
                levelButton.setLocked(!isUnlocked);
                final int levelIndex = i;
                levelButton.addActionListener(e -> game.startLevel(difficulty, levelIndex));
                buttonGridPanel.add(levelButton);
            }

            int remainingSlots = (numRows * BUTTONS_PER_ROW) - levels.size();
            for (int i = 0; i < remainingSlots; i++) {
                buttonGridPanel.add(new JLabel());
            }

            gbc.gridy = 1;
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.CENTER;
            add(buttonGridPanel, gbc);

            CustomButton backButton = new CustomButton("Back", EXIT_COLOR, true, false);
            backButton.addActionListener(e -> showDifficulties());
            gbc.gridy = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.CENTER;
            add(backButton, gbc);
        }

        revalidate();
        repaint();
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
