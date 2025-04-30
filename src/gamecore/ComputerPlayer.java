package gamecore;
import java.util.HashMap;
import java.util.Map;

public class ComputerPlayer extends Player {
    public ComputerPlayer(String name) {
        super(name);
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
        // Stratégie simple: jouer la première carte valide
        for (int i = 0; i < hand.size(); i++) {
            Card card = hand.get(i);
            if (card.canPlayOn(topCard)) {
                return hand.remove(i);
            }
        }
        
        // Si aucune carte ne peut être jouée, retourner null pour piocher
        return null;
    }
    
    @Override
    public String chooseColor() {
        // Stratégie: choisir la couleur la plus fréquente dans la main
        Map<String, Integer> colorCounts = new HashMap<>();
        colorCounts.put("Rouge", 0);
        colorCounts.put("Bleu", 0);
        colorCounts.put("Vert", 0);
        colorCounts.put("Jaune", 0);
        
        for (Card card : hand) {
            String color = card.getColor();
            if (!color.equals("Noir")) {
                colorCounts.put(color, colorCounts.get(color) + 1);
            }
        }
        
        String mostFrequentColor = "Rouge"; // Couleur par défaut
        int maxCount = -1;
        
        for (Map.Entry<String, Integer> entry : colorCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostFrequentColor = entry.getKey();
            }
        }
        
        return mostFrequentColor;
    }
}