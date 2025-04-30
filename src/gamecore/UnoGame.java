package gamecore;

import framework.core.Window;
import framework.events.EventListener;
import framework.events.MouseEvent;
import framework.widgets.Button;
import framework.widgets.CardView;
import framework.widgets.Label;
import framework.widgets.Panel;
import framework.widgets.TextField;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    private Panel playerSelectionPanel;
    private List<TextField> playerNameFields;
    private Button startGameButton;
    private Button addPlayerButton;
    private Button removePlayerButton;
    private int maxPlayers = 4;

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
                updatePanelLayouts();
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
            startSoloButton.setBackground(new Color(240, 240, 240)); // Elegant white-grey
            startSoloButton.setTextColor(new Color(50, 50, 50)); // Dark grey text
            startSoloButton.setFont(new Font("Arial", Font.BOLD, 16));
            System.out.println("Bouton solo créé");

            startMultiButton = new Button("MultiPlayer");
            startMultiButton.setBounds((window.getWidth() - buttonWidth) / 2,
                                     startSoloButton.getY() + buttonHeight + spacing,
                                     buttonWidth, buttonHeight);
            startMultiButton.setBackground(new Color(240, 240, 240)); // Elegant white-grey
            startMultiButton.setTextColor(new Color(50, 50, 50)); // Dark grey text
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
                showPlayerSelectionScreen();
            }

            @Override
            public void onMousePress(MouseEvent event) {}

            @Override
            public void onMouseRelease(MouseEvent event) {}
        });

        // Main panel with responsive layout
        mainPanel = new Panel("mainPanel");
        mainPanel.setBounds(0, 0, window.getWidth(), window.getHeight());
        mainPanel.setBackground(new Color(0, 100, 0));
        mainPanel.setOpaque(true);
        mainPanel.setVisible(false);
        System.out.println("Main panel créé");

        // Computer panel (top) - 25% of height
        computerPanel = new Panel("computerPanel");
        computerPanel.setBounds(0, 0, window.getWidth(), window.getHeight() / 4);
        computerPanel.setBackground(new Color(0, 100, 0));
        computerPanel.setOpaque(true);
        System.out.println("Computer panel créé");

        // Game panel (center) - 50% of height, wider side margins
        gamePanel = new Panel("gamePanel");
        gamePanel.setBounds(window.getWidth() / 6, window.getHeight() / 4, 
                          window.getWidth() * 2/3, window.getHeight() / 2);
        gamePanel.setBackground(new Color(0, 100, 0));
        gamePanel.setOpaque(true);
        System.out.println("Game panel créé");

        // Player panel (bottom) - 25% of height
        playerPanel = new Panel("playerPanel");
        playerPanel.setBounds(0, window.getHeight() * 3/4, 
                            window.getWidth(), window.getHeight() / 4);
        playerPanel.setBackground(new Color(0, 100, 0));
        playerPanel.setOpaque(true);
        System.out.println("Player panel créé");

        // Draw button - positioned relative to game panel
        drawButton = new Button("Piocher");
        drawButton.setBounds(gamePanel.getWidth() / 2 - 50, gamePanel.getHeight() / 2, 100, 40);
        drawButton.setOpaque(true);
        drawButton.setVisible(false);
        System.out.println("Draw button créé");

        mainPanel.addChild(computerPanel);
        mainPanel.addChild(gamePanel);
        mainPanel.addChild(playerPanel);
        mainPanel.addChild(drawButton);
        System.out.println("Tous les panels de jeu ajoutés au main panel");

        // Initialize player selection panel
        playerSelectionPanel = new Panel("playerSelectionPanel");
        playerSelectionPanel.setBounds(0, 0, window.getWidth(), window.getHeight());
        playerSelectionPanel.setBackground(new Color(0, 100, 0));
        playerSelectionPanel.setOpaque(true);
        playerSelectionPanel.setVisible(false);

        // Add title label
        Label titleLabel = new Label("Player Setup");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setTextColor(Color.WHITE);
        titleLabel.setBounds(window.getWidth() / 2 - 100, 50, 200, 30);
        playerSelectionPanel.addChild(titleLabel);

        // Initialize buttons with new colors and styles
        startGameButton = new Button("Start Game");
        startGameButton.setBounds(window.getWidth() / 2 + 10, window.getHeight() - 100, 150, 40);
        startGameButton.setBackground(new Color(46, 204, 113)); // Green color
        startGameButton.setTextColor(Color.WHITE);

        addPlayerButton = new Button("Add Player");
        addPlayerButton.setBounds(window.getWidth() / 2 - 160, window.getHeight() - 100, 150, 40);
        addPlayerButton.setBackground(new Color(52, 152, 219)); // Blue color
        addPlayerButton.setTextColor(Color.WHITE);

        removePlayerButton = new Button("Remove Player");
        removePlayerButton.setBounds(window.getWidth() / 2 - 160, window.getHeight() - 150, 150, 40);
        removePlayerButton.setBackground(new Color(231, 76, 60)); // Red color
        removePlayerButton.setTextColor(Color.WHITE);

        // Add event listeners for all buttons
        startGameButton.addEventListener(new EventListener() {
            @Override
            public void onMouseClick(MouseEvent event) {
                System.out.println("Start Game button clicked!");
                startMultiplayerGame();
            }
            @Override public void onMousePress(MouseEvent event) {}
            @Override public void onMouseRelease(MouseEvent event) {}
            @Override public void onMouseEnter(MouseEvent event) {
                startGameButton.setBackground(new Color(39, 174, 96)); // Darker green
            }
            @Override public void onMouseExit(MouseEvent event) {
                startGameButton.setBackground(new Color(46, 204, 113)); // Original green
            }
        });

        addPlayerButton.addEventListener(new EventListener() {
            @Override
            public void onMouseClick(MouseEvent event) {
                System.out.println("Add Player button clicked!");
                addPlayerField();
            }
            @Override public void onMousePress(MouseEvent event) {}
            @Override public void onMouseRelease(MouseEvent event) {}
            @Override public void onMouseEnter(MouseEvent event) {
                addPlayerButton.setBackground(new Color(41, 128, 185)); // Darker blue
            }
            @Override public void onMouseExit(MouseEvent event) {
                addPlayerButton.setBackground(new Color(52, 152, 219)); // Original blue
            }
        });

        removePlayerButton.addEventListener(new EventListener() {
            @Override
            public void onMouseClick(MouseEvent event) {
                System.out.println("Remove Player button clicked!");
                removePlayerField();
            }
            @Override public void onMousePress(MouseEvent event) {}
            @Override public void onMouseRelease(MouseEvent event) {}
            @Override public void onMouseEnter(MouseEvent event) {
                removePlayerButton.setBackground(new Color(192, 57, 43)); // Darker red
            }
            @Override public void onMouseExit(MouseEvent event) {
                removePlayerButton.setBackground(new Color(231, 76, 60)); // Original red
            }
        });

        // Add buttons to panel
        playerSelectionPanel.addChild(startGameButton);
        playerSelectionPanel.addChild(addPlayerButton);
        playerSelectionPanel.addChild(removePlayerButton);

        // Add panels to window
        window.getRootContainer().addChild(menuPanel);
        window.getRootContainer().addChild(playerSelectionPanel);
        window.getRootContainer().addChild(mainPanel);

        // Initialize player name fields list
        playerNameFields = new ArrayList<>();

        // Add initial player fields
        addPlayerField();
        addPlayerField();

        System.out.println("UI initialization complete");

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
        playerSelectionPanel.setVisible(false);
        mainPanel.setVisible(true);
        drawButton.setVisible(true);
    }

    private void startNewGame(int humanPlayers, int computerPlayers) {
        System.out.println("Starting new game with " + humanPlayers + " humans and " + computerPlayers + " computers");
        game = new Game(humanPlayers, computerPlayers);
        game.initializeGame();
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

    private void updatePanelLayouts() {
        if (mainPanel != null) {
            // Update main panel
            mainPanel.setBounds(0, 0, window.getWidth(), window.getHeight());

            // Update computer panel (25% of height, increased from 20%)
            computerPanel.setBounds(0, 0, window.getWidth(), window.getHeight() * 1/4);

            // Update game panel (50% of height, reduced from 60%)
            // and increase side margins (from 1/8 to 1/6 of width)
            gamePanel.setBounds(window.getWidth() / 6, window.getHeight() / 4,
                              window.getWidth() * 2/3, window.getHeight() / 2);

            // Update player panel (25% of height, increased from 20%)
            playerPanel.setBounds(0, window.getHeight() * 3/4,
                                window.getWidth(), window.getHeight() / 4);

            // Update draw button position
            if (drawButton.isVisible()) {
                drawButton.setBounds(gamePanel.getWidth() / 2 - 50, gamePanel.getHeight() / 2, 100, 40);
            }

            // Force repaint of all panels
            mainPanel.revalidate();
            mainPanel.repaint();
            computerPanel.revalidate();
            computerPanel.repaint();
            gamePanel.revalidate();
            gamePanel.repaint();
            playerPanel.revalidate();
            playerPanel.repaint();
        }
    }

    private void updateGameView() {
        System.out.println("=== Updating Game View ===");
        try {
            // Clear all panels
            gamePanel.removeAll();
            playerPanel.removeAll();
            computerPanel.removeAll();

            if (game == null) {
                System.err.println("ERROR: Game is null!");
                return;
            }

            // Display top card
            Card topCard = game.getCurrentTopCard();
            if (topCard != null) {
                System.out.println("Displaying top card: " + topCard);
                CardView topCardView = new CardView(topCard);
                int cardWidth = 100;
                int cardHeight = 150;
                int topCardX = (gamePanel.getWidth() - cardWidth) / 2;
                int topCardY = (gamePanel.getHeight() - cardHeight) / 2;
                topCardView.setBounds(topCardX, topCardY, cardWidth, cardHeight);
                topCardView.setFaceUp(true);
                gamePanel.addChild(topCardView);
            }

            // Add draw button to game panel
            drawButton.setBounds(gamePanel.getWidth() / 2 + 100, gamePanel.getHeight() / 2, 100, 40);
            drawButton.setVisible(game.getCurrentPlayer() instanceof HumanPlayer);
            gamePanel.addChild(drawButton);

            // Display players and their cards
            List<Player> players = game.getPlayers();
            if (players == null || players.isEmpty()) {
                System.err.println("ERROR: No players in game!");
                return;
            }

            for (Player player : players) {
                Panel targetPanel = player instanceof HumanPlayer ? playerPanel : computerPanel;
                
                // Add player name with visual effect for current player
                Label nameLabel = new Label(player.getName());
                nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
                
                // Highlight current player's name
                if (player == game.getCurrentPlayer()) {
                    nameLabel.setTextColor(new Color(255, 255, 0)); // Yellow color for current player
                    nameLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Slightly larger font
                } else {
                    nameLabel.setTextColor(Color.WHITE);
                }
                
                nameLabel.setBounds(10, 10, 200, 20);
                targetPanel.addChild(nameLabel);

                // Display cards
                List<Card> cards = player.getHand();
                
                // Calculate card layout
                int cardWidth = 80;
                int cardHeight = 120;
                int spacing = 30;
                int totalWidth = (cards.size() * cardWidth) - ((cards.size() - 1) * spacing);
                int startX = (targetPanel.getWidth() - totalWidth) / 2;
                int startY = (targetPanel.getHeight() - cardHeight) / 2;

                for (int i = 0; i < cards.size(); i++) {
                    Card card = cards.get(i);
                    if (card == null) continue;

                    CardView cardView = new CardView(card);
                    cardView.setBounds(
                        startX + (i * (cardWidth - spacing)),
                        startY,
                        cardWidth,
                        cardHeight
                    );
                    // Afficher les cartes face cachée pour l'ordinateur
                    cardView.setFaceUp(player instanceof HumanPlayer);
                    
                    if (player instanceof HumanPlayer) {
                        final int cardIndex = i;
                        cardView.addCardClickListener(new CardView.CardClickListener() {
                            @Override
                            public void onCardClicked(CardView cv) {
                                handleCardClick(cv);
                            }
                        });
                    }
                    
                    targetPanel.addChild(cardView);
                }
            }

            // Force repaint
            mainPanel.revalidate();
            mainPanel.repaint();
            gamePanel.revalidate();
            gamePanel.repaint();
            playerPanel.revalidate();
            playerPanel.repaint();
            computerPanel.revalidate();
            computerPanel.repaint();
            
        } catch (Exception e) {
            System.err.println("ERROR: Failed to update game view");
            e.printStackTrace();
        }
    }

    private void handleCardClick(CardView cardView) {
        Player currentPlayer = game.getCurrentPlayer();
        if (currentPlayer instanceof HumanPlayer) {
            List<Card> playerCards = currentPlayer.getHand();
            int cardIndex = playerCards.indexOf(cardView.getCard());
            
            if (cardIndex != -1) {
                Card selectedCard = playerCards.get(cardIndex);
                Card topCard = game.getCurrentTopCard();
                
                if (selectedCard.canPlayOn(topCard)) {
                    if (game.playCard(cardIndex)) {
                        // Mettre à jour l'affichage après avoir joué la carte
                        updateGameView();
                        
                        // Vérifier si le jeu est terminé
                        if (game.isGameOver()) {
                            announceWinner();
                            return;
                        }
                        
                        // Forcer le tour de l'ordinateur
                        if (game.getCurrentPlayer() instanceof ComputerPlayer) {
                            // Utiliser SwingUtilities.invokeLater pour éviter le blocage de l'interface
                            javax.swing.SwingUtilities.invokeLater(() -> {
                                try {
                                    Thread.sleep(1000); // Délai pour voir l'action
                                    playComputerTurn();
                                    if (game.isGameOver()) {
                                        announceWinner();
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    }
                } else {
                    javax.swing.JOptionPane.showMessageDialog(
                        window,
                        "Carte invalide! Vous ne pouvez pas jouer cette carte.",
                        "Carte invalide",
                        javax.swing.JOptionPane.WARNING_MESSAGE
                    );
                }
            }
        }
    }

    private void playComputerTurn() {
        Player currentPlayer = game.getCurrentPlayer();
        if (currentPlayer instanceof ComputerPlayer) {
            System.out.println("Tour de l'ordinateur: " + currentPlayer.getName());
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
            javax.swing.JOptionPane.showMessageDialog(
                window,
                winner + " a gagné la partie!",
                "Fin de la partie",
                javax.swing.JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    private void addPlayerField() {
        if (playerNameFields.size() >= maxPlayers) {
            return;
        }

        int index = playerNameFields.size();
        
        // Create a panel for each player's settings
        Panel playerSettingsPanel = new Panel();
        playerSettingsPanel.setBounds(
            window.getWidth() / 2 - 200,
            150 + index * 100,
            400,
            80
        );

        // Add player name field
        TextField field = new TextField("Player " + (index + 1));
        field.setBounds(0, 0, 200, 40);
        playerNameFields.add(field);
        playerSettingsPanel.addChild(field);

        // Add player type selection
        Button humanButton = new Button("Human");
        humanButton.setBounds(210, 0, 90, 40);
        humanButton.setBackground(new Color(52, 152, 219));
        humanButton.setTextColor(Color.WHITE);
        humanButton.setSelected(true);

        Button computerButton = new Button("Computer");
        computerButton.setBounds(310, 0, 90, 40);
        computerButton.setBackground(new Color(231, 76, 60));
        computerButton.setTextColor(Color.WHITE);

        // Add click listeners to handle selection
        humanButton.addEventListener(new EventListener() {
            @Override
            public void onMouseClick(MouseEvent event) {
                humanButton.setSelected(true);
                computerButton.setSelected(false);
            }
            @Override public void onMousePress(MouseEvent event) {}
            @Override public void onMouseRelease(MouseEvent event) {}
            @Override public void onMouseEnter(MouseEvent event) {}
            @Override public void onMouseExit(MouseEvent event) {}
        });

        computerButton.addEventListener(new EventListener() {
            @Override
            public void onMouseClick(MouseEvent event) {
                computerButton.setSelected(true);
                humanButton.setSelected(false);
            }
            @Override public void onMousePress(MouseEvent event) {}
            @Override public void onMouseRelease(MouseEvent event) {}
            @Override public void onMouseEnter(MouseEvent event) {}
            @Override public void onMouseExit(MouseEvent event) {}
        });

        playerSettingsPanel.addChild(humanButton);
        playerSettingsPanel.addChild(computerButton);

        playerSelectionPanel.addChild(playerSettingsPanel);
        playerSelectionPanel.revalidate();
        playerSelectionPanel.repaint();

        updatePlayerButtons();
    }

    private void removePlayerField() {
        if (playerNameFields.size() <= 2) {
            return;
        }

        // Remove the last player settings panel
        Panel lastPanel = (Panel) playerSelectionPanel.getChildren().get(playerSelectionPanel.getChildren().size() - 1);
        playerSelectionPanel.removeChild(lastPanel);
        
        // Remove the last name field
        playerNameFields.remove(playerNameFields.size() - 1);
        
        playerSelectionPanel.revalidate();
        playerSelectionPanel.repaint();

        updatePlayerButtons();
    }

    private void updatePlayerButtons() {
        addPlayerButton.setEnabled(playerNameFields.size() < maxPlayers);
        removePlayerButton.setEnabled(playerNameFields.size() > 2);
    }

    private void startMultiplayerGame() {
        System.out.println("=== Starting Multiplayer Game ===");
        System.out.println("Current game state: " + (game == null ? "null" : "initialized"));
        
        if (playerNameFields == null || playerNameFields.isEmpty()) {
            System.err.println("ERROR: No player fields found!");
            return;
        }

        try {
            // Get player information
            List<String> playerNames = new ArrayList<>();
            List<Boolean> isHuman = new ArrayList<>();

            System.out.println("Processing " + playerNameFields.size() + " player fields");
            for (int i = 0; i < playerNameFields.size(); i++) {
                TextField field = playerNameFields.get(i);
                String name = field.getText().trim();
                if (name.isEmpty()) {
                    name = field.getPlaceholder();
                }
                playerNames.add(name);
                System.out.println("Player " + (i + 1) + " name: " + name);

                try {
                    Panel playerSettingsPanel = (Panel) playerSelectionPanel.getChildren().get(i + 1);
                    Button humanButton = (Button) playerSettingsPanel.getChildren().get(1);
                    boolean isHumanPlayer = humanButton.isSelected();
                    isHuman.add(isHumanPlayer);
                    System.out.println("Player " + (i + 1) + " type: " + (isHumanPlayer ? "Human" : "Computer"));
                } catch (Exception e) {
                    System.err.println("ERROR: Failed to get player type for player " + (i + 1));
                    e.printStackTrace();
                    return;
                }
            }

            // Create and initialize game
            int humanCount = (int) isHuman.stream().filter(b -> b).count();
            int computerCount = isHuman.size() - humanCount;
            System.out.println("Creating new game with " + humanCount + " humans and " + computerCount + " computers");

            game = new Game(humanCount, computerCount);
            System.out.println("Game instance created: " + game);

            // Set up players with their names
            List<Player> players = game.getPlayers();
            int humanIndex = 0;
            int computerIndex = humanCount;
            for (int i = 0; i < playerNames.size(); i++) {
                try {
                    if (isHuman.get(i)) {
                        System.out.println("Setting up human player " + humanIndex + ": " + playerNames.get(i));
                        players.set(humanIndex, new HumanPlayer(playerNames.get(i)));
                        humanIndex++;
                    } else {
                        System.out.println("Setting up computer player " + computerIndex + ": " + playerNames.get(i));
                        players.set(computerIndex, new ComputerPlayer(playerNames.get(i)));
                        computerIndex++;
                    }
                } catch (Exception e) {
                    System.err.println("ERROR: Failed to set up player " + i);
                    e.printStackTrace();
                    return;
                }
            }

            // Initialize game and deal cards
            System.out.println("Initializing game...");
            game.initializeGame();
            System.out.println("Game initialized");

            // Update UI visibility
            System.out.println("Updating UI visibility...");
            menuPanel.setVisible(false);
            playerSelectionPanel.setVisible(false);
            mainPanel.setVisible(true);
            drawButton.setVisible(true);

            // Update game view to show cards
            System.out.println("Updating game view...");
            updateGameView();

            // Force window update
            System.out.println("Forcing window update...");
            window.revalidate();
            window.repaint();
            System.out.println("=== Game Started Successfully ===");

        } catch (Exception e) {
            System.err.println("ERROR: Failed to start game");
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(
                window,
                "Failed to start game: " + e.getMessage(),
                "Error",
                javax.swing.JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void showPlayerSelectionScreen() {
        System.out.println("Showing player selection screen");
        System.out.println("Current panel visibility:");
        System.out.println("Menu Panel visible: " + menuPanel.isVisible());
        System.out.println("Player Selection Panel visible: " + playerSelectionPanel.isVisible());
        System.out.println("Main Panel visible: " + mainPanel.isVisible());

        menuPanel.setVisible(false);
        playerSelectionPanel.setVisible(true);
        mainPanel.setVisible(false);

        System.out.println("After visibility update:");
        System.out.println("Menu Panel visible: " + menuPanel.isVisible());
        System.out.println("Player Selection Panel visible: " + playerSelectionPanel.isVisible());
        System.out.println("Main Panel visible: " + mainPanel.isVisible());

        // Make sure buttons are properly positioned and visible
        startGameButton.setBounds(window.getWidth() / 2 + 10, window.getHeight() - 100, 150, 40);
        addPlayerButton.setBounds(window.getWidth() / 2 - 160, window.getHeight() - 100, 150, 40);
        removePlayerButton.setBounds(window.getWidth() / 2 - 160, window.getHeight() - 150, 150, 40);

        // Make sure buttons are visible
        startGameButton.setVisible(true);
        addPlayerButton.setVisible(true);
        removePlayerButton.setVisible(true);

        playerSelectionPanel.revalidate();
        playerSelectionPanel.repaint();
        window.revalidate();
        window.repaint();
        System.out.println("Player selection screen setup complete");
    }

    public static void main(String[] args) {
        new UnoGame();
    }
}
