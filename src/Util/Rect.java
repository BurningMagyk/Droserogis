package Util;

public class Rect
{
    private double left, top, width, height;

    public Rect(double left, double top, double width, double height)
    {
        setWidth(width);
        setHeight(height);
        this.left   = left;
        this.top    = top;
    }


    public double getLeft() { return left; }
    public double getTop() { return top; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }

    public double getRight() {return left+width;}
    public double getBottom() {return top+height;}


    public void setLeft(double left) {this.left = left;}
    public void setTop(double top)   {this.top  = top;}


    public void setWidth(double width)
    {
        if (width <= 0) throw new IllegalArgumentException("width must be > 0");
        this.width  = width;
    }

    public void setHeight(double height)
    {
        if (height <= 0) throw new IllegalArgumentException("height must be > 0");
        this.height = height;
    }
}
