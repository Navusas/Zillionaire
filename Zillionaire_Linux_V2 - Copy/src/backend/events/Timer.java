package backend.events;

import javax.swing.*;
import java.text.DecimalFormat;
import java.util.TimerTask;

/**
 * @author Domantas Giedraitis - Navus
 * @version 1.001
 **/

public class Timer extends TimerTask {
    private long runtime_seconds = backend.Config.CHRONOMETER_TIME; // takes default value from Config class
    private JLabel middleCircleLabel,timeLabel;
    private boolean isRunning = false;
    private long startTime = System.currentTimeMillis();
    private boolean waitingForDecision = false;

    public Timer(JLabel middleCircleLabel, JLabel timeLabel, int milliseconds, boolean waitingForDecision){
        this.middleCircleLabel = middleCircleLabel;
        this.timeLabel = timeLabel;
        this.runtime_seconds = milliseconds;
        this.waitingForDecision = true;
        run();
    }
    public Timer(JLabel middleCircleLabel, JLabel timeLabel, int milliseconds){
        this.middleCircleLabel = middleCircleLabel;
        this.timeLabel = timeLabel;
        this.runtime_seconds = milliseconds;
        run();
    }
    public Timer(JLabel middleCircleLabel, JLabel timeLabel){
        this.middleCircleLabel = middleCircleLabel;
        this.timeLabel = timeLabel;
        run();
    }
    /*
    * Calculates how many seconds , milliseconds passed since this object has been created, then
    * minus them from the desired runtime seconds.
    * For example : Runtime =  30 Seconds,
    *               Already Passed = 15 Seconds (since this object has been created),
    *               It will display 30-15  = 15 seconds on the label.
    * */
    public void run() {
        long startTime = this.startTime;
        long runTime = this.runtime_seconds;
        this.isRunning = true;
        middleCircleLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("../../Sprites/circleMain200x200.png")));
        long now = System.currentTimeMillis();
        long dif = now - startTime;
        if (dif >= runTime) {
            dif = runTime;
        }

        dif = runTime - dif;
        long seconds = dif / 1000;
        dif = Math.round(dif % 1000);
        long milliseconds = dif / 100;

        if (!this.waitingForDecision) { updateView(seconds,milliseconds); }
        else {
            updateDecisionView(seconds);
        }

    }

    /*
    * sets seconds and milliseconds to timer label in human related manner - 00:0 ,
    * changes circle image (giving warning) when appropriate time is reached.
    * @param seconds, milliseconds
    * */
    private void updateView(long seconds, long milliseconds) {
        this.isRunning = true;
        DecimalFormat minuteFormatter = new DecimalFormat("00");
        DecimalFormat secondFormatter = new DecimalFormat("0");
        if((seconds == 10 || seconds == 8 || seconds == 6 ) && (milliseconds <= 9 && milliseconds >= 7)) {
            setCircleReminder();
        }
        else if((seconds == 4 || seconds == 3 || seconds == 2 || seconds == 1) && (milliseconds <= 9 && milliseconds >= 7)) {
            setCircleWarning();
        }
        else if(seconds == 0) {
            setCircleWarning();
            if(milliseconds == 0) {
                isRunning = false;
            }
        }
        this.timeLabel.setText(minuteFormatter.format(seconds) + "."
                + secondFormatter.format(milliseconds));
    }
    private void setCircleWarning() {
        middleCircleLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("../../Sprites/circle_warning.png")));

    }
    private void setCircleReminder() {
        middleCircleLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("../../Sprites/circle_reminder.png")));

    }
    private void updateDecisionView(long seconds) {
        DecimalFormat newMinuteFormatter = new DecimalFormat("0");
        this.timeLabel.setText(newMinuteFormatter.format(seconds));
    }
}