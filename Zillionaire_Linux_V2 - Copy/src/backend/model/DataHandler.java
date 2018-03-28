package backend.model;

import backend.ErrorDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.*;
import org.json.simple.parser.*;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Domantas Giedraitis - Navus
 * @version 1.001
 **/

public class DataHandler {
    private Question[] allQuestions;
    private ArrayList<Question> easyQuestions;
    private ArrayList<Question> mediumQuestions;
    private ArrayList<Question> hardQuestions;
    private int lastID = -1;
    private String category = "General"; // by default
    private JSONArray jsonDataArray;

    public DataHandler(String category) {
        this.category = category;
    }

    /*
    * Reads the file and stores all his JSON objects into one array,
    * then by using @param , creates new JSON object, and stores it into array.
    * Eventually, saves updated array to file.
    * @param level , question, answers array, ratings array
    * */
    public boolean storeNewQuestion(Short level, String question, String[] answers, Integer[] ratings) {
        loadDataArray();
        JSONArray answersArray = new JSONArray();
        for(int i = 0 ; i < 4 ;i++) {
            JSONObject newAnswer = new JSONObject();
            newAnswer.put("Answer",answers[i]);
            newAnswer.put("Rating",ratings[i]);
            answersArray.add(newAnswer);
        }
        Map obj = new LinkedHashMap<>();
        obj.put("id",this.lastID);
        obj.put("Question",question);
        obj.put("Level",level);
        obj.put("Answers",answersArray);
        jsonDataArray.add(obj);
        return storeToFile();

    }
    /*
    * Opens file, converts JSON object to GSON (for pretty printing)
    * Writes new gson array into file and closes file.
    * */
    public boolean storeToFile() {
        try (FileWriter file = new FileWriter("data/questions/" + category)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(jsonDataArray);
            file.write(json);
            file.flush();
            //  System.out.println("Succesfully stored new question to : " + category);
            return true;
        } catch (IOException e2) {
            return false;
        }
    }

