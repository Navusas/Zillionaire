package backend;

/**
 * @author Domantas Giedraitis - Navus
 * @version 1.001
 **/

public class Config {
    public static String TITLE = "Who wants to be a Zillionaire?";
    public static double CLIENT_VERSION = 0.99;
    public static int MAX_PLAYERS_AMOUNT = 8;
    public static final int DEFAULT_PLAYERS_AMOUNT = 2;
    public static final Integer[] AMOUNTS_TO_WIN = {100,200,300,500,1000,
                                                           2000,4000,8000,16000,32000,
                                                  64000,125000,250000,500000,1000000}; // MAX 15 !
    public static final String[] CATEGORIES = {"General","Geographic","Sports"};
    public static long CHRONOMETER_TIME = 30000; // milliseconds
    public static final String USERNAME = "D3v3lop3r";
    public static final String PASSWORD = "Password";
    public static final int DEVELOPER_MAX_PLAYERS = 200;
    public static final long DEVELOPER_MAX_CHRONOMETER_TIME = 600000;
    public static final int DEVELOPER_MIN_PLAYERS = 1;
    public static final long DEVELOPER_MIN_CHRONOMETER_TIME = 5000;

}