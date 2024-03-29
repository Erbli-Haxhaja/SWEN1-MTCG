package GameClasses;

public class Card {
    private String name;
    private int damage;
    private String elementType;
    private String cardType;
    private String id;

    public Card(String id, String name, int damage, String cardType, String elementType) {
        this.id = id;
        this.name = name;
        this.damage = damage;
        this.cardType = cardType;
        this.elementType = elementType;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }
}