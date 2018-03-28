package backend;

import backend.events.Clock;
import backend.events.SoundPlayer;
import backend.model.Player;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import static java.util.concurrent.TimeUnit.MILLISECONDS;


/**
 * @author Domantas Giedraitis - Navus
 * @version 1.001
 **/


public class Client extends SoundPlayer {
    private Clock clock;
    private CardLayout cardLayout;
    private JPanel cards;
    private JLabel questionLabel,chronometerLabel,inputPlayerLabel;
    private JButton answerA_BT, answerB_BT, answerC_BT, answerD_BT;
    private JButton help_fiftyFifty_BT, help_audience_BT, help_chance_BT;
    private JPanel audiencePanel, audiencePanel_opposite;

    private ArrayList<Gameplay> allGameplays = new ArrayList<>();
    private ErrorDialog errorDialog = new ErrorDialog();
    private int amountOfPlayers = backend.Config.DEFAULT_PLAYERS_AMOUNT; // default
    private int currentPlayer = -1;
    private boolean waitingForAnswer = false;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private JTable resultsTable;
    private boolean isPlayerPlaying = false;


    public Client (){};
    public Client(CardLayout cardLayout, JPanel cards, JLabel questionLabel,
                  JButton answerA_BT, JButton answerB_BT, JButton answerC_BT, JButton answerD_BT,
                  JLabel chronometerLabel, JLabel inputPlayerLabel, JButton help_fiftyFifty, JButton help_audience_BT,
                  JButton help_chance_BT, JTable resultsTable) {
        clock = new Clock(chronometerLabel);
        this.cardLayout = cardLayout;
        this.cards = cards;
        this.questionLabel = questionLabel;
        this.answerA_BT = answerA_BT;
        this.answerB_BT = answerB_BT;
        this.answerC_BT = answerC_BT;
        this.answerD_BT = answerD_BT;
        this.chronometerLabel = chronometerLabel;
        this.inputPlayerLabel = inputPlayerLabel;
        this.help_audience_BT = help_audience_BT;
        this.help_fiftyFifty_BT = help_fiftyFifty;
        this.help_chance_BT = help_chance_BT;
        this.resultsTable = resultsTable;

        // Start theme song...
        startPlayer("theme");
    }

    /* Prepares screen for new user (sets view to default),
       Creates gameplay & player instance.
       Prepares question for player
    * @param : username , category  */
    public void startGameForPlayer(String username, String categoryName) {
        cardLayout.show(this.cards,"gameplay");
        addPlayer(username);
        if(setCategory(categoryName)) {
            updateScreen();
        }
        setHelpDefault();
        isPlayerPlaying = true;

    }
    /* Creates new gameplay, which then creates the player instance.
    * @param : username */
    public void addPlayer(String username) {
        allGameplays.add(new Gameplay(username));
        currentPlayer++;
    }
    /* Starts Sound Player
       Sets all icons to default (cleans screen for new use)
       Shows new Question
       Starts Chronometer
     *  */
    private void updateScreen() {
        startPlayerDependingOnQuestion();
        setAllButtonIconDefaults();
        showNewQuestion();
        startChronometer();
        isChronometerFinished();
        if(getCurrentPlayer().hasUsedHelp_Audience()) {
            audiencePanel.setVisible(false);
            audiencePanel_opposite.setVisible(false);
        }
    }
    /*
    * Starts music player depending on what level player is at the moment.
    * */
    private void startPlayerDependingOnQuestion() {
        if(getCurrentGameplay().getCurrentLevel() < 5){
            startPlayer("easyQuestions");
        }
        else if (getCurrentGameplay().getCurrentLevel() < 10) {
            startPlayer("mediumQuestions");
        }
        else if(getCurrentGameplay().getCurrentLevel() < 14) {
            startPlayer("hardQuestions");
        }
        else if(getCurrentGameplay().getCurrentLevel() == 15) {
            startPlayer("finalQuestion");
        }
    }

