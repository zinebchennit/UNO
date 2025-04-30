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
  public void setChildBounds(Component child, int x, int y, int width, int height) {
    child.setBounds(x, y, width, height);
}
public void resizeChild(Component child, int newWidth, int newHeight) {
  child.setSize(newWidth, newHeight);
  revalidate();
  repaint();
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

  public List<Component> getChildren() {
    return children;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    // Les enfants sont automatiquement peints par Swing
    // Pas besoin de les repeindre explicitement
  }
}