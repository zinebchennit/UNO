package gamecore;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private ArrayList<Card> cards;
    private ArrayList<Card> discardPile;
    
    public Deck() {
        this.cards = new ArrayList<>();
        this.discardPile = new ArrayList<>();
        initializeDeck();
        shuffle();
    }
    
    private void initializeDeck() {
        // Couleurs standard
        String[] colors = {"Rouge", "Bleu", "Vert", "Jaune"};
        
        // Création des cartes numérotées (0-9)
        for (String color : colors) {
            // Une seule carte 0 par couleur
            cards.add(new Card(color, "Number", 0));
            
            // Deux cartes 1-9 par couleur
            for (int i = 1; i <= 9; i++) {
                cards.add(new Card(color, "Number", i));
                cards.add(new Card(color, "Number", i));
            }
            
            // Deux cartes spéciales par couleur (Skip, Reverse, Draw Two)
            for (int i = 0; i < 2; i++) {
                cards.add(new Card(color, "Skip", 20));
                cards.add(new Card(color, "Reverse", 20));
                cards.add(new Card(color, "Draw Two", 20));
            }
        }
        
        // Ajout des cartes Wild
        for (int i = 0; i < 4; i++) {
            cards.add(new Card("Noir", "Wild", 50));
            cards.add(new Card("Noir", "Wild Draw Four", 50));
        }
    }
    
    public void shuffle() {
        Collections.shuffle(cards);
    }
    
    public Card drawCard() {
        // Si le deck est vide, mélanger la pile de défausse sauf la carte du dessus
        if (cards.isEmpty() && discardPile.size() > 1) {
            Card topCard = discardPile.remove(discardPile.size() - 1);
            cards.addAll(discardPile);
            discardPile.clear();
            discardPile.add(topCard);
            shuffle();
        }
        
        // Si le deck est encore vide après avoir essayé de le reconstituer, retourner null
        if (cards.isEmpty()) {
            return null;
        }
        
        return cards.remove(cards.size() - 1);
    }
    
    public void discard(Card card) {
        discardPile.add(card);
    }
    
    public Card getTopCard() {
        if (discardPile.isEmpty()) {
            return null;
        }
        return discardPile.get(discardPile.size() - 1);
    }
    
    public int getDeckSize() {
        return cards.size();
    }
    
    public int getDiscardPileSize() {
        return discardPile.size();
    }
}