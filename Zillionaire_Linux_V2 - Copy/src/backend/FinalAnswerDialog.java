package backend;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
* @author Domantas Giedraitis - Navus
* @version 1.001
**/

public class FinalAnswerDialog extends JDialog {

    private JPanel contentPane;
    private JLabel answerText;
    private JButton yesButton, noButton;
    private boolean isRunning = false, isOkeyPressed = false;

    public FinalAnswerDialog(JPanel cards,int delay, String text) {
        isRunning = true;
        createDialog();
        setValues(text);
        startAutoExitTimer(delay);
        yesButton.addActionListener(e -> onOkey());
        noButton.addActionListener(e -> onCancel());
        showDialog(cards);

    }
    private void createDialog() {
        setAlwaysOnTop(true);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(yesButton);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    private void showDialog(JPanel cards) {
        pack();
        setLocationRelativeTo(cards);
        setVisible(true);
    }
    /*
    * Method which automatically dispoe this window after time has finished.
    *
    * @param: delay -  the value after which this window will dispose
    * */
    private void startAutoExitTimer(int delay) {
        Timer timer = new Timer( (delay*1000), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if(isRunning) {
                    isRunning = false;
                    dispose();
                }
            }
        });
        timer.setRepeats(false);//the timer should only go off once

        //start timer to close JDialog as dialog modal we must start the timer before its visible
        timer.start();
    }


    private void onOkey() {
        isRunning = false;
        isOkeyPressed = true;
        dispose();
    }
    private void onCancel() {
        isRunning = false;
        dispose();
    }
    public boolean isOkeyPressed() {
        return isOkeyPressed;
    }

    private void setValues(String labelText) {
        answerText.setText(labelText);
        setTitle("Confirm your final answer");
    }
}
