package framework.core;

import java.awt.*;
import javax.swing.JFrame;

public class Window extends JFrame {
  private Container rootContainer;

  public Window(String title, int width, int height) {
    super(title);
    System.out.println("Initialisation de la fenêtre : " + title);
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
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Create poker table felt background
        Color feltColor = new Color(0, 100, 0); // Dark green felt color
        g2d.setColor(feltColor);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Add subtle texture pattern
        g2d.setColor(new Color(0, 120, 0, 30));
        for (int i = 0; i < getWidth(); i += 4) {
          for (int j = 0; j < getHeight(); j += 4) {
            g2d.fillOval(i, j, 2, 2);
          }
        }

        // Add darker border
        g2d.setColor(new Color(0, 50, 0));
        g2d.setStroke(new BasicStroke(20));
        g2d.drawRect(10, 10, getWidth() - 20, getHeight() - 20);

        // Add inner border highlight
        g2d.setColor(new Color(0, 150, 0, 100));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(20, 20, getWidth() - 40, getHeight() - 40);
      }
    };
    rootContainer.setBounds(0, 0, width, height);
    setContentPane(rootContainer);
    System.out.println("Root container configuré");

    // Add window resize listener
    addComponentListener(new java.awt.event.ComponentAdapter() {
      @Override
      public void componentResized(java.awt.event.ComponentEvent e) {
        rootContainer.setBounds(0, 0, getWidth(), getHeight());
        rootContainer.revalidate();
        rootContainer.repaint();
        
        // Notify all components about the resize
        notifyComponentsResized(getWidth(), getHeight());
      }
    });
  }

  private void notifyComponentsResized(int width, int height) {
    for (Component child : rootContainer.getChildren()) {
      if (child instanceof Container) {
        Container container = (Container) child;
        container.setBounds(0, 0, width, height);
        // Update child panels of mainPanel
        for (Component mainChild : container.getChildren()) {
          if (mainChild instanceof Container) {
            Container subContainer = (Container) mainChild;
            String name = subContainer.getName();
            if (name != null) {
              if (name.equals("computerPanel")) {
                subContainer.setBounds(0, 0, width, height / 4);
              } else if (name.equals("gamePanel")) {
                subContainer.setBounds(width / 4, height / 4, width / 2, height / 2);
              } else if (name.equals("playerPanel")) {
                subContainer.setBounds(0, height * 3 / 4, width, height / 4);
              }
            }
          }
        }
        container.revalidate();
        container.repaint();
      }
    }
  }

  public Container getRootContainer() {
    return rootContainer;
  }
}
