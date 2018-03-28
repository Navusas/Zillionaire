package scripts;

import backend.Config;
import backend.ErrorDialog;
import backend.model.DataHandler;

import javax.swing.*;
import java.io.*;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.ThreadLocalRandom;

public class txtToJSON {
    private JPanel addQuestion;
    private JPanel questionPanel;
    private JTextField questionTF;
    private JTextField answer3;
    private JTextField answer4;
    private JTextField answer2;
    private JTextField answer1;
    private JTextField rating3;
    private JTextField rating4;
    private JTextField rating1;
    private JTextField rating2;
    private JRadioButton hardRadioButton;
    private JRadioButton easyRadioButton;
    private JRadioButton mediumRadioButton;
    private JButton saveAndNextBT;
    private JPanel mainPanel;
    private JComboBox categoryBox;
    private JButton passNextQuestionButton;
    private BufferedWriter writer = null;
    private FileInputStream inputStream = null;
    private Scanner sc = null;
    private ErrorDialog errorDialog = new ErrorDialog();

    /*
    * Read file
    * Create temp file
    * Load and generate questions and answers, as well as random integers
    * Put question in to temp file.
    * */

    public txtToJSON() {
        makeTextfieldsEmpty();
        int randomIndex = ThreadLocalRandom.current().nextInt(0,4);
        readFile();
        showRandomRatings(randomIndex);
        showNew(randomIndex);
        saveAndNextBT.addActionListener(e -> {

            DataHandler dataHandler = new DataHandler((String) categoryBox.getSelectedItem());
            String question = questionTF.getText();
            if (question.length() < 10) {
                errorDialog.showErrorDialog(null, "Error", "Question to shoort");
            } else if (!isAnswerFieldOKEY()) {
                errorDialog.showErrorDialog(null,"Error","Answer fields are empty");
            } else {
                String[] answers = {answer1.getText(), answer2.getText(),
                        answer3.getText(), answer4.getText()};
                Integer[] ratings = getRatings();
                if (!dataHandler.storeNewQuestion((short) getLevelIndex(), question, answers, ratings)) {
                    errorDialog.showErrorDialog(null,"Error",
                            "Something went wrong while trying to store question");
                } else {
                    System.out.println("All okey! ! ! ");
                }
            }
            sc.nextLine();
            makeTextfieldsEmpty();
            int index = ThreadLocalRandom.current().nextInt(0,4);
            showNew(index);
            showRandomRatings(index);
        });
        passNextQuestionButton.addActionListener(e -> {
            sc.nextLine();
            makeTextfieldsEmpty();
            int index = ThreadLocalRandom.current().nextInt(0,4);
            showNew(index);
            showRandomRatings(index);
        });
}

    private void showNew(int index) {
        JTextField allAnswers[] = {answer1,answer2,answer3,answer4};
        if (sc.hasNextLine()) {
            String nextLine = sc.nextLine();
            StringTokenizer tokenizer = new StringTokenizer(nextLine,"\t");
            while(tokenizer.hasMoreTokens()) {
                questionTF.setText(tokenizer.nextToken()); // sets question
                allAnswers[index].setText(tokenizer.nextToken());
            }

        }
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
        if(answer1.getText().equals("")) {
            return false;
        }
        else if (answer2.getText().equals("")) {
            return false;
        }
        else if (answer3.getText().equals("")) {
            return false;
        }
        else if (answer4.getText().equals("")) {
            return false;
        }
        return true;
    }
    private Integer[] getRatings() {
        int rating_1 = Integer.parseInt(rating1.getText());
        int rating_2 = Integer.parseInt(rating2.getText());
        int rating_3 = Integer.parseInt(rating3.getText());
        int rating_4 = Integer.parseInt(rating4.getText());
        return new Integer[] {rating_1,rating_2,rating_3,rating_4};
    }

    public void readFile() {
        try {
            File oldFile = new File("data/questions.txt").getAbsoluteFile();
            inputStream = new FileInputStream(oldFile.toString());
            sc = new Scanner(inputStream, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void showRandomRatings(int index) {
        boolean[] isRatingSet = new boolean[4];
        JTextField[] allRatingsTF = {rating1,rating2,rating3,rating4};
        Integer[] allNewRatings = new Integer[4];
        for(int i = 0; i < allNewRatings.length; i++) {
            allNewRatings[i] = 0;
        }

        allNewRatings[index] = ThreadLocalRandom.current().nextInt(50,76);
        allRatingsTF[index].setText(String.valueOf(allNewRatings[index]));
        isRatingSet[index] = true;

        int variablesSet = 1;
        while(variablesSet < 4) {
            int newIndex = ThreadLocalRandom.current().nextInt(0,4);
            if(!isRatingSet[newIndex]) {
                if(variablesSet == 3) {
                    allNewRatings[newIndex] = 100 - arraySum(allNewRatings);
                    allRatingsTF[newIndex].setText(Integer.toString(allNewRatings[newIndex]));
                    break;
                }
                int newInt = ThreadLocalRandom.current().nextInt(0, 101-arraySum(allNewRatings));
                allRatingsTF[newIndex].setText(Integer.toString(newInt));
                allNewRatings[newIndex] = newInt;
                isRatingSet[newIndex] = true;
                variablesSet++;
            }
        }
        for(int i : allNewRatings) {
            System.out.println(i);
        }
        System.out.println("Sum " + arraySum(allNewRatings));
    }
    private int arraySum(Integer[] toCount) {
        int sum = 0;
        for(int i : toCount) {
            sum+=i;
        }
        return sum;
    }
    private void makeTextfieldsEmpty() {
        answer1.setText("");
        answer2.setText("");
        answer3.setText("");
        answer4.setText("");
        rating1.setText("0");
        rating2.setText("0");
        rating3.setText("0");
        rating4.setText("0");
    }
    public static void main(String[] args) {
        JFrame frame = new JFrame("Json Script - by Navus");
        frame.setContentPane(new txtToJSON().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {
        categoryBox = new JComboBox();
        for(int i = 0; i < Config.CATEGORIES.length; i++) {
            categoryBox.addItem(Config.CATEGORIES[i]);
        }
        // TODO: place custom component creation code here
    }
}