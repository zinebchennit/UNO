package gamecore;

import framework.core.Window;
import framework.events.EventListener;
import framework.events.MouseEvent;
import framework.widgets.Button;
import framework.widgets.CardView;
import framework.widgets.Panel;
import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

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

        // Menu principal
        menuPanel = new Panel("menuPanel");
        menuPanel.setBounds(0, 0, 1024, 768);
        menuPanel.setBackground(new Color(255, 255, 255));
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
            
            JLabel logoLabel = new JLabel(new ImageIcon(logoImage.getScaledInstance(200, 300, Image.SCALE_SMOOTH)));
            logoLabel.setBounds(412, 100, 200, 300);
            logoLabel.setOpaque(true);
            menuPanel.add(logoLabel);
            System.out.println("Logo ajouté au menu panel");
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de l'image : " + e.getMessage());
            e.printStackTrace();
        }

        // Boutons du menu
        startSoloButton = new Button("Play Solo");
        startSoloButton.setBounds(412, 450, 200, 50);
        startSoloButton.setBackground(new Color(135, 206, 235)); // Bleu clair
        startSoloButton.setOpaque(true);
        System.out.println("Bouton solo créé");

        startMultiButton = new Button("MultiPlayer");
        startMultiButton.setBounds(412, 520, 200, 50);
        startMultiButton.setBackground(new Color(255, 182, 193)); // Rose clair
        startMultiButton.setOpaque(true);
        System.out.println("Bouton multi créé");

        menuPanel.addChild(startSoloButton);
        menuPanel.addChild(startMultiButton);
        System.out.println("Boutons ajoutés au menu panel");

        // Panels du jeu
        mainPanel = new Panel("mainPanel");
        mainPanel.setBounds(0, 0, 1024, 768);
        mainPanel.setBackground(new Color(34, 139, 34));
        mainPanel.setOpaque(true);
        mainPanel.setVisible(false);
        System.out.println("Main panel créé");

        // Panel pour l'ordinateur (en haut)
        computerPanel = new Panel("computerPanel");
        computerPanel.setBounds(0, 0, 1024, 200);
        computerPanel.setBackground(new Color(45, 180, 45));
        computerPanel.setOpaque(true);
        System.out.println("Computer panel créé");

        // Panel central pour le jeu
        gamePanel = new Panel("gamePanel");
        gamePanel.setBounds(200, 200, 624, 368);
        gamePanel.setBackground(new Color(40, 160, 40));
        gamePanel.setOpaque(true);
        System.out.println("Game panel créé");

        // Panel pour le joueur (en bas)
        playerPanel = new Panel("playerPanel");
        playerPanel.setBounds(0, 568, 1024, 200);
        playerPanel.setBackground(new Color(45, 180, 45));
        playerPanel.setOpaque(true);
        System.out.println("Player panel créé");

        drawButton = new Button("Piocher");
        drawButton.setBounds(462, 334, 100, 40);
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
        startSoloButton.addEventListener(new EventListener() {
            @Override
            public void onMouseClick(MouseEvent event) {
                startNewGame(1, 1); // 1 joueur humain, 1 ordinateur
                showGameScreen();
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

        startMultiButton.addEventListener(new EventListener() {
            @Override
            public void onMouseClick(MouseEvent event) {
                startNewGame(2, 0); // 2 joueurs humains
                showGameScreen();
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
        drawButton.setVisible(true);
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
