package Util;

public class Rect
{
    private double left, top, right, bottom;

    public Rect(double left, double top, double width, double height)
    {
        this.left   = left;
        this.top    = top;
        this.right  = left + width;
        this.bottom = top + height;
    }

    public double getLeft() { return left; }
    public double getTop() { return top; }
    public double getRight() {return right;}
    public double getBottom() {return bottom;}
    public double getWidth() { return right-left; }
    public double getHeight() { return bottom-top; }

    public void setLeft(double left) {this.left = left;}
    public void setTop(double top)   {this.top  = top;}
    public void setRight(double right) { this.right = right; }
    public void setBottom(double bottom) { this.bottom = bottom; }
}
