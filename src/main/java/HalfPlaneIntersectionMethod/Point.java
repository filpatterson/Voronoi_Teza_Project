package HalfPlaneIntersectionMethod;

import Globals.Parameters;

import java.awt.geom.Point2D;

/**
 * Custom class that stores location of the point on 2-dimensional space, supports convertion to Point2D from Swing
 */
public class Point extends Point2D.Float {
    /**
     * Constructor, simple point with X and Y coordinates
     * @param x X-axis coordinate
     * @param y Y-axis coordinate
     */
    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Find if coordinates match coordinates of the point
     * @param x X-axis coordinate to check
     * @param y Y-axis coordinate to check
     * @return True if point has the same coordinates as transmitted ones, False if not
     */
    public boolean isEqual(float x, float y) {
        return this.x == x && this.y == y;
    }

    /**
     * Find if coordinates of this point match coordinates of another point
     * @param anotherPoint another point
     * @return True if points have the same coordinates, False if not
     */
    public boolean isEqual(Point anotherPoint) {
        return this.x == anotherPoint.getX() && this.y == anotherPoint.getY();
    }

    public boolean isOnTheSameXLevel(float x) {
        return this.x == x;
    }

    public boolean isOnTheSameYLevel(float y) {
        return this.y == y;
    }

    public boolean incrementX() {
        if (this.x < Parameters.xLimit)
            this.x += 1;
        else
            return false;

        return true;
    }

    public boolean incrementY() {
        if (this.y < Parameters.yLimit)
            this.y += 1;
        else
            return false;

        return true;
    }

    public boolean decrementX() {
        if (this.x > 0)
            this.x -= 1;
        else
            return false;

        return true;
    }

    public boolean decrementY() {
        if (this.x > 0)
            this.x -= 1;
        else
            return false;

        return true;
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
