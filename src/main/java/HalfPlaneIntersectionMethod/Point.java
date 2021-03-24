package HalfPlaneIntersectionMethod;

import java.awt.geom.Point2D;

public class Point {
    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

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

    public Point2D convertToGraphics() {
        return new Point2D.Float(this.x, this.y);
    }

    public boolean isEqual(int x, int y) {
        if (this.x == x) {
            if (this.y == y) {
                return true;
            }
        }

        return false;
    }

    public boolean isEqual(Point anotherPoint) {
        if (this.x == anotherPoint.getX()) {
            if (this.y == anotherPoint.getY()) {
                return true;
            }
        }

        return false;
    }
}
