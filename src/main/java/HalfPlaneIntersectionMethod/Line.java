package HalfPlaneIntersectionMethod;

import Globals.Parameters;

import java.awt.geom.Line2D;
import java.util.ArrayList;

/**
 *  Custom class defining line with specification of two endpoints of this line. Supports definition of first-degree
 * polynomial describing shape of line, definition of the perpendicular, distance estimation by Euclidean and Manhattan
 * algorithms, has "exact" and "epsilon-error-based" algorithms of finding point in line. Supports conversion into
 * Line2D from Swing
 */
public class Line {
    //  ending points of the line
    private Point firstPoint;
    private Point secondPoint;

    //  coefficients for equation of the line in the format: y = mx + b
    private float m;
    private float b;

    //  perpendicular of the line
    private Line perpendicular;

    /**
     * Constructor, creates line basing on given points
     * @param firstPoint first point
     * @param secondPoint second point
     */
    public Line(Point firstPoint, Point secondPoint) {
        this.firstPoint = firstPoint;
        this.secondPoint = secondPoint;
    }

    /**
     * Constructor, creates line basing on given coordinates on each axis of two points
     * @param x1 first point x coordinate
     * @param y1 first point y coordinate
     * @param x2 second point x coordinate
     * @param y2 second point y coordinate
     */
    public Line(float x1, float y1, float x2, float y2) {
        this.firstPoint = new Point(x1, y1);
        this.secondPoint = new Point(x2, y2);
    }

    /**
     * Find line length in Euclidean algorithm
     * @return length in Euclidean algorithm
     */
    public double lineDistanceEuclidean(){
        return Math.sqrt(Math.pow((firstPoint.getX() - secondPoint.getX()), 2) + Math.pow((firstPoint.getY() - secondPoint.getY()), 2));
    }

    /**
     * Find line length in Manhattan algorithm
     * @return length in Manhattan algorithm
     */
    public float lineDistanceManhattan(){
        return Math.abs(firstPoint.getX() - secondPoint.getX()) + Math.abs(firstPoint.getY() - secondPoint.getY());
    }

    /**
     * Find middle point of the line
     * @return middle point of the line
     */
    public Point middleOfLine(){
        float x = (firstPoint.getX() + secondPoint.getX()) / 2;
        float y = (firstPoint.getY() + secondPoint.getY()) / 2;
        return new Point(x, y);
    }

    /**
     * Find line angle comparing with X-axis in degrees
     * @return line angle in degrees
     */
    public double angleOfLineInDegrees(){
        return Math.toDegrees(Math.atan2(secondPoint.getY() - firstPoint.getY(), secondPoint.getX() - firstPoint.getX()));
    }

    /**
     * Find line angle comparing with X-axis in radians
     * @return line angle in radians
     */
    public double angleOfLineInRadians(){
        return Math.atan2(secondPoint.getY() - firstPoint.getY(), secondPoint.getX() - firstPoint.getX());
    }

    /**
     * Find angle between the line and its perpendicular (use for testing)
     * @return angle between the line and its perpendicular in degrees
     */
    public double angleOfPerpendicularToLine(){
        Point middle = middleOfLine();
        return Math.toDegrees(
                Math.atan2(middle.getY() - perpendicular.getFirstPoint().getY(), middle.getX() - perpendicular.getFirstPoint().getX()) -
                Math.atan2(middle.getY() - firstPoint.getY(), middle.getX() - firstPoint.getX())
        );
    }

