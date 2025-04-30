package gamecore;

public class Card {
    private String color;      
    private String type;       
    private int value;         

    public Card(String color, String type, int value) {
        this.color = color;
        this.type = type;
        this.value = value;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    public boolean canBePlayedOn(Card topCard) {
        
        if (this.type.equals("Wild") || this.type.equals("Wild Draw Four")) {
            return true;
        }
       
        if (this.color.equals(topCard.getColor())) {
            return true;
        }
        
       
        if (this.type.equals(topCard.getType()) && !this.type.equals("Number")) {
            return true;
        }
       
        if (this.type.equals("Number") && topCard.getType().equals("Number") && this.value == topCard.getValue()) {
            return true;
        }
        
        return false;
    }
    
    @Override
    public String toString() {
        if (type.equals("Number")) {
            return color + " " + value;
        } else {
            return color + " " + type;
        }
    }
}