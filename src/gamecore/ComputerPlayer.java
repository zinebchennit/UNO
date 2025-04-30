package gamecore;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ComputerPlayer extends Player {
    private String name;
    private List<Card> hand;
    private Random random;

    public ComputerPlayer(String name) {
        super(name);
        this.hand = new ArrayList<>();
        this.random = new Random();
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

    public Card removeCard(int index) {
        if (index >= 0 && index < hand.size()) {
            return hand.remove(index);
        }
        return null;
    }

    @Override
    public Card playCard(Card topCard) {
        for (Card card : hand) {
            if (card.canPlayOn(topCard)) {
                hand.remove(card);
                if (card.getType().equals("Wild") || card.getType().equals("Wild Draw Four")) {
                    card.setColor(chooseColor());
                }
                setLastPlayedCard(card);
                return card;
            }
        }
        return null;
    }

    @Override
    public String chooseColor() {
        String[] colors = {"Rouge", "Bleu", "Vert", "Jaune"};
        return colors[random.nextInt(colors.length)];
    }
}