    private void setArraySize(int size) {
        allQuestions = new Question[size];
        easyQuestions = new ArrayList<>();
        mediumQuestions = new ArrayList<>();
        hardQuestions = new ArrayList<>();
    }
    /*
    * Opens file - reads data and converts it to JSON array object
    * */
    private void loadDataArray() {
        String filePath = "data/questions/" + category;
        System.out.println("Reading data...");
        JSONParser parser = new JSONParser();
        try (FileReader fileReader = new FileReader(filePath)) {
            // store whole JSON data into array
            this.jsonDataArray = (JSONArray) parser.parse(fileReader);
        }
        catch (IOException | ParseException e1) {
            System.out.println("Error while reading data... File is empty or file does not exists" );
            System.out.println("Creating new file...");
            createFile(category);
            this.jsonDataArray = new JSONArray();
        }

        //set last ID;
        this.lastID = this.jsonDataArray.size();
    }
    private void createFile(String filename) {
        Writer writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("filename.txt"), "utf-8"));
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                assert writer != null;
                writer.close();
            }
            catch (Exception ex) {/*ignore*/}
        }
    }
    /*
    * Reads the specified file, iterates through it and stores all JSON object
    * within it into an JSON array .
    * */
    public boolean loadQuestions() {
        int questionsIterator = 0;

        // read data from question file and store it into Json Objects array
        loadDataArray();

        // create Question array.
        setArraySize(this.jsonDataArray.size());

        // Iterate through JSON data
        for (Object dataIterator : this.jsonDataArray) {
            readAndStoreSingleObject(dataIterator, questionsIterator);
            questionsIterator++;
        }
        System.out.println("Data reading finished...");
        return checkArraySize();
    }
    /*
    * Shows error message if there is not enough questions to complete gameplay.
    * */
    private boolean checkArraySize() {
        if(this.jsonDataArray.size() < 15) {
            ErrorDialog errorDialog = new ErrorDialog();
            errorDialog.showErrorDialog(null,"Data failure","Not enough questions in database." +
                    " </br>Please contact Support !");
            return false;
        }
         return true;
    }

    /*
    * 'Translates' one JSON object to Strings, ints and arrays,
    * then eventually pass those converted values to other method,
    * which stores them in a new Question object , in arrays.
    * @param : dataIterator - JSON Object
    * @param : questionIterator - index to show current json array place.
    * */
    private void readAndStoreSingleObject(Object dataIterator,int questionIterator) {
        int iterator = 0;
        String[] answersArray = new String[4];
        Integer[] ratingsArray = new Integer[4];
        // set current JSON object (Current Question)
        JSONObject currentQuestion = (JSONObject) dataIterator;

        // Setters follow
        int id = (int)((long)currentQuestion.get("id"));
        short level = (short)((long)currentQuestion.get("Level"));
        String question = (String) currentQuestion.get("Question");

        // create JSON array object and pass Answers to it
        JSONArray answers = (JSONArray) currentQuestion.get("Answers");

        // Iterate through answers array ' 4  times '
        for (Object answersIterator : answers)
        {
            // get objects ( Answers  / Ratings )
            JSONObject currentAnswer = (JSONObject) answersIterator;
            answersArray[iterator] = (String)currentAnswer.get("Answer");
            ratingsArray[iterator] = (int)((long)currentAnswer.get("Rating"));
            iterator++;
        }
        storeData(questionIterator,id,level,question,answersArray,ratingsArray);
    }

    /*
    * Creates new Question object, and depending on level, stores it to either
    * Easy, Medium or Hard array lists. Without that, it ALWAYS stores the file
    * into allQuestion array.
    * */
    private void storeData(int questionsIterator, int id, short level, String question, String[] answersArray, Integer[] ratingsArray) {
        // set Question.
        allQuestions[questionsIterator] = new Question(id,level,question,answersArray,ratingsArray);
        switch (level) {
            // store data to easy Questions array
            case 0:
                easyQuestions.add(allQuestions[questionsIterator]);
                break;
            // store data to medium Questions array
            case 1:
                mediumQuestions.add(allQuestions[questionsIterator]);
                break;
            // store data to hard Questions array
            case 2:
                hardQuestions.add(allQuestions[questionsIterator]);
                break;
            default:
                System.out.println("Error while storing questions into arrays... See 'model -> DataHandler.java'");
                break;
        }
    }

    public int getLastID() {
        return lastID;
    }

    public void setLastID(int lastID) {
        this.lastID = lastID;
    }

    @Override
    public String toString() {
        return "Questions:  \n" + Arrays.toString(allQuestions);
    }

    public Question[] getAllQuestions() {
        return allQuestions;
    }

    public void setAllQuestions(Question[] allQuestions) {
        this.allQuestions = allQuestions;
    }

    public ArrayList<Question> getEasyQuestions() {
        return easyQuestions;
    }

    public void setEasyQuestions(ArrayList<Question> easyQuestions) {
        this.easyQuestions = easyQuestions;
    }

    public ArrayList<Question> getMediumQuestions() {
        return mediumQuestions;
    }

    public void setMediumQuestions(ArrayList<Question> mediumQuestions) {
        this.mediumQuestions = mediumQuestions;
    }

    public ArrayList<Question> getHardQuestions() {
        return hardQuestions;
    }

    public void setHardQuestions(ArrayList<Question> hardQuestions) {
        this.hardQuestions = hardQuestions;
    }
    public Question getQuestionByID(int id) {
        return this.allQuestions[id];
    }
    public Question getEasyQuestionByID(int id) {
        return this.easyQuestions.get(id);
    }
    public Question getMediumQuestionByID(int id) {
        return this.mediumQuestions.get(id);
    }
    public Question getHardQuestionByID(int id) {
        return this.hardQuestions.get(id);
    }
    public int getEasyQuestionSize() {
        return this.easyQuestions.size();
    }
    public int getMediumQuestionSize() {
        return this.mediumQuestions.size();
    }
    public int getHardQuestionSize() {
        return this.hardQuestions.size();
    }
    public int getAllQuestionSize() {return this.allQuestions.length;}
    public int getJsonArraySize() {return this.jsonDataArray.size();}
}
