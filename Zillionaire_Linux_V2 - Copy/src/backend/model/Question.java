package backend.model;

import java.util.Arrays;

/**
 * @author Domantas Giedraitis - Navus
 * @version 1.001
 **/

public class Question {
    private int id;
    private short level;
    private String question;
    private String[] answers;
    private Integer[] ratings;
    private boolean isTaken = false;
    private int correctAnswerIndex;

    public Question(int id, short level, String question, String[] answers, Integer[] ratings) {
        this.id = id;
        this.level = level;
        this.question = question;
        this.answers = answers;
        this.ratings = ratings;
        setCorrectAnswerIndex();
    }

    /*
    * By watching ratings of each answer, automatically finds
    * which answer is correct (has more ratings).
    * */
    private void setCorrectAnswerIndex() {
        int maxRating = Integer.MIN_VALUE;
        int oldIndex = 0;
        for(int i = 0; i < ratings.length; i++) {
            if(maxRating < ratings[i]) { maxRating = ratings[i];
            oldIndex = i;}
        }
        this.correctAnswerIndex = oldIndex;
    }
    public int getCorrectAnswerIndex() {
        return this.correctAnswerIndex;
    }
    public String getAnswerByIndex(int index) {
        return this.answers[index];
    }
    public Integer getRatingByIndex(int index) {
        return this.ratings[index];
    }
    public boolean isTaken() {
        return this.isTaken;
    }

    public void setTaken(boolean taken) {
        this.isTaken = taken;
    }
    public int getId() {
        return this.id;
    }

    public short getLevel() {
        return this.level;
    }

    public void setLevel(short level) {
        this.level = level;
    }

    public String getQuestion() {
        return this.question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String[] getAnswers() {
        return this.answers;
    }

    public void setAnswers(String[] answers) {
        this.answers = answers;
    }

    public Integer[] getRatings() {
        return this.ratings;
    }

    public void setRatings(Integer[] ratings) {
        this.ratings = ratings;
    }

    @Override
    public String toString() {
        return "id = " + id +
                ", level = " + level +
                ", question = '" + question + '\'' +
                ", answers = " + Arrays.toString(answers) +
                ", ratings = " + Arrays.toString(ratings) +
                '}' + "\n";
    }
}
