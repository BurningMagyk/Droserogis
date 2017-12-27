package Menus;

import Util.Print;
import Util.Reactor;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class Keyboard implements EventHandler<KeyEvent>
{
    Reactor reactor;

    @Override
    public void handle(KeyEvent event)
    {
        EventType type = event.getEventType();
        KeyCode code = event.getCode();

        if (type == KeyEvent.KEY_PRESSED) reactor.key(true, code);
        else if (type == KeyEvent.KEY_RELEASED) reactor.key(false, code);
    }

    public void setReactor(Reactor reactor)
    {
        this.reactor = reactor;
        Print.blue("reactor: " + reactor);
    }
}
