package flowfree.view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomButton extends JButton {
    private Color baseColor;
    private Color hoverColor;
    private Color lockedColor;
    private boolean isBackButton;
    private boolean isLevelButton;
    private boolean isHovered;
    private boolean isLocked;
    private boolean animateUnlock; // For unlock animation
    private float animationAlpha; // For glow effect
    private Timer animationTimer;


    public CustomButton(String text, Color baseColor, boolean isBackButton, boolean isLevelButton) {
        super(text);
        this.baseColor = baseColor;
        this.isBackButton = isBackButton;
        this.isLevelButton = isLevelButton;
        this.isHovered = false;
        this.isLocked = !isEnabled(); // Initially locked if disabled
        this.animateUnlock = false;
        this.animationAlpha = 0f;

        // Calculate hover color (lighter version of base color)
        int r = Math.min(255, baseColor.getRed() + 50);
        int g = Math.min(255, baseColor.getGreen() + 50);
        int b = Math.min(255, baseColor.getBlue() + 50);
        this.hoverColor = new Color(r, g, b);

        // Locked color (grayed out version of base color)
        this.lockedColor = new Color(100, 100, 100);

        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        if (isLevelButton) {
            setPreferredSize(new Dimension(60, 60)); // Circular button for levels
        } else {
            setPreferredSize(new Dimension(100, 50)); // Rectangular button for difficulties and Back
        }
//        setPreferredSize(new Dimension(280, 50));
        setFont(new Font("VNM Sans Display", Font.BOLD, 20));

        // Add hover effect
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (isEnabled()) {
                    isHovered = true;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    public void setLocked(boolean locked) {
        this.isLocked = locked;
        setEnabled(!locked);
        repaint();
    }

    public void startUnlockAnimation() {
        if (!isLocked) {
            animateUnlock = true;
            animationAlpha = 0f;
            if (animationTimer != null && animationTimer.isRunning()) {
                animationTimer.stop();
            }
            animationTimer = new Timer(30, e -> {
                animationAlpha += 0.05f;
                if (animationAlpha >= 1f) {
                    animationAlpha = 0f;
                    animateUnlock = false;
                    ((Timer)e.getSource()).stop();
                }
                repaint();
            });
            animationTimer.start();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Determine the base color based on state
        Color currentBaseColor = isLocked ? lockedColor : baseColor;
        Color currentHoverColor = isLocked ? lockedColor : hoverColor;

        if (isLevelButton) {
            // Circular button for level selection
            int diameter = Math.min(getWidth(), getHeight()) - 10;
            int x = (getWidth() - diameter) / 2;
            int y = (getHeight() - diameter) / 2;

            // Draw shadow
            g2d.setColor(new Color(0, 0, 0, isHovered ? 100 : 50));
            g2d.fillOval(x + 5, y + 5, diameter, diameter);

            // Draw button background with gradient
            Color startColor = isHovered ? currentHoverColor : currentBaseColor;
            Color endColor = new Color(
                    Math.max(0, startColor.getRed() - 30),
                    Math.max(0, startColor.getGreen() - 30),
                    Math.max(0, startColor.getBlue() - 30)
            );
            GradientPaint gradient = new GradientPaint(
                    x, y, startColor,
                    x, y + diameter, endColor
            );
            g2d.setPaint(gradient);
            g2d.fillOval(x, y, diameter, diameter);

            // Draw unlock animation (glow effect)
            if (animateUnlock) {
                g2d.setColor(new Color(255, 255, 255, (int)(animationAlpha * 200)));
                g2d.setStroke(new BasicStroke(4));
                g2d.drawOval(x, y, diameter, diameter);
            }

            // Draw glow effect (white border)
            g2d.setColor(new Color(255, 255, 255, isHovered ? 200 : 100));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(x, y, diameter, diameter);

            // Draw lock icon if locked
            if (isLocked) {
                g2d.setColor(Color.WHITE);
                int lockSize = 16;
                int lockX = x + (diameter - lockSize) / 2;
                int lockY = y + (diameter - lockSize) / 2;

                // Draw lock body
                g2d.fillRoundRect(lockX + 5, lockY + 5, lockSize - 10, lockSize - 5, 5, 5);

                // Draw lock shackle
                g2d.setStroke(new BasicStroke(3));
                g2d.drawArc(lockX + 3, lockY - 5, lockSize - 6, 10, 0, 180);
            }

            // Draw text (number for level buttons)
            String text = getText();
            g2d.setFont(getFont());
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getHeight();
            int textX = x + (diameter - textWidth) / 2;
            int textY = y + (diameter + textHeight) / 2 - fm.getDescent();

            // Draw text shadow
            g2d.setColor(new Color(0, 0, 0, 50));
            g2d.drawString(text, textX + 1, textY + 1);

            // Draw text
            g2d.setColor(Color.WHITE);
            g2d.drawString(text, textX, textY);

            // Draw checkmark if completed
//            if (text.endsWith("✓")) {
//                g2d.setColor(Color.GREEN);
//                g2d.setStroke(new BasicStroke(3));
//                int checkX = x + diameter - 20;
//                int checkY = y + diameter - 20;
//                g2d.drawLine(checkX, checkY + 5, checkX + 5, checkY + 10);
//                g2d.drawLine(checkX + 5, checkY + 10, checkX + 15, checkY);
//            }
        } else {
            // Rectangular button for difficulties and Back
            // Draw shadow
            g2d.setColor(new Color(0, 0, 0, isHovered ? 100 : 50));
            g2d.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 30, 30);

            // Draw button background with gradient
            Color startColor = isHovered ? currentHoverColor : currentBaseColor;
            Color endColor = new Color(
                    Math.max(0, startColor.getRed() - 30),
                    Math.max(0, startColor.getGreen() - 30),
                    Math.max(0, startColor.getBlue() - 30)
            );
            GradientPaint gradient = new GradientPaint(
                    0, 0, startColor,
                    0, getHeight(), endColor
            );
            g2d.setPaint(gradient);
            g2d.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 30, 30);

            // Draw unlock animation (glow effect)
            if (animateUnlock) {
                g2d.setColor(new Color(255, 255, 255, (int)(animationAlpha * 200)));
                g2d.setStroke(new BasicStroke(4));
                g2d.drawRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 30, 30);
            }

            // Draw glow effect (white border)
            g2d.setColor(new Color(255, 255, 255, isHovered ? 200 : 100));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 30, 30);

            // Draw lock icon if locked
            if (isLocked) {
                g2d.setColor(Color.WHITE);
                int lockSize = 20;
                int lockX = 20;
                int lockY = (getHeight() - lockSize) / 2;

                // Draw lock body
                g2d.fillRoundRect(lockX + 5, lockY + 5, lockSize - 10, lockSize - 5, 5, 5);

                // Draw lock shackle
                g2d.setStroke(new BasicStroke(3));
                g2d.drawArc(lockX + 3, lockY - 5, lockSize - 6, 10, 0, 180);
            }

            // Draw text
            String text = getText();
            g2d.setFont(getFont());
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getHeight();
            int x = (getWidth() - textWidth) / 2;
            int y = (getHeight() + textHeight) / 2 - fm.getDescent() - 5;

            // Draw text shadow
            g2d.setColor(new Color(0, 0, 0, 50));
            g2d.drawString(text, x + 1, y + 1);

            // Draw text
            g2d.setColor(Color.WHITE);
            g2d.drawString(text, x, y);

        }
    }
}
