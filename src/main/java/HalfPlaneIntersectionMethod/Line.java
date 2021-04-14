package HalfPlaneIntersectionMethod;

import Globals.Parameters;

import java.awt.geom.Line2D;

/**
 *  Custom class defining line with specification of two endpoints of this line. Supports definition of first-degree
 * polynomial describing shape of line, definition of the perpendicular, distance estimation by Euclidean and Manhattan
 * algorithms, has "exact" and "epsilon-error-based" algorithms of finding point in line. Supports conversion into
 * Line2D from Swing
 */
public class Line extends Line2D.Float {
    //  another form of points of line
    private Point firstPoint;
    private Point secondPoint;

    //  coefficients for equation of the line in the format: y = mx + b
    public float m;
    public float b;

    //  code of line defining its property: vertical (2) for x1 = x2, horizontal (1) for y1 = y2
    // and simple (0) for other case
    public byte codeOfLine;

    //  coordinates of the middle
    private Point middle;

    /**
     * Constructor, creates line basing on given points
     * @param firstPoint first point
     * @param secondPoint second point
     */
    public Line(Point firstPoint, Point secondPoint) {
        //  set x and y values for each point
        x1 = firstPoint.x;
        x2 = secondPoint.x;
        y1 = firstPoint.y;
        y2 = secondPoint.y;

        //  set points
        this.firstPoint = firstPoint;
        this.secondPoint = secondPoint;

        //  find if line is vertical, horizontal or simple
        if (x1 == x2)
            codeOfLine = Parameters.VERTICAL_LINE_CODE;
        else if (y1 == y2)
            codeOfLine = Parameters.HORIZONTAL_LINE_CODE;
        else
            codeOfLine = Parameters.SIMPLE_LINE_CODE;

        //  if line is simple, then find coefficients for formula describing line (y = mx + b)
        if (codeOfLine == Parameters.SIMPLE_LINE_CODE) {
            m = (y2 - y1) / (x2 - x1);
            b = y2 - m * x2;
        }
    }

    /**
     * Constructor, creates line basing on given coordinates on each axis of two points
     * @param x1 first point x coordinate
     * @param y1 first point y coordinate
     * @param x2 second point x coordinate
     * @param y2 second point y coordinate
     */
    public Line(float x1, float y1, float x2, float y2) {
        //  set x and y values for each point
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;

        //  set points
        firstPoint = new Point(x1, y1);
        secondPoint = new Point(x2, y2);

        //  find if line is vertical, horizontal or simple
        if (this.x1 == this.x2)
            codeOfLine = Parameters.VERTICAL_LINE_CODE;
        else if (this.y1 == this.y2)
            codeOfLine = Parameters.HORIZONTAL_LINE_CODE;
        else
            codeOfLine = Parameters.SIMPLE_LINE_CODE;

        //  if line is simple, then find coefficients for formula describing line (y = mx + b)
        if (codeOfLine == Parameters.SIMPLE_LINE_CODE) {
            m = (this.y2 - this.y1) / (this.x2 - this.x1);
            b = this.y2 - m * this.x2;
        }
    }

    private Line(float x1, float y1, float x2, float y2, float m, float b) {
        //  set x and y values for each point
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;

        //  set points
        firstPoint = new Point(x1, y1);
        secondPoint = new Point(x2, y2);

        this.m = m;
        this.b = b;

        codeOfLine = Parameters.SIMPLE_LINE_CODE;
    }



    /**
     * Find line length in Euclidean algorithm
     * @return length in Euclidean algorithm
     */
    public float lengthEuclidean(){
        return (float) Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
    }

