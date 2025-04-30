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
    private Panel playerPanel; // South
    private Panel computerPanel; // North (or opponent 1)
    private Panel leftPlayerPanel; // West (opponent 2)
    private Panel rightPlayerPanel; // East (opponent 3)
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
    private boolean hasDrawnThisTurn = false;
    private Button passButton = new Button("Passer");

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
                updatePanelLayouts(); // Update layouts on resize
                updateGameView(); // Redraw game elements after resize
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
                System.err.println("Erreur : Fichier image non trouvé à " + imagePath);
                return; // Stop if logo is missing
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
                    // Reposition logo and buttons on resize
                    int newWidth = window.getWidth();
                    int newHeight = window.getHeight();
                    menuPanel.setBounds(0, 0, newWidth, newHeight); // Resize menu panel

                    int newCenterY = (newHeight - (logoHeight + 120)) / 2;
                    logoPanel.setBounds((newWidth - logoWidth) / 2, newCenterY, logoWidth, logoHeight);

                    startSoloButton.setBounds((newWidth - buttonWidth) / 2,
                                            logoPanel.getY() + logoHeight + spacing,
                                            buttonWidth, buttonHeight);
                    startMultiButton.setBounds((newWidth - buttonWidth) / 2,
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
                startSoloButton.setBackground(new Color(220, 220, 220)); // Slightly darker on hover
            }

            @Override
            public void onMouseExit(MouseEvent event) {
                startSoloButton.setBackground(new Color(240, 240, 240)); // Back to original
            }

            @Override
            public void onMouseClick(MouseEvent event) {
                System.out.println("Solo button clicked");
                startNewGame();
            }

            @Override
            public void onMousePress(MouseEvent event) {}

            @Override
            public void onMouseRelease(MouseEvent event) {}
        });

        startMultiButton.addEventListener(new EventListener() {
            @Override
            public void onMouseEnter(MouseEvent event) {
                startMultiButton.setBackground(new Color(220, 220, 220)); // Slightly darker on hover
            }

            @Override
            public void onMouseExit(MouseEvent event) {
                startMultiButton.setBackground(new Color(240, 240, 240)); // Back to original
            }

            @Override
            public void onMouseClick(MouseEvent event) {
                System.out.println("Multiplayer button clicked");
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

        // Define panel proportions
        int playerPanelHeight = window.getHeight() / 4; // North and South panels height
        int sidePanelWidth = window.getWidth() / 6; // West and East panels width
        int gamePanelY = playerPanelHeight;
        int gamePanelHeight = window.getHeight() - (2 * playerPanelHeight);
        int gamePanelX = sidePanelWidth;
        int gamePanelWidth = window.getWidth() - (2 * sidePanelWidth);


        // Player panel (South) - Bottom 25%
        playerPanel = new Panel("playerPanel");
        playerPanel.setBounds(0, window.getHeight() - playerPanelHeight, window.getWidth(), playerPanelHeight);
        playerPanel.setBackground(new Color(0, 80, 0)); // Slightly different shade
        playerPanel.setOpaque(true);
        System.out.println("Player panel (South) créé");

        // Computer panel (North) - Top 25%
        computerPanel = new Panel("computerPanel");
        computerPanel.setBounds(0, 0, window.getWidth(), playerPanelHeight);
        computerPanel.setBackground(new Color(0, 80, 0)); // Slightly different shade
        computerPanel.setOpaque(true);
        System.out.println("Computer panel (North) créé");

        // Left Player panel (West) - Left 1/6th, between North and South
        leftPlayerPanel = new Panel("leftPlayerPanel");
        leftPlayerPanel.setBounds(0, gamePanelY, sidePanelWidth, gamePanelHeight);
        leftPlayerPanel.setBackground(new Color(0, 60, 0)); // Darker shade
        leftPlayerPanel.setOpaque(true);
        System.out.println("Left player panel (West) créé");

        // Right Player panel (East) - Right 1/6th, between North and South
        rightPlayerPanel = new Panel("rightPlayerPanel");
        rightPlayerPanel.setBounds(window.getWidth() - sidePanelWidth, gamePanelY, sidePanelWidth, gamePanelHeight);
        rightPlayerPanel.setBackground(new Color(0, 60, 0)); // Darker shade
        rightPlayerPanel.setOpaque(true);
        System.out.println("Right player panel (East) créé");

        // Game panel (Center) - Fills the middle area
        gamePanel = new Panel("gamePanel");
        gamePanel.setBounds(gamePanelX, gamePanelY, gamePanelWidth, gamePanelHeight);
        gamePanel.setBackground(new Color(0, 100, 0)); // Original green
        gamePanel.setOpaque(true);
        System.out.println("Game panel (Center) créé");


        // Draw button - positioned relative to game panel (center bottom)
        drawButton = new Button("Piocher");
        // Position will be updated in updateGameView based on game state
        drawButton.setOpaque(true);
        drawButton.setVisible(false);
        System.out.println("Draw button créé");

        mainPanel.addChild(playerPanel); // South
        mainPanel.addChild(computerPanel); // North
        mainPanel.addChild(leftPlayerPanel); // West
        mainPanel.addChild(rightPlayerPanel); // East
        mainPanel.addChild(gamePanel); // Center (on top of player panels edges)
        // Draw button added dynamically in updateGameView
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
                System.out.println("Draw button clicked");
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
        passButton.addEventListener(new EventListener() {
            @Override
            public void onMouseClick(MouseEvent event) {
                if (hasDrawnThisTurn) {
                    hasDrawnThisTurn = false;
                    game.moveToNextPlayer();
                    updateGameView();
                    if (game.getCurrentPlayer() instanceof ComputerPlayer) {
                        triggerComputerTurnWithDelay();
                    }
                }
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
        drawButton.setVisible(true); // Initially visible, updateGameView will adjust
    }

    private void startNewGame() { // Removed parameters as it's always 1v1
        System.out.println("Starting new solo game...");

        // Create the list of players for solo mode (1 Human, 1 Computer)
        List<Player> players = new ArrayList<>();
        players.add(new HumanPlayer("Player 1")); // Default name for human
        players.add(new ComputerPlayer("Computer")); // Default name for computer

        // Create the Game instance with the player list
        game = new Game(players); // Use the correct constructor
        System.out.println("Game instance created for solo mode.");

        // Initialize the game (deal cards, set top card)
        game.initializeGame();
        System.out.println("Solo game initialized.");

        showGameScreen();
        // drawButton.setVisible(true); // Visibility handled by updateGameView
        updateGameView();

        // Check if the first player is a computer
        if (game.getCurrentPlayer() instanceof ComputerPlayer) {
            System.out.println("First player is computer, initiating turn...");
            triggerComputerTurnWithDelay(); // Use helper method
        } else {
             System.out.println("First player is human.");
             // Visibility handled by updateGameView
             // drawButton.setVisible(true);
             updateGameView(); // Update view to highlight the human player
        }
    }

    private void drawCard() {
        Player currentPlayer = game.getCurrentPlayer();
        // Cette fonction fonctionne de la même manière en solo et multijoueur
        if (currentPlayer instanceof HumanPlayer && !hasDrawnThisTurn) {
            Card drawnCard = game.drawCard();
            if (drawnCard != null) {
                System.out.println(currentPlayer.getName() + " a pioché une carte: " + drawnCard);
                currentPlayer.addCard(drawnCard);
                hasDrawnThisTurn = true;
                updateGameView();
                
                // Si la carte piochée est jouable, le joueur peut la jouer en cliquant dessus
                // Sinon, il doit cliquer sur "Passer" pour terminer son tour
                // Cette logique est commune aux modes solo et multijoueur
            } else {
                System.out.println("Le deck est vide, impossible de piocher.");
            }
        }
    }

    private void updatePanelLayouts() {
        if (mainPanel != null && mainPanel.isVisible()) { // Only update if visible
            // Update main panel size first
            mainPanel.setBounds(0, 0, window.getWidth(), window.getHeight());

            // Define panel proportions based on current window size
            int playerPanelHeight = window.getHeight() / 4; // North and South panels height
            int sidePanelWidth = window.getWidth() / 6; // West and East panels width
            int gamePanelY = playerPanelHeight;
            int gamePanelHeight = window.getHeight() - (2 * playerPanelHeight);
            int gamePanelX = sidePanelWidth;
            int gamePanelWidth = window.getWidth() - (2 * sidePanelWidth);

            // Update Player panel (South)
            playerPanel.setBounds(0, window.getHeight() - playerPanelHeight, window.getWidth(), playerPanelHeight);

            // Update Computer panel (North)
            computerPanel.setBounds(0, 0, window.getWidth(), playerPanelHeight);

            // Update Left Player panel (West)
            leftPlayerPanel.setBounds(0, gamePanelY, sidePanelWidth, gamePanelHeight);

            // Update Right Player panel (East)
            rightPlayerPanel.setBounds(window.getWidth() - sidePanelWidth, gamePanelY, sidePanelWidth, gamePanelHeight);

            // Update Game panel (Center)
            gamePanel.setBounds(gamePanelX, gamePanelY, gamePanelWidth, gamePanelHeight);

            // Re-position elements within gamePanel (like top card and draw button) if needed
            // This is handled by updateGameView which should be called after resize

            // Force revalidation and repaint of all affected panels
            mainPanel.revalidate();
            mainPanel.repaint();
            // Individual panel repaint might not be strictly necessary if mainPanel repaint covers it
            // playerPanel.revalidate(); playerPanel.repaint();
            // computerPanel.revalidate(); computerPanel.repaint();
            // leftPlayerPanel.revalidate(); leftPlayerPanel.repaint();
            // rightPlayerPanel.revalidate(); rightPlayerPanel.repaint();
            // gamePanel.revalidate(); gamePanel.repaint();
        }
         // Update menu panel layout if it's visible
        if (menuPanel != null && menuPanel.isVisible()) {
             menuPanel.setBounds(0, 0, window.getWidth(), window.getHeight());
             // Reposition menu elements (logo, buttons) - This part seems handled by the existing resize listener in initializeUI
             menuPanel.revalidate();
             menuPanel.repaint();
        }
         // Update player selection panel layout if it's visible
        if (playerSelectionPanel != null && playerSelectionPanel.isVisible()) {
             playerSelectionPanel.setBounds(0, 0, window.getWidth(), window.getHeight());
             // Reposition player selection elements (title, fields, buttons)
             // Example: reposition title
             framework.core.Component title = playerSelectionPanel.getChildren().get(0); // Assuming title is first
             if (title instanceof Label) {
                 title.setBounds(window.getWidth() / 2 - 100, 50, 200, 30);
             }
             // Reposition player fields and buttons similarly... (This might need more detailed logic)
             // For now, just revalidate
             playerSelectionPanel.revalidate();
             playerSelectionPanel.repaint();
        }
    }

    private void updateGameView() {
        System.out.println("=== Updating Game View (Multiplayer Layout) ===");
        try {
            // Clear all panels first
            gamePanel.removeAll(); // Clear central panel (top card, draw button)
            playerPanel.removeAll(); // South
            computerPanel.removeAll(); // North
            leftPlayerPanel.removeAll(); // West
            rightPlayerPanel.removeAll(); // East

            if (game == null) {
                System.out.println("Game not initialized, cannot update view.");
                return;
            }

            List<Player> players = game.getPlayers();
            int numPlayers = players.size();
            if (players == null || numPlayers == 0) {
                System.out.println("No players in the game, cannot update view.");
                return;
            }

            // --- Determine Player Positions ---
            int humanPlayerIndex = -1;
            for (int i = 0; i < numPlayers; i++) {
                if (players.get(i) instanceof HumanPlayer) {
                    humanPlayerIndex = i;
                    break; // Assume only one human player for now
                }
            }
            // If no human player (e.g., all computer game), default to player 0 as South.
            if (humanPlayerIndex == -1) {
                humanPlayerIndex = 0; // Or handle error if a human is required
                System.out.println("Warning: No human player found, defaulting player 0 to South panel.");
            }

            // --- Display Top Card and Draw Button in Game Panel ---
            Card topCard = game.getCurrentTopCard();
            if (topCard != null) {
                CardView topCardView = new CardView(topCard);
                int cardWidth = 70;
                int cardHeight = 105;
                // Position top card in the center-left of the game panel
                topCardView.setBounds(
                    gamePanel.getWidth() / 2 - cardWidth - 10, // Left of center
                    (gamePanel.getHeight() - cardHeight) / 2,
                    cardWidth, cardHeight
                );
                topCardView.setFaceUp(true);
                gamePanel.addChild(topCardView);
                System.out.println("Top card displayed: " + topCard);
            } else {
                System.out.println("No top card to display.");
            }

            // Add draw button to game panel - Position near the bottom center of game panel
            int drawButtonWidth = 100;
            int drawButtonHeight = 40;
            drawButton.setBounds(
                (gamePanel.getWidth() - drawButtonWidth) / 2,
                gamePanel.getHeight() - drawButtonHeight - 20, // Positioned at the bottom
                drawButtonWidth, drawButtonHeight
            );
            // Only visible if it's a human player's turn
            drawButton.setVisible(game.getCurrentPlayer() instanceof HumanPlayer && !hasDrawnThisTurn);
            gamePanel.addChild(drawButton);

            // Pass button logic
            passButton.setVisible(game.getCurrentPlayer() instanceof HumanPlayer && hasDrawnThisTurn);
            passButton.setBounds(
                (gamePanel.getWidth() - 100) / 2,
                gamePanel.getHeight() - 80,
                100, 40
            );
            gamePanel.addChild(passButton);

            // --- Display Players and Cards in Respective Panels ---
            for (int i = 0; i < numPlayers; i++) {
                // Calculate the actual player index based on the human player's position
                int playerIndex = (humanPlayerIndex + i) % numPlayers;
                Player player = players.get(playerIndex);
                List<Card> cards = player.getHand();

                Panel targetPanel;
                boolean isVerticalLayout;

                // Assign player to panel based on relative position to human (i)
                switch (i) {
                    case 0: // Human player (or player 0 if no human)
                        targetPanel = playerPanel; // South
                        isVerticalLayout = false;
                        break;
                    case 1: // Player to the left of human
                        targetPanel = (numPlayers == 2) ? computerPanel : rightPlayerPanel; // North (2p) or East (3+p)
                        isVerticalLayout = (numPlayers > 2); // Vertical only if 3+ players
                        break;
                    case 2: // Player across from human (or next if 3p)
                        targetPanel = (numPlayers == 3) ? leftPlayerPanel : computerPanel; // West (3p) or North (4p)
                        isVerticalLayout = (numPlayers == 3); // Vertical only if 3 players
                        break;
                    case 3: // Player to the right of human (only in 4p)
                        targetPanel = leftPlayerPanel; // West
                        isVerticalLayout = true;
                        break;
                    default:
                        System.err.println("Warning: More than 4 players detected, skipping display for player index " + playerIndex);
                        continue; // Skip players beyond 4
                }

                System.out.println("Assigning Player " + player.getName() + " (Index " + playerIndex + ", Relative " + i + ") to Panel: " + targetPanel.getName());

                // Add player name label
                Label nameLabel = new Label(player.getName() + " (" + cards.size() + ")");
                nameLabel.setTextColor(Color.WHITE);
                nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
                if (isVerticalLayout) {
                    nameLabel.setBounds(10, 5, targetPanel.getWidth() - 20, 20); // Top of vertical panel
                } else if (targetPanel == playerPanel) {
                    nameLabel.setBounds(10, 5, targetPanel.getWidth() - 20, 20); // Top of South panel
                } else { // computerPanel (North)
                    nameLabel.setBounds(10, targetPanel.getHeight() - 25, targetPanel.getWidth() - 20, 20); // Bottom of North panel
                }
                targetPanel.addChild(nameLabel);

                // Highlight current player
                if (player == game.getCurrentPlayer()) {
                    nameLabel.setTextColor(Color.YELLOW); // Highlight current player's name
                    // Optionally add a border or background change to the panel
                    // targetPanel.setBorder(javax.swing.BorderFactory.createLineBorder(Color.YELLOW, 2));
                } else {
                    // targetPanel.setBorder(null); // Remove border if not current player
                }


            // Display cards - Use a helper method for layout flexibility
            // Only show cards face up if it's a Human player AND they are in the playerPanel (South)
            boolean showFaceUp = (player instanceof HumanPlayer && targetPanel == playerPanel);
            layoutCards(targetPanel, cards, showFaceUp, isVerticalLayout, player == game.getCurrentPlayer());

        } // End player loop

            // Force repaint of all panels involved
            mainPanel.revalidate();
            mainPanel.repaint();
            // gamePanel.revalidate(); gamePanel.repaint(); // Might be covered by mainPanel
            // playerPanel.revalidate(); playerPanel.repaint();
            // computerPanel.revalidate(); computerPanel.repaint();
            // leftPlayerPanel.revalidate(); leftPlayerPanel.repaint();
            // rightPlayerPanel.revalidate(); rightPlayerPanel.repaint();

        } catch (Exception e) {
            System.err.println("ERROR: Failed to update game view");
            e.printStackTrace();
        }
    }

    // Helper method to layout cards within a panel
    private void layoutCards(Panel targetPanel, List<Card> cards, boolean faceUp, boolean isVertical, boolean isCurrentPlayer) {
        if (cards.isEmpty()) return;

        int cardWidth = 70; // Default card width
        int cardHeight = 105; // Default card height
        int overlap; // Overlap amount depends on layout and card count

        int panelWidth = targetPanel.getWidth();
        int panelHeight = targetPanel.getHeight();
        int numCards = cards.size();

        // Calculate available space, leaving some padding
        int availableWidth = panelWidth - 40; // 20px padding each side
        int availableHeight = panelHeight - 60; // 30px padding top/bottom (adjust for name label)

        if (isVertical) {
            // Vertical layout (West/East panels)
            cardWidth = Math.min(60, availableWidth); // Smaller cards for vertical, fit width
            cardHeight = (int) (cardWidth * 1.5); // Maintain aspect ratio
            overlap = cardHeight / 2; // Overlap by half

            // Calculate total height and adjust overlap if needed
            int totalHeight = cardHeight + (numCards - 1) * (cardHeight - overlap);
            if (totalHeight > availableHeight && numCards > 1) {
                // Reduce overlap to fit cards vertically
                overlap = cardHeight - (availableHeight - cardHeight) / (numCards - 1);
                overlap = Math.max(10, overlap); // Ensure minimum visibility
                totalHeight = cardHeight + (numCards - 1) * (cardHeight - overlap);
            }

            int startX = (panelWidth - cardWidth) / 2; // Center horizontally
            int startY = (panelHeight - totalHeight) / 2; // Center vertically
            startY = Math.max(30, startY); // Ensure space for name label at top

            for (int i = 0; i < numCards; i++) {
                Card card = cards.get(i);
                CardView cardView = new CardView(card);
                cardView.setBounds(startX, startY + i * (cardHeight - overlap), cardWidth, cardHeight);
                cardView.setFaceUp(faceUp); // Use passed faceUp value
                targetPanel.addChild(cardView);
                // No click listener for opponent cards
            }

        } else {
            // Horizontal layout (North/South panels)
            overlap = cardWidth / 2; // Overlap by half

            // Calculate total width and adjust overlap if needed
            int totalWidth = cardWidth + (numCards - 1) * (cardWidth - overlap);
            if (totalWidth > availableWidth && numCards > 1) {
                // Reduce overlap to fit cards horizontally
                overlap = cardWidth - (availableWidth - cardWidth) / (numCards - 1);
                overlap = Math.max(10, overlap); // Ensure minimum visibility
                totalWidth = cardWidth + (numCards - 1) * (cardWidth - overlap);
            }

            int startX = (panelWidth - totalWidth) / 2; // Center horizontally
            int startY;
            if (targetPanel == playerPanel) { // South panel (Human)
                startY = 30; // Position near top, below name label
            } else { // North panel (Opponent)
                startY = panelHeight - cardHeight - 30; // Position near bottom, above name label
            }
            startY = Math.max(5, Math.min(startY, panelHeight - cardHeight - 5)); // Clamp within bounds


            for (int i = 0; i < numCards; i++) {
                Card card = cards.get(i);
                CardView cardView = new CardView(card);
                cardView.setBounds(startX + i * (cardWidth - overlap), startY, cardWidth, cardHeight);
                cardView.setFaceUp(faceUp); // Use passed faceUp value

                // Add click listener ONLY to the human player's cards IF it's their turn
                if (faceUp && targetPanel == playerPanel && isCurrentPlayer) {
                    cardView.addEventListener(new EventListener() {
                        @Override
                        public void onMouseClick(MouseEvent event) {
                            handleCardClick(cardView);
                        }
                        // Implement other mouse events if needed (hover effects, etc.)
                        @Override public void onMousePress(MouseEvent event) {}
                        @Override public void onMouseRelease(MouseEvent event) {}
                        @Override public void onMouseEnter(MouseEvent event) {
                             // Optional: Add hover effect like a border
                             cardView.setBorder(javax.swing.BorderFactory.createLineBorder(Color.YELLOW, 2));
                        }
                        @Override public void onMouseExit(MouseEvent event) {
                             cardView.setBorder(null); // Remove border on exit
                        }
                    });
                }
                targetPanel.addChild(cardView);
            }
        }
    }


    private void handleCardClick(CardView cardView) {
        Player currentPlayer = game.getCurrentPlayer();
        // Ensure it's the human player's turn AND the card belongs to them
        if (currentPlayer instanceof HumanPlayer && playerPanel.isAncestorOf(cardView)) {
            List<Card> playerCards = currentPlayer.getHand();
            int cardIndex = playerCards.indexOf(cardView.getCard()); // Find the card in the player's hand

            if (cardIndex != -1) { // Card found in hand
                Card clickedCard = playerCards.get(cardIndex);
                System.out.println("Human player clicked: " + clickedCard);

                // Attempt to play the card using the Game logic
                // Pass the index, not the card object
                if (game.getCurrentTopCard() != null && clickedCard.canPlayOn(game.getCurrentTopCard())) {
                    // Vérifier si c'est une carte Wild avant de la jouer
                    boolean isWildCard = clickedCard.getType().equals("Wild") || clickedCard.getType().equals("Wild Draw Four");
                    String chosenColor = null;
                    
                    if (isWildCard) {
                        // Récupérer la couleur choisie avant de jouer la carte
                        chosenColor = ((HumanPlayer)currentPlayer).chooseColor();
                        if (chosenColor == null) {
                            return; // L'utilisateur a annulé la sélection de couleur
                        }
                    }
                    
                    if (game.playCard(cardIndex)) {
                        hasDrawnThisTurn = false;
                        
                        // Afficher un message pour informer de la couleur choisie
                        if (isWildCard && chosenColor != null) {
                            javax.swing.JOptionPane.showMessageDialog(window, 
                                currentPlayer.getName() + " a choisi la couleur: " + chosenColor,
                                "Couleur choisie", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                        }
                        
                        updateGameView(); // Update the view after playing

                        // Check for game over
                        if (game.isGameOver()) {
                            announceWinner();
                        } else {
                            // If game not over, check if the next player is a computer
                            if (game.getCurrentPlayer() instanceof ComputerPlayer) {
                                triggerComputerTurnWithDelay(); // Start computer's turn after a delay
                            } else {
                                 updateGameView(); // Ensure view updates for next human player if any
                            }
                        }
                    } else {
                        System.out.println("Card cannot be played.");
                        // Optionally provide feedback to the user (e.g., shake the card view)
                         javax.swing.JOptionPane.showMessageDialog(window, "You cannot play this card.", "Invalid Move", javax.swing.JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    System.out.println("Card cannot be played.");
                    javax.swing.JOptionPane.showMessageDialog(window, "You cannot play this card.", "Invalid Move", javax.swing.JOptionPane.WARNING_MESSAGE);
                }
            } else {
                System.err.println("Error: Clicked card not found in player's hand?");
            }
        }
    }

    private void playComputerTurn() {
        if (game.isGameOver()) {
            System.out.println("Computer turn skipped: Game is over.");
            return;
        }

        Player currentPlayer = game.getCurrentPlayer();
        if (currentPlayer instanceof ComputerPlayer) {
            System.out.println("--- Starting computer turn: " + currentPlayer.getName() + " ---");

            // Sauvegardons la carte du dessus avant que l'ordinateur joue
            Card topCardBefore = game.getCurrentTopCard();
            
            // Use the Game's method which handles logic and turn progression
            boolean actionTaken = game.playComputerCard(); // Store result

            // Vérifions si l'ordinateur a joué une carte Wild
            if (actionTaken) {
                Card topCardAfter = game.getCurrentTopCard();
                
                // Si la carte jouée était une Wild ou Wild Draw Four
                if (topCardAfter != null && 
                    (topCardAfter.getType().equals("Wild") || topCardAfter.getType().equals("Wild Draw Four"))) {
                    
                    // Afficher message de la couleur choisie
                    javax.swing.JOptionPane.showMessageDialog(
                        window,
                        currentPlayer.getName() + " a choisi la couleur: " + topCardAfter.getColor(),
                        "Couleur choisie par l'ordinateur",
                        javax.swing.JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }

            System.out.println("Computer " + currentPlayer.getName() + " action completed. Action taken: " + actionTaken);

            // Update the view AFTER the computer has played/drawn
            updateGameView();

            // Check for game over AFTER the computer's turn
            if (game.isGameOver()) {
                announceWinner();
            } else {
                // Check if the NEXT player is also a computer
                if (game.getCurrentPlayer() instanceof ComputerPlayer) {
                    System.out.println("Next player is also a computer, triggering their turn.");
                    triggerComputerTurnWithDelay(); // Chain computer turns
                } else {
                    System.out.println("Next player is human.");
                    updateGameView(); // Ensure view is updated for human player
                }
            }
        } else {
            System.err.println("Error: playComputerTurn called when current player is not a ComputerPlayer.");
        }
    }

    private void announceWinner() {
        String winner = null;
        for (Player player : game.getPlayers()) {
            if (player.getHand().isEmpty()) {
                winner = player.getName();
                break;
            }
        }
        if (winner != null) {
            System.out.println("Game Over! Winner: " + winner);
            
            // Utiliser JOptionPane avec un bouton "OK" pour retourner au menu principal
            int response = javax.swing.JOptionPane.showConfirmDialog(
                window,
                "Partie terminée!\nVainqueur: " + winner + "\n\nCliquez sur OK pour retourner au menu principal.",
                "Fin de partie",
                javax.swing.JOptionPane.OK_CANCEL_OPTION,
                javax.swing.JOptionPane.INFORMATION_MESSAGE
            );
            
            // Si l'utilisateur clique sur OK, retourner au menu principal
            if (response == javax.swing.JOptionPane.OK_OPTION) {
                returnToMainMenu();
            }
        } else {
            System.out.println("Game Over called but no winner found?");
        }
    }
    
    // Nouvelle méthode pour retourner au menu principal
    private void returnToMainMenu() {
        // Réinitialiser l'état du jeu
        game = null;
        hasDrawnThisTurn = false;
        
        // Cacher tous les panneaux de jeu et afficher le menu principal
        mainPanel.setVisible(false);
        playerSelectionPanel.setVisible(false);
        menuPanel.setVisible(true);
        
        // Rafraîchir l'affichage
        window.revalidate();
        window.repaint();
    }

    private void addPlayerField() {
        if (playerNameFields.size() >= maxPlayers) {
             javax.swing.JOptionPane.showMessageDialog(window, "Maximum number of players (" + maxPlayers + ") reached.", "Max Players", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        int index = playerNameFields.size();

        // Create a panel for each player's settings
        Panel playerSettingsPanel = new Panel(); // No specific name needed here
        playerSettingsPanel.setBounds(
            window.getWidth() / 2 - 200,
            150 + index * 100, // Position vertically based on index
            400, // Width of the settings panel
            80 // Height of the settings panel
        );
        playerSettingsPanel.setOpaque(false); // Make background transparent

        // Add player name field
        TextField field = new TextField("Player " + (index + 1));
        field.setBounds(0, 0, 200, 40); // Position within the settings panel
        playerNameFields.add(field); // Add to the list for later retrieval
        playerSettingsPanel.addChild(field);

        // Add player type selection (Human/Computer buttons)
        Button humanButton = new Button("Human");
        humanButton.setBounds(210, 0, 90, 40);
        humanButton.setBackground(new Color(52, 152, 219)); // Blue
        humanButton.setTextColor(Color.WHITE);
        humanButton.setSelected(index == 0); // Select Human by default only for the first player

        Button computerButton = new Button("Computer");
        computerButton.setBounds(310, 0, 90, 40);
        computerButton.setBackground(new Color(231, 76, 60)); // Red
        computerButton.setTextColor(Color.WHITE);
        computerButton.setSelected(index != 0); // Select Computer by default for others

        // Group buttons logically (though not strictly necessary with current framework)
        // ButtonGroup typeGroup = new ButtonGroup(); // Conceptual grouping
        // typeGroup.add(humanButton);
        // typeGroup.add(computerButton);

        // Add click listeners to handle selection state
        humanButton.addEventListener(new EventListener() {
            @Override
            public void onMouseClick(MouseEvent event) {
                humanButton.setSelected(true);
                computerButton.setSelected(false);
                // Update appearance if needed (e.g., border, background)
                humanButton.setBackground(new Color(41, 128, 185)); // Darker selected blue
                computerButton.setBackground(new Color(231, 76, 60)); // Normal red
            }
             @Override public void onMousePress(MouseEvent event) {} @Override public void onMouseRelease(MouseEvent event) {} @Override public void onMouseEnter(MouseEvent event) {} @Override public void onMouseExit(MouseEvent event) {}
        });

        computerButton.addEventListener(new EventListener() {
            @Override
            public void onMouseClick(MouseEvent event) {
                computerButton.setSelected(true);
                humanButton.setSelected(false);
                // Update appearance
                computerButton.setBackground(new Color(192, 57, 43)); // Darker selected red
                humanButton.setBackground(new Color(52, 152, 219)); // Normal blue
            }
             @Override public void onMousePress(MouseEvent event) {} @Override public void onMouseRelease(MouseEvent event) {} @Override public void onMouseEnter(MouseEvent event) {} @Override public void onMouseExit(MouseEvent event) {}
        });

        // Set initial selected appearance
        if (humanButton.isSelected()) humanButton.setBackground(new Color(41, 128, 185));
        if (computerButton.isSelected()) computerButton.setBackground(new Color(192, 57, 43));


        playerSettingsPanel.addChild(humanButton);
        playerSettingsPanel.addChild(computerButton);

        // Add the whole settings panel to the main player selection panel
        playerSelectionPanel.addChild(playerSettingsPanel);
        playerSelectionPanel.revalidate();
        playerSelectionPanel.repaint();

        updatePlayerButtons(); // Enable/disable Add/Remove buttons
    }

    private void removePlayerField() {
        if (playerNameFields.size() <= 2) { // Minimum 2 players
             javax.swing.JOptionPane.showMessageDialog(window, "Minimum number of players is 2.", "Min Players", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get the last player settings panel
        Panel lastPlayerPanel = null;
        for (int i = playerSelectionPanel.getChildren().size() - 1; i >= 0; i--) {
            framework.core.Component child = playerSelectionPanel.getChildren().get(i);
            if (child instanceof Panel) {
                Panel panel = (Panel) child;
                // Check if this panel contains a TextField (player name field)
                for (framework.core.Component subChild : panel.getChildren()) {
                    if (subChild instanceof TextField) {
                        lastPlayerPanel = panel;
                        break;
                    }
                }
                if (lastPlayerPanel != null) break;
            }
        }

        if (lastPlayerPanel != null) {
            // Remove the panel from the player selection panel
            playerSelectionPanel.removeChild(lastPlayerPanel);
            
            // Remove the last TextField from the list
            if (!playerNameFields.isEmpty()) {
                playerNameFields.remove(playerNameFields.size() - 1);
            }

            // Update the UI
            playerSelectionPanel.revalidate();
            playerSelectionPanel.repaint();
            
            // Update button states
            updatePlayerButtons();
            
            System.out.println("Successfully removed player field");
        } else {
            System.err.println("Could not find player panel to remove");
            javax.swing.JOptionPane.showMessageDialog(
                window,
                "Could not remove player. Please try again.",
                "Error",
                javax.swing.JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void updatePlayerButtons() {
        addPlayerButton.setEnabled(playerNameFields.size() < maxPlayers);
        removePlayerButton.setEnabled(playerNameFields.size() > 2);
    }

    private void startMultiplayerGame() {
        System.out.println("=== Starting Multiplayer Game Setup (Revised Parsing) ===");

        // --- VALIDATION FIRST ---
        // Validate based on the number of fields the user has added/removed
        System.out.println("Validating player count. Number of player fields: " + playerNameFields.size());
        if (playerNameFields.size() < 2) {
            System.err.println("Error: Not enough players configured based on field count.");
            javax.swing.JOptionPane.showMessageDialog(
                window,
                "You need at least 2 players to start the game.",
                "Not Enough Players",
                javax.swing.JOptionPane.WARNING_MESSAGE
            );
            return; // Stop game start
        }
        if (playerNameFields.size() > maxPlayers) {
             System.err.println("Error: Too many players configured based on field count.");
             javax.swing.JOptionPane.showMessageDialog(
                window,
                "You cannot have more than " + maxPlayers + " players.",
                "Too Many Players",
                javax.swing.JOptionPane.WARNING_MESSAGE
            );
            return; // Stop game start
        }

        // --- PARSE PLAYER DETAILS (Revised Logic) ---
        try {
            List<Player> players = new ArrayList<>();
            System.out.println("Processing player fields. Expected players: " + playerNameFields.size());

            // Iterate through the known player name fields
            for (int i = 0; i < playerNameFields.size(); i++) {
                TextField nameField = playerNameFields.get(i);
                String playerName = nameField.getText().trim();
                System.out.println("Processing field for Player " + (i + 1) + ": Name='" + playerName + "'");

                // Basic name validation
                if (playerName.isEmpty()) {
                    System.err.println("Error: Player name is empty for field " + (i + 1));
                    javax.swing.JOptionPane.showMessageDialog(
                       window,
                       "Player name cannot be empty for Player " + (i + 1) + ".",
                       "Invalid Name",
                       javax.swing.JOptionPane.WARNING_MESSAGE
                   );
                   return; // Stop game start
                }

                // Find the parent Panel containing this TextField and its buttons
                framework.core.Container parentPanel = null;
                if (nameField.getParent() instanceof framework.core.Container) {
                    parentPanel = (framework.core.Container) nameField.getParent();
                } else {
                    System.err.println("Error: Parent is not a framework.core.Container.");
                    return;
                }
                if (!(parentPanel instanceof Panel)) {
                     System.err.println("Error: Could not find parent Panel for name field " + (i + 1));
                     // Show generic error and stop
                     javax.swing.JOptionPane.showMessageDialog(window, "Internal error reading player settings.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                     return;
                }

                Button humanButton = null;
                Button computerButton = null;

                // Find the Human and Computer buttons within the same parent panel
                System.out.println("  Searching for buttons in parent panel of " + playerName);
                for (framework.core.Component sibling : parentPanel.getChildren()) {
                    if (sibling instanceof Button) {
                        Button btn = (Button) sibling;
                         System.out.println("    Found button: " + btn.getText() + ", Selected: " + btn.isSelected());
                        if ("Human".equals(btn.getText())) {
                            humanButton = btn;
                        } else if ("Computer".equals(btn.getText())) {
                            computerButton = btn;
                        }
                    }
                }

                // Check if buttons were found and determine player type
                if (humanButton != null && computerButton != null) {
                    if (humanButton.isSelected()) {
                        players.add(new HumanPlayer(playerName));
                        System.out.println("  Added Human Player: " + playerName);
                    } else if (computerButton.isSelected()) {
                        players.add(new ComputerPlayer(playerName));
                        System.out.println("  Added Computer Player: " + playerName);
                    } else {
                        // This case should ideally not happen if defaults are set correctly
                        System.err.println("Error: Neither Human nor Computer selected for player: " + playerName);
                        javax.swing.JOptionPane.showMessageDialog(
                           window,
                           "Please select Human or Computer for player: " + playerName,
                           "Player Type Error",
                           javax.swing.JOptionPane.WARNING_MESSAGE
                       );
                       return; // Stop game start
                    }
                } else {
                    System.err.println("Error: Could not find Human/Computer buttons for player: " + playerName);
                    // Show generic error and stop
                    javax.swing.JOptionPane.showMessageDialog(window, "Internal error reading player settings (buttons not found).", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } // End loop through playerNameFields

            System.out.println("Finished processing fields. Final player count collected: " + players.size());

            // --- FINAL CHECK (Sanity Check) ---
            // This check should now be much less likely to fail, but kept as a safeguard.
            if (players.size() != playerNameFields.size()) {
                System.err.println("Error: Mismatch between expected players (" + playerNameFields.size() + ") and parsed players (" + players.size() + "). Check UI parsing logic.");
                javax.swing.JOptionPane.showMessageDialog(
                   window,
                   "Error reading player settings. Could not start game.", // Keep original message for consistency
                   "Internal Error",
                   javax.swing.JOptionPane.ERROR_MESSAGE
               );
               return; // Stop game start
            }

            // Check for at least one human player
            boolean hasHuman = players.stream().anyMatch(p -> p instanceof HumanPlayer);
            if (!hasHuman) {
                 javax.swing.JOptionPane.showMessageDialog(
                    window,
                    "You need at least one Human player.",
                    "No Human Player",
                    javax.swing.JOptionPane.WARNING_MESSAGE
                );
                return; // Stop game start
            }

            // --- Start Game ---
            System.out.println("Creating Game instance with " + players.size() + " players.");
            game = new Game(players);
            game.initializeGame();
            System.out.println("Multiplayer game initialized.");

            showGameScreen();
            updateGameView();

            if (game.getCurrentPlayer() instanceof ComputerPlayer) {
                System.out.println("First player is computer, initiating turn...");
                triggerComputerTurnWithDelay();
            } else {
                System.out.println("First player is human.");
                updateGameView();
            }

        } catch (Exception e) {
            System.err.println("Error starting multiplayer game: " + e.getMessage());
            e.printStackTrace();
             javax.swing.JOptionPane.showMessageDialog(
                window,
                "An error occurred while starting the game: " + e.getMessage(),
                "Game Start Error",
                javax.swing.JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Helper method to trigger computer turn with delay
    private void triggerComputerTurnWithDelay() {
        // Use Swing Timer for a delay before the computer plays
        javax.swing.Timer timer = new javax.swing.Timer(1000, e -> { // 1000 ms = 1 second delay
            System.out.println("Timer fired, executing computer turn.");
            playComputerTurn(); // Execute the computer's turn logic
        });
        timer.setRepeats(false); // Ensure the timer only runs once
        timer.start();
        System.out.println("Computer turn timer started.");
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

        // Reposition existing player fields
        List<framework.core.Component> children = playerSelectionPanel.getChildren();
        int playerFieldIndex = 0;
        for(framework.core.Component child : children) {
             if (child instanceof Panel && child.getName() == null) { // Player setting panels
                 child.setBounds(
                    window.getWidth() / 2 - 200,
                    150 + playerFieldIndex * 100,
                    400,
                    80
                 );
                 playerFieldIndex++;
             }
        }


        playerSelectionPanel.revalidate();
        playerSelectionPanel.repaint();
        window.revalidate();
        window.repaint();
        System.out.println("Player selection screen setup complete");
    }

    public static void main(String[] args) {
        // Ensure UI operations are on the Event Dispatch Thread
        javax.swing.SwingUtilities.invokeLater(() -> new UnoGame());
    }
}
