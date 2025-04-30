package gamecore;

import java.util.Scanner;

public class UnoGame {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("==================================");
        System.out.println("       JEU DE UNO TEXTUEL         ");
        System.out.println("==================================");

        int humanPlayers = getNumberOfPlayers("humains", 1, 4);
        int computerPlayers = getNumberOfPlayers("ordinateurs", 1, 4 - humanPlayers);

        // Créer et démarrer le jeu
        Game game = new Game(humanPlayers, computerPlayers);
        game.playGame();

        scanner.close();
    }

    private static int getNumberOfPlayers(String type, int min, int max) {
        int number = 0;
        boolean validInput = false;

        while (!validInput) {
            System.out.println("Combien de joueurs " + type + "? (entre " + min + " et " + max + ")");
            try {
                number = Integer.parseInt(scanner.nextLine());
                if (number >= min && number <= max) {
                    validInput = true;
                } else {
                    System.out.println("Nombre invalide! Veuillez entrer un nombre entre " + min + " et " + max + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrée invalide! Veuillez entrer un nombre.");
            }
        }

        return number;
    }
}