    /**
     *  Find perpendicular of the line using differential approach (find line angle, perpendicular angle, iterate in
     * both directions via angular differentials). Primitive approach.
     * @return perpendicular line
     */
    public Line getPerpendicularByAngleIteration(){
        //  find angle of perpendicular: get line angle and turn it by 90 deg
        double perpendicularAngleInRadians = angleOfLineInRadians() + Math.PI/2;

        //  get middle of the line
        Point middle = middleOfLine();

        //  set coordinates of perpendicular points to be the center of line
        double perpendicularFirstPointX = middle.getX();
        double perpendicularFirstPointY = middle.getY();
        double perpendicularSecondPointX = middle.getX();
        double perpendicularSecondPointY = middle.getY();

        //  calculate differentials for each axis that will be applied for finding perpendicular endpoints
        double difX = Math.cos(perpendicularAngleInRadians);
        double difY = Math.sin(perpendicularAngleInRadians);

        //  iterate in one direction until reaching axis limits
        while((perpendicularFirstPointX > 0 && perpendicularFirstPointX < Parameters.xLimit) &&
                (perpendicularFirstPointY > 0 && perpendicularFirstPointY < Parameters.yLimit)){
            perpendicularFirstPointX += difX;
            perpendicularFirstPointY += difY;
        }

        //  iterate in another direction until reaching axis limits
        while((perpendicularSecondPointX > 0 && perpendicularSecondPointX < Parameters.xLimit) &&
                (perpendicularSecondPointY > 0 && perpendicularSecondPointY < Parameters.yLimit)){
            perpendicularSecondPointX -= difX;
            perpendicularSecondPointY -= difY;
        }

        //  set reference to perpendicular in current line to estimated perpendicular
        this.perpendicular = new Line((int) perpendicularFirstPointX, (int) perpendicularFirstPointY,
                (int) perpendicularSecondPointX, (int) perpendicularSecondPointY);

        //  return perpendicular line
        return perpendicular;
    }

    /**
     * Finds perpendicular of the line basing on first degree polynomial calculations.
     * @return perpendicular of the line limited by area
     */
    public Line getPerpendicularByEquation() {
        //  if line is horizontal, then get vertical perpendicular
        if (firstPoint.getY() == secondPoint.getY()) {
            Point middle = middleOfLine();
            return new Line(new Point(middle.getX(), 0), new Point(middle.getX(), Parameters.yLimit));
        }

        //  if line is vertical, then get horizontal perpendicular
        else if (firstPoint.getX() == secondPoint.getX()) {
            Point middle = middleOfLine();
            return new Line(new Point(0, middle.getY()), new Point(Parameters.xLimit, middle.getY()));
        }

        //  storage for perpendicular endpoints
        ArrayList<Point> perpendicularEndpoints = new ArrayList<>();

        if (this.m == 0 && this.b == 0) {
            //  find coefficient of the line, defining its "angle"
            this.m = (secondPoint.getY() - firstPoint.getY()) / (secondPoint.getX() - firstPoint.getX());
            this.b = secondPoint.getY() - this.m * secondPoint.getX();
        }

        //  find equation of the perpendicular
        double perpM = -1 / this.m;
        Point middle = middleOfLine();
        double perpB = middle.getY() - perpM * middle.getX();

        //  check if there is perpendicular endpoint on the left border
        double yValueAtXBorder = perpM * 0 + perpB;
        if (yValueAtXBorder <= Parameters.yLimit && yValueAtXBorder >= 0)
            perpendicularEndpoints.add(new Point(0, (int) yValueAtXBorder));

        //  check if it is on the right border
        yValueAtXBorder = perpM * Parameters.xLimit + perpB;
        if (yValueAtXBorder <= Parameters.yLimit && yValueAtXBorder >= 0)
            perpendicularEndpoints.add(new Point(Parameters.xLimit, (int) yValueAtXBorder));

        //  check if it is on the top border
        double xValueAtYBorder = 0;
        if (perpendicularEndpoints.size() < 2) {
            xValueAtYBorder = (0 - perpB) / perpM;
            if (xValueAtYBorder <= Parameters.xLimit && xValueAtYBorder >= 0)
                perpendicularEndpoints.add(new Point((int) xValueAtYBorder, 0));
        }

        //  check if it is on the bottom border
        if (perpendicularEndpoints.size() < 2) {
            xValueAtYBorder = (Parameters.yLimit - perpB) / perpM;
            if (xValueAtYBorder <= Parameters.xLimit && xValueAtYBorder >= 0)
                perpendicularEndpoints.add(new Point((int) xValueAtYBorder, Parameters.yLimit));
        }

        if (perpendicularEndpoints.size() == 0) {
            System.out.println("somethings wrong!");
        }

        //  return perpendicular line
        return new Line(perpendicularEndpoints.get(0), perpendicularEndpoints.get(1));
    }