    /*
    * If this is the last player playing, creates result table,
    * Clears every variable, data for new users (if user would decide to play again)
    * Shows gameover screen
    *
    * Otherwise - shows next user preferences screen
    * */
    private void playNextPlayer() {
        waitingForAnswer = false;
        clock.reset();
        if (currentPlayer == (amountOfPlayers - 1)) {
            exitWholeGameplays();
        } else {
            // Start theme song...
            startPlayer("theme");
            cardLayout.show(cards, "userInputs");
            int playerIndex = currentPlayer + 2;
            inputPlayerLabel.setText("Player " + playerIndex);
        }

    }
    public void playerExit() {
        isPlayerPlaying = false;
        disposePlayer();
        clock.reset();
        playNextPlayer();
    }

    public void exitWholeGameplays() {
        createTable();
        setToDefaultForNewGame();
        // Start theme song...
        startPlayer("gameoverTheme");
        cardLayout.show(cards, "gameover");
    }

    /*
    Method don't let user to select multiple answers as his final answers
    @see checkAnswer
    * */
    public void confirmFinalAnswer(JButton pressedButton, JLabel creditLabel) {
        if (waitingForAnswer) {
            showErrorMessage("Slow down!","You are already waiting for decision!");
        } else {
            pressedButton.setIcon(new ImageIcon(getClass().getResource("../Sprites/answerBar_clicked.png")));
            // finalAnswersAndLifelines
            startPlayer("finalAnswersAndLifelines");
            checkAnswer(pressedButton, creditLabel);
        }
    }
    /*
    * Creates and shows FinalAnswerDialog instance. Checks if user press YES/NO.
    * @return true if user press YES || false if user press NO
    * */
    private boolean isUsersFinalAnswer(JButton pressedButton) {

        int delay = (int) Double.parseDouble(chronometerLabel.getText());

        final FinalAnswerDialog finalAnswerDialog = new FinalAnswerDialog(cards,delay,pressedButton.getText());

        if(finalAnswerDialog.isOkeyPressed()) {
            setButtonIconPending(pressedButton);
            startPlayer("clockSound");
            clock.reset();
            return true;
        }
        else {
            setButtonIconDefault(pressedButton);
            return false;
        }

    }
    /*
    * Compares the answers
    * @return true/false depending if choosen answer is correct or not.
    * */
    private boolean isAnswerCorrect(String text) {
        text = text.substring(3); // deletes 'A: ' , 'B: ' etc..
        int correctIndex = getCurrentGameplay().getCorrectAnswerIndex();
        return getCurrentGameplay().getAnswer(correctIndex).equals(text);
    }

    /*
    * Shows dialog and asks user to confirm his choice.
    * Checks if answer is correct
    * @see isUsersFinalAnswer
    * @see ifAnswerCorrect
    * @see ifAnswerIncorrect
    * @see startPlayerDependingOnQuestion
    * */
    private void checkAnswer(JButton pressedButton, JLabel creditLabel) {
        String text = pressedButton.getText();
        if (isUsersFinalAnswer(pressedButton)) {
            waitingForAnswer = true;
            int milliSeconds = randomMilliseconds();
            // this creates new timer , and countdowns from random value in the middle of screen
            clock.startDecisionTimer(chronometerLabel, milliSeconds);

            // do the following tasks after dedicated time, (to keep game intensive)
            scheduler.schedule(() -> {
                clock.reset();
                if (isAnswerCorrect(text) && waitingForAnswer) {
                    ifAnswerCorrect(creditLabel,pressedButton);

                } else if(waitingForAnswer){
                    ifAnswerIncorrect(pressedButton);
                }
            }, milliSeconds - 1000, MILLISECONDS);
        }
        else {
            // if user presses 'No' , play normal music.
            startPlayerDependingOnQuestion();
        }
    }

    /*
    * Adds credit to player , shows that answer is correct.
    * */
    private void ifAnswerCorrect(JLabel creditLabel, JButton pressedButton) {
        getCurrentGameplay().addCredit();
        creditLabel.setText("" + getCurrentPlayer().getCredit());
        chronometerLabel.setText("Correct");
        startPlayer("correctAnswer");
        setButtonIconCorrect(pressedButton);

        // schedule following tasks to keep game intensive
        scheduler.schedule(() -> {
            if(isPlayerPlaying) {
                if (getCurrentGameplay().getCurrentLevel() == 15) {
                    cardLayout.show(cards, "win");
                    startPlayer("gameoverTheme");
                }
                //  level ups player and updates screen with new questions and default icons.
                this.allGameplays.get(currentPlayer).nextLevel();
                updateScreen();
                waitingForAnswer = false;
            }
        }, 4000, MILLISECONDS);
    }

