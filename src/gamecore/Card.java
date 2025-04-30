package gamecore;

public class Card {
    private String color;
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
        if (type.equals("Wild") || type.equals("Wild Draw Four")) {
            return true;
        }

        if (color.equals("Noir")) {
            return false;
        }

        return color.equals(topCard.getColor()) || type.equals(topCard.getType());
    }

    public void setColor(String color) {
        if (this.type.equals("Wild") || this.type.equals("Wild Draw Four")) {
            this.color = color;
        }
    }

    @Override
    public String toString() {
        return color + " " + type + (type.equals("Number") ? " " + value : "");
    }
}