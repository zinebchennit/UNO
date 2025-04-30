package framework.core;

import framework.events.EventListener;
import framework.events.MouseEvent;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

public class Component extends JComponent {
    private final List<EventListener> listeners = new ArrayList<>();
    private String name;

    public Component(String name) {
        super();
        this.name = name;
        setOpaque(false);
        setupMouseListener();
    }

    public Component() {
        setOpaque(false);
    }

    private void setupMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                MouseEvent event = new MouseEvent(e.getX(), e.getY(), e.getButton(), Component.this);
                notifyMouseClick(event);
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                MouseEvent event = new MouseEvent(e.getX(), e.getY(), e.getButton(), Component.this);
                notifyMousePress(event);
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                MouseEvent event = new MouseEvent(e.getX(), e.getY(), e.getButton(), Component.this);
                notifyMouseRelease(event);
            }
        });
    }

    public void addEventListener(EventListener listener) {
        listeners.add(listener);
    }

    public void removeEventListener(EventListener listener) {
        listeners.remove(listener);
    }

    protected void notifyMouseClick(MouseEvent event) {
        for (EventListener listener : listeners) {
            listener.onMouseClick(event);
        }
    }

    protected void notifyMousePress(MouseEvent event) {
        for (EventListener listener : listeners) {
            listener.onMousePress(event);
        }
    }

    protected void notifyMouseRelease(MouseEvent event) {
        for (EventListener listener : listeners) {
            listener.onMouseRelease(event);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}