package backend;

import backend.model.DataHandler;
import backend.model.Player;
import backend.model.Question;

import java.util.ArrayList;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Domantas Giedraitis - Navus
 * @version 1.001
 **/

public class Gameplay  {
    private Player player;
    private DataHandler dataHandler;
    private Question currentQuestion;
    private Integer creditAmounts[];
    private String category = "General"; // By default
    private int currentLevel = 0; // 0-4 - Easy, 5 - 9 - Medium, 9 - 14 - Hard

    public Gameplay(String username) {
        this.creditAmounts = Config.AMOUNTS_TO_WIN;
        this.player = new Player(username);
    }

    public void nextLevel() {
        this.currentLevel++;
    }

    /*
    * Loads questions for choosen category
    * @param category - String meaning category
    * */
    public boolean setCategory(String category) {
        this.category = category;
        dataHandler = new DataHandler(category);
        return dataHandler.loadQuestions();
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void addCredit() {
        player.setCredit(creditAmounts[currentLevel]);
    }

    /*
    * Gets question depending on current level
    * 0-4 - Easy (5 questions)
    * 5-9 - Medium (5 questions)
    * 9-14 - Hard (5 questions)
    * @return String (question) || null (if error occurs)
    * */
    public String getQuestion() {
        ArrayList<Question> questions = null;
        if (currentLevel < 5) {
            questions = dataHandler.getEasyQuestions();
        } else if (currentLevel < 10) {
            questions = dataHandler.getMediumQuestions();
        } else if (currentLevel < 15) {
            questions = dataHandler.getHardQuestions();
        } else {
            System.out.println("Issue while trying to get question.. See Gameplay.java...");
        }
        if (getRandomQuestion(questions)) {
            return currentQuestion.getQuestion();
        }
        return null;
    }

    /*
    * If there is any more questions, which haven't been taken before
    * returns question object and makes it unreachable for next time,
    * so that you can't get the same question.
    * For all other cases - (Not enough questions, error ) - returns null.
    * @param questions
    * @return Question object || null;
    * */
    private boolean getRandomQuestion(ArrayList<Question> questions) {
        int index = ThreadLocalRandom.current().nextInt(0, questions.size());
        Question tempQuestion = questions.get(index);
        if (questions.size() <= currentLevel) {
            return false;
        } else {
            while (tempQuestion.isTaken()) {
                index = ThreadLocalRandom.current().nextInt(0, questions.size());
                tempQuestion = questions.get(index);
            }
            currentQuestion = tempQuestion;
            tempQuestion.setTaken(true);
        }
        return true;
    }

    public String getAnswer(int index) {
        return currentQuestion.getAnswerByIndex(index);
    }

    public int getCorrectAnswerIndex() {
        return this.currentQuestion.getCorrectAnswerIndex();
    }

    public Integer getRating(int index) {
        return currentQuestion.getRatingByIndex(index);
    }
    public Integer[] getAllRatings() {
        return this.currentQuestion.getRatings();
    }

    /*
    * Calculates and finds 2 lowest having ratings indexes from
    * current question object.
    * @return 2 lowest ratings having index
    * */
    public Integer[] getWorstTwoRatingIndexes() {
        int minA = Integer.MAX_VALUE, minB = Integer.MAX_VALUE;
        int aIndex = 0, bIndex = 0;
        int index = 0;
        for (int value : this.currentQuestion.getRatings()) {
            if (value < minA) {
                bIndex = aIndex;
                aIndex = index;
                minB = minA;
                minA = value;
            } else if (value < minB) {
                bIndex = index;
                minB = value;
            }
            index++;
        }
        return new Integer[]{aIndex, bIndex};
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getCategory() {
        return this.category;
    }

    public String resultsToString() {
        return "Player: " + this.player.getUsername() + " , Credit: " + this.player.getCredit();
    }
}
