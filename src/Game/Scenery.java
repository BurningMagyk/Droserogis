package Game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

class Scenery implements Actor
{
    GraphicsContext context;
    Clock clock;
    Sky sky;
    Horizon horizon;

    Scenery(GraphicsContext context)
    {
        this.context = context;
        clock = new Clock();
        sky = new Sky(context);
        horizon = new Horizon(context);
    }

    @Override
    public void act()
    {
        clock.act();
        sky.act();
        horizon.act();
    }

    @Override
    public void draw(int x, int y)
    {
        sky.act();
        horizon.act();
    }

    /**
     * Will mostly just draw a gradient.
     * Can be acted upon by certain Actors.
     */
    private class Sky implements Actor
    {
        GraphicsContext context;
        int width, height;
        /* How far from the top there is no gradient */
        int solidDistance;
        int grad[] = new int[3];
        int solid[] = new int[3];
        Sky(GraphicsContext context)
        {
            this.context = context;
            width = (int) context.getCanvas().getWidth();
            height = (int) context.getCanvas().getHeight();

            /* Sample value for solidDistance.
             * Should get value from in-game clock */
            solidDistance = height / 2;

            solid[0] = (int) (Color.LIGHTBLUE.getRed() * 255);
            solid[1] = (int) (Color.LIGHTBLUE.getGreen() * 255);
            solid[2] = (int) (Color.LIGHTBLUE.getBlue() * 255);

            grad[0] = (int) (Color.ORANGE.getRed() * 255);
            grad[1] = (int) (Color.ORANGE.getGreen() * 255);
            grad[2] = (int) (Color.ORANGE.getBlue() * 255);
        }
        public void act(){}
        public void draw(int x, int y)
        {
            context.setStroke(Color.rgb(
                    solid[0], solid[1], solid[2]));
            for (int i = 0; i < solidDistance; i++)
            {
                context.strokeLine(0, i, width - 1, i);
            }
            int remainingDistance = height - solidDistance;
            double solidColor[] = {
                    this.solid[0],
                    this.solid[1],
                    this.solid[2]};
            double colorInc[] = {
                    (solidColor[0] - grad[0]) / remainingDistance,
                    (solidColor[1] - grad[1]) / remainingDistance,
                    (solidColor[2] - grad[2]) / remainingDistance};
            for (int i = solidDistance + 1; i < height; i++)
            {
                solidColor[0] -= colorInc[0];
                solidColor[1] -= colorInc[1];
                solidColor[2] -= colorInc[2];
                context.setStroke(Color.rgb((int) solidColor[0], (int) solidColor[1], (int) solidColor[2]));
                context.strokeLine(0, i, width - 1, i);
            }
        }
    }

    private class Horizon implements Actor
    {
        GraphicsContext context;

        Horizon(GraphicsContext context)
        {
            this.context = context;
        }

        @Override
        public void act() {

        }

        @Override
        public void draw(int x, int y) {

        }
    }

    private class Clock
    {
        public void act() {

        }
    }
}