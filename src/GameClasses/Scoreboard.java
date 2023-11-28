package GameClasses;
import java.util.HashMap;

public class Scoreboard {
    private HashMap<String, String> stats = new HashMap<>();

    public Scoreboard() {

    }

    public void printStats() {
        System.out.println("All Stats: ");
        this.stats.forEach((key, value) -> System.out.println(key + ": " + value));
    }

    public void addStat(String username, int elo) {
        this.stats.put(username, Integer.toString(elo));
    }
}
