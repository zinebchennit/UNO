package gamecore;

import java.util.ArrayList;
import java.util.List;
// Removed unused import
// import java.util.logging.Level;

public class Game {
    // Removed unused logger field
    // private static final Logger LOGGER = Logger.getLogger(Game.class.getName());
    private final Deck deck;
    private final List<Player> players; // Changed to List<Player>
    private boolean isClockwise = true;
    private int currentPlayerIndex = 0;
    private Card topCard;

    // Modified Constructor to accept a list of players
    public Game(List<Player> playersList) {
        System.out.println("Creating new game with provided player list");
        this.deck = new Deck();
        this.players = new ArrayList<>(playersList); // Use the provided list

        System.out.println("Total players received: " + players.size());

        // Deal initial cards to all players first
        // dealInitialCards(); // Moved to initializeGame
        // System.out.println("Initial cards dealt to players");

        // Then set the top card
        // drawInitialTopCard(); // Moved to initializeGame
        // System.out.println("Game initialized with top card: " + topCard);
    }

    // Helper method to draw the initial top card, ensuring it's not a special card
    private void drawInitialTopCard() {
        do {
            if (topCard != null) { // If we are redrawing, put the previous one back
                deck.discard(topCard);
            }
            topCard = deck.drawCard();
            System.out.println("Drew initial top card: " + topCard);
            if (topCard == null) {
                 System.err.println("Error: Deck is empty during initial top card draw!");
                 // Handle this case, maybe reshuffle discard pile? For now, log error.
                 break;
            }
        } while (topCard.getType().equals("Wild") || topCard.getType().equals("Wild Draw Four"));
        System.out.println("Final initial top card: " + topCard);
    }


    public void initializeGame() {
        System.out.println("Initializing game...");
        // Shuffle deck before dealing
        deck.shuffle();
        System.out.println("Deck shuffled.");

        // Deal initial cards to all players
        dealInitialCards();
        System.out.println("Initial cards dealt");

        // Draw the initial top card
        drawInitialTopCard();
        System.out.println("Initial top card set: " + topCard);

        // Reset current player index
        currentPlayerIndex = 0;
        System.out.println("Starting player: " + getCurrentPlayer().getName());

        System.out.println("Game initialization complete.");
    }

    public void dealInitialCards() {
        final int INITIAL_CARDS = 7; // Constante pour le nombre de cartes initial
        System.out.println("Dealing " + INITIAL_CARDS + " cards to each player");

        for (Player player : players) {
            System.out.println("Dealing to " + player.getName());
            // Vider la main du joueur d'abord (important if re-initializing)
            player.clearHand(); // Add a clearHand method to Player

            // Distribuer exactement 7 cartes
            for (int i = 0; i < INITIAL_CARDS; i++) {
                Card card = deck.drawCard();
                if (card != null) {
                    player.addCard(card);
                    // System.out.println("  Dealt: " + card + " to " + player.getName()); // Reduce log noise
                } else {
                    System.err.println("Warning: Could not draw card " + (i+1) + " for " + player.getName() + ". Deck empty?");
                    // Handle deck empty during deal? Reshuffle?
                    break; // Stop dealing to this player if deck is empty
                }
            }
            System.out.println(player.getName() + " has " + player.getHandSize() + " cards");
        }
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
        // Removed check for HumanPlayer, let Player handle its own cards
        // if (currentPlayer instanceof HumanPlayer) {
            // HumanPlayer humanPlayer = (HumanPlayer) currentPlayer;
            Card card = currentPlayer.getCard(cardIndex); // Add getCard(index) to Player

            if (card != null && card.canPlayOn(topCard)) {
                // Gérer les cartes spéciales
                if (card.getType().equals("Wild") || card.getType().equals("Wild Draw Four")) {
                    String chosenColor = currentPlayer.chooseColor(); // Let Player decide how to choose

                    // If player cancels color selection (e.g., Human closes dialog)
                    if (chosenColor == null) {
                        System.out.println("Player cancelled color selection for Wild card.");
                        return false; // Card play is cancelled
                    }

                    card.setColor(chosenColor);
                    System.out.println(currentPlayer.getName() + " chose color: " + chosenColor);
                }

                // Jouer la carte
                currentPlayer.removeCard(cardIndex); // Add removeCard(index) to Player
                deck.discard(topCard); // Mettre l'ancienne carte dans la défausse
                topCard = card;        // Mettre la nouvelle carte comme carte du dessus
                System.out.println(currentPlayer.getName() + " played: " + card);

                // Check for win condition immediately after playing
                if (currentPlayer.getHandSize() == 0) {
                    System.out.println(currentPlayer.getName() + " has won!");
                    // Game over logic will be checked by isGameOver()
                    return true; // Play was successful
                }

                // Gérer les effets de la carte et passer le tour
                handleCardEffect(card); // This method now handles moving to the next player

                return true;
            } else {
                 System.out.println("Cannot play card " + card + " on " + topCard);
            }
        // }
        return false;
    }

    public Card drawCard() {
        return deck.drawCard();
    }

