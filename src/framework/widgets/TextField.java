package framework.widgets;

import framework.core.Component;
import framework.events.EventListener;
import framework.events.MouseEvent;
import java.awt.*;
import java.awt.event.*;

public class TextField extends Component {
    private String text = "";
    private String placeholder;
    private Color textColor = Color.BLACK;
    private Color backgroundColor = Color.WHITE;
    private Color borderColor = new Color(200, 200, 200);
    private Font font = new Font("Arial", Font.PLAIN, 14);
    private boolean isFocused = false;
    private int caretPosition = 0;
    private javax.swing.Timer caretTimer;
    private boolean showCaret = true;

    public TextField(String placeholder) {
        super();
        this.placeholder = placeholder;
        setupListeners();
        setupCaretTimer();
    }

    private void setupListeners() {
        addEventListener(new EventListener() {
            @Override
            public void onMouseClick(MouseEvent event) {
                requestFocus();
            }

            @Override
            public void onMousePress(MouseEvent event) {}

            @Override
            public void onMouseRelease(MouseEvent event) {}

            @Override
            public void onMouseEnter(MouseEvent event) {
                setCursor(new Cursor(Cursor.TEXT_CURSOR));
            }

            @Override
            public void onMouseExit(MouseEvent event) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
                    if (text.length() > 0 && caretPosition > 0) {
                        text = text.substring(0, caretPosition - 1) + text.substring(caretPosition);
                        caretPosition--;
                    }
                } else if (!Character.isISOControl(e.getKeyChar())) {
                    text = text.substring(0, caretPosition) + e.getKeyChar() + text.substring(caretPosition);
                    caretPosition++;
                }
                repaint();
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT && caretPosition > 0) {
                    caretPosition--;
                    repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && caretPosition < text.length()) {
                    caretPosition++;
                    repaint();
                }
            }
        });

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                caretTimer.start();
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                caretTimer.stop();
                repaint();
            }
        });
    }

    private void setupCaretTimer() {
        caretTimer = new javax.swing.Timer(500, e -> {
            showCaret = !showCaret;
            repaint();
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Draw background
        g2d.setColor(backgroundColor);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

        // Draw border
        g2d.setColor(isFocused ? new Color(52, 152, 219) : borderColor);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);

        // Draw text
        g2d.setFont(font);
        FontMetrics metrics = g2d.getFontMetrics();
        int textY = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();

        if (text.isEmpty() && !isFocused) {
            // Draw placeholder
            g2d.setColor(new Color(150, 150, 150));
            g2d.drawString(placeholder, 10, textY);
        } else {
            // Draw actual text
            g2d.setColor(textColor);
            g2d.drawString(text, 10, textY);

            // Draw caret
            if (isFocused && showCaret) {
                String textBeforeCaret = text.substring(0, caretPosition);
                int caretX = 10 + metrics.stringWidth(textBeforeCaret);
                g2d.drawLine(caretX, textY - metrics.getAscent(), caretX, textY + metrics.getDescent());
            }
        }

        g2d.dispose();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        caretPosition = text.length();
        repaint();
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    @Override
    public boolean isFocusable() {
        return true;
    }
} 