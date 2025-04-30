package gamecore;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HumanPlayer extends Player {
    private String name;
    private List<Card> hand;
    private Card selectedCard;
    private String selectedColor;
    private final Scanner scanner;

    public HumanPlayer(String name) {
        this.hand = new ArrayList<>();
        this.name = name;
        this.selectedCard = null;
        this.selectedColor = null;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void addCard(Card card) {
        hand.add(card);
    }

    @Override
    public List<Card> getHand() {
        return hand;
    }

    @Override
    public int getHandSize() {
        return hand.size();
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Nouvelles méthodes pour l'interface graphique
    public Card getCard(int index) {
        if (index >= 0 && index < hand.size()) {
            return hand.get(index);
        }
        return null;
    }

    public Card removeCard(int index) {
        if (index >= 0 && index < hand.size()) {
            return hand.remove(index);
        }
        return null;
    }

    @Override
    public boolean playTurn(Card topCard) {
        Card playedCard = playCard(topCard);
        if (playedCard != null) {
            setLastPlayedCard(playedCard);
            return true;
        }
        return false;
    }

    @Override
    public Card playCard(Card topCard) {
        if (selectedCard != null && selectedCard.canPlayOn(topCard)) {
            hand.remove(selectedCard);
            Card cardToPlay = selectedCard;
            if (cardToPlay.getType().equals("Wild") || cardToPlay.getType().equals("Wild Draw Four")) {
                cardToPlay.setColor(selectedColor);
            }
            selectedCard = null;
            selectedColor = null;
            return cardToPlay;
        }
        return null;
    }

    @Override
    public String chooseColor() {
        return selectedColor != null ? selectedColor : "Rouge";
    }

    public void setSelectedCard(Card card) {
        this.selectedCard = card;
    }

    public void setSelectedColor(String color) {
        this.selectedColor = color;
    }

    public void displayHand() {
        System.out.println("\nVotre main (" + name + "):");
        for (int i = 0; i < hand.size(); i++) {
            System.out.println((i + 1) + ": " + hand.get(i));
        }
        System.out.println();
    }
}