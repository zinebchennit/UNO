package framework.core;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class Container extends Component {
  protected List<Component> children;

  public Container(String name) {
    super(name);
    children = new ArrayList<>();
    setLayout(null);
  }

  public void addChild(Component child) {
    children.add(child);
    add(child);
    revalidate();
    repaint();
  }

  public void removeChild(Component child) {
    children.remove(child);
    remove(child);
    revalidate();
    repaint();
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    // Les enfants sont automatiquement peints par Swing
    // Pas besoin de les repeindre explicitement
  }
}