package backend.events;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import javax.swing.*;
import java.io.File;

/**
 * @author Domantas Giedraitis - Navus
 * @version 1.001
 **/

abstract public class SoundPlayer {
    private double volume = 1;
    private static MediaPlayer mediaPlayer;
    private boolean isInitialized = false;
    private final JFXPanel fxPanel = new JFXPanel();

    public void mutePlayer() {
        this.volume = 0;
        mediaPlayer.setVolume(volume);
    }
    public void unmutePlayer() {
        this.volume = 1;
        mediaPlayer.setVolume(volume);};

    public void continuePlayer() {
        mediaPlayer.play();
    }
    public void stopPlayer() { mediaPlayer.stop(); }
    public void pausePlayer() { mediaPlayer.pause(); }
    public void disposePlayer() { mediaPlayer.dispose();}

    /*
    * Finds the file to be played, and plays it.
    * Automatically starts the same sound after it finishes.
    * @param name - file name to be played
    * */
    public void startPlayer(String name) {
        if(isInitialized) {
            mediaPlayer.stop();
            mediaPlayer.dispose();}
        File newfile = new File("src/Sounds/"+name+".mp3").getAbsoluteFile();
        try {
            Media hit = new Media(newfile.toURI().toString());
            mediaPlayer = new MediaPlayer(hit);
            mediaPlayer.play();
            mediaPlayer.setVolume(this.volume);
            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.seek(Duration.ZERO);
            });
            isInitialized = true;
        }
        catch (MediaException e1) {
            mediaPlayer.dispose();
            JOptionPane.showMessageDialog(null, ".WAV is not supported","ERROR",
                    JOptionPane.WARNING_MESSAGE,null);
        }
        catch (NullPointerException e2) {
            mediaPlayer.dispose();
            JOptionPane.showMessageDialog(null, ".File not found","ERROR",
                    JOptionPane.WARNING_MESSAGE,null);
        }
    }
}
