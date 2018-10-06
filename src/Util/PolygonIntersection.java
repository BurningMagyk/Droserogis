package Util;

//======================================================================================================================
//This class tests intersections of convex polygons.
//======================================================================================================================
public class PolygonIntersection
{

    //==================================================================================================================
    // poly1 and poly2 are assumed to be arrays of vertices of closed, convex polygons with the last element assumed
    //    to be connected to the first.
    //
    // returns true iff the polygons 1 and 2 intersect.
    //==================================================================================================================
    public static boolean isIntersect(Point[] poly1, Point[] poly2)
    {
        for (int i=0; i<poly1.length; i++)
        {
            int ii = (i+1) % poly1.length;

            for (int k=0; k<poly2.length; k++)
            {
                int kk = (k + 1) % poly2.length;

                if (isIntersect(poly1[i], poly1[ii], poly2[k], poly2[kk])) return true;
            }
        }

        return false;
    }







    //==================================================================================================================
    // poly is assumed to be an array of vertices of a closed, convex polygon with the last element assumed to be
    //    connected to the first.
    //
    // rect is assumed to be an axis aligned rectangle.
    //
    // returns true iff the polygon and rectangle intersect.
    //==================================================================================================================
    public static boolean isIntersect(Point[] poly, Rect rect)
    {
        for (int i=0; i<poly.length; i++)
        {
            int ii = (i+1) % poly.length;
            if (isIntersect(poly[i], poly[ii], rect)) return true;
        }

        return false;
    }





    //==================================================================================================================
    // p1 and p2 are assumed to be endpoints of a line segment.
    //
    // rect is assumed to be an axis aligned rectangle.
    //
    // returns true iff the line segment and rectangle intersect.
    //==================================================================================================================
    public static boolean isIntersect(Point p1, Point p2, Rect rect)
    {
        if (p1.x < rect.getLeft() && p2.x < rect.getLeft()) return false;
        if (p1.y < rect.getTop()  && p2.y < rect.getTop())  return false;

        if (p1.x > rect.getRight()  && p2.x > rect.getRight())  return false;
        if (p1.y > rect.getBottom() && p2.y > rect.getBottom()) return false;
        return true;
    }







    //==================================================================================================================
    // p1 and p2 are assumed to be endpoints line segment a.
    // p3 and p4 are assumed to be endpoints line segment b.
    //
    // returns true iff the line segments a and b intersect.
    //==================================================================================================================
    public static boolean isIntersect(Point p1, Point p2, Point p3, Point p4)
    {
        //if (p1.x < rect.getLeft() && p2.x < rect.getLeft()) return false;
        //if (p1.y < rect.getTop()  && p2.y < rect.getTop())  return false;

        //if (p1.x > rect.getRight()  && p2.x > rect.getRight())  return false;
        //if (p1.y > rect.getBottom() && p2.y > rect.getBottom()) return false;
        return true;
    }





    //==================================================================================================================
    // Test cases. To run, add vm option: -enableassertions
    //==================================================================================================================
    public static void main(String[] args)
    {

        Rect rect1 = new Rect(10,0, 20, 10);

        Point[] poly1 =
        {
                new Point( 0,40),
                new Point(20,20),
                new Point(30,30),
                new Point(10,50)
        };

        assert(isIntersect(poly1, rect1) == false);
        rect1.setHeight(20);
        assert(isIntersect(poly1, rect1) == true);


        Rect rect2 = new Rect(20,30, 40, 10);
        assert(isIntersect(poly1, rect2) == true);
        rect2.setLeft(31);
        assert(isIntersect(poly1, rect2) == false);




    }
}
