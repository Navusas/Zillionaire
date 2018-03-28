package backend.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class DataHandlerTest {
    @Test
    public void storeNewQuestion() throws Exception {
        DataHandler dataHandler = new DataHandler("FileWhichNotExists");
        dataHandler.loadQuestions();
        int size = dataHandler.getAllQuestionSize();
        dataHandler.storeNewQuestion((short)1,"Question",new String[] {"a1","a2","a3","a4"},
                                                new Integer[] {50,20,20,10});
        int newSize = dataHandler.getJsonArraySize();
        assertEquals(size+1,newSize);

    }

    @Test
    public void loadQuestions() throws Exception {
        DataHandler dataHandler = new DataHandler("fileWhichDoesNotExists");
        assertEquals(false,dataHandler.loadQuestions());
    }

}