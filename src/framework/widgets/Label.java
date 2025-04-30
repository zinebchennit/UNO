package framework.widgets;

import framework.core.Component;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class Label extends Component {
    private String text;
    private Color textColor;
    private Font font;

    public Label(String text) {
        super();
        this.text = text;
        this.textColor = Color.WHITE;
        this.font = new Font("Arial", Font.PLAIN, 12);
    }

    public void setText(String text) {
        this.text = text;
        repaint();
    }

    public void setTextColor(Color color) {
        this.textColor = color;
        repaint();
    }

    @Override
    public void setFont(Font font) {
        this.font = font;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (text != null) {
            g.setColor(textColor);
            g.setFont(font);
            g.drawString(text, 0, g.getFontMetrics().getAscent());
        }
    }
} 