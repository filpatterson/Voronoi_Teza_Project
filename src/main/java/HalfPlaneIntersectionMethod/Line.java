package HalfPlaneIntersectionMethod;

import java.awt.geom.Line2D;

public class Line {
    private Point firstPoint;
    private Point secondPoint;

    /**
     * line constructor using Point class
     * @param firstPoint first point reference
     * @param secondPoint second point reference
     */
    public Line(Point firstPoint, Point secondPoint) {
        this.firstPoint = firstPoint;
        this.secondPoint = secondPoint;
    }

    /**
     * line constructor using coordinates of two points
     * @param x1 first point x coordinate
     * @param y1 first point y coordinate
     * @param x2 second point x coordinate
     * @param y2 second point y coordinate
     */
    public Line(int x1, int y1, int x2, int y2) {
        this.firstPoint = new Point(x1, y1);
        this.secondPoint = new Point(x2, y2);
    }

    /**
     * find line distance in Euclidean algorithm
     * @return distance in Euclidean algorithm
     */
    public double lineDistanceEuclidean(){
        return Math.sqrt(Math.pow((firstPoint.getX() - secondPoint.getX()), 2) + Math.pow((firstPoint.getY() - secondPoint.getY()), 2));
    }

    /**
     * find line distance in Manhattan algorithm
     * @return distance in Manhattan algorithm
     */
    public double lineDistanceManhattan(){
        return Math.abs(firstPoint.getX() - secondPoint.getX()) + Math.abs(firstPoint.getY() - secondPoint.getY());
    }

    /**
     * gets coordinates of the middle of the line
     * @return middle-point of the line
     */
    public Point middleOfLine(){
        int x = (firstPoint.getX() + secondPoint.getX()) / 2;
        int y = (firstPoint.getY() + secondPoint.getY()) / 2;
        return new Point(x, y);
    }

    /**
     * get line angle in degrees
     * @return
     */
    public double angleOfLineInDegrees(){
        return Math.toDegrees(Math.atan2(secondPoint.getY() - firstPoint.getY(), secondPoint.getX() - firstPoint.getX()));
    }

    /**
     * get line angle in radians
     * @return
     */
    public double angleOfLineInRadians(){
        return Math.atan2(secondPoint.getY() - firstPoint.getY(), secondPoint.getX() - firstPoint.getX());
    }

    public Line2D convertLineToGraphics(){
        return new Line2D.Float(firstPoint.getX(), firstPoint.getY(), secondPoint.getX(), secondPoint.getY());
    }
}
