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
  private String group;
  private boolean selected;

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
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g.create();
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    // Background with gradient
    Color topColor = isPressed ? pressedColor : isHovered ? hoverColor : backgroundColor;
    Color bottomColor = new Color(
        Math.max(0, topColor.getRed() - 20),
        Math.max(0, topColor.getGreen() - 20),
        Math.max(0, topColor.getBlue() - 20));

    GradientPaint gradient = new GradientPaint(
        0, 0, topColor,
        0, getHeight(), bottomColor);

    // Draw shadow
    if (!isPressed) {
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 15, 15);
    }

    // Draw button background
    g2d.setPaint(gradient);
    g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

    // Draw highlight on top edge
    g2d.setStroke(new BasicStroke(1.5f));
    g2d.setColor(new Color(255, 255, 255, isPressed ? 30 : 70));
    g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 15, 15);

    // Draw text
    String text = getText();
    g2d.setFont(getFont().deriveFont(Font.BOLD));
    FontMetrics metrics = g2d.getFontMetrics();
    int x = (getWidth() - metrics.stringWidth(text)) / 2;
    int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();

    if (isPressed) {
        x += 1;
        y += 1;
    }

    // Draw text shadow
    g2d.setColor(new Color(0, 0, 0, 100));
    g2d.drawString(text, x + 1, y + 1);

    // Draw main text
    g2d.setColor(textColor);
    g2d.drawString(text, x, y);

    if (selected) {
        // Draw a thicker border for selected state
        g.setColor(new Color(255, 255, 255, 150));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(new Color(255, 255, 255));
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        g.drawRect(1, 1, getWidth() - 3, getHeight() - 3);
    }

    g2d.dispose();
  }

  @Override
  public void setBackground(Color color) {
    this.backgroundColor = color;
    this.hoverColor = new Color(
        Math.min(255, (int)(color.getRed() * 0.9)),
        Math.min(255, (int)(color.getGreen() * 0.9)),
        Math.min(255, (int)(color.getBlue() * 0.9)));
    this.pressedColor = new Color(
        Math.max(0, (int)(color.getRed() * 0.8)),
        Math.max(0, (int)(color.getGreen() * 0.8)),
        Math.max(0, (int)(color.getBlue() * 0.8)));
    repaint();
  }

  public void setText(String text) {
    this.text = text;
    repaint();
  }

  public String getText() {
    return text;
  }

  public void setTextColor(Color color) {
    this.textColor = color;
    repaint();
  }

  public void setGroup(String group) {
    this.group = group;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
    repaint();
  }

  public boolean isSelected() {
    return selected;
  }
}