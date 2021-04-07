package HalfPlaneIntersectionMethod;

import java.awt.geom.Point2D;

/**
 * Custom class that stores location of the point on 2-dimensional space, supports convertion to Point2D from Swing
 */
public class Point {
    //  coordinates on the X and Y axis
    private float x;
    private float y;

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
     * Convert point to Swing graphics float Point entity
     * @return Swing graphics formatted float Point entity
     */
    public Point2D convertToGraphics() {
        return new Point2D.Float(this.x, this.y);
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

    //  getters and setters

    public float getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
