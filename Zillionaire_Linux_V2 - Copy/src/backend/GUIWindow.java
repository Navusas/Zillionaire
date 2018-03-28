package backend;

import javax.swing.*;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

/**
 * @author Domantas Giedraitis - Navus
 * @version 1.001
 **/


public class GUIWindow {

    private static JFrame frame;
    private JTable resultsTable;
    private JPanel cards;
    private JPanel gameplayScreen;
    private JPanel supportScreen;
    private JPanel gameoverScreen;
    private JTable highScoreTable;private JLabel creditLabel;
    private JLabel currentPlayerIndexLabel;
    private JLabel playerNameLabel;
    private JLabel questionLabel;
    private JButton answerButton_A;
    private JButton answerButton_B;
    private JButton answerButton_C;
    private JButton answerButton_D;
    private JPanel introScreen;
    private JSlider playerAmountSlider;
    private JButton startLoopBT;
    private JPanel userInputScreen;
    private JTextField usernameTF;
    private JList categoryList;
    private JButton startGameBT;
    private JLabel playerPreferencesLabel;
    private JButton backToIntroBT;
    private JLabel selectedPlayersLabel;
    private JLabel chronometreLabel;
    private JButton exitBT;
    private JButton help_5050Button;
    private JButton help_audienceButton;
    private JButton help_chanceButton;
    private JButton developerModeButton;
    private JButton introSupport_BT;
    private JButton userInput_SupportBT;
    private JButton supportScreen_BackBT;
    private JButton quitButton;
    private JButton gameover_SupportBT;
    private JButton gameover_AgainBT;
    private JPanel winnerScreen;
    private JButton winnerContinueButton;
    private JLabel gifLabel;
    private JButton muteSoundButton;
    private JProgressBar progressBar1;
    private JProgressBar progressBar2;
    private JProgressBar progressBar3;
    private JProgressBar progressBar4;
    private JPanel audienceHelpPanel;
    private JPanel audienceHelpPanel_Opposite;
    private JButton exitGameplaysButton;
    private JButton update;
    private CardLayout cardLayout = (CardLayout) cards.getLayout();
    private Client client = new Client(cardLayout, cards, questionLabel,
            answerButton_A, answerButton_B, answerButton_C, answerButton_D,
            chronometreLabel, playerPreferencesLabel, help_5050Button, help_audienceButton, help_chanceButton, resultsTable);
    private boolean isSoundRunning = true;