    protected void handleCardEffect(Card card) {
        System.out.println("Handling effect of card: " + card);
        String cardType = card.getType();

        switch (cardType) {
            case "Skip":
                System.out.println("Skipping next player");
                moveToNextPlayer(); // Move once for the skip
                moveToNextPlayer(); // Move again because the turn naturally passes
                break;

            case "Reverse":
                System.out.println("Reversing direction");
                isClockwise = !isClockwise;
                if (players.size() == 2) {
                    moveToNextPlayer(); // In 2-player game, reverse acts like a skip
                    moveToNextPlayer(); // Move again for natural turn pass
                } else {
                    moveToNextPlayer(); // Just move to the next player in the new direction
                }
                break;

            case "Draw Two":
                System.out.println("Next player must draw 2 cards");
                moveToNextPlayer(); // Move to the player who will draw
                Player nextPlayerDrawTwo = getCurrentPlayer();
                System.out.println(nextPlayerDrawTwo.getName() + " must draw 2 cards.");
                for (int i = 0; i < 2; i++) {
                    Card drawnCard = deck.drawCard();
                    if (drawnCard != null) {
                        nextPlayerDrawTwo.addCard(drawnCard);
                        System.out.println("  Drew: " + drawnCard);
                    } else {
                         System.err.println("Warning: Deck empty while drawing for Draw Two effect.");
                         break;
                    }
                }
                moveToNextPlayer(); // Skip the player who drew cards (move turn again)
                break;

            case "Wild Draw Four":
                System.out.println("Next player must draw 4 cards");
                moveToNextPlayer(); // Move to the player who will draw
                Player wildDrawPlayer = getCurrentPlayer();
                 System.out.println(wildDrawPlayer.getName() + " must draw 4 cards.");
                for (int i = 0; i < 4; i++) {
                    Card drawnCard = deck.drawCard();
                    if (drawnCard != null) {
                        wildDrawPlayer.addCard(drawnCard);
                         System.out.println("  Drew: " + drawnCard);
                    } else {
                         System.err.println("Warning: Deck empty while drawing for Wild Draw Four effect.");
                         break;
                    }
                }
                moveToNextPlayer(); // Skip the player who drew cards (move turn again)
                break;

            case "Wild":
                // La couleur a déjà été choisie, pas besoin d'action supplémentaire ici
                // Just move to the next player
                moveToNextPlayer();
                break;

            default: // Normal number card
                // Just move to the next player
                moveToNextPlayer();
                break;
        }

        System.out.println("Turn moves to: " + getCurrentPlayer().getName());
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

    public List<Player> getPlayers() {
        return players;
    }

    public boolean playComputerCard() {
        Player currentPlayer = getCurrentPlayer();
        if (currentPlayer instanceof ComputerPlayer) {
            ComputerPlayer computer = (ComputerPlayer) currentPlayer;
            System.out.println("Computer " + computer.getName() + "'s turn. Top card: " + topCard);
            List<Card> hand = computer.getHand();
            int playableCardIndex = -1;

            // Chercher une carte jouable
            for (int i = 0; i < hand.size(); i++) {
                if (hand.get(i).canPlayOn(topCard)) {
                    playableCardIndex = i;
                    break; // Play the first valid card found (simple strategy)
                }
            }

            if (playableCardIndex != -1) {
                // Jouer la carte trouvée
                 System.out.println("Computer " + computer.getName() + " found playable card: " + hand.get(playableCardIndex));
                // Use the common playCard method
                if (playCard(playableCardIndex)) { // playCard handles effects and turn moving
                     System.out.println("Computer " + computer.getName() + " successfully played card.");
                    return true; // Card was played
                } else {
                    // This should ideally not happen if canPlayOn was true,
                    // unless it was a Wild card and color choice failed (which is not possible for computer)
                     System.err.println("Error: Computer " + computer.getName() + " failed to play a card deemed playable.");
                     // As a fallback, computer draws a card
                     Card drawnCard = drawCard();
                     if (drawnCard != null) {
                         computer.addCard(drawnCard);
                         System.out.println("Computer " + computer.getName() + " drew a card instead: " + drawnCard);
                     }
                     moveToNextPlayer(); // Move turn after drawing
                     return false; // Card was not played, but turn moved
                }

            } else {
                // Si aucune carte ne peut être jouée, piocher une carte
                System.out.println("Computer " + computer.getName() + " has no playable cards. Drawing...");
                Card drawnCard = drawCard();
                if (drawnCard != null) {
                    computer.addCard(drawnCard);
                    System.out.println("Computer " + computer.getName() + " drew: " + drawnCard);

                    // Check if the drawn card can be played immediately
                    if (drawnCard.canPlayOn(topCard)) {
                        System.out.println("Computer " + computer.getName() + " can play the drawn card!");
                        // Find the index of the drawn card (should be the last one)
                        int drawnCardIndex = computer.getHand().size() - 1;
                        if (playCard(drawnCardIndex)) {
                             System.out.println("Computer " + computer.getName() + " played the drawn card.");
                            return true; // Card played
                        } else {
                             System.err.println("Error: Computer " + computer.getName() + " failed to play the drawn card.");
                             // Turn should still pass if play failed
                             moveToNextPlayer();
                             return false; // Card not played, turn moved
                        }
                    } else {
                        // Cannot play the drawn card, turn passes
                        System.out.println("Computer " + computer.getName() + " cannot play the drawn card. Turn passes.");
                        moveToNextPlayer();
                        return false; // Card not played, turn moved
                    }
                } else {
                     System.err.println("Warning: Deck empty when computer tried to draw.");
                     // Turn should still pass if deck is empty
                     moveToNextPlayer();
                     return false; // Card not played, turn moved
                }
            }
        }
        return false; // Not a computer player's turn
    }
}