package backend.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Domantas Giedraitis - Navus
 * @version 1.001
 **/

public class Player {
    private String lastTimePlayed;
    private String username;
    private String lowerCaseUsername;
    private int credit = 0;
    private boolean help_FiftyFifty = false;
    private boolean help_SecondChance = false;
    private boolean help_Audience = false;

    public Player(String username) {
        this.lastTimePlayed = new SimpleDateFormat("yyyy.MM.dd  HH.mm.ss").format(new Date());
        this.username = username;
        this.lowerCaseUsername = username.toLowerCase();
    }

    public String getLowerCaseUsername() {
        return this.lowerCaseUsername;
    }
    public String getLastTimePlayed() {
        return lastTimePlayed;
    }

    public boolean hasUsedHelp_FiftyFifty() {
        return help_FiftyFifty;
    }

    public void setHelp_FiftyFifty(boolean help_FiftyFifty) {
        this.help_FiftyFifty = help_FiftyFifty;
    }

    public boolean hasUsedHelp_SecondChance() {
        return help_SecondChance;
    }

    public void setHelp_SecondChance(boolean help_SecondChance) {
        this.help_SecondChance = help_SecondChance;
    }

    public boolean hasUsedHelp_Audience() {
        return help_Audience;
    }

    public void setHelp_Audience(boolean help_Audience) {
        this.help_Audience = help_Audience;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    @Override
    public String toString() {
        return "Player{" +
                "lastTimePlayed=" + lastTimePlayed +
                ", username='" + username + '\'' +
                ", credit=" + credit +
                '}';
    }
}
