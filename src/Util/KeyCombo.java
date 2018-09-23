package Util;

import javafx.scene.input.KeyCode;

public class KeyCombo
{
    KeyCode[] keyCodes;

    KeyCombo(KeyCode... keyCodes)
    {
        this.keyCodes = keyCodes;
    }

    public boolean matches(KeyCode... keyCodes)
    {
        if (this.keyCodes.length != keyCodes.length) return false;
        for (int i = 0; i < keyCodes.length; i++)
        {
            if (!keyCodes[i].equals(this.keyCodes[i])) return false;
        }
        return true;
    }

    public boolean matches(KeyCombo keyCombo)
    {
        return matches(keyCombo);
    }
}
