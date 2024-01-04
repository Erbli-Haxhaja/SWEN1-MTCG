package GameClasses;
import java.util.ArrayList;

public class User {
    private String username;
    private String password;
    private int elo = 100;
    private boolean configrured = false;

    private int coins = 20;
    private ArrayList<Card> deck = new ArrayList<>();
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

    public void addToStack(Card card) {
        this.stack.add(card);
    }

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

    public boolean isConfigrured() {
        return configrured;
    }

    public void setConfigrured(boolean configrured) {
        this.configrured = configrured;
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

    public void unconfiguredDeck() {
        int c = 0;
        for (Card card : this.stack) {
            if(c==4) {
                break;
            }
            this.deck.add(card);
            c++;
        }
    }

}