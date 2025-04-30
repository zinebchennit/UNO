package framework.events;

public class MouseEvent {
    private final int x;
    private final int y;
    private final int button;
    private final Object source;

    public MouseEvent(int x, int y, int button, Object source) {
        this.x = x;
        this.y = y;
        this.button = button;
        this.source = source;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getButton() {
        return button;
    }

    public Object getSource() {
        return source;
    }
}