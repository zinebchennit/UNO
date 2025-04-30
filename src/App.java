import gamecore.UnoGame;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        // Ensure UI is created on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("Starting UNO game...");
                UnoGame game = new UnoGame();
            } catch (Exception e) {
                System.err.println("Error starting game: ");
                e.printStackTrace();
            }
        });
    }
}