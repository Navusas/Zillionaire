package backend;

import backend.Gameplay;
import backend.model.DataHandler;
import backend.model.Question;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.xml.crypto.Data;

import static org.junit.Assert.*;

public class GameplayTest {
    @Test
    public void nextLevel() throws Exception {
        Gameplay gameplay = new Gameplay("name");
        int currentLevel = gameplay.getCurrentLevel(); // 0
        gameplay.nextLevel(); // 1
        assertEquals(currentLevel+1, gameplay.getCurrentLevel());
    }
    @Test
    public void addCredit() throws Exception{
        Gameplay gameplay = new Gameplay("name");
        int currentCredit = gameplay.getPlayer().getCredit(); // 0
        gameplay.addCredit(); // 100
        assertEquals(currentCredit+100, gameplay.getPlayer().getCredit());
    }
}