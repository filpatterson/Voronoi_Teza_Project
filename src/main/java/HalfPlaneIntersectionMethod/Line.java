package HalfPlaneIntersectionMethod;

import java.awt.geom.Line2D;

public class Line {
    private Point firstPoint;
    private Point secondPoint;
    private Line perpendicular;

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
     * @return angle of line to X dimension in degrees
     */
    public double angleOfLineInDegrees(){
        return Math.toDegrees(Math.atan2(secondPoint.getY() - firstPoint.getY(), secondPoint.getX() - firstPoint.getX()));
    }

    /**
     * get line angle in radians
     * @return angle of line to X dimension in radians
     */
    public double angleOfLineInRadians(){
        return Math.atan2(secondPoint.getY() - firstPoint.getY(), secondPoint.getX() - firstPoint.getX());
    }

    /**
     * find angle between perpendicular of the line to the line itself (use for testing)
     * @return angle between perpendicular and the line in degrees
     */
    public double angleOfPerpendicularToLine(){
        Point middle = middleOfLine();
        return Math.toDegrees(
                Math.atan2(middle.getY() - perpendicular.getFirstPoint().getY(), middle.getX() - perpendicular.getFirstPoint().getX()) -
                Math.atan2(middle.getY() - firstPoint.getY(), middle.getX() - firstPoint.getX())
        );
    }

    /**
     * calculate and create perpendicular to the line
     * @param xLimit limit of X axis size
     * @param yLimit limit of Y axis size
     * @return perpendicular line object
     */
    public Line getPerpendicularLine(int xLimit, int yLimit){
        //  find calculate angle that will be perpendicular to line
        double perpendicularAngleInRadians = angleOfLineInRadians() + Math.PI/2;

        //  find middle of the line
        Point middle = middleOfLine();

        //  set all values of the perpendicular to the center of the line
        double perpendicularFirstPointX = middle.getX();
        double perpendicularFirstPointY = middle.getY();
        double perpendicularSecondPointX = middle.getX();
        double perpendicularSecondPointY = middle.getY();

        //  calculate differentials for each axis that will be applied for finding perpendicular endpoints
        double difX = Math.cos(perpendicularAngleInRadians);
        double difY = Math.sin(perpendicularAngleInRadians);

        //  iterate in one direction until reaching axis limits
        while((perpendicularFirstPointX > 0 && perpendicularFirstPointX < xLimit) && (perpendicularFirstPointY > 0 && perpendicularFirstPointY < yLimit)){
            perpendicularFirstPointX += difX;
            perpendicularFirstPointY += difY;
        }

        //  iterate in another direction until reaching axis limits
        while((perpendicularSecondPointX > 0 && perpendicularSecondPointX < xLimit) && (perpendicularSecondPointY > 0 && perpendicularSecondPointY < yLimit)){
            perpendicularSecondPointX -= difX;
            perpendicularSecondPointY -= difY;
        }

        //  set perpendicular line reference to the current line
        this.perpendicular = new Line((int) perpendicularFirstPointX, (int) perpendicularFirstPointY, (int) perpendicularSecondPointX, (int) perpendicularSecondPointY);

        //  return perpendicular line
        return perpendicular;
    }

    /**
     * make line drawable for Swing graphics
     * @return swing graphics drawable 2D line
     */
    public Line2D convertLineToGraphics(){
        return new Line2D.Float(firstPoint.getX(), firstPoint.getY(), secondPoint.getX(), secondPoint.getY());
    }

    public Point getFirstPoint() {
        return firstPoint;
    }

    public Point getSecondPoint() {
        return secondPoint;
    }

    @Override
    public String toString() {
        return "Line{" +
                "firstPoint=" + firstPoint +
                ", secondPoint=" + secondPoint +
                '}';
    }
}
