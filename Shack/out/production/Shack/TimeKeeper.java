import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class TimeKeeper implements ActionListener {
    private Player thePlayer;
    private int clock;
    private int increment;
    private int clockStep = 100;
    private Timer timer = new Timer(clockStep, this);
    private boolean timerIsRunning, timerIsPaused;

    TimeKeeper(Player thePlayer, int clock, int increment) {
        this.thePlayer = thePlayer;
        this.clock = clock;
        this.increment = increment;
    }

    void incrementClock() {clock += increment;}

    void start() {
        timer.start();
        timerIsRunning = true;
    }

    void stop() {
        timer.stop();
        timerIsRunning = false;
    }

    void pause() {
        timer.stop();
        timerIsPaused = timerIsRunning;
    }

    void restart() {
        if (timerIsPaused) timer.start();
    }

    String timeToString(int time) {
        int minutes = time / 60000;
        int seconds = (time % 60000)/1000;
        int secondsFirstDigit = seconds / 10;
        int secondsSecondDigit = seconds % 10;
        String result = "";
        if (minutes > 0) result += minutes;
        result += ":"+secondsFirstDigit;
        result += secondsSecondDigit;
        return result;
    }

    String timeToString() {return timeToString(clock);}

    public void actionPerformed(ActionEvent e) {
        clock -= clockStep;
        if (clock % 1000 == 0) {
            thePlayer.showTime(true);
            thePlayer.sendTime(timeToString());
        }
        if (clock < 0) {
            timer.stop();
            thePlayer.loseOnTime();
        }
    }
}

