package framework.events;

public interface EventListener {
    void onMouseClick(MouseEvent event);
    void onMousePress(MouseEvent event);
    void onMouseRelease(MouseEvent event);
}