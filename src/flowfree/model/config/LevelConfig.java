package flowfree.model.config;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class LevelConfig {
    private int rows, cols;
    private Map<Character, Point[]> dots;
    private String fileName;
    private String difficulty;

    public LevelConfig(int rows, int cols, Map<Character, Point[]> dots, String fileName, String difficulty) {
        this.rows = rows;
        this.cols = cols;
        this.dots = dots;
        this.fileName = fileName;
        this.difficulty = difficulty;
    }

    public static LevelConfig fromFile(File file, String difficulty) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String[] dimensions = reader.readLine().trim().split("\\s+");
            if (dimensions.length != 2) {
                throw new IOException("Invalid dimensions in " + file.getName());
            }

            int rows, cols;
            try {
                rows = Integer.parseInt(dimensions[0]);
                cols = Integer.parseInt(dimensions[1]);
            } catch (NumberFormatException e) {
                throw new IOException("Invalid dimensions format in " + file.getName());
            }

            Map<Character, Point[]> dots = new HashMap<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length != 5) {
                    throw new IOException("Invalid dot format in " + file.getName());
                }

                char color;
                int x1, y1, x2, y2;
                try {
                    color = parts[0].charAt(0);
                    x1 = Integer.parseInt(parts[1]);
                    y1 = Integer.parseInt(parts[2]);
                    x2 = Integer.parseInt(parts[3]);
                    y2 = Integer.parseInt(parts[4]);
                } catch (NumberFormatException e) {
                    throw new IOException("Invalid number format in " + file.getName());
                }

                if (x1 < 0 || x1 >= rows || y1 < 0 || y1 >= cols ||
                        x2 < 0 || x2 >= rows || y2 < 0 || y2 >= cols) {
                    throw new IOException("Dot coordinates out of bounds in " + file.getName());
                }

                dots.put(color, new Point[]{new Point(x1, y1), new Point(x2, y2)});
            }

            return new LevelConfig(rows, cols, dots, file.getName(), difficulty);
        }
    }

    // For reading multiple levels from a single file (used for easy)
    public static List<LevelConfig> fromSingleFile(File file, String difficulty) throws IOException {
        List<LevelConfig> levels = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int levelIndex = 0;
            int rows = 0, cols = 0;
            Map<Character, Point[]> dots = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.equals("---")) {
                    // End of a level, add to list if we have data
                    if (dots != null && rows > 0 && cols > 0) {
                        levels.add(new LevelConfig(rows, cols, dots, "easy_" + levelIndex + ".txt", difficulty));
                        levelIndex++;
                    }
                    // Reset for the next level
                    rows = 0;
                    cols = 0;
                    dots = null;
                    continue;
                }

                if (rows == 0 && cols == 0) {
                    // First line of a level: dimensions
                    String[] dimensions = line.split("\\s+");
                    if (dimensions.length != 2) {
                        throw new IOException("Invalid dimensions in " + file.getName() + " at level " + (levelIndex + 1));
                    }
                    try {
                        rows = Integer.parseInt(dimensions[0]);
                        cols = Integer.parseInt(dimensions[1]);
                    } catch (NumberFormatException e) {
                        throw new IOException("Invalid dimensions format in " + file.getName() + " at level " + (levelIndex + 1));
                    }
                    dots = new HashMap<>();
                } else {
                    // Dot data
                    String[] parts = line.split("\\s+");
                    if (parts.length != 5) {
                        throw new IOException("Invalid dot format in " + file.getName() + " at level " + (levelIndex + 1));
                    }

                    char color;
                    int x1, y1, x2, y2;
                    try {
                        color = parts[0].charAt(0);
                        x1 = Integer.parseInt(parts[1]);
                        y1 = Integer.parseInt(parts[2]);
                        x2 = Integer.parseInt(parts[3]);
                        y2 = Integer.parseInt(parts[4]);
                    } catch (NumberFormatException e) {
                        throw new IOException("Invalid number format in " + file.getName() + " at level " + (levelIndex + 1));
                    }

                    if (x1 < 0 || x1 >= rows || y1 < 0 || y1 >= cols ||
                            x2 < 0 || x2 >= rows || y2 < 0 || y2 >= cols) {
                        throw new IOException("Dot coordinates out of bounds in " + file.getName() + " at level " + (levelIndex + 1));
                    }

                    dots.put(color, new Point[]{new Point(x1, y1), new Point(x2, y2)});
                }
            }

            // Add the last level if it exists
            if (dots != null && rows > 0 && cols > 0) {
                levels.add(new LevelConfig(rows, cols, dots, "easy_" + levelIndex + ".txt", difficulty));
            }
        }

        if (levels.isEmpty()) {
            throw new IOException("No valid levels found in " + file.getName());
        }

        return levels;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public Map<Character, Point[]> getDots() {
        return dots;
    }

    public String getFileName() {
        return fileName;
    }

    public String getDifficulty() {
        return difficulty;
    }
}
