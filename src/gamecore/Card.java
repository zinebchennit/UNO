package gamecore;

public class Card {
    private final String color;
    private final String type;
    private final int value;

    public Card(String color, String type, int value) {
        this.color = color;
        this.type = type;
        this.value = value;
    }

    public String getColor() {
        return color;
    }

    public String getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    public boolean canPlayOn(Card topCard) {
        if (color.equals("Noir")) {  // Wild cards can be played on any card
            return true;
        }
        return color.equals(topCard.getColor()) || type.equals(topCard.getType());
    }

    @Override
    public String toString() {
        if (type.equals("Number")) {
            return color + " " + value;
        }
        return color + " " + type;
    }
}