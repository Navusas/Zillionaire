package backend;

import backend.model.DataHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author Domantas Giedraitis - Navus
 * @version 1.001
 **/

public class Developer extends JDialog {
    private JPanel contentPane;
    private JPanel logInCard;
    private JTextField usernameField;
    private JTextField passwordField;
    private JButton logIn_BT;
    private JLabel errorLabel;
    private JPanel developerMode;
    private JButton save_developer;
    private JButton cancel_developer;
    private JTextField titleTF;
    private JTextField maxPlayersTF;
    private JTextField chronometerTimeTF;
    private JButton addQuestionButton;
    private JLabel developerErrrorLabel;
    private JPanel addQuestion;
    private JComboBox categoryBox;
    private JPanel questionPanel;
    private JTextField addQuestion_questionTF;
    private JTextField addQuestion_answer3;
    private JTextField addQuestion_answer4;
    private JTextField addQuestion_answer2;
    private JTextField addQuestion_answer1;
    private JTextField addQuestion_rating3;
    private JTextField addQuestion_rating4;
    private JTextField addQuestion_rating1;
    private JTextField addQuestion_rating2;
    private JRadioButton hardRadioButton;
    private JRadioButton easyRadioButton;
    private JRadioButton mediumRadioButton;
    private JButton back_addQuestion;
    private JButton save_addQuestion;
    private JLabel messageLabel;

    private CardLayout cardLayout = (CardLayout) contentPane.getLayout();
    private String currentCard = "intro";

