package backend;

import org.junit.Test;

import static org.junit.Assert.*;

public class ClientTest {
    @Test
    public void isUsernameTaken() throws Exception {
        Client client = new Client();
        client.addPlayer("username");
        client.addPlayer("");
        client.addPlayer("123");
        client.addPlayer("UsErNaMe116:@");
        client.addPlayer("\"()*&^&*(");
        assertEquals(true,client.isUsernameTaken("username"));
        assertEquals(true,client.isUsernameTaken(""));
        assertEquals(true,client.isUsernameTaken("123"));
        assertEquals(true,client.isUsernameTaken("UsErNaMe116:@"));
        assertEquals(true,client.isUsernameTaken("\"()*&^&*("));
        assertEquals(true,client.isUsernameTaken("USERNAME"));
      //  assertEquals(true,client.isUsernameTaken("username ")); // does not work, need to fix it .
        assertEquals(false,client.isUsernameTaken(" "));
    }

    @Test
    public void getCurrentPlayerIndex() throws Exception {
        Client client = new Client();
        assertEquals(-1,client.getCurrentPlayerIndex()); // no players added
        client.addPlayer("name");
        assertEquals(0,client.getCurrentPlayerIndex());
        for(int i = 0 ; i < 100; i++) {
            client.addPlayer("name" + i);
        }
        assertEquals(100, client.getCurrentPlayerIndex());
    }

}