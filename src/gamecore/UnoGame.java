package gamecore;

import java.awt.Color;
import java.util.List;
import javax.swing.SwingUtilities;

import framework.core.Window;
import framework.core.Container;
import framework.widgets.Panel;
import framework.widgets.Button;
import framework.widgets.CardView;
import framework.events.EventListener;
import framework.events.MouseEvent;

public class UnoGame {
    private Window window;
    private Panel mainPanel;
    private Panel playerPanel;
    private Panel computerPanel;
    private Panel gamePanel;
    private Game game;
    private Button startButton;
    private Button drawButton;

    public UnoGame() {
        initializeUI();
        setupEventListeners();
    }

    private void initializeUI() {
        window = new Window("UNO Game", 1024, 768);

        mainPanel = new Panel("mainPanel");
        mainPanel.setBounds(0, 0, 1024, 768);
        mainPanel.setBackgroundColor(new Color(34, 139, 34));

        // Panel pour l'ordinateur (en haut)
        computerPanel = new Panel("computerPanel");
        computerPanel.setBounds(0, 0, 1024, 200);
        computerPanel.setBackgroundColor(new Color(45, 180, 45));

        // Panel central pour le jeu
        gamePanel = new Panel("gamePanel");
        gamePanel.setBounds(200, 200, 624, 368);
        gamePanel.setBackgroundColor(new Color(40, 160, 40));

        // Panel pour le joueur (en bas)
        playerPanel = new Panel("playerPanel");
        playerPanel.setBounds(0, 568, 1024, 200);
        playerPanel.setBackgroundColor(new Color(45, 180, 45));

        startButton = new Button("Nouvelle Partie");
        startButton.setBounds(462, 284, 100, 40);

        drawButton = new Button("Piocher");
        drawButton.setBounds(462, 334, 100, 40);
        drawButton.setVisible(false);

        mainPanel.addChild(computerPanel);
        mainPanel.addChild(gamePanel);
        mainPanel.addChild(playerPanel);
        mainPanel.addChild(startButton);
        mainPanel.addChild(drawButton);

        window.getRootContainer().addChild(mainPanel);
        window.show();
    }

    private void setupEventListeners() {
        startButton.addEventListener(new EventListener() {
            @Override
            public void onMouseClick(MouseEvent event) {
                startNewGame();
            }

            @Override
            public void onMousePress(MouseEvent event) {
            }

            @Override
            public void onMouseRelease(MouseEvent event) {
            }
        });

        drawButton.addEventListener(new EventListener() {
            @Override
            public void onMouseClick(MouseEvent event) {
                drawCard();
            }

            @Override
            public void onMousePress(MouseEvent event) {
            }

            @Override
            public void onMouseRelease(MouseEvent event) {
            }
        });
    }

    private void startNewGame() {
        game = new Game(1, 1);
        drawButton.setVisible(true);
        startButton.setVisible(false); // Cacher le bouton start après le début de la partie
        updateGameView();
    }

    private void drawCard() {
        Player currentPlayer = game.getCurrentPlayer();
        if (currentPlayer instanceof HumanPlayer) {
            currentPlayer.addCard(game.drawCard());
            updateGameView();
        }
    }

    private void updateGameView() {
        gamePanel.removeAll();
        playerPanel.removeAll();
        computerPanel.removeAll();

        // Afficher la carte du dessus
        Card topCard = game.getCurrentTopCard();
        if (topCard != null) {
            CardView topCardView = new CardView(topCard);
            topCardView.setBounds(272, 84, 80, 120);
            gamePanel.addChild(topCardView);
        }

        // Afficher les cartes du joueur humain
        List<Card> playerCards = null;
        for (Player player : game.getPlayers()) {
            if (player instanceof HumanPlayer) {
                playerCards = ((HumanPlayer) player).getHand();
                break;
            }
        }

        if (playerCards != null) {
            int cardWidth = 80;
            int spacing = 20;
            int totalWidth = (cardWidth + spacing) * playerCards.size() - spacing;
            int startX = (playerPanel.getWidth() - totalWidth) / 2;

            for (Card card : playerCards) {
                CardView cardView = new CardView(card);
                cardView.setBounds(startX, 40, cardWidth, 120);
                cardView.addCardClickListener(new CardView.CardClickListener() {
                    @Override
                    public void onCardClicked(CardView cardView) {
                        handleCardClick(cardView);
                    }
                });
                playerPanel.addChild(cardView);
                startX += cardWidth + spacing;
            }
        }

        // Afficher les cartes de l'ordinateur (face cachée)
        for (Player player : game.getPlayers()) {
            if (player instanceof ComputerPlayer) {
                int handSize = player.getHandSize();
                int cardWidth = 80;
                int spacing = 20;
                int totalWidth = (cardWidth + spacing) * handSize - spacing;
                int startX = (computerPanel.getWidth() - totalWidth) / 2;

                for (int i = 0; i < handSize; i++) {
                    CardView cardView = new CardView(new Card("Noir", "Hidden", 0));
                    cardView.setBounds(startX, 40, cardWidth, 120);
                    cardView.setFaceUp(false);
                    computerPanel.addChild(cardView);
                    startX += cardWidth + spacing;
                }
                break;
            }
        }

        gamePanel.revalidate();
        gamePanel.repaint();
        playerPanel.revalidate();
        playerPanel.repaint();
        computerPanel.revalidate();
        computerPanel.repaint();
    }

    private void handleCardClick(CardView cardView) {
        Player currentPlayer = game.getCurrentPlayer();
        if (currentPlayer instanceof HumanPlayer) {
            List<Card> playerCards = game.getCurrentPlayerCards();
            int cardIndex = playerCards.indexOf(cardView.getCard());
            if (cardIndex != -1 && game.playCard(cardIndex)) {
                updateGameView();
            }
        }
    }

    public static void main(String[] args) {
        new UnoGame();
    }
}