    public Developer() {
        setContentPane(contentPane);
        setModal(true);

        logIn_BT.addActionListener(e -> {
            confirmLogin();
        });
        save_developer.addActionListener(e -> {
            update();
        });
        cancel_developer.addActionListener(e -> {onCancel();});

        KeyAdapter enterPress = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                switch(e.getKeyCode()){
                    case KeyEvent.VK_ENTER:
                        if(currentCard.equals("intro")) {
                            confirmLogin();
                        }
                        else if(currentCard.equals("developer")) {
                            update();
                        }
                        break;
                }
            }
        };
        usernameField.addKeyListener(enterPress);
        passwordField.addKeyListener(enterPress);
        logIn_BT.addKeyListener(enterPress);
        save_developer.addKeyListener(enterPress);
        titleTF.addKeyListener(enterPress);
        maxPlayersTF.addKeyListener(enterPress);
        chronometerTimeTF.addKeyListener(enterPress);

        addQuestionButton.addActionListener(e -> cardLayout.show(contentPane,"addQuestion"));
        back_addQuestion.addActionListener(e -> cardLayout.show(contentPane,"developer"));

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        save_addQuestion.addActionListener(e -> storeQuestion());
    }
    /*
    * Reads data from GUI, if there is an error shows error message
    * Otherwise, opens the file user choosen to store the question
    * Creates JSON object, converts it to GSON object (for nice layout)
    * Stores into file and closes file.
    * */
    private void storeQuestion() {
        DataHandler dataHandler = new DataHandler((String)categoryBox.getSelectedItem());
        String question = addQuestion_questionTF.getText();
        if(question.length() < 10) {
            showErrorDialog("ERROR", "Question is tooooo short");
        }
        else if(!isAnswerFieldOKEY()) {
            showErrorDialog("ERROR","Answer Fields can not be empty!!!");
        }
        else {
            String[] answers = {addQuestion_answer1.getText(), addQuestion_answer2.getText(),
                    addQuestion_answer3.getText(), addQuestion_answer4.getText()};
            Integer[] ratings = getRatings();
            if(ratings != null) {
                /*Checks if there was error while storing a file.. */
                if(!dataHandler.storeNewQuestion((short)getLevelIndex(),question,answers,ratings)) {
                    showErrorDialog("ERROR","Something went wrong. </br> Please contact support team...");
                }
                else {
                    messageLabel.setText("Question saved...");
                    clearTextFields();
                }
            }
        }
    }
    private void clearTextFields() {
        addQuestion_questionTF.setText("");
        addQuestion_answer1.setText("");
        addQuestion_answer2.setText("");
        addQuestion_answer3.setText("");
        addQuestion_answer4.setText("");
        addQuestion_rating1.setText("");
        addQuestion_rating2.setText("");
        addQuestion_rating3.setText("");
        addQuestion_rating4.setText("");
    }

    private int getLevelIndex() {
        if(easyRadioButton.isSelected()) {
            return 0;
        }
        else if (mediumRadioButton.isSelected()){
            return 1;
        }
        else if(hardRadioButton.isSelected()){
            return 2;
        }
        else return -1;
    }
    private boolean isAnswerFieldOKEY() {
        if(addQuestion_answer1.getText().equals("")) {
            return false;
        }
        else if (addQuestion_answer2.getText().equals("")) {
            return false;
        }
        else if (addQuestion_answer3.getText().equals("")) {
            return false;
        }
        else if (addQuestion_answer4.getText().equals("")) {
            return false;
        }
        return true;
    }
    private Integer[] getRatings() {
        checkIfEmptyRatings();
        try {
            int rating1 = Integer.parseInt(addQuestion_rating1.getText());
            int rating2 = Integer.parseInt(addQuestion_rating2.getText());
            int rating3 = Integer.parseInt(addQuestion_rating3.getText());
            int rating4 = Integer.parseInt(addQuestion_rating4.getText());
            if(isRatingsSumHundred(rating1,rating2,rating3,rating4)) {
                return new Integer[]{rating1,rating2,rating3,rating4};
            }
        }
        catch (NumberFormatException e1) {
            showErrorDialog("Error", "Ratings input must be integers!");
        }
        return null;
    }
    private boolean isRatingsSumHundred(int rating1, int rating2, int rating3, int rating4) {
        if(rating1+rating2+rating3+rating4 == 100) {
            return true;
        }
        else {
            showErrorDialog("Error","Ratings sum MUST be 100");
        }
        return false;
    }
    private void checkIfEmptyRatings() {
        if(addQuestion_rating1.getText().equals("") ) {
            addQuestion_rating1.setText("0");
        }
        else if (addQuestion_rating2.getText().equals("")) {
            addQuestion_rating2.setText("0");
        }
        else if (addQuestion_rating3.getText().equals("")) {
            addQuestion_rating3.setText("0");
        }
        else if (addQuestion_rating4.getText().equals("")) {
            addQuestion_rating4.setText("0");
        }
    }
    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    /*
    * Checks if username and password inputs are equal the ones
    * in Config class, if it is , opens other screen.
    * */
    private void confirmLogin() {
        if(usernameField.getText().equals(Config.USERNAME) && passwordField.getText().equals(Config.PASSWORD)) {
            updateTF();
            cardLayout.show(contentPane,"developer");
            currentCard = "developer";
        }
        else {
            errorLabel.setText("Incorrect. Try again ");
            errorLabel.setVisible(true);
            errorLabel.setPreferredSize(new Dimension(-1,20));
        }
    }

    /*
    * Updates the title, timer and player amount.
    * Shows confirmation text..
    * */
    private void update() {
        if(updateTimer() && updatePlayerAmount()) {
            Config.TITLE = titleTF.getText();
            developerErrrorLabel.setText("Preferences saved... You can close window now");
        }
    }

    /*
    * Checking if users inputed values are between minimum and maximum
    * stated in Config class. If it is not, showing error message
    * */
    private boolean updateTimer() {
        try {
            long newChronometerTime = Integer.parseInt(chronometerTimeTF.getText());
            if(newChronometerTime > Config.DEVELOPER_MAX_CHRONOMETER_TIME) {
                Config.CHRONOMETER_TIME = Config.DEVELOPER_MAX_CHRONOMETER_TIME;
                showErrorDialog("Value to HIGH!","Maximum timer time is : "+ Config.DEVELOPER_MAX_CHRONOMETER_TIME);
                chronometerTimeTF.setText(""+Config.DEVELOPER_MAX_CHRONOMETER_TIME);
            }
            else if( newChronometerTime < Config.DEVELOPER_MIN_CHRONOMETER_TIME)  {
                Config.CHRONOMETER_TIME = Config.DEVELOPER_MIN_CHRONOMETER_TIME;
                showErrorDialog("Value to LOW!","Minimum timer time is : "+ Config.DEVELOPER_MIN_CHRONOMETER_TIME);
                chronometerTimeTF.setText(""+Config.DEVELOPER_MIN_CHRONOMETER_TIME);
            }
            else {
                Config.CHRONOMETER_TIME = newChronometerTime;
                return true;
            }
        }
        catch (NumberFormatException e1) {
            chronometerTimeTF.setText(""+Config.CHRONOMETER_TIME);
            showErrorDialog("ERROR","Timer input value can be integer only! ");
        }
        return false;
    }
    /*
   * Checking if users inputed values are between minimum and maximum
   * stated in Config class. If it is not, showing error message
   * */
    private boolean updatePlayerAmount() {
        try {
            long playerAmount = Integer.parseInt(maxPlayersTF.getText());
            if(playerAmount > Config.DEVELOPER_MAX_PLAYERS) {
                Config.MAX_PLAYERS_AMOUNT = Config.DEVELOPER_MAX_PLAYERS;
                showErrorDialog("Value to HIGH!","Maximum amount of players is : "+ Config.DEVELOPER_MAX_PLAYERS);
                maxPlayersTF.setText(""+Config.DEVELOPER_MAX_PLAYERS);
            }
            else if (playerAmount < Config.DEVELOPER_MIN_PLAYERS) {
                showErrorDialog("Value to low!","Minimum amount of players is : "+ Config.DEVELOPER_MIN_PLAYERS);
                maxPlayersTF.setText(""+Config.DEVELOPER_MIN_PLAYERS);
            }
            else  {
                Config.MAX_PLAYERS_AMOUNT = (int) playerAmount;
                return true;
            }
        }
        catch (NumberFormatException e2) {
            maxPlayersTF.setText(""+Config.MAX_PLAYERS_AMOUNT);
            showErrorDialog("ERROR","Players input value can be integer only! ");
        }
        return false;
    }
    private void showErrorDialog(String title, String message) {
        ErrorDialog errorDialog = new ErrorDialog();
        errorDialog.showErrorDialog(null,title,message);
    }
    private void updateTF() {
        this.maxPlayersTF.setText(""+Config.MAX_PLAYERS_AMOUNT);
        this.chronometerTimeTF.setText(""+Config.CHRONOMETER_TIME);
        this.titleTF.setText(Config.TITLE);
    }

    private void createUIComponents() {
        passwordField = new JPasswordField(32);
        // TODO: place custom component creation code here
        categoryBox = new JComboBox();
        for(int i = 0; i < Config.CATEGORIES.length; i++) {
            categoryBox.addItem(Config.CATEGORIES[i]);
        }
    }
}
