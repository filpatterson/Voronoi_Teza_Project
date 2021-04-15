package HalfPlaneIntersectionMethod;

import Globals.Parameters;

import java.awt.geom.Point2D;

/**
 * Custom class that stores location of the point on 2-dimensional space, supports convertion to Point2D from Swing
 */
public class Point extends Point2D.Double {
    /**
     * Constructor, simple point with X and Y coordinates
     * @param x X-axis coordinate
     * @param y Y-axis coordinate
     */
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Find if coordinates match coordinates of the point
     * @param x X-axis coordinate to check
     * @param y Y-axis coordinate to check
     * @return True if point has the same coordinates as transmitted ones, False if not
     */
    public boolean isEqual(double x, double y) {
        return this.x == x && this.y == y;
    }

    /**
     * Find if coordinates of this point match coordinates of another point
     * @param anotherPoint another point
     * @return True if points have the same coordinates, False if not
     */
    public boolean isEqual(Point anotherPoint) {
        return x == anotherPoint.x && y == anotherPoint.y;
    }

    /**
     * find "weight" of the line basing on its coordinates
     * @return "weight" of the point
     */
    public double getWeight() {
        return y * Parameters.xLimit + x;
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
