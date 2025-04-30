package gamecore;

import java.util.ArrayList;
import java.util.List;

public abstract class Player {
    protected String name;
    protected List<Card> hand;
    protected Card lastPlayedCard; // May not be needed if Game handles top card

    public Player() {
        // Default constructor
        this.hand = new ArrayList<>();
    }

    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
    }

    public void addCard(Card card) {
        if (card != null) {
            hand.add(card);
        }
    }

    // New method to clear hand
    public void clearHand() {
        this.hand.clear();
    }

    // New method to get a card by index
    public Card getCard(int index) {
        if (index >= 0 && index < hand.size()) {
            return hand.get(index);
        }
        return null;
    }

    // New method to remove a card by index
    public boolean removeCard(int index) {
        if (index >= 0 && index < hand.size()) {
            hand.remove(index);
            return true;
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public int getHandSize() {
        return hand.size();
    }

    // This might be redundant now as Game tracks the top card
    // public Card getLastPlayedCard() {
    //     return lastPlayedCard;
    // }

    // protected void setLastPlayedCard(Card card) {
    //     this.lastPlayedCard = card;
    //     hand.remove(card); // Removing should happen via removeCard(index)
    // }

    // playTurn might be better handled directly in Game/UnoGame logic
    // public boolean playTurn(Card topCard) {
    //     Card playedCard = playCard(topCard);
    //     if (playedCard != null) {
    //         return true;
    //     }
    //     return false;
    // }

    // Abstract method for choosing color for Wild cards
    public abstract String chooseColor();

    // Method to get the player's hand (already exists essentially)
    public List<Card> getHand() {
        return hand;
    }

    // playCard might be better handled directly in Game/UnoGame logic
    // public abstract Card playCard(Card topCard);
}