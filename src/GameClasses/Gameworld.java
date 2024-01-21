package GameClasses;

import Database.DatabaseInitializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Gameworld implements Runnable{
    public static List<User> users = new ArrayList<>();
    public static int packageFirstId = 1;
    public static int addPackageLastId = 1;

    private String user1;
    private String user2;

    public String log="";

    public Gameworld(String user1, String user2) {
        this.user1 = user1;
        this.user2 = user2;
    }

    @Override
    public void run() {
        this.log+="\n" + "Battle between " + user1 + " and " + user2 + "\n";
        battle();
    }

    private void battle() {
        int roundCount=0;
        while(roundCount<100) {
            if(users.get(0).getDeck().isEmpty() || users.get(1).getDeck().isEmpty()) {
                break;
            }

            int r0 = randomNumber(users.get(0).getDeck().size());
            int r1 = randomNumber(users.get(1).getDeck().size());
            Card card0 = users.get(0).getDeck().get(r0);
            Card card1 = users.get(1).getDeck().get(r1);

            this.log += "" + user1 + ": " + card0.getName() + " (" + card0.getDamage() + " Damage) VS "
                    + user2 + ": " + card1.getName() + " (" + card1.getDamage() + " Damage)";

            // Pure monster fights
            if(card0.getCardType().equals("monster") && card1.getCardType().equals("monster")) {
                int winner = monsterFight(card0, card1);
                // Draw
                if(winner == 2) {
                    this.log += " => " + " Draw!\n";
                    for(int c=0; c<2; c++) {
                        int draws = users.get(c).getDraws();
                        users.get(c).setDraws(draws+1);
                    }
                }
                // Winner
                else {
                    this.log += " => " + users.get(winner).getUsername() + " wins\n";

                    if(winner==0) {
                        // Elo calc
                        users.get(0).setElo(users.get(0).getElo()+3);
                        users.get(1).setElo(users.get(1).getElo()-5);

                        // Card take over
                        users.get(0).getDeck().add(users.get(1).getDeck().get(r1));

                        //Card removal
                        users.get(1).getDeck().remove(r1);

                        // Wins and losses update
                        users.get(0).setWins(users.get(0).getWins()+1);
                        users.get(1).setLosses(users.get(1).getLosses()+1);
                    }
                    else {
                        // Elo calc
                        users.get(1).setElo(users.get(1).getElo()+3);
                        users.get(0).setElo(users.get(0).getElo()-5);

                        // Card take over
                        users.get(1).getDeck().add(users.get(0).getDeck().get(r0));

                        //Card removal
                        users.get(0).getDeck().remove(r0);

                        // Wins and losses update
                        users.get(1).setWins(users.get(1).getWins()+1);
                        users.get(0).setLosses(users.get(0).getLosses()+1);
                    }
                }
            }
            // Mixed fights
            else {
                int winner = mixedFight(card0, card1);
                // Draw
                if(winner == 2) {
                    this.log += " => " + " Draw!\n";
                    for(int c=0; c<2; c++) {
                        int draws = users.get(c).getDraws();
                        users.get(c).setDraws(draws+1);
                    }
                }
                // Winner
                else {
                    this.log += " => " + users.get(winner).getUsername() + " wins\n";

                    if(winner==0) {
                        // Elo calc
                        users.get(0).setElo(users.get(0).getElo()+3);
                        users.get(1).setElo(users.get(1).getElo()-5);

                        // Card take over
                        users.get(0).getDeck().add(users.get(1).getDeck().get(r1));

                        //Card removal
                        users.get(1).getDeck().remove(r1);

                        // Wins and losses update
                        users.get(0).setWins(users.get(0).getWins()+1);
                        users.get(1).setLosses(users.get(1).getLosses()+1);
                    }
                    else {
                        // Elo calc
                        users.get(1).setElo(users.get(1).getElo()+3);
                        users.get(0).setElo(users.get(0).getElo()-5);

                        // Card take over
                        users.get(1).getDeck().add(users.get(0).getDeck().get(r0));

                        //Card removal
                        users.get(0).getDeck().remove(r0);

                        // Wins and losses update
                        users.get(1).setWins(users.get(1).getWins()+1);
                        users.get(0).setLosses(users.get(0).getLosses()+1);
                    }
                }
            }
            roundCount++;
        }

        // Determine the winner
        for(int c=0; c<2; c++) {
            if(users.get(c).getDeck().isEmpty()) {
                this.log += "\n" + users.get(c).getUsername() + " has no more cards to play with!";
                this.log += "\n|----------------------------|";
                this.log += "\n" + "|     WINNER => " + users.get(toggleValue(c)).getUsername() + "     |";
                this.log += "\n|----------------------------|\n";
            }
        }
        // In case of draw
        if (roundCount == 100) {
            this.log += "\n100 Rounds were reached with no winner! ==>> DRAW!\n";
        }

        // Database Stats and Scoreboard Update
        for(int c=0; c<2; c++) {
            DatabaseInitializer database = new DatabaseInitializer("MonsterTradingCards", "postgres", "eeeeeeee");
            String sql = "UPDATE scoreboard SET elo=" + users.get(c).getElo() + " WHERE username='" + users.get(c).getUsername() + "';";
            String sql1 = "UPDATE stats SET elo=" + users.get(c).getElo() + ", wins=" + users.get(c).getWins() + ", draws=" + users.get(c).getDraws()
                    + ", losses=" + users.get(c).getLosses() + " WHERE username='" + users.get(c).getUsername() + "';";
            System.out.println(sql);
            System.out.println(sql1);
            database.insert(sql);
            database.insert(sql1);
        }

    }

    public int monsterFight(Card card0, Card card1) {
        // Special Cases
        if((card0.getName().equals("WaterGoblin") || card0.getName().equals("FireGoblin")) && card1.getName().equals("Dragon")) {
            return 1;
        }
        else if (card0.getName().equals("Dragon") && (card1.getName().equals("WaterGoblin") || card1.getName().equals("FireGoblin"))) {
            return 0;
        }
        else if (card0.getName().equals("Wizzard") && card1.getName().equals("Ork")) {
            return 0;
        }
        else if (card0.getName().equals("Ork") && card1.getName().equals("Wizzard")) {
            return 1;
        }
        // Normal monster fight
        else {
            return damageCal(card0.getDamage(), card1.getDamage());
        }
    }

    public int mixedFight(Card card0, Card card1) {
        // Same Element type fights
        if(card0.getElementType().equals("normal") && card1.getElementType().equals("normal")) {
            return damageCal(card0.getDamage(), card1.getDamage());
        }
        else if (card0.getElementType().equals("water") && card1.getElementType().equals("water")) {
            return damageCal(card0.getDamage(), card1.getDamage());
        }
        else if (card0.getElementType().equals("fire") && card1.getElementType().equals("fire")) {
            return damageCal(card0.getDamage(), card1.getDamage());
        }
        // Special Cases
        else if (card0.getName().equals("Knight") && card1.getName().equals("WaterSpell")) {
            return 1;
        }
        else if (card0.getName().equals("WaterSpell") && card1.getName().equals("Knight")) {
            return 0;
        }
        else if (card0.getName().equals("FireElf") && card1.getName().equals("Dragon")) {
            return 0;
        }
        else if (card0.getName().equals("Dragon") && card1.getName().equals("FireElf")) {
            return 1;
        }
        else if (card0.getName().equals("Kraken") && card1.getCardType().equals("spell")) {
            return 1;
        }
        else if (card0.getCardType().equals("spell") && card1.getName().equals("Kraken")) {
            return 0;
        }
        // Mixed Fight cases
        else {
            if(card0.getElementType().equals("fire") && card1.getElementType().equals("water")) {
                this.log += " => " + card0.getDamage()/2 + " VS " + card1.getDamage()*2;
                return damageCal(card0.getDamage()/2, card1.getDamage()*2);
            }
            else if(card0.getElementType().equals("water") && card1.getElementType().equals("fire")) {
                this.log += " => " + card0.getDamage()*2 + " VS " + card1.getDamage()/2;
                return damageCal(card0.getDamage()*2, card1.getDamage()/2);
            }
            else if(card0.getElementType().equals("fire") && card1.getElementType().equals("normal")) {
                this.log += " => " + card0.getDamage()*2 + " VS " + card1.getDamage()/2;
                return damageCal(card0.getDamage()*2, card1.getDamage()/2);
            }
            else if(card0.getElementType().equals("normal") && card1.getElementType().equals("fire")) {
                this.log += " => " + card0.getDamage()/2 + " VS " + card1.getDamage()*2;
                return damageCal(card0.getDamage()/2, card1.getDamage()*2);
            }
            else if(card0.getElementType().equals("normal") && card1.getElementType().equals("water")) {
                this.log += " => " + card0.getDamage()*2 + " VS " + card1.getDamage()/2;
                return damageCal(card0.getDamage()*2, card1.getDamage()/2);
            }
            else if(card0.getElementType().equals("water") && card1.getElementType().equals("normal")) {
                this.log += " => " + card0.getDamage()/2 + " VS " + card1.getDamage()*2;
                return damageCal(card0.getDamage()/2, card1.getDamage()*2);
            }
            else {
                return 2;
            }
        }
    }

    public int damageCal(int card0Damage, int card1Damage) {
        if(card0Damage > card1Damage) {
            return 0;
        } else if (card0Damage < card1Damage) {
            return 1;
        }
        else {
            return 2;
        }
    }
    public int randomNumber(int max) {
        Random random = new Random();
        return random.nextInt(max);
    }

    public int toggleValue(int value) {
        if (value == 0) {
            return 1;
        } else if (value == 1) {
            return 0;
        } else {
            throw new IllegalArgumentException("Value must be 0 or 1");
        }
    }
}
