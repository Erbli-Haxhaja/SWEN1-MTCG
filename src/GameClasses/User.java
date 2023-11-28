package GameClasses;
import java.util.ArrayList;

public class User {
    private String username;
    private String password;
    private int elo = 100;

    private int coins = 20;
    private ArrayList<Card> deck = new ArrayList<>(4);
    private ArrayList<Card> stack = new ArrayList<>();

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void login(){}
    public void register(){}
    public void acquirePackage(){}
    public void defineDeck(){}
    public void battle(){}
    public void compareStats(){}
    public void editProfile(){}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public ArrayList<Card> getDeck() {
        return deck;
    }

    public void setDeck(ArrayList<Card> deck) {
        this.deck = deck;
    }

    public ArrayList<Card> getStack() {
        return stack;
    }

    public void setStack(ArrayList<Card> stack) {
        this.stack = stack;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}