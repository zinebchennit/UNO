package gamecore;

import java.util.List;
import java.util.Scanner;

public class HumanPlayer extends Player {
    private final Scanner scanner;

    public HumanPlayer(String name) {
        super(name);
        this.scanner = new Scanner(System.in);
    }

    // Nouvelles méthodes pour l'interface graphique
    public List<Card> getHand() {
        return hand;
    }

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

    private Card playCard(Card topCard) {
        // Vérifier si le joueur a des cartes jouables
        if (!hasPlayableCard(topCard)) {
            System.out.println(name + ", vous n'avez pas de carte jouable. Vous devez piocher une carte.");
            return null;
        }

        // Afficher les cartes disponibles
        displayHand();

        System.out.println("Quelle carte voulez-vous jouer? (entrez le numéro de la carte, ou 0 pour piocher)");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrée invalide. Veuillez entrer un nombre.");
            return playCard(topCard);
        }

        // Si le joueur choisit de piocher
        if (choice == 0) {
            return null;
        }

        // Vérifier si la sélection est valide
        if (choice < 1 || choice > hand.size()) {
            System.out.println("Choix invalide. Veuillez choisir un numéro entre 1 et " + hand.size());
            return playCard(topCard);
        }

        Card selectedCard = hand.get(choice - 1);

        // Vérifier si la carte peut être jouée
        if (!selectedCard.canPlayOn(topCard)) {
            System.out.println("Cette carte ne peut pas être jouée. Veuillez en choisir une autre.");
            return playCard(topCard);
        }

        return hand.remove(choice - 1);
    }

    private boolean hasPlayableCard(Card topCard) {
        for (Card card : hand) {
            if (card.canPlayOn(topCard)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String chooseColor() {
        boolean validInput = false;
        while (!validInput) {
            System.out.println("Choisissez une couleur:");
            System.out.println("1. Rouge");
            System.out.println("2. Bleu");
            System.out.println("3. Vert");
            System.out.println("4. Jaune");
    
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        return "Rouge";
                    case 2:
                        return "Bleu";
                    case 3:
                        return "Vert";
                    case 4:
                        return "Jaune";
                    default:
                        System.out.println("Choix invalide. Veuillez choisir un nombre entre 1 et 4.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrée invalide. Veuillez entrer un nombre entre 1 et 4.");
            }
            // La boucle continue car validInput reste false
        }
        return "Rouge"; // Couleur par défaut (ne devrait jamais être atteint)
    }
    
    public void displayHand() {
        System.out.println("\nVotre main (" + name + "):");
        for (int i = 0; i < hand.size(); i++) {
            System.out.println((i + 1) + ": " + hand.get(i));
        }
        System.out.println();
    }
}