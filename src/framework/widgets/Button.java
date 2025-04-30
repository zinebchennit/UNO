package framework.widgets;

import framework.core.Component;
import framework.events.EventListener;
import framework.events.MouseEvent;
import java.awt.*;

public class Button extends Component {
  private String text;
  private boolean isHovered = false;
  private boolean isPressed = false;
  private Color backgroundColor = new Color(60, 60, 65);
  private Color hoverColor = new Color(75, 75, 80);
  private Color pressedColor = new Color(45, 45, 50);
  private Color textColor = new Color(240, 240, 240);

  public Button(String text) {
    super("Button");
    this.text = text;
    setupEventListener();
  }

  private void setupEventListener() {
    addEventListener(new EventListener() {
      @Override
      public void onMouseClick(MouseEvent event) {
      }

      @Override
      public void onMousePress(MouseEvent event) {
        isPressed = true;
        repaint();
      }

      @Override
      public void onMouseRelease(MouseEvent event) {
        isPressed = false;
        repaint();
      }

      @Override
      public void onMouseEnter(MouseEvent event) {
        isHovered = true;
        repaint();
      }

      @Override
      public void onMouseExit(MouseEvent event) {
        isHovered = false;
        isPressed = false;
        repaint();
      }
    });
  }

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2d = (Graphics2D) g.create();
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    // Effet d'ombre
    if (!isPressed) {
      g2d.setColor(new Color(0, 0, 0, 50));
      g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 10, 10);
    }

    // Fond du bouton avec dégradé
    Color topColor = isPressed ? pressedColor : isHovered ? hoverColor : backgroundColor;
    Color bottomColor = new Color(
        Math.max(0, topColor.getRed() - 15),
        Math.max(0, topColor.getGreen() - 15),
        Math.max(0, topColor.getBlue() - 15));

    GradientPaint gradient = new GradientPaint(
        0, 0, topColor,
        0, getHeight(), bottomColor);

    g2d.setPaint(gradient);
    g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

    // Bordure brillante
    g2d.setStroke(new BasicStroke(1.5f));
    g2d.setColor(new Color(255, 255, 255, 50));
    g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() / 2 - 1, 10, 10);

    // Texte
    g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 14f));
    FontMetrics metrics = g2d.getFontMetrics();
    int x = (getWidth() - metrics.stringWidth(text)) / 2;
    int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();

    if (isPressed) {
      x += 1;
      y += 1;
    }

    // Ombre du texte
    g2d.setColor(new Color(0, 0, 0, 100));
    g2d.drawString(text, x + 1, y + 1);

    // Texte principal
    g2d.setColor(textColor);
    g2d.drawString(text, x, y);

    g2d.dispose();
  }

  public void setText(String text) {
    this.text = text;
    repaint();
  }

  public String getText() {
    return text;
  }

  public void setBackgroundColor(Color color) {
    this.backgroundColor = color;
    this.hoverColor = new Color(
        Math.min(255, color.getRed() + 15),
        Math.min(255, color.getGreen() + 15),
        Math.min(255, color.getBlue() + 15));
    this.pressedColor = new Color(
        Math.max(0, color.getRed() - 15),
        Math.max(0, color.getGreen() - 15),
        Math.max(0, color.getBlue() - 15));
    repaint();
  }

  public void setTextColor(Color color) {
    this.textColor = color;
    repaint();
  }
}