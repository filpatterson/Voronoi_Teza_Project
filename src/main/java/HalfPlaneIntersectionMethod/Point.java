package HalfPlaneIntersectionMethod;

import java.awt.geom.Point2D;

public class Point {
    //  coordinates on the X and Y axis
    private int x;
    private int y;

    /**
     * Constructor, simple point with X and Y coordinates
     * @param x X-axis coordinate
     * @param y Y-axis coordinate
     */
    public Point(int x, int y) {
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
    public boolean isEqual(int x, int y) {
        if (this.x == x) {
            return this.y == y;
        }

        return false;
    }

    /**
     * Find if coordinates of this point match coordinates of another point
     * @param anotherPoint another point
     * @return True if points have the same coordinates, False if not
     */
    public boolean isEqual(Point anotherPoint) {
        if (this.x == anotherPoint.getX()) {
            return this.y == anotherPoint.getY();
        }

        return false;
    }

    //  getters and setters

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
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
