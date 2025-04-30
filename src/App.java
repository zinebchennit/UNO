import gamecore.Card; // Importer la classe Card du package gamecore

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");

        // Exemple d'utilisation de la classe Card
        Card card = new Card("Red", "Number", 5);
        System.out.println("Created card: " + card);
    }
}