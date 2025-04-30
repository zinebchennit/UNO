package framework.core;

import java.awt.*;
import javax.swing.JFrame;

public class Window extends JFrame {
  private Container rootContainer;

  public Window(String title, int width, int height) {
    super(title);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(width, height);
    setMinimumSize(new Dimension(800, 600));
    setLocationRelativeTo(null);

    // Style moderne avec bordures
    getRootPane().setBorder(javax.swing.BorderFactory.createLineBorder(new Color(30, 30, 30), 2));

    // Create root container
    rootContainer = new Container("root") {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        GradientPaint gp = new GradientPaint(0, 0, new Color(45, 45, 48),
            0, getHeight(), new Color(30, 30, 33));
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
      }
    };
    rootContainer.setBounds(0, 0, width, height);
    setContentPane(rootContainer);
  }

  public Container getRootContainer() {
    return rootContainer;
  }
}