    /*
    * Shows user that answer is incorrect &
    * If user did not used his chance - let's him play again, otherwise he quits game.
    * */
    private void ifAnswerIncorrect(JButton pressedButton) {
        startPlayer("wrongAnswer");
        chronometerLabel.setText("Wrong");
        setButtonIconIncorrect(pressedButton);
        scheduler.schedule(() -> {
                    if(isPlayerPlaying) {
                        checkIfUsedSecondChance("Answer is incorrect and you already used your second chance" +
                                "<br/> It is GAME OVER");
                        waitingForAnswer = false;
                    }
        }, 3000, MILLISECONDS);
    }

    /*
    * Thread is started everytime when main timer starts.
    * This method ensures, that user would fail, if he did not managed to choose answer
    * and the time finishes.
    * */
    private void isChronometerFinished()  {
        new Thread(() -> {
            try {
                while(!clock.circleLabel()) {
                    // sleep for 1 seconds until clock is '00.0'
                    Thread.sleep(1000);
                }
                checkIfUsedSecondChance(getCurrentPlayer().getUsername()
                        + " your time has finished... ");
            } catch(InterruptedException v) {
                v.printStackTrace();
            }
        }).start();
    }


    /*
    * Sets all buton to Defaults, makes them enabled for cases, when 50:50 help has been used.
    * */
    private void setAllButtonIconDefaults() {
        answerA_BT.setIcon(new javax.swing.ImageIcon(getClass().getResource("../Sprites/answerBar.png")));
        answerB_BT.setIcon(new javax.swing.ImageIcon(getClass().getResource("../Sprites/answerBar.png")));
        answerC_BT.setIcon(new javax.swing.ImageIcon(getClass().getResource("../Sprites/answerBar.png")));
        answerD_BT.setIcon(new javax.swing.ImageIcon(getClass().getResource("../Sprites/answerBar.png")));
        answerB_BT.setEnabled(true);
        answerA_BT.setEnabled(true);
        answerC_BT.setEnabled(true);
        answerD_BT.setEnabled(true);
    }
    /* Starts timer */
    private void startChronometer() {
        clock.startBackTimer(chronometerLabel);
    }

    /* Gets new answers and questions and sets values to GUI labels and buttons */
    private void showNewQuestion() {
        questionLabel.setText(getQuestion());
        answerA_BT.setText("A: " + getAnswer(0));
        answerB_BT.setText("B: " + getAnswer(1));
        answerC_BT.setText("C: " + getAnswer(2));
        answerD_BT.setText("D: " +  getAnswer(3));
    }
    /*
    * simplifies showing error dialog
    * @param title, message
    * */
    private void showErrorMessage(String title, String message) {
        errorDialog.showErrorDialog(cards,title,message);
    }

    /*
    * Case sensitively checks if username already has been taken by previous players.
    * @param name - name, which new player wants to take
    * @return true if the name already exists
    * @return false if name does not exists
    * */
    public boolean isUsernameTaken(String name) {
        for (Gameplay p1 : allGameplays) {
            if (p1.getPlayer().getLowerCaseUsername().equals(name.toLowerCase())) {
                showErrorMessage("Username Taken!", "Username already taken.</br> Please choose other username!");
                return true;
            }
        }
        return false;
    }

    /*
    * after user choose wrong answer, checking if it already used his second chance facility
    * If it is not available anymore - play next player or exit gameplay if no more players exists.
    * @param message - desired message when player finishes the game
    * @see playNextPlayer();
    * */
    private void checkIfUsedSecondChance(String message) {
        if(getCurrentPlayer().hasUsedHelp_SecondChance()) {
            // if had used the +1 help facility already...
            showErrorMessage("THE END", message);
            clock.reset();
            playNextPlayer();
        }
        else {
            // if haven't used second chance help facility
            startPlayer("finalQuestion");
            showErrorMessage("Second chance", "You have 1 more chance to prove your knowledge!");
            getCurrentPlayer().setHelp_SecondChance(true);
            setHelp_chanceUsed();
            scheduler.schedule(() -> {
                errorDialog.closeWindow();
                clock.reset();
                updateScreen();
            },3000,MILLISECONDS);
        }
    }

