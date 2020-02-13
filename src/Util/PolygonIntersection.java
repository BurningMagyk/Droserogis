package Util;

import Gameplay.entity.Entity;

import java.awt.geom.Line2D;
import java.util.ArrayList;

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
    public static boolean isIntersect(Vec2[] poly1, Vec2[] poly2)
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
    public static boolean isIntersect(Vec2[] poly, Rect rect)
    {
        for (int i=0; i<poly.length; i++)
        {
            int ii = (i+1) % poly.length;
            if (isIntersect(poly[i], poly[ii], rect)) return true;
        }

        return false;
    }
    public static boolean isIntersect(Vec2[] poly, Entity entity)
    {
        for (int i=0; i<poly.length; i++)
        {
            int ii = (i+1) % poly.length;
            if (isIntersect(poly[i], poly[ii], entity)) return true;
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
    public static boolean isIntersect(Vec2 p1, Vec2 p2, Rect rect)
    {
        if (p1.x < rect.getLeft() && p2.x < rect.getLeft()) return false;
        if (p1.y < rect.getTop()  && p2.y < rect.getTop())  return false;

        if (p1.x > rect.getRight()  && p2.x > rect.getRight())  return false;
        if (p1.y > rect.getBottom() && p2.y > rect.getBottom()) return false;
        return true;
    }
    public static boolean isIntersect(Vec2 p1, Vec2 p2, Entity entity)
    {
        if (p1.x < entity.getLeftEdge() && p2.x < entity.getLeftEdge()) return false;
        if (p1.y < entity.getTopEdge()  && p2.y < entity.getTopEdge())  return false;

        if (p1.x > entity.getRightEdge()  && p2.x > entity.getRightEdge())  return false;
        if (p1.y > entity.getBottomEdge() && p2.y > entity.getBottomEdge()) return false;
        return true;
    }







    //==================================================================================================================
    // p1 and p2 are assumed to be endpoints line segment a.
    // p3 and p4 are assumed to be endpoints line segment b.
    //
    // returns true iff the line segments a and b intersect.
    //==================================================================================================================
    public static boolean isIntersect(Vec2 p1, Vec2 p2, Vec2 p3, Vec2 p4)
    {
        return Line2D.linesIntersect(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y, p4.x, p4.y);
    }





    //==================================================================================================================
    // Given two sets of points, this method returns the convex hull of the points.
    //
    // There is no assumption about the ordering of the points in either given polygon.
    // There is no assumption of uniqueness of points.
    // The returned array will be the smallest number of points that define the convex hull in clock-wise order
    // within an error = epsilon.
    //
    // A new array is returned with all new data elements. Thus, if the input polygons can later be changed without
    // changing the data values in the returned polygon.
    // The data in the arrays passed into this method is not changed.
    //
    // The convex hull of a set X of points in the Euclidean plane is the smallest convex set that contains X.
    //   For instance, when X is a bounded subset of the plane, the convex hull may be visualized as the shape
    //   enclosed by a rubber band stretched around X.
    //
    // A Graham's scan is a method of finding the convex hull of a finite set of points in the plane with time
    // complexity O(n log n). It is named after Ronald Graham, who published the original algorithm in 1972.
    // The algorithm finds all vertices of the convex hull ordered along its boundary.
    // It uses a stack to detect and remove concavities in the boundary efficiently.
    //
    // The first step in this algorithm is to find the point with the lowest y-coordinate.
    // If the lowest y-coordinate exists in more than one point in the set, the point with the lowest x-coordinate
    // out of the candidates should be chosen.
    // Call this point P. This step takes O(n), where n is the number of points in question.
    //
    // Next, the points must be sorted in increasing order of the angle they and the point P make with the x-axis.
    // Since the number of points in this app is expected to be small, I just use bubble sort, O(n*n).
    //
    // For each point, it is first determined whether traveling from the two points immediately preceding this point
    // constitutes making a left turn or a right turn. If a right turn, the second-to-last point is not part of the
    // convex hull, and lies 'inside' it. The same determination is then made for the set of the latest point and the
    // two points that immediately precede the point found to have been inside the hull, and is repeated until a
    // "left turn" set is encountered, at which point the algorithm moves on to the next point in the set of points
    // in the sorted array minus any points that were found to be inside the hull.
    //
    // see: https://en.wikipedia.org/wiki/Graham_scan
    //==================================================================================================================
    public static Vec2[] convexHull(Vec2[] poly1, Vec2[] poly2)
    {
        double epsilon = 0.00001;

        //sort points by y-coordinate
        int totalPoints = poly1.length + poly2.length;

        //Merge all points from both polygons into a single array.
        Vec2[] poly3 = new Vec2[totalPoints];
        for (int i=0; i<poly1.length; i++)
        {
            poly3[i] = new Vec2(poly1[i]);
        }
        for (int i=0; i<poly2.length; i++)
        {
            poly3[i+poly1.length] = new Vec2(poly2[i]);
        }


        //Move lowest left most point to first element of array
        int minIdx = -1;
        float minx = Float.MAX_VALUE;
        float miny = Float.MAX_VALUE;
        for (int i=0; i<totalPoints; i++)
        {
            if ((poly3[i].y < miny) || (poly3[i].y == miny && poly3[i].x < minx))
            {
                minx = poly3[i].x;
                miny = poly3[i].y;
                minIdx = i;
            }
        }
        Vec2 tmp = poly3[0];
        poly3[0] = poly3[minIdx];
        poly3[minIdx] = tmp;


        //Compute the angle each point and the first point make with the x-axis.
        double[] angle = new double[totalPoints];
        for (int i=0; i<totalPoints; i++)
        {
            //cos of angle by dot product
            double dx = poly3[i].x - poly3[0].x;
            double dy = poly3[i].y - poly3[0].y;
            double r  = Math.sqrt(dx*dx + dy*dy);
            if (r<epsilon) angle[i] = -1;
            else
            {
                angle[i] = 1 - dx / r;
            }
            //System.out.println(i + 1 + ") " + angle[i]);
        }


        //Bubble sort in increasing order of the angle each point and the first point make with the x-axis.
        boolean didSwap = true;
        for (int i=1; i<totalPoints; i++)
        {
            for (int k=1; k<(totalPoints-i); k++)
            {
               if (angle[k]>angle[k+1])
               {
                   tmp = poly3[k];
                   poly3[k] = poly3[k+1];
                   poly3[k+1] = tmp;

                   double tmpAngle = angle[k];
                   angle[k] = angle[k+1];
                   angle[k+1] = tmpAngle;
                   didSwap = true;
               }
            }
            if (!didSwap) break;
        }

        //Add the first point to the hull and the next point that is far enough from the first point as to be interesting.
        ArrayList<Vec2> hullList = new ArrayList<>(totalPoints);
        hullList.add(poly3[0]);
        //System.out.println("Add ("+poly3[0].x+", "+poly3[0].y+")");
        int idx;
        for (idx=1; idx<totalPoints-1; idx++)
        {
            float dx = Math.abs(poly3[0].x - poly3[idx].x);
            float dy = Math.abs(poly3[0].y - poly3[idx].y);
            if ((dx > epsilon) || (dy > epsilon))
            {
                break;
            }
        }

        hullList.add(poly3[idx]);
        //System.out.println("Add ("+poly3[idx].x+", "+poly3[idx].y+")");
        //Add points to the hull that make a left turn.
        for (int i=idx+1; i<totalPoints; i++)
        {
            Vec2 p3 = poly3[i];
            while (hullList.size() > 2)
            {
                Vec2 p1 = hullList.get(hullList.size() - 2);
                Vec2 p2 = hullList.get(hullList.size() - 1);

                float a = (p2.x - p1.x) * (p3.y - p1.y);
                float b = (p2.y - p1.y) * (p3.x - p1.x);
                if (a - b <= epsilon)
                {
                    hullList.remove(hullList.size() - 1);
                    //System.out.println("Remove (" + p2.x + ", " + p2.y + ")");
                }
                else break;
            }
            hullList.add(p3);
            //System.out.println("Add (" + p3.x + ", " + p3.y + ")");
        }
        return hullList.toArray(new Vec2[hullList.size()]);
    }





    //==================================================================================================================
    // Test cases. To run, add vm option: -enableassertions
    //==================================================================================================================
    public static void main(String[] args)
    {

        Rect rect1 = new Rect(10,0, 20, 10);

        Vec2[] poly1 =
        {
                new Vec2( 0,40),
                new Vec2(20,20),
                new Vec2(30,30),
                new Vec2(10,50)
        };

        assert(isIntersect(poly1, rect1) == false);
        rect1.setBottom(rect1.getLeft()+20);
        assert(isIntersect(poly1, rect1) == true);


        Rect rect2 = new Rect(20,30, 40, 10);
        assert(isIntersect(poly1, rect2) == true);
        rect2.setLeft(31);
        assert(isIntersect(poly1, rect2) == false);


        Vec2[] sword =
        {
                new Vec2( 0,10),
                new Vec2(10, 0),
                new Vec2(40,30),
                new Vec2(30,40)
        };

        Vec2[] tri1 =
        {
                new Vec2(40,0),
                new Vec2(50,10),
                new Vec2(30,10)
        };

        Vec2[] tri2 =
        {
                new Vec2(40,0),
                new Vec2(50,10),
                new Vec2(20,20)
        };


        assert(isIntersect(sword, tri1) == false);
        assert(isIntersect(sword, tri2) == true);


        Vec2[] poly =
                {
                        new Vec2(40,0),
                        new Vec2(50,10),
                        new Vec2(20,20),
                        new Vec2(50,50),
                        new Vec2(70,30),
                        new Vec2(50,0),
                        new Vec2(0,40),
                        new Vec2(0,0),
                        new Vec2(60,-20),
                        new Vec2(20,60)
                };



        Vec2[] hull = convexHull(sword, poly);
        System.out.println("Convex Hull:");
        for (int i=0; i<hull.length; i++)
        {
            System.out.println(i+" : " + hull[i].x + ", "+hull[i].y);
        }

    }
}
