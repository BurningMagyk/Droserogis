package Util;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Sprite
{
    public enum SpriteEnum {IDLE, RUN , SLIDE, THRUST, SLASH, CROUCH}
    private int totalFrames;
    private int frame = 0;
    private int width, height;
    private int offsetX, offsetY;

    private Image image;
    private String name;
    private SpriteEnum type;

    public Sprite(Image image, int totalFrames, int width, int height, int offsetX, int offsetY)
    {
        this.image = image;
        this.totalFrames = totalFrames;
        this.width = width;
        this.height = height;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    public void render(GraphicsContext gtx, float x, float y, float viewHeight)
    {
        float viewWidth = (width/height)*viewHeight;
        int srcX = offsetX + (frame*width);
        int srcY = offsetY;

        float xx = x - width / 2;
        float yy = y - height / 2;
        //gtx.clearRect(xx, yy, width, height);
        gtx.drawImage(image, srcX, srcY, width, height, xx, yy, viewWidth, viewHeight);

        frame = (frame + 1) %totalFrames;
    }
}
