package Menus;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import static javafx.scene.input.MouseEvent.MOUSE_MOVED;
import static javafx.scene.input.MouseEvent.MOUSE_PRESSED;
import static javafx.scene.input.MouseEvent.MOUSE_RELEASED;

public class Mouse implements EventHandler<MouseEvent>
{
    Menu menu;

    @Override
    public void handle(MouseEvent event)
    {
        int x = (int) event.getX();
        int y = (int) event.getY();

        EventType type = event.getEventType();
        MouseButton button = event.getButton();

        if (type == MOUSE_PRESSED) menu.mouse(true, button, x, y);
        else if (type == MOUSE_RELEASED) menu.mouse(false, button, x, y);
        else if (type == MOUSE_MOVED) menu.mouse(x, y);
    }

    public void setMenu(Menu menu)
    {
        this.menu = menu;
    }
}
