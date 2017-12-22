package Menus;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class Keyboard implements EventHandler<KeyEvent>
{
    Menu menu;

    @Override
    public void handle(KeyEvent event)
    {
        EventType type = event.getEventType();
        KeyCode code = event.getCode();

        if (type == KeyEvent.KEY_PRESSED) menu.key(true, code);
        else if (type == KeyEvent.KEY_RELEASED) menu.key(false, code);
    }

    public void setMenu(Menu menu)
    {
        this.menu = menu;
    }
}
