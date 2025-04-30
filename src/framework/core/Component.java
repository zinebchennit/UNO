package framework.core;

import framework.events.EventListener;
import framework.events.MouseEvent;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

public class Component extends JComponent {
    private List<EventListener> eventListeners = new ArrayList<>();
    private String name;

    public Component() {
        this("Component");
    }

    public Component(String name) {
        super();
        this.name = name;
        setOpaque(false);
        setupMouseListener();
    }

    private void setupMouseListener() {
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                notifyMouseClick(new MouseEvent(e.getX(), e.getY()));
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                notifyMousePress(new MouseEvent(e.getX(), e.getY()));
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                notifyMouseRelease(new MouseEvent(e.getX(), e.getY()));
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                notifyMouseEnter(new MouseEvent(e.getX(), e.getY()));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                notifyMouseExit(new MouseEvent(e.getX(), e.getY()));
            }
        });
    }

    public void addEventListener(EventListener listener) {
        eventListeners.add(listener);
    }

    public void removeEventListener(EventListener listener) {
        eventListeners.remove(listener);
    }

    protected void notifyMouseClick(MouseEvent event) {
        for (EventListener listener : eventListeners) {
            listener.onMouseClick(event);
        }
    }

    protected void notifyMousePress(MouseEvent event) {
        for (EventListener listener : eventListeners) {
            listener.onMousePress(event);
        }
    }

    protected void notifyMouseRelease(MouseEvent event) {
        for (EventListener listener : eventListeners) {
            listener.onMouseRelease(event);
        }
    }

    protected void notifyMouseEnter(MouseEvent event) {
        for (EventListener listener : eventListeners) {
            listener.onMouseEnter(event);
        }
    }

    protected void notifyMouseExit(MouseEvent event) {
        for (EventListener listener : eventListeners) {
            listener.onMouseExit(event);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    @Override
    public String getName() {
        return name;
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        repaint();
    }
}