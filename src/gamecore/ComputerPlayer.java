package gamecore;

import java.util.Random;

public class ComputerPlayer extends Player {
    private Random random;

    public ComputerPlayer(String name) {
        super(name);
        this.random = new Random();
    }

    @Override
    public String chooseColor() {
        int rouge = 0, bleu = 0, vert = 0, jaune = 0;

        for (Card card : hand) {
            if (card.getColor() == null) continue;

            switch (card.getColor()) {
                case "Rouge": rouge++; break;
                case "Bleu": bleu++; break;
                case "Vert": vert++; break;
                case "Jaune": jaune++; break;
            }
        }

        int max = Math.max(Math.max(rouge, bleu), Math.max(vert, jaune));

        if (max == 0) {
            String[] colors = {"Rouge", "Bleu", "Vert", "Jaune"};
            return colors[random.nextInt(colors.length)];
        }

        if (max == rouge) return "Rouge";
        if (max == bleu) return "Bleu";
        if (max == vert) return "Vert";
        return "Jaune";
    }
}