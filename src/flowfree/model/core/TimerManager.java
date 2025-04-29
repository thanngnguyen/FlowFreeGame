package flowfree.model.core;

import javax.swing.Timer;
import java.io.Serializable;

public class TimerManager implements Serializable {
    private int timeLimit;
    private int timeRemaining;
    private transient Timer timer; // transient vì Timer không thể serialize
    private transient Runnable onTimeUp; // Callback khi hết thời gian

    public TimerManager(int timeLimit) {
        if (timeLimit <= 0) {
            throw new IllegalArgumentException("Time limit must be positive: " + timeLimit);
        }
        this.timeLimit = timeLimit;
        this.timeRemaining = timeLimit;
    }

    public void setOnTimeUp(Runnable onTimeUp) {
        this.onTimeUp = onTimeUp;
    }

    public int getTimeRemaining() {
        return timeRemaining;
    }

    public void startTimer(boolean isGameWon) {
        if (timer == null) {
            timer = new Timer(1000, e -> {
                if (timeRemaining > 0) {
                    timeRemaining--;
                }
                if (timeRemaining <= 0) {
                    timer.stop();
                    if (onTimeUp != null && !isGameWon) {
                        onTimeUp.run();
                    }
                }
            });
            timer.start();
        }
    }

    public void stopTimer() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }
}