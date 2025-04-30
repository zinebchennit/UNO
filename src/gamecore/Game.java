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
        System.out.println("Creating new game with " + humanPlayers + " human players and " + computerPlayers + " computer players");
        this.deck = new Deck();
        this.players = new ArrayList<>();

        // Initialize players
        for (int i = 0; i < humanPlayers; i++) {
            players.add(new HumanPlayer("Human " + (i + 1)));
            System.out.println("Added human player " + (i + 1));
        }
        for (int i = 0; i < computerPlayers; i++) {
            players.add(new ComputerPlayer("Computer " + (i + 1)));
            System.out.println("Added computer player " + (i + 1));
        }

        System.out.println("Total players added: " + players.size());

        // Deal initial cards to all players first
        dealInitialCards();
        System.out.println("Initial cards dealt to players");

        // Then set the top card
        do {
            topCard = deck.drawCard();
            System.out.println("Drew initial top card: " + topCard);
            if (topCard != null && (topCard.getType().equals("Wild") || topCard.getType().equals("Wild Draw Four"))) {
                System.out.println("Redrawing top card (was special card)");
                deck.discard(topCard);
            }
        } while (topCard != null && (topCard.getType().equals("Wild") || topCard.getType().equals("Wild Draw Four")));
        
        System.out.println("Game initialized with top card: " + topCard);
    }

    public void initializeGame() {
        System.out.println("Initializing game...");
        // Deal initial cards to all players
        dealInitialCards();
        System.out.println("Initial cards dealt");
        
        // Make sure top card is valid
        if (topCard == null || topCard.getType().equals("Wild") || topCard.getType().equals("Wild Draw Four")) {
            System.out.println("Top card needs to be redrawn");
            do {
                if (topCard != null) {
                    deck.discard(topCard);
                }
                topCard = deck.drawCard();
                System.out.println("Drew new top card: " + topCard);
            } while (topCard != null && (topCard.getType().equals("Wild") || topCard.getType().equals("Wild Draw Four")));
        }
        System.out.println("Game initialization complete. Top card: " + topCard);
    }

    public void dealInitialCards() {
        final int INITIAL_CARDS = 7; // Constante pour le nombre de cartes initial
        System.out.println("Dealing " + INITIAL_CARDS + " cards to each player");
        
        for (Player player : players) {
            System.out.println("Dealing to " + player.getName());
            // Vider la main du joueur d'abord
            while (!player.getHand().isEmpty()) {
                Card card = player.getHand().remove(0);
                deck.discard(card);
            }
            // Distribuer exactement 7 cartes
            for (int i = 0; i < INITIAL_CARDS; i++) {
                Card card = deck.drawCard();
                if (card != null) {
                    player.addCard(card);
                    System.out.println("  Dealt: " + card + " to " + player.getName());
                } else {
                    System.err.println("Warning: Could not draw card for " + player.getName());
                }
            }
            System.out.println(player.getName() + " has " + player.getHandSize() + " cards");
        }
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
            HumanPlayer humanPlayer = (HumanPlayer) currentPlayer;
            Card card = humanPlayer.getCard(cardIndex);
            
            if (card != null && card.canPlayOn(topCard)) {
                // Gérer les cartes spéciales
                if (card.getType().equals("Wild") || card.getType().equals("Wild Draw Four")) {
                    String[] colors = {"Rouge", "Bleu", "Vert", "Jaune"};
                    String newColor = (String) javax.swing.JOptionPane.showInputDialog(
                        null,
                        "Choisissez une couleur:",
                        "Changer de couleur",
                        javax.swing.JOptionPane.QUESTION_MESSAGE,
                        null,
                        colors,
                        colors[0]
                    );
                    
                    if (newColor != null) {
                        card.setColor(newColor);
                    }
                }
                
                // Jouer la carte
                humanPlayer.removeCard(cardIndex);
                deck.discard(topCard); // Mettre l'ancienne carte dans la défausse
                topCard = card;        // Mettre la nouvelle carte comme carte du dessus
                
                // Gérer les effets de la carte
                handleCardEffect(card);
                
                // Ne pas passer au joueur suivant ici, car handleCardEffect s'en charge déjà
                // pour les cartes spéciales
                if (card.getType().equals("Number")) {
                    moveToNextPlayer();
                }
                
                return true;
            }
        }
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
                moveToNextPlayer(); // Skip the next player
                break;
                
            case "Reverse":
                System.out.println("Reversing direction");
                isClockwise = !isClockwise;
                if (players.size() == 2) {
                    moveToNextPlayer(); // In 2-player game, reverse acts like a skip
                }
                break;
                
            case "Draw Two":
                System.out.println("Next player must draw 2 cards");
                moveToNextPlayer(); // Move to the player who will draw
                Player nextPlayer = getCurrentPlayer();
                for (int i = 0; i < 2; i++) {
                    Card drawnCard = deck.drawCard();
                    if (drawnCard != null) {
                        nextPlayer.addCard(drawnCard);
                    }
                }
                moveToNextPlayer(); // Skip the player who drew cards
                break;
                
            case "Wild Draw Four":
                System.out.println("Next player must draw 4 cards");
                moveToNextPlayer(); // Move to the player who will draw
                Player wildDrawPlayer = getCurrentPlayer();
                for (int i = 0; i < 4; i++) {
                    Card drawnCard = deck.drawCard();
                    if (drawnCard != null) {
                        wildDrawPlayer.addCard(drawnCard);
                    }
                }
                moveToNextPlayer(); // Skip the player who drew cards
                break;
                
            case "Wild":
                // La couleur a déjà été choisie, pas besoin d'action supplémentaire
                break;
                
            default:
                // Carte normale, pas d'effet spécial
                break;
        }
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
            ComputerPlayer computer = (ComputerPlayer) currentPlayer;
            List<Card> hand = computer.getHand();
            
            // Chercher une carte jouable
            for (int i = 0; i < hand.size(); i++) {
                Card card = hand.get(i);
                if (card.canPlayOn(topCard)) {
                    // Si c'est une carte Wild, choisir une couleur
                    if (card.getType().equals("Wild") || card.getType().equals("Wild Draw Four")) {
                        String newColor = computer.chooseColor();
                        card.setColor(newColor);
                    }
                    
                    // Jouer la carte
                    computer.removeCard(i);
                    deck.discard(topCard);
                    topCard = card;
                    
                    // Gérer les effets de la carte
                    handleCardEffect(card);
                    
                    // Ne pas passer au joueur suivant ici, car handleCardEffect s'en charge déjà
                    // pour les cartes spéciales
                    if (card.getType().equals("Number")) {
                        moveToNextPlayer();
                    }
                    
                    return true;
                }
            }
            
            // Si aucune carte ne peut être jouée, piocher une carte
            Card drawnCard = drawCard();
            if (drawnCard != null) {
                computer.addCard(drawnCard);
                // Passer au joueur suivant après avoir pioché
                moveToNextPlayer();
            }
        }
        return false;
    }
}