    private GUIWindow() {
        // Update elements using Config class static properties
        updateElements();

        // Action Listeners Follows...
        startLoopBT.addActionListener(e -> {
            // show current player index in human related manner
            int currentPlayer = client.getCurrentPlayerIndex()+2;
            playerPreferencesLabel.setText("Player " + currentPlayer);

            cardLayout.show(cards, "userInputs");

            /*Update property in client class*/
            client.setAmountOfPlayers(playerAmountSlider.getValue());
        });
        backToIntroBT.addActionListener(e -> cardLayout.show(cards, "intro"));
        startGameBT.addActionListener(e -> {
            /*If username haven't been used before - let user to start the game*/
            /*Otherwise, show error dialog. */
            exitGameplaysButton.setVisible(true);
            if (!usernameTF.getText().equals("")) {
                start();
                usernameTF.setText("");
            } else {
                JOptionPane.showMessageDialog(cards, "Name field can not be empty!", "Wrong input", JOptionPane.ERROR_MESSAGE);
            }
        });

        /*Make slider work with both - click and drag*/
        playerAmountSlider.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                updateSliderLabel();
            }
        });
        playerAmountSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                updateSliderLabel();
            }
        });
        ActionListener choosingAnswer = e -> {
            client.confirmFinalAnswer((JButton) e.getSource(), creditLabel);
        };
        answerButton_A.addActionListener(choosingAnswer);
        answerButton_B.addActionListener(choosingAnswer);
        answerButton_C.addActionListener(choosingAnswer);
        answerButton_D.addActionListener(choosingAnswer);

        exitBT.addActionListener(e -> client.playerExit());
        help_5050Button.addActionListener(e -> client.useFiftyFiftyHelp());
        developerModeButton.addActionListener(e -> openDeveloperMode());
        introSupport_BT.addActionListener(e -> cardLayout.show(cards,"support" ));
        userInput_SupportBT.addActionListener(e -> cardLayout.show(cards,"support" ));
        supportScreen_BackBT.addActionListener(e -> cardLayout.show(cards,"intro" ));
        quitButton.addActionListener(e -> System.exit(0));
        winnerContinueButton.addActionListener(e -> client.playerExit());

        gameover_SupportBT.addActionListener(e ->  {
            cardLayout.show(cards,"support" );
            backToIntroBT.setEnabled(true);
            userInput_SupportBT.setEnabled(true);
        });
        gameover_AgainBT.addActionListener(e -> {
            backToIntroBT.setEnabled(true);
            userInput_SupportBT.setEnabled(true);
            cardLayout.show(cards,"intro");
        });
        muteSoundButton.addActionListener(e -> {
            isSoundRunning = !isSoundRunning;
            if(isSoundRunning) {
                muteSoundButton.setIcon(new ImageIcon(getClass().getResource("../Sprites/soundIcon_Default.png")));
                client.unmutePlayer();
            }
            else {
                muteSoundButton.setIcon(new ImageIcon(getClass().getResource("../Sprites/soundIcon_Crossed.png")));
                client.mutePlayer();
            }
        });
        help_audienceButton.addActionListener(e -> {
            JProgressBar[] allBars = new JProgressBar[] {this.progressBar1,this.progressBar2,
                                                                 this.progressBar3,this.progressBar4};
            client.useAudienceHelp(allBars,this.audienceHelpPanel_Opposite,this.audienceHelpPanel);
        });
        exitGameplaysButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.exitWholeGameplays();
                exitGameplaysButton.setVisible(false);
            }
        });
    }
    /*
    * Creates and shows 'Developer Mode' dialog
    * Updates Config and GUI elements after dialog closed
    * */
    private void openDeveloperMode() {
        Developer developerDialog = new Developer();
        developerDialog.pack();
        developerDialog.setLocationRelativeTo(frame);
        developerDialog.setVisible(true);

        /*Update GUI elements (slider, title, chronometer time) after finishing*/
        updateElements();
    }

    /*
    * Updates Slider, label and title
    */
    private void updateElements(){
        playerAmountSlider.setMaximum(Config.MAX_PLAYERS_AMOUNT);
        playerAmountSlider.setValue(Config.DEFAULT_PLAYERS_AMOUNT);
        updateSliderLabel();
        frame.setTitle(Config.TITLE);
    }

    private void updateSliderLabel() {
        selectedPlayersLabel.setText("Players Amount : " + String.valueOf(playerAmountSlider.getValue()));
    }

    /*
    * If username is not taken before, starts main gameplay loop and set text
    * to human related manner.
    * */
    private void start() {
        if (!client.isUsernameTaken(usernameTF.getText())) {
            backToIntroBT.setEnabled(false);
            userInput_SupportBT.setEnabled(false);
            creditLabel.setText("0");
            playerNameLabel.setText(usernameTF.getText());
            int playerIndex = client.getCurrentPlayerIndex()+2;
            currentPlayerIndexLabel.setText("Currently playing: "+ playerIndex +"/"+playerAmountSlider.getValue());
            client.startGameForPlayer(usernameTF.getText(), (String) categoryList.getSelectedValue());
        }
    }

    /*
    * Creates main frame , sets its contentPane from designed Form.
    * */
    static void createAndShowGui() {
        frame = new JFrame(Config.TITLE);
        frame.setContentPane(new GUIWindow().cards);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    private void createUIComponents() {

        // TO - DO MANUALLY ADD COMPONENTS TO FRAME
        updateCategoryList();
        createResultsTable();
    }

    /*
    * Creates result table headers, sets model and align.
    * It is needed for gameover screen to show results.
    * */
    private void createResultsTable() {
        DefaultTableModel tableModel = new DefaultTableModel(0,0) {
          @Override
          public boolean isCellEditable(int row, int cell) {
              return false;
          }
        };
        String[] columnNames = {"#", "Username", "Winnings","Category Played"};

        tableModel.setColumnIdentifiers(columnNames);
        resultsTable = new JTable();
        resultsTable.setModel(tableModel);

        DefaultTableCellRenderer centerAlign = new DefaultTableCellRenderer();

        centerAlign.setHorizontalAlignment(JLabel.CENTER);
        for(int i = 0; i < resultsTable.getColumnCount(); i++) {
            resultsTable.getColumnModel().getColumn(i).setCellRenderer(centerAlign);
        }

        resultsTable.getColumnModel().getColumn(0).setPreferredWidth(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }

    /*
    * Reads category lists from Config class, and updates the categoryList
    * for 'userInput' screen
    * */
    private void updateCategoryList() {
        DefaultListModel<String> categoriesModel = new DefaultListModel<>();
        this.categoryList = new JList<>(categoriesModel);
        for (int i = 0; i < Config.CATEGORIES.length; i++) {
            categoriesModel.addElement(Config.CATEGORIES[i]);
        }
        categoryList.setSelectedIndex(0);
    }
}