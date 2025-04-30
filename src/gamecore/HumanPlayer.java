package gamecore;

// Removed unused imports
// import java.util.ArrayList;
// import java.util.List;
import javax.swing.JOptionPane; // Import for dialog

public class HumanPlayer extends Player {
    // Removed redundant fields already in Player
    // private String name;
    // private List<Card> hand;
    // private Card selectedCard; // Selection handled by UI click
    // private String selectedColor; // Color choice handled by chooseColor()
    // private final Scanner scanner; // Input handled by UI

    public HumanPlayer(String name) {
        super(name); // Call superclass constructor
        // this.hand = new ArrayList<>(); // Hand initialized in superclass
        // this.name = name;
        // this.selectedCard = null;
        // this.selectedColor = null;
        // this.scanner = new Scanner(System.in);
    }

    // Methods addCard, getHand, getHandSize, getName, clearHand, getCard, removeCard
    // are inherited from Player or implemented there.

    // Removed redundant setName, already handled by constructor
    // public void setName(String name) {
    //     this.name = name;
    // }

    // Removed redundant getCard and removeCard, now in Player
    // public Card getCard(int index) { ... }
    // public Card removeCard(int index) { ... }

    // Removed playTurn and playCard, logic is now primarily in Game.playCard
    // @Override
    // public boolean playTurn(Card topCard) { ... }
    // @Override
    // public Card playCard(Card topCard) { ... }

    @Override
    public String chooseColor() {
        String[] colors = {"Rouge", "Bleu", "Vert", "Jaune"};
        String newColor = (String) JOptionPane.showInputDialog(
            null, // Parent component (can be null)
            "Choisissez une couleur:",
            "Changer de couleur",
            JOptionPane.QUESTION_MESSAGE,
            null, // Icon (default)
            colors, // Choices
            colors[0] // Default choice
        );
        // Return the chosen color, or null if the dialog was cancelled
        return newColor;
    }

    // Removed setSelectedCard and setSelectedColor, UI/Game handles this flow
    // public void setSelectedCard(Card card) { ... }
    // public String setSelectedColor(String color) { ... }

    // Removed displayHand, UI handles display
    // public void displayHand() { ... }
}