    /**
     * Find out if current line contains point
     * @param point point
     * @return True if point is on the line, False if not
     */
    public boolean contains(Point point) {
        //  if line is vertical or horizontal -> find if it contains the point
        if (innerHorizontalAndVerticalContainsCheck(point))
            return true;

        //  if line is neither horizontal nor vertical then find if point is on the line via distances check
        else {
            //  acceptable error
            double epsilon = 0.0001d;

            //  create two lines (let's call them segments): from first line end to point and from second line end to point
            Line firstToPoint = new Line(this.getFirstPoint(), point);
            Line secondToPoint = new Line(this.getSecondPoint(), point);

            //  find lengths of segments
            double firstToPointDistance = firstToPoint.lineDistanceEuclidean();
            double secondToPointDistance = secondToPoint.lineDistanceEuclidean();

            //  find length of the original line
            double firstToSecondDistance = this.lineDistanceEuclidean();

            //  if sum of segments is the same as line or negation between line and sum of segments is lower than
            // acceptable error
            if (firstToSecondDistance == firstToPointDistance + secondToPointDistance)
                return true;
            else
                return Math.abs(firstToSecondDistance - firstToPointDistance - secondToPointDistance) < epsilon;
        }
    }

    /**
     * Find out if current line contains point using first degree polynomial check: y = mx + b
     * @param point point presence of which must be checked
     * @return true if point is on the line, false if not
     */
    public boolean containsByEquation(Point point) {
        //  if line is vertical or horizontal -> find if it contains the point
        if (innerHorizontalAndVerticalContainsCheck(point))
            return true;

        //  if m and b coefficients have not been calculated -> calculate them
        if (this.m == 0 && this.b == 0) {
            this.m = (secondPoint.getY() - firstPoint.getY()) / (secondPoint.getX() - firstPoint.getX());
            this.b = secondPoint.getY() - this.m * secondPoint.getX();
        }

        // check if point is on the line
        Point checkPoint = new Point(point.getX(), this.m * point.getX() + this.b);
        return checkPoint.isEqual(point);
    }

    /**
     * Checks presence of point on the line if the line is horizontal or vertical
     * @param point point presence of which is requested to check
     * @return true if point is present on the line, false if not
     */
    private boolean innerHorizontalAndVerticalContainsCheck(Point point) {
        //  check if line is vertical and if point has the same X coordinate
        if (this.getFirstPoint().getX() == this.getSecondPoint().getX() && this.getSecondPoint().getX() == point.getX())
            //  find which end of line is lower, which end is higher, and if point is between ends
            if(this.getFirstPoint().getY() < this.getSecondPoint().getY())
                return point.getY() > this.getFirstPoint().getY() || point.getY() < this.getSecondPoint().getY();
            else
                return point.getY() < this.getFirstPoint().getY() || point.getY() > this.getSecondPoint().getY();

            //  check if line is horizontal and if point has the same Y coordinate
        else if (this.getFirstPoint().getY() == this.getSecondPoint().getY() && this.getFirstPoint().getY() == point.getY())
            //  find which end of line if left, which end is right, and if point is between ends
            if (this.getFirstPoint().getX() < this.getSecondPoint().getX())
                return point.getX() > this.getFirstPoint().getX() || point.getX() < this.getSecondPoint().getX();
            else
                return point.getX() < this.getFirstPoint().getX() || point.getX() > this.getSecondPoint().getX();

        //  presence is not detected, line may be not horizontal or vertical, more precise check is required
        return false;
    }

    /**
     * Convert line to the Swing graphics float line
     * @return swing graphics float line
     */
    public Line2D convertLineToGraphics(){
        return new Line2D.Float(firstPoint.getX(), firstPoint.getY(), secondPoint.getX(), secondPoint.getY());
    }

    //  getters

    public Point getFirstPoint() {
        return firstPoint;
    }

    public Point getSecondPoint() {
        return secondPoint;
    }

    public void setFirstPoint(Point firstPoint) {
        this.firstPoint = firstPoint;
    }

    public void setSecondPoint(Point secondPoint) {
        this.secondPoint = secondPoint;
    }

    @Override
    public String toString() {
        return "Line{" +
                "firstPoint=" + firstPoint +
                ", secondPoint=" + secondPoint +
                '}';
    }
}
