package Gameplay.Entities;

import Util.Print;
import javafx.scene.paint.Color;

public class DebugText
{
    private final float LIFE = 1F;
    private float life;
    private float dist;
    private Color color;

    String text = null;

    public DebugText(String text, Color color)
    {
        life = LIFE;
        dist = 0;
        this.text = text;
        this.color = color;
    }

    public boolean step(float deltaSec)
    {
        life -= deltaSec;
        if (life > LIFE / 2) dist = (1 - (life / LIFE)) * 2;
        else dist = 1;
        return life <= 0;
    }

    public String getText() { return text; }
    public float getDist() { return dist / LIFE; }
    public Color getColor() { return color; }
}
