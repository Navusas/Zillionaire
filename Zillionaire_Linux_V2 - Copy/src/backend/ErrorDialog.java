package backend;

import javax.swing.*;
import java.awt.event.*;

/**
 * @author Domantas Giedraitis - Navus
 * @version 1.001
 **/

public class ErrorDialog extends JDialog {

    private JPanel contentPane;
    private JButton buttonOK;
    private JLabel messageLabel;

    public ErrorDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setResizable(false);

        buttonOK.addActionListener(e -> onOK());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
    public void closeWindow() {
        dispose();
    }

    private void setValues(String title, String message) {
        setTitle(title);
        this.messageLabel.setText("<html>"+message+"</html>");
    }
    public void showErrorDialog(JPanel frame, String title, String message) {
        SwingUtilities.invokeLater(() -> {
            setValues(title,message);
            pack();
            setLocationRelativeTo(frame);
            setVisible(true);
        });
    }
}