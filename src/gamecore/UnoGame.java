package gamecore;

import framework.core.Window;
import framework.events.EventListener;
import framework.events.MouseEvent;
import framework.widgets.Button;
import framework.widgets.CardView;
import framework.widgets.Label;
import framework.widgets.Panel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class UnoGame {
    private Window window;
    private Panel mainPanel;
    private Panel menuPanel;
    private Panel playerPanel;
    private Panel computerPanel;
    private Panel gamePanel;
    private Game game;
    private Button startSoloButton;
    private Button startMultiButton;
    private Button drawButton;

    public UnoGame() {
        initializeUI();
        setupEventListeners();
    }

    private void initializeUI() {
        window = new Window("UNO Game", 1024, 768);
        System.out.println("Fenêtre créée avec succès");

        // Add window resize listener
        window.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                if (game != null) {
                    updateGameView();
                }
            }
        });

        // Menu principal
        menuPanel = new Panel("menuPanel");
        menuPanel.setBounds(0, 0, window.getWidth(), window.getHeight());
        menuPanel.setBackground(new Color(45, 45, 48));
        menuPanel.setOpaque(true);
        System.out.println("Menu panel créé et configuré");

        // Logo UNO centré
        try {
            String imagePath = System.getProperty("user.dir") + "/src/resources/images/cards/BackFace.png";
            System.out.println("Tentative de chargement de l'image depuis : " + imagePath);
            
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                System.err.println("Le fichier image n'existe pas : " + imagePath);
                return;
            }
            
            Image logoImage = ImageIO.read(imageFile);
            System.out.println("Image chargée avec succès");
            
            Panel logoPanel = new Panel();
            int logoWidth = 200;
            int logoHeight = 300;
            int centerY = (window.getHeight() - (logoHeight + 120)) / 2; // Account for buttons height
            logoPanel.setBounds((window.getWidth() - logoWidth) / 2, centerY, logoWidth, logoHeight);
            logoPanel.setOpaque(false);
            
            javax.swing.JLabel logoLabel = new javax.swing.JLabel(new ImageIcon(logoImage.getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH)));
            logoLabel.setBounds(0, 0, logoWidth, logoHeight);
            logoPanel.add(logoLabel);
            menuPanel.addChild(logoPanel);
            System.out.println("Logo ajouté au menu panel");

            // Boutons du menu
            int buttonWidth = 200;
            int buttonHeight = 45;
            int spacing = 20;
            
            startSoloButton = new Button("Play Solo");
            startSoloButton.setBounds((window.getWidth() - buttonWidth) / 2, 
                                    logoPanel.getY() + logoHeight + spacing, 
                                    buttonWidth, buttonHeight);
            startSoloButton.setBackground(new Color(52, 152, 219));
            startSoloButton.setTextColor(Color.WHITE);
            startSoloButton.setFont(new Font("Arial", Font.BOLD, 16));
            System.out.println("Bouton solo créé");

            startMultiButton = new Button("MultiPlayer");
            startMultiButton.setBounds((window.getWidth() - buttonWidth) / 2,
                                     startSoloButton.getY() + buttonHeight + spacing,
                                     buttonWidth, buttonHeight);
            startMultiButton.setBackground(new Color(231, 76, 60));
            startMultiButton.setTextColor(Color.WHITE);
            startMultiButton.setFont(new Font("Arial", Font.BOLD, 16));
            System.out.println("Bouton multi créé");

            // Add window resize listener for responsive layout
            window.addComponentListener(new java.awt.event.ComponentAdapter() {
                @Override
                public void componentResized(java.awt.event.ComponentEvent e) {
                    int newCenterY = (window.getHeight() - (logoHeight + 120)) / 2;
                    // Update logo position
                    logoPanel.setBounds((window.getWidth() - logoWidth) / 2,
                                      newCenterY,
                                      logoWidth, logoHeight);
                    
                    // Update buttons position
                    startSoloButton.setBounds((window.getWidth() - buttonWidth) / 2,
                                            logoPanel.getY() + logoHeight + spacing,
                                            buttonWidth, buttonHeight);
                    
                    startMultiButton.setBounds((window.getWidth() - buttonWidth) / 2,
                                             startSoloButton.getY() + buttonHeight + spacing,
                                             buttonWidth, buttonHeight);
                    
                    menuPanel.revalidate();
                    menuPanel.repaint();
                }
            });

            menuPanel.addChild(startSoloButton);
            menuPanel.addChild(startMultiButton);

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
            e.printStackTrace();
        }

        // Add hover effects for buttons
        startSoloButton.addEventListener(new EventListener() {
            @Override
            public void onMouseEnter(MouseEvent event) {
                startSoloButton.setBackground(new Color(41, 128, 185));
                startSoloButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            }

            @Override
            public void onMouseExit(MouseEvent event) {
                startSoloButton.setBackground(new Color(52, 152, 219));
            }

            @Override
            public void onMouseClick(MouseEvent event) {
                startNewGame(1, 1);
                showGameScreen();
            }

            @Override
            public void onMousePress(MouseEvent event) {}

            @Override
            public void onMouseRelease(MouseEvent event) {}
        });

        startMultiButton.addEventListener(new EventListener() {
            @Override
            public void onMouseEnter(MouseEvent event) {
                startMultiButton.setBackground(new Color(192, 57, 43));
                startMultiButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            }

            @Override
            public void onMouseExit(MouseEvent event) {
                startMultiButton.setBackground(new Color(231, 76, 60));
            }

            @Override
            public void onMouseClick(MouseEvent event) {
                startNewGame(2, 0);
                showGameScreen();
            }

            @Override
            public void onMousePress(MouseEvent event) {}

            @Override
            public void onMouseRelease(MouseEvent event) {}
        });

        // Main panel with responsive layout
        mainPanel = new Panel("mainPanel");
        mainPanel.setBounds(0, 0, window.getWidth(), window.getHeight());
        mainPanel.setBackground(new Color(34, 139, 34));
        mainPanel.setOpaque(true);
        mainPanel.setVisible(false);
        System.out.println("Main panel créé");

        // Computer panel (top)
        computerPanel = new Panel("computerPanel");
        computerPanel.setBounds(0, 0, window.getWidth(), window.getHeight() / 4);
        computerPanel.setBackground(new Color(45, 180, 45));
        computerPanel.setOpaque(true);
        System.out.println("Computer panel créé");

        // Game panel (center)
        gamePanel = new Panel("gamePanel");
        gamePanel.setBounds(window.getWidth() / 4, window.getHeight() / 4, 
                          window.getWidth() / 2, window.getHeight() / 2);
        gamePanel.setBackground(new Color(40, 160, 40));
        gamePanel.setOpaque(true);
        System.out.println("Game panel créé");

        // Player panel (bottom)
        playerPanel = new Panel("playerPanel");
        playerPanel.setBounds(0, window.getHeight() * 3 / 4, 
                            window.getWidth(), window.getHeight() / 4);
        playerPanel.setBackground(new Color(45, 180, 45));
        playerPanel.setOpaque(true);
        System.out.println("Player panel créé");

        // Draw button
        drawButton = new Button("Piocher");
        drawButton.setBounds(window.getWidth() / 2 - 50, window.getHeight() / 2 - 20, 100, 40);
        drawButton.setOpaque(true);
        drawButton.setVisible(false);
        System.out.println("Draw button créé");

        mainPanel.addChild(computerPanel);
        mainPanel.addChild(gamePanel);
        mainPanel.addChild(playerPanel);
        mainPanel.addChild(drawButton);
        System.out.println("Tous les panels de jeu ajoutés au main panel");

        window.getRootContainer().addChild(menuPanel);
        window.getRootContainer().addChild(mainPanel);
        System.out.println("Panels principaux ajoutés à la fenêtre");

        window.setVisible(true);
        System.out.println("Fenêtre rendue visible");
        System.out.println("Nombre de composants dans le root container : " + window.getRootContainer().getComponentCount());
        System.out.println("Nombre de composants dans le menu panel : " + menuPanel.getComponentCount());
    }

    private void setupEventListeners() {
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

            @Override
            public void onMouseEnter(MouseEvent event) {
            }

            @Override
            public void onMouseExit(MouseEvent event) {
            }
        });
    }

    private void showGameScreen() {
        menuPanel.setVisible(false);
        mainPanel.setVisible(true);
    }

    private void startNewGame(int humanPlayers, int computerPlayers) {
        game = new Game(humanPlayers, computerPlayers);
        showGameScreen();
        drawButton.setVisible(true);
        updateGameView();
        
        // Vérifier si c'est le tour de l'ordinateur au début du jeu
        if (game.getCurrentPlayer() instanceof ComputerPlayer) {
            playComputerTurn();
        }
    }

    private void drawCard() {
        Player currentPlayer = game.getCurrentPlayer();
        if (currentPlayer instanceof HumanPlayer) {
            // Le joueur humain pioche une carte
            Card drawnCard = game.drawCard();
            if (drawnCard != null) {
                currentPlayer.addCard(drawnCard);
                updateGameView();
                
                // Passer au tour suivant
                game.moveToNextPlayer();
                
                // Si c'est maintenant le tour de l'ordinateur, le faire jouer
                if (game.getCurrentPlayer() instanceof ComputerPlayer) {
                    playComputerTurn();
                }
            } else {
                // Si le deck est vide, afficher un message
                javax.swing.JOptionPane.showMessageDialog(window, 
                    "Le deck est vide!", 
                    "Pioche impossible", 
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void updateGameView() {
        // Calculate responsive card sizes based on panel size
        int cardWidth = Math.max(60, gamePanel.getWidth() / 8);
        int cardHeight = (int) (cardWidth * 1.5);

        gamePanel.removeAll();
        playerPanel.removeAll();
        computerPanel.removeAll();

        // Display top card
        Card topCard = game.getCurrentTopCard();
        if (topCard != null) {
            CardView topCardView = new CardView(topCard);
            int topCardX = (gamePanel.getWidth() - cardWidth) / 2;
            int topCardY = (gamePanel.getHeight() - cardHeight) / 2;
            topCardView.setBounds(topCardX, topCardY, cardWidth, cardHeight);
            topCardView.setFaceUp(true);
            gamePanel.addChild(topCardView);
        }

        // Add draw button to game panel
        drawButton.setBounds(gamePanel.getWidth() / 2 + cardWidth, gamePanel.getHeight() / 2, 100, 40);
        drawButton.setVisible(game.getCurrentPlayer() instanceof HumanPlayer);
        gamePanel.addChild(drawButton);

        // Display player cards
        List<Card> playerCards = null;
        String playerName = "";
        for (Player player : game.getPlayers()) {
            if (player instanceof HumanPlayer) {
                playerCards = ((HumanPlayer) player).getHand();
                playerName = player.getName();
                break;
            }
        }

        if (playerCards != null) {
            // Add player name label
            Label nameLabel = new Label(playerName);
            nameLabel.setTextColor(Color.WHITE);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
            nameLabel.setBounds(10, 10, 200, 20);
            playerPanel.addChild(nameLabel);

            int spacing = Math.max(10, cardWidth / 8);
            int totalWidth = (cardWidth + spacing) * playerCards.size() - spacing;
            int startX = (playerPanel.getWidth() - totalWidth) / 2;
            int startY = (playerPanel.getHeight() - cardHeight) / 2; // Center vertically

            for (Card card : playerCards) {
                CardView cardView = new CardView(card);
                cardView.setBounds(startX, startY, cardWidth, cardHeight);
                cardView.setFaceUp(true);
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

        // Display computer cards (face down)
        for (Player player : game.getPlayers()) {
            if (player instanceof ComputerPlayer) {
                // Add computer name label
                Label nameLabel = new Label(player.getName());
                nameLabel.setTextColor(Color.WHITE);
                nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
                nameLabel.setBounds(10, 10, 200, 20);
                computerPanel.addChild(nameLabel);

                int handSize = player.getHandSize();
                int spacing = Math.max(10, cardWidth / 8);
                int totalWidth = (cardWidth + spacing) * handSize - spacing;
                int startX = (computerPanel.getWidth() - totalWidth) / 2;
                int startY = (computerPanel.getHeight() - cardHeight) / 2; // Center vertically

                for (int i = 0; i < handSize; i++) {
                    CardView cardView = new CardView(new Card("Noir", "Hidden", 0));
                    cardView.setBounds(startX, startY, cardWidth, cardHeight);
                    cardView.setFaceUp(false);
                    computerPanel.addChild(cardView);
                    startX += cardWidth + spacing;
                }
                break;
            }
        }

        if (game.isGameOver()) {
            announceWinner();
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
                // Mettre à jour l'interface après avoir joué la carte
                updateGameView();
                
                // Réduire le délai pour les cartes spéciales à 500ms
                Card playedCard = game.getCurrentTopCard();
                if (playedCard.getType().equals("Draw Two") || playedCard.getType().equals("Wild Draw Four")) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Si c'est maintenant le tour de l'ordinateur, le faire jouer
                if (game.getCurrentPlayer() instanceof ComputerPlayer) {
                    playComputerTurn();
                }
            }
        }
    }

    private void playComputerTurn() {
        Player currentPlayer = game.getCurrentPlayer();
        if (currentPlayer instanceof ComputerPlayer) {
            // Réduire le délai à 500ms
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }

            // Laisser l'ordinateur jouer son tour
            boolean played = game.playComputerCard();
            
            if (!played) {
                // Si l'ordinateur ne peut pas jouer, il pioche une carte
                currentPlayer.addCard(game.drawCard());
                // Mettre à jour l'interface
                updateGameView();
                // Passer au joueur suivant
                game.moveToNextPlayer();
            } else {
                // Mettre à jour l'interface après avoir joué
                updateGameView();
                // Passer au joueur suivant après que l'ordinateur a joué
                game.moveToNextPlayer();
            }
            
            System.out.println("Tour de l'ordinateur terminé");
        }
    }

    private void announceWinner() {
        String winner = null;
        for (Player player : game.getPlayers()) {
            if (player.getHandSize() == 0) {
                winner = player.getName();
                break;
            }
        }
        if (winner != null) {
            javax.swing.JOptionPane.showMessageDialog(window, 
                winner + " a gagné la partie!", 
                "Fin de la partie", 
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new UnoGame();
    }
}
