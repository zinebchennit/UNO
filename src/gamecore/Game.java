package gamecore;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Game {
    private static final Logger LOGGER = Logger.getLogger(Game.class.getName());
    private final Deck deck;
    private final ArrayList<Player> players;
    private boolean isClockwise = true;
    private int currentPlayerIndex = 0;
    private Card topCard;

    public Game(int humanPlayers, int computerPlayers) {
        this.deck = new Deck();
        this.players = new ArrayList<>();

        // Initialize players
        for (int i = 0; i < humanPlayers; i++) {
            players.add(new HumanPlayer("Human " + (i + 1)));
        }
        for (int i = 0; i < computerPlayers; i++) {
            players.add(new ComputerPlayer("Computer " + (i + 1)));
        }

        // Deal initial cards
        dealInitialCards();
        // Set initial top card
        topCard = deck.drawCard();
    }

    private void dealInitialCards() {
        // Distribuer les cartes initiales
        for (Player player : players) {
            for (int i = 0; i < 7; i++) {
                player.addCard(deck.drawCard());
            }
        }

        // S'assurer que la première carte n'est pas une carte spéciale
        do {
            topCard = deck.drawCard();
            if (topCard.getType().equals("Wild") || topCard.getType().equals("Wild Draw Four")) {
                deck.discard(topCard);
            }
        } while (topCard.getType().equals("Wild") || topCard.getType().equals("Wild Draw Four"));
    }

    public void playGame() {
        while (!isGameOver()) {
            Player currentPlayer = players.get(currentPlayerIndex);
            System.out.println("\nTop card: " + topCard);
            boolean played = currentPlayer.playTurn(topCard);

            if (played) {
                topCard = currentPlayer.getLastPlayedCard();
                handleCardEffect(currentPlayer.getLastPlayedCard());
            } else {
                currentPlayer.addCard(deck.drawCard());
                moveToNextPlayer();
            }
        }
        announceWinner();
    }

    // Nouvelles méthodes pour l'interface graphique
    public Card getCurrentTopCard() {
        return topCard;
    }

    public List<Card> getCurrentPlayerCards() {
        if (currentPlayerIndex < players.size()) {
            Player currentPlayer = players.get(currentPlayerIndex);
            if (currentPlayer instanceof HumanPlayer) {
                return ((HumanPlayer) currentPlayer).getHand();
            }
        }
        return new ArrayList<>();
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public boolean playCard(int cardIndex) {
        Player currentPlayer = getCurrentPlayer();
        if (currentPlayer instanceof HumanPlayer) {
            Card card = ((HumanPlayer) currentPlayer).getCard(cardIndex);
            if (card != null) {
                // Vérifier si la carte peut être jouée
                if (card.canPlayOn(topCard)) {
                    // Si c'est une carte Wild, demander la couleur
                    if (card.getType().equals("Wild") || card.getType().equals("Wild Draw Four")) {
                        String[] options = {"Rouge", "Bleu", "Vert", "Jaune"};
                        String newColor = (String) javax.swing.JOptionPane.showInputDialog(
                            null,
                            "Choisissez une couleur:",
                            "Changer de couleur",
                            javax.swing.JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[0]
                        );
                        if (newColor != null) {
                            card.setColor(newColor);
                            ((HumanPlayer) currentPlayer).removeCard(cardIndex);
                            topCard = card;
                            handleCardEffect(card);
                            return true;
                        }
                        return false;
                    } else {
                        ((HumanPlayer) currentPlayer).removeCard(cardIndex);
                        topCard = card;
                        handleCardEffect(card);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Card drawCard() {
        return deck.drawCard();
    }

    protected void handleCardEffect(Card card) {
        String cardType = card.getType();
        switch (cardType) {
            case "Skip" -> skipNextPlayer();
            case "Reverse" -> reverseDirection();
            case "DrawTwo" -> nextPlayerDrawCards(2);
            case "Wild" -> handleWildCard();
            case "WildDrawFour" -> {
                handleWildCard();
                nextPlayerDrawCards(4);
            }
        }
    }

    private void skipNextPlayer() {
        moveToNextPlayer(); // Skip one extra time
        moveToNextPlayer();
    }

    private void reverseDirection() {
        isClockwise = !isClockwise;
        if (players.size() == 2) {
            moveToNextPlayer(); // In 2-player game, reverse acts like skip
        }
    }

    private void nextPlayerDrawCards(int count) {
        moveToNextPlayer();
        Player player = players.get(currentPlayerIndex);
        System.out.println(player.getName() + " doit piocher " + count + " cartes");
        for (int i = 0; i < count; i++) {
            Card drawnCard = deck.drawCard();
            if (drawnCard != null) {
                player.addCard(drawnCard);
                System.out.println(player.getName() + " pioche une carte");
            }
        }
        // Ne pas passer au joueur suivant ici, car le joueur qui a pioché doit jouer son tour
    }

    private void handleWildCard() {
        // Color change handled by the player class
        moveToNextPlayer();
    }

    public void moveToNextPlayer() {
        if (isClockwise) {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        } else {
            currentPlayerIndex = (currentPlayerIndex - 1 + players.size()) % players.size();
        }
    }

    public boolean isGameOver() {
        for (Player player : players) {
            if (player.getHandSize() == 0) {
                return true;
            }
        }
        return false;
    }

    private void announceWinner() {
        for (Player player : players) {
            if (player.getHandSize() == 0) {
                System.out.println("\n" + player.getName() + " wins!");
                return;
            }
        }
    }

    private void delay() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            LOGGER.log(Level.WARNING, "Sleep interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

    public List<Player> getPlayers() {
        return players;
    }

    public boolean playComputerCard() {
        Player currentPlayer = getCurrentPlayer();
        if (currentPlayer instanceof ComputerPlayer) {
            boolean played = currentPlayer.playTurn(topCard);
            if (played) {
                Card playedCard = currentPlayer.getLastPlayedCard();
                topCard = playedCard;
                handleCardEffect(playedCard);
                if (playedCard.getType().equals("Wild") || playedCard.getType().equals("Wild Draw Four")) {
                    String newColor = ((ComputerPlayer) currentPlayer).chooseColor();
                    playedCard.setColor(newColor);
                }
                return true;
            }
        }
        return false;
    }
}