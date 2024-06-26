package Menus;

import Util.Reactor;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import static javafx.scene.input.MouseEvent.MOUSE_MOVED;
import static javafx.scene.input.MouseEvent.MOUSE_PRESSED;
import static javafx.scene.input.MouseEvent.MOUSE_RELEASED;

public class Mouse implements EventHandler<MouseEvent>
{
    private Reactor reactor;

    private double lastX = -1;
    private double lastY = -1;

    @Override
    public void handle(MouseEvent event)
    {
        double x = event.getX();
        double y = event.getY();

        /* TODO: deadzone value will need testing later */
        double deadzone = 1;
        boolean inDeadzone =
                Math.abs(x - lastX) + Math.abs(y - lastY) < deadzone;

        EventType type = event.getEventType();
        MouseButton button = event.getButton();

        if (type == MOUSE_PRESSED)
            reactor.mouse(true, button, (int) x, (int) y);
        else if (type == MOUSE_RELEASED)
            reactor.mouse(false, button, (int) x, (int) y);
        else if (type == MOUSE_MOVED && !inDeadzone)
            reactor.mouse((int) x, (int) y);

        /* Keep track of previous coordinates to test for dead zone */
        lastX = x; lastY = y;
    }

    public void setReactor(Reactor reactor)
    {
        this.reactor = reactor;
    }
}
