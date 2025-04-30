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

    // Fond avec texture de feutre de poker plus sophistiquée
    Color baseFeltColor = new Color(0, 80, 0); // Darker green base
    Color highlightFeltColor = new Color(0, 120, 0); // Brighter green highlight
    
    // Dégradé subtil pour le fond
    GradientPaint gradient = new GradientPaint(
        0, 0, highlightFeltColor,
        0, getHeight(), baseFeltColor);
    g2d.setPaint(gradient);
    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

    // Texture de feutre plus sophistiquée
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
    for (int i = 0; i < getWidth(); i += 6) {
      for (int j = 0; j < getHeight(); j += 6) {
        // Variation de taille et d'opacité pour un effet plus naturel
        int size = (i + j) % 12 == 0 ? 3 : 2;
        int alpha = (i + j) % 12 == 0 ? 40 : 20;
        g2d.setColor(new Color(0, 140, 0, alpha));
        g2d.fillOval(i, j, size, size);
      }
    }

    // Bordure extérieure élégante (réduite)
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    g2d.setColor(new Color(0, 40, 0));
    g2d.setStroke(new BasicStroke(12, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    g2d.drawRoundRect(6, 6, getWidth() - 12, getHeight() - 12, 15, 15);

    // Bordure intérieure avec effet de brillance (réduite)
    g2d.setColor(new Color(0, 160, 0, 120));
    g2d.setStroke(new BasicStroke(2));
    g2d.drawRoundRect(12, 12, getWidth() - 24, getHeight() - 24, 15, 15);

    // Effet de brillance supplémentaire
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
    g2d.setColor(new Color(255, 255, 255, 30));
    g2d.fillOval(0, 0, getWidth() / 2, getHeight() / 2);

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
