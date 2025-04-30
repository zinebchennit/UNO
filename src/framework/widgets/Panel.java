package framework.widgets;

import framework.core.Container;
import java.awt.*;

public class Panel extends Container {
  private Color backgroundColor = new Color(40, 40, 45);
  private boolean useTexture = true;
  private float opacity = 1.0f;

  public Panel() {
    this("Panel");
  }

  public Panel(String name) {
    super(name);
  }

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2d = (Graphics2D) g.create();
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

    // Appliquer l'opacité
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

    // Fond avec dégradé subtil
    GradientPaint gradient = new GradientPaint(
        0, 0, new Color(backgroundColor.getRed() + 5, backgroundColor.getGreen() + 5, backgroundColor.getBlue() + 5),
        0, getHeight(), backgroundColor);
    g2d.setPaint(gradient);
    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

    if (useTexture) {
      // Ajouter une texture subtile
      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
      for (int i = 0; i < getWidth(); i += 4) {
        for (int j = 0; j < getHeight(); j += 4) {
          if ((i + j) % 8 == 0) {
            g2d.setColor(Color.WHITE);
            g2d.fillRect(i, j, 1, 1);
          }
        }
      }
    }

    // Bordure avec effet de brillance
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
    g2d.setColor(Color.WHITE);
    g2d.setStroke(new BasicStroke(1.5f));
    g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

    g2d.dispose();
    super.paintComponent(g);
  }

  public void setBackgroundColor(Color color) {
    this.backgroundColor = color;
    repaint();
  }

  public void setUseTexture(boolean useTexture) {
    this.useTexture = useTexture;
    repaint();
  }

  public void setOpacity(float opacity) {
    this.opacity = Math.max(0f, Math.min(1f, opacity));
    repaint();
  }
}
