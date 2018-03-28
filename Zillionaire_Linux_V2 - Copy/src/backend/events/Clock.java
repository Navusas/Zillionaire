package backend.events;

import javax.swing.*;

/**
 * @author Domantas Giedraitis - Navus
 * @version 1.001
 **/

public class Clock {
    private java.util.Timer timer;
    private JLabel middleCircleLabel;

    public Clock(JLabel middleCircleLabel) {
        this.middleCircleLabel = middleCircleLabel;
        timer = new java.util.Timer();
    }
    /*
    * Creates instance of a new class , and then schedules a
    * back timer, which required milliseconds. If seconds are not provided
    * sets them by default from Config class file.
    * period - the time in milliseconds to refresh the thread.
    * @param - timeLabel - label where to show timer.
    * */
    public void startBackTimer(JLabel timeLabel) {
        timer = new java.util.Timer();
        Timer Timer = new Timer(middleCircleLabel,timeLabel);
        // Execute timer thread every 10 milliseconds, without delay.
        timer.scheduleAtFixedRate(Timer,0, 10 );
    }
    public void startDecisionTimer(JLabel timeLabel, int milliseconds) {
        timer = new java.util.Timer();
        Timer Timer = new Timer(middleCircleLabel, timeLabel,milliseconds,true);
        // Execute timer thread every 10 milliseconds, without delay.
        timer.scheduleAtFixedRate(Timer,0,10);
    }
    public void reset() {
        timer.cancel();
    }
    public boolean circleLabel() {
        return middleCircleLabel.getText().equals("00.0");
    }


}