    /*
    * if haven't used this facility before - uses it.
    * @see showFiftyFifty();
    * */
    public void useFiftyFiftyHelp() {
        if(waitingForAnswer) {
            showErrorMessage("STOP!","You can't use help facility right now!!");
        }
        else if(!getCurrentPlayer().hasUsedHelp_FiftyFifty()) {
            setHelp_fiftyFiftyUsed();
            showFiftyFifty();
        }
        else {
            showErrorMessage("Help used !","You have used this help facility before!");
        }
    }
    /*
    * Thread coded to create nice and smooth incorrect answers disappearing
    * Method gets wrongAnswerIndexes, and disables same indexes buttons in the screen
    * with 0.5seconds difference each.
    * */
    private void showFiftyFifty() {
        Thread timing = new Thread(() -> {
            Integer[] wrongAnswersIndexes = getCurrentGameplay().getWorstTwoRatingIndexes();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for(int integer : wrongAnswersIndexes) {
                if(integer == 0) {
                    answerA_BT.setEnabled(false);
                }
                else if(integer== 1) {
                    answerB_BT.setEnabled(false);
                }
                else if(integer == 2) {
                    answerC_BT.setEnabled(false);
                }
                else if(integer == 3){
                    answerD_BT.setEnabled(false);
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        timing.start();
    }
    /*
    * Updates and shows audience panels in the GUI.
    * */
    public void useAudienceHelp(JProgressBar[] progressBars,JPanel audiencePanel, JPanel audiencePanel_Opposite) {
        if(waitingForAnswer) {
            showErrorMessage("STOP!","You can't use help facility right now!!");
        }
        else if(!getCurrentPlayer().hasUsedHelp_Audience()) {
            this.audiencePanel_opposite = audiencePanel_Opposite;
            this.audiencePanel = audiencePanel;
            setHelp_audienceUsed();
            showProgressBars(progressBars);
            this.audiencePanel.setVisible(true);
            this.audiencePanel_opposite.setVisible(true);
        }
        else {
            showErrorMessage("Help used !","You have used this help facility before!");
        }

    }
    /*
    * Sets value to each progress bar
    * @see useAudienceHelp
    * */
    private void showProgressBars(JProgressBar[] progressBars) {
        Integer[] ratings = allGameplays.get(currentPlayer).getAllRatings();
        for(int i = 0; i < 4; i++) {
            progressBars[i].setValue(ratings[i]);
        }
    }
    /*
    * Stores all current data to nice table and sorts it , so that person having most winnings
    * is at the top of table.
    * */
    private void createTable() {
        DefaultTableModel model = (DefaultTableModel) resultsTable.getModel();
        emptyTableData(model);
        int index = 1;
        for(Gameplay gameplays: allGameplays) {
            Object[] temp= new Object[] {index, gameplays.getPlayer().getUsername(), gameplays.getPlayer().getCredit(),
                    gameplays.getCategory()};
            model.addRow(temp);
            index++;
        }
        resultsTable.setAutoCreateRowSorter(true);
        sortTable();
        updateTable();
        resultsTable.setModel(model);
        resultsTable.removeEditor();
    }

    /*
    * Clears table content (to prepare table for next gameplay)
    * */
    private void emptyTableData(DefaultTableModel tableModel) {
        if (tableModel.getRowCount() > 0) {
            for (int i = tableModel.getRowCount() - 1; i > -1; i--) {
                tableModel.removeRow(i);
            }
        }
    }
    /*
    * Sorts so that most winnings having player is at the top of table.
    * */
    private void sortTable() {
        DefaultRowSorter sorter = ((DefaultRowSorter)resultsTable.getRowSorter());
        ArrayList list = new ArrayList();
        list.add( new RowSorter.SortKey(2, SortOrder.DESCENDING) );
        sorter.setSortKeys(list);
        sorter.sort();
    }
    private void updateTable() {
        for(int i = 0; i < allGameplays.size(); i++) {
            resultsTable.setValueAt(i+1,i,0);
        }
    }

    /*Generate random number between numbers. */
    private int randomMilliseconds() {
        return (ThreadLocalRandom.current().nextInt(3, 7)) * 1000;
    }

    /*Checks if setCategory fails. If it does, removes the player, and let him try again
    * It is for those moments, when player chooses to play category, which does not have
    * enough questions. He get unlimited chances with this method!
    * @return true if everything is okey
    * @return false if dataLoading failed
    * */
    private boolean setCategory(String category) {
        if(!getCurrentGameplay().setCategory(category)) {
            allGameplays.remove(currentPlayer);
            currentPlayer--;
            playNextPlayer();
            return false;
        }
        return true;
    }

    private void setHelpDefault() {
        this.help_chance_BT.setIcon(new javax.swing.ImageIcon(getClass().getResource("../Sprites/help_chance_default.png")));
        this.help_fiftyFifty_BT.setIcon(new javax.swing.ImageIcon(getClass().getResource("../Sprites/help_5050_default.png")));
        this.help_audience_BT.setIcon(new javax.swing.ImageIcon(getClass().getResource("../Sprites/help_audience_default.png")));
    }
    private void setHelp_fiftyFiftyUsed() {
        getCurrentPlayer().setHelp_FiftyFifty(true);
        this.help_fiftyFifty_BT.setIcon(new javax.swing.ImageIcon(getClass().getResource("../Sprites/help_5050_crossed.png")));
    }
    private void setHelp_audienceUsed() {
        getCurrentPlayer().setHelp_Audience(true);
        this.help_audience_BT.setIcon(new javax.swing.ImageIcon(getClass().getResource("../Sprites/help_audience_crossed.png")));
    }
    private void setHelp_chanceUsed() {
        getCurrentPlayer().setHelp_SecondChance(true);
        this.help_chance_BT.setIcon(new javax.swing.ImageIcon(getClass().getResource("../Sprites/help_chance_crossed.png")));
    }

    private void setToDefaultForNewGame() {
        allGameplays.removeAll(allGameplays);
        currentPlayer = -1;
    }
    private Gameplay getCurrentGameplay() {
        return allGameplays.get(currentPlayer);
    }
    public int getCurrentPlayerIndex() {
        return currentPlayer;
    }

    private void setButtonIconDefault(JButton buttonToSet) {
        buttonToSet.setIcon(new javax.swing.ImageIcon(getClass().getResource("../Sprites/answerBar.png")));
    }
    private void setButtonIconCorrect(JButton buttonToSet) {
        buttonToSet.setIcon(new javax.swing.ImageIcon(getClass().getResource("../Sprites/answerBar_correct.png")));
    }
    private void setButtonIconIncorrect(JButton buttonToSet) {
        buttonToSet.setIcon(new javax.swing.ImageIcon(getClass().getResource("../Sprites/answerBar_incorrect.png")));
    }
    private void setButtonIconPending(JButton buttonToSet) {
        buttonToSet.setIcon(new javax.swing.ImageIcon(getClass().getResource("../Sprites/answerBar_pending.png")));
    }
    private Player getCurrentPlayer() {
        return this.allGameplays.get(currentPlayer).getPlayer();
    }

    /*
    * Gets question and checks if it is not null (No more questions to show)
    * @return Question (String)
    * */
    public String getQuestion() {
        String question = getCurrentGameplay().getQuestion();
        if(question == null) {
            showErrorMessage("Fatal error...","So sorry... There are no more questions to show. </br>" +
                    "Please contact Support team." + " </br>"
                    +"We do apologies for any inconvenience caused");
            playNextPlayer();
        }
        return getCurrentGameplay().getQuestion();
    }

    private String getAnswer(int index) {
        return this.allGameplays.get(currentPlayer).getAnswer(index);
    }

    public void setAmountOfPlayers(int amountOfPlayers) {
        this.amountOfPlayers = amountOfPlayers;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch(Exception ignored){}

            GUIWindow.createAndShowGui();
        });
    }

}