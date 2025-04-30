package framework.widgets;

import framework.core.Component;
import framework.events.EventListener;
import framework.events.MouseEvent;
import gamecore.Card;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class CardView extends Component {
  private final Card card;
  private boolean faceUp = true;
  private final List<CardClickListener> clickListeners = new ArrayList<>();
  private static Image backImage;
  private Image cardImage;
  private boolean isHovered = false;
  private static final int SHADOW_SIZE = 4;
  private static final float HOVER_SCALE = 1.1f;

  public interface CardClickListener {
    void onCardClicked(CardView cardView);
  }

  public CardView(Card card) {
    super("CardView");
    this.card = card;
    setSize(80, 120);
    setupEventListener();
    loadCardImage();
  }

  private void loadCardImage() {
    try {
      // Charge l'image de dos de carte si pas encore chargée
      if (backImage == null) {
        String backPath = System.getProperty("user.dir") + "/src/resources/images/cards/BackFace.png";
        System.out.println("Loading back image from: " + backPath);
        backImage = ImageIO.read(new File(backPath));
      }

      // Construit le nom du fichier de l'image en fonction de la carte
      String filename = getImageFilename();
      String cardPath = System.getProperty("user.dir") + "/src/resources/images/cards/" + filename;
      System.out.println("Loading card image from: " + cardPath);
      cardImage = ImageIO.read(new File(cardPath));
    } catch (IOException e) {
      System.err.println("Erreur lors du chargement de l'image: " + e.getMessage());
      e.printStackTrace();
      // En cas d'erreur, on utilisera le rendu par défaut
      cardImage = null;
    }
  }

  private String getImageFilename() {
    String color = card.getColor().equals("Rouge") ? "Red"
        : card.getColor().equals("Bleu") ? "Blue"
            : card.getColor().equals("Vert") ? "Green" : card.getColor().equals("Jaune") ? "Yellow" : "";

    if (card.getType().equals("Number")) {
      String number = switch (card.getValue()) {
        case 0 -> "Zero";
        case 1 -> "One";
        case 2 -> "Two";
        case 3 -> "Three";
        case 4 -> "Four";
        case 5 -> "Five";
        case 6 -> "Six";
        case 7 -> "Seven";
        case 8 -> "Eigth";
        case 9 -> "Nine";
        default -> String.valueOf(card.getValue());
      };
      return number + "_" + color + ".png";
    } else if (card.getColor().equals("Noir")) {
      return card.getType().equals("Wild") ? "Wild.png" : "WildFour.png";
    } else {
      String type = switch (card.getType()) {
        case "Skip" -> "Passer";
        case "Reverse" -> "Inverser";
        case "Draw Two" -> "+2";
        default -> card.getType();
      };
      return type + "_" + color + ".png";
    }
  }

  private void setupEventListener() {
    addEventListener(new EventListener() {
      @Override
      public void onMouseClick(MouseEvent event) {
        notifyCardClicked();
      }

      @Override
      public void onMousePress(MouseEvent event) {
      }

      @Override
      public void onMouseRelease(MouseEvent event) {
      }

      @Override
      public void onMouseEnter(MouseEvent event) {
        isHovered = true;
        repaint();
      }

      @Override
      public void onMouseExit(MouseEvent event) {
        isHovered = false;
        repaint();
      }
    });
  }

  public void addCardClickListener(CardClickListener listener) {
    clickListeners.add(listener);
  }

  private void notifyCardClicked() {
    for (CardClickListener listener : clickListeners) {
      listener.onCardClicked(this);
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2d = (Graphics2D) g.create();
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

    // Effet d'ombre
    if (!isHovered) {
      g2d.setColor(new Color(0, 0, 0, 50));
      g2d.fillRoundRect(SHADOW_SIZE, SHADOW_SIZE, getWidth() - SHADOW_SIZE, getHeight() - SHADOW_SIZE, 15, 15);
    }

    // Effet de hover
    if (isHovered) {
      g2d.translate((getWidth() * (HOVER_SCALE - 1)) / 2, (getHeight() * (HOVER_SCALE - 1)) / 2);
      g2d.scale(HOVER_SCALE, HOVER_SCALE);
    }

    if (cardImage != null || backImage != null) {
      // Rendu de l'image avec effet de brillance
      Image imageToDraw = faceUp ? cardImage : backImage;
      if (imageToDraw != null) {
        g2d.drawImage(imageToDraw, 0, 0, getWidth(), getHeight(), this);

        // Effet de brillance
        if (isHovered) {
          GradientPaint gp = new GradientPaint(0, 0,
              new Color(255, 255, 255, 50),
              getWidth(), getHeight(),
              new Color(255, 255, 255, 0));
          g2d.setPaint(gp);
          g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
          g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        }
        g2d.dispose();
        return;
      }
    }

    // Rendu par défaut amélioré si pas d'image
    if (!faceUp) {
      drawBackside(g2d);
      g2d.dispose();
      return;
    }

    // Fond de la carte avec dégradé
    Color baseColor = getCardColor();
    Color lighterColor = getLighterColor(baseColor);
    GradientPaint gradient = new GradientPaint(0, 0, lighterColor, 0, getHeight(), baseColor);
    g2d.setPaint(gradient);
    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

    // Bordure améliorée
    g2d.setColor(new Color(255, 255, 255, 180));
    g2d.setStroke(new java.awt.BasicStroke(2));
    g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 15, 15);

    // Texte de la carte avec ombre
    g2d.setFont(g2d.getFont().deriveFont(java.awt.Font.BOLD, 22f));
    String displayText = getDisplayText();
    int x = (getWidth() - g2d.getFontMetrics().stringWidth(displayText)) / 2;
    int y = (getHeight() + g2d.getFontMetrics().getAscent()) / 2;

    // Ombre du texte
    g2d.setColor(new Color(0, 0, 0, 100));
    g2d.drawString(displayText, x + 1, y + 1);

    // Texte principal
    g2d.setColor(Color.WHITE);
    g2d.drawString(displayText, x, y);

    g2d.dispose();
  }

  private Color getLighterColor(Color base) {
    float[] hsb = Color.RGBtoHSB(base.getRed(), base.getGreen(), base.getBlue(), null);
    return Color.getHSBColor(hsb[0], Math.max(0, hsb[1] - 0.1f), Math.min(1, hsb[2] + 0.2f));
  }

  private void drawBackside(Graphics2D g) {
    g.setColor(Color.DARK_GRAY);
    g.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
    g.setColor(Color.RED);
    g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
    g.drawString("UNO", getWidth() / 3, getHeight() / 2);
  }

  private Color getCardColor() {
    return switch (card.getColor()) {
      case "Rouge" -> Color.RED;
      case "Bleu" -> Color.BLUE;
      case "Vert" -> Color.GREEN;
      case "Jaune" -> Color.YELLOW;
      case "Noir" -> Color.BLACK;
      default -> Color.GRAY;
    };
  }

  private String getDisplayText() {
    if (card.getType().equals("Number")) {
      return String.valueOf(card.getValue());
    }
    return card.getType();
  }

  public void setFaceUp(boolean faceUp) {
    this.faceUp = faceUp;
    repaint();
  }

  public Card getCard() {
    return card;
  }
}