    /**
     * Find line length in Manhattan algorithm
     * @return length in Manhattan algorithm
     */
    public float lengthManhattan(){
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    /**
     * Find middle point of the line
     * @return middle point of the line
     */
    public Point middleOfLine() {
        //  if line has not been already found
        if (middle == null)
            middle = new Point((x1 + x2) / 2, (y1 + y2) / 2);

        //  give middle of the line
        return middle;
    }

    public float getXOfMiddle() {
        return (x1 + x2) / 2;
    }

    public float getYOfMiddle() {
        return (y1 + y2) / 2;
    }

    /**
     * Find line angle comparing with X-axis in degrees
     * @return line angle in degrees
     */
    public double angleOfLineInDegrees(){
        return Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));
    }

    /**
     * Find line angle comparing with X-axis in radians
     * @return line angle in radians
     */
    public double angleOfLineInRadians(){
        return Math.atan2(y2 - y1, x2 - x1);
    }

    /**
     * Finds perpendicular of the line basing on first degree polynomial calculations.
     * @return perpendicular of the line limited by area
     */
    public Line getPerpendicularOfLine() {
        Point middle = middleOfLine();

        //  if line is horizontal, then get vertical perpendicular
        if (y1 == y2)
            return new Line(new Point(middle.x, 0), new Point(middle.x, Parameters.yLimit));

        //  if line is vertical, then get horizontal perpendicular
        else if (x1 == x2)
            return new Line(new Point(0, middle.y), new Point(Parameters.xLimit, middle.y));

        //  find equation of the perpendicular (perpY = perpM * perpX + perpB)
        float perpM = -1 / m;
        float perpB = middle.y - perpM * middle.x;

        //  return the line that will be limited conform limits of x-axis
        return new Line(0, perpM * 0 + perpB, Parameters.xLimit, perpM * Parameters.xLimit + perpB, perpM, perpB);
    }

    /**
     * finds perpendicular of line between two points without constructing the line itself
     * @param firstPoint first point of "virtual" line
     * @param secondPoint second point of "virtual" line
     * @return perpendicular for those points
     */
    public static Line getPerpendicularOfPoints(Point firstPoint, Point secondPoint) {
        //  find middle between two points
        Point middle = new Point((firstPoint.x + secondPoint.x) / 2, (firstPoint.y + secondPoint.y) / 2);

        //  if line is horizontal, then get vertical perpendicular
        if (firstPoint.y == secondPoint.y)
            return new Line(new Point(middle.x, 0), new Point(middle.x, Parameters.yLimit));
            //  if line is vertical, then get horizontal perpendicular
        else if (firstPoint.x == secondPoint.x)
            return new Line(new Point(0, middle.y), new Point(Parameters.xLimit, middle.y));

        //  find m of original line
        float m = (secondPoint.y - firstPoint.y) / (secondPoint.x - firstPoint.x);

        //  find equation of the perpendicular (perpY = perpM * perpX + perpB)
        float perpM = -1 / m;
        float perpB = middle.y - perpM * middle.x;

        //  return the line that will be limited conform limits of x-axis
        return new Line(0, perpM * 0 + perpB, Parameters.xLimit, perpM * Parameters.xLimit + perpB, perpM, perpB);
    }

    /**
     * Find out if current line contains point using first degree polynomial check: y = mx + b
     * @param point point presence of which must be checked
     * @return true if point is on the line, false if not
     */
    public boolean containsByEquation(Point point) {
        //  if line is vertical or horizontal -> find if it contains the point
        if (codeOfLine != Parameters.SIMPLE_LINE_CODE)
            return innerHorizontalAndVerticalContainsCheck(point);

        // check if point is on the line
        return point.isEqual(point.x, m * point.x + b);
    }

    public boolean containsByEquation(float x, float y) {
        //  if line is vertical or horizontal -> find if it contains the point
        if (codeOfLine != Parameters.SIMPLE_LINE_CODE)
            return innerHorizontalAndVerticalContainsCheck(x, y);

        // check if point is on the line
        return m * x + b == y;
    }

    /**
     * Checks presence of point on the line if the line is horizontal or vertical
     * @param point point presence of which is requested to check
     * @return true if point is present on the line, false if not
     */
    private boolean innerHorizontalAndVerticalContainsCheck(Point point) {
        //  check if line is vertical and if point has the same X coordinate
        if (codeOfLine == Parameters.VERTICAL_LINE_CODE && x2 == point.x)
            //  find which end of line is lower, which end is higher, and if point is between ends
            return (y1 < y2) ? point.y > y1 || point.y < y2 : point.y < y1 || point.y > y2;

        //  check if line is horizontal and if point has the same Y coordinate
        else if (codeOfLine == Parameters.HORIZONTAL_LINE_CODE && y1 == point.y)
            //  find which end of line if left, which end is right, and if point is between ends
            return (x1 < x2) ? point.x > x1 || point.x < x2 : point.x < x1 || point.x > x2;

        else
            return false;
    }

    private boolean innerHorizontalAndVerticalContainsCheck(float x, float y) {
        //  check if line is vertical and if point has the same X coordinate
        if (codeOfLine == Parameters.VERTICAL_LINE_CODE && x2 == x)
            //  find which end of line is lower, which end is higher, and if point is between ends
            return (y1 < y2) ? y > y1 || y < y2 : y < y1 || y > y2;

            //  check if line is horizontal and if point has the same Y coordinate
        else if (codeOfLine == Parameters.HORIZONTAL_LINE_CODE && y1 == y)
            //  find which end of line if left, which end is right, and if point is between ends
            return (x1 < x2) ? x > x1 || x < x2 : x < x1 || x > x2;

        else
            return false;
    }

    /**
     * find point of two lines intersection
     * @param perpendicular perpendicular line
     * @return point of two lines intersection
     */
    public Point findIntersection(Line perpendicular) {
        //  if lines are identical by their type
        if (codeOfLine == perpendicular.codeOfLine) {
            //  if lines are simple
            if (codeOfLine == Parameters.SIMPLE_LINE_CODE) {
                //  if lines have the same formulas
                if (perpendicular.m == m && perpendicular.b == b) {
                    System.err.println("Perp = " + perpendicular + "; " +
                            "edge = " + this + "; " +
                            "perp = {m = " + perpendicular.m + ", b = " + perpendicular.b + "}; " +
                            "edge = {m = " + m + ", b = " + b + "}");
                    return null;
                //  if lines have different formulas
                } else {
                    float intersectionX = Math.abs((perpendicular.b - this.b) / (this.m - perpendicular.m));
                    return new Point(intersectionX, this.m * intersectionX + this.b);
                }
            //  lines are both vertical or horizontal, no intersection possible
            } else
                return null;
        } else if (codeOfLine == Parameters.HORIZONTAL_LINE_CODE) {
            if (perpendicular.codeOfLine == Parameters.VERTICAL_LINE_CODE)
                return new Point(perpendicular.x1, y1);
            else
                return new Point(Math.abs((perpendicular.b - y1) / perpendicular.m), y1);
        } else if (codeOfLine == Parameters.VERTICAL_LINE_CODE) {
            if (perpendicular.codeOfLine == Parameters.HORIZONTAL_LINE_CODE)
                return new Point(x1, perpendicular.y1);
            else
                return new Point(x1, perpendicular.m * x1 + perpendicular.b);
        } else if (perpendicular.codeOfLine == Parameters.HORIZONTAL_LINE_CODE)
            return new Point(Math.abs((b - perpendicular.y1) / m), perpendicular.y1);
        //  perpendicular is vertical and another line is simple -> find intersection for this scenario
        else
            return new Point(perpendicular.x1, m * perpendicular.x1 + b);


//        if ((codeOfLine == Parameters.HORIZONTAL_LINE_CODE && perpendicular.codeOfLine == Parameters.HORIZONTAL_LINE_CODE) ||
//                (codeOfLine == Parameters.VERTICAL_LINE_CODE && perpendicular.codeOfLine == Parameters.VERTICAL_LINE_CODE))
//            return null;
//        else if (codeOfLine == Parameters.HORIZONTAL_LINE_CODE && perpendicular.codeOfLine == Parameters.VERTICAL_LINE_CODE)
//            return new Point(perpendicular.x1, y1);
//        else if (codeOfLine == Parameters.VERTICAL_LINE_CODE && perpendicular.codeOfLine == Parameters.HORIZONTAL_LINE_CODE)
//            return new Point(x1, perpendicular.y1);
//        else if (codeOfLine == Parameters.HORIZONTAL_LINE_CODE)
//            return new Point(Math.abs((perpendicular.b - y1) / perpendicular.m), y1);
//        else if (codeOfLine == Parameters.VERTICAL_LINE_CODE)
//            return new Point(x1, perpendicular.m * x1 + perpendicular.b);
//        else if (perpendicular.codeOfLine == Parameters.HORIZONTAL_LINE_CODE)
//            return new Point(Math.abs((b - perpendicular.y1) / m), perpendicular.y1);
//        else if (perpendicular.codeOfLine == Parameters.VERTICAL_LINE_CODE)
//            return new Point(perpendicular.x1, m * perpendicular.x1 + b);
//        else if (perpendicular.m == m && perpendicular.b == b) {
//            System.err.println("Perp = " + perpendicular + "; " +
//                    "edge = " + this + "; " +
//                    "perp = {m = " + perpendicular.m + ", b = " + perpendicular.b + "}; " +
//                    "edge = {m = " + m + ", b = " + b + "}");
//            return null;
//        }
//        else {
//            float intersectionX = Math.abs((perpendicular.b - this.b) / (this.m - perpendicular.m));
//            return new Point(intersectionX, this.m * intersectionX + this.b);
//        }
    }

    //  getters


    public Point getFirstPoint() {
        return firstPoint;
    }

    public Point getSecondPoint() {
        return secondPoint;
    }

    @Override
    public String toString() {
        return "Line{" +
                "firstPoint=" + x1 + ", " + y1 +
                ", secondPoint=" + x2 + ", " + y2 +
                '}';
    }
}
