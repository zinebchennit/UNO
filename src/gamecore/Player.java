package gamecore;

import java.util.ArrayList;
import java.util.List;

public abstract class Player {
    protected String name;
    protected List<Card> hand;
    protected Card lastPlayedCard;

    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
    }

    public void addCard(Card card) {
        hand.add(card);
    }

    public String getName() {
        return name;
    }

    public int getHandSize() {
        return hand.size();
    }

    public Card getLastPlayedCard() {
        return lastPlayedCard;
    }

    protected void setLastPlayedCard(Card card) {
        this.lastPlayedCard = card;
        hand.remove(card);
    }

    public abstract boolean playTurn(Card topCard);
    
    public abstract String chooseColor();
}