package HalfPlaneIntersectionMethod;

import Globals.Utils;

import java.awt.geom.Line2D;

/**
 *  Custom line class that extends standard Swing-based line class. Supports definition of the first-degree polynomial
 * describing line shape, perpendicular definition, distance estimation by Euclidean and Manhattan algorithms, has
 * polynomial based and "epsilon-error-sum-based" algorithms defining point presence on the line.
 */
public class Line extends Line2D.Double {

    //  coefficients for equation of the line in the format: y = mx + b
    public double m;
    public double b;

    //  code of line defining its property: vertical (2) for x1 = x2, horizontal (1) for y1 = y2
    // and simple (0) for other case
    public byte codeOfLine;

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

        //  find if line is vertical, horizontal or simple
        if (x1 == x2) {
            codeOfLine = Utils.VERTICAL_LINE_CODE;
        } else if (y1 == y2) {
            codeOfLine = Utils.HORIZONTAL_LINE_CODE;
        } else {
            //  if line is simple, then find coefficients for formula describing line (y = mx + b)
            codeOfLine = Utils.SIMPLE_LINE_CODE;
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
    public Line(double x1, double y1, double x2, double y2) {
        //  set x and y values for each point
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;

        //  find if line is vertical, horizontal or simple
        if (x1 == x2) {
            codeOfLine = Utils.VERTICAL_LINE_CODE;
        } else if (y1 == y2) {
            codeOfLine = Utils.HORIZONTAL_LINE_CODE;
        } else {
            //  if line is simple, then find coefficients for formula describing line (y = mx + b)
            codeOfLine = Utils.SIMPLE_LINE_CODE;
            m = (y2 - y1) / (x2 - x1);
            b = y2 - m * x2;
        }
    }

    private Line(double x1, double y1, double x2, double y2, double m, double b) {
        //  set x and y values for each point
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;

        this.m = m;
        this.b = b;

        codeOfLine = Utils.SIMPLE_LINE_CODE;
    }

    /**
     * Find line length in Euclidean algorithm
     * @return length in Euclidean algorithm
     */
    public double lengthEuclidean(){
        return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
    }

    /**
     * Find line length in Manhattan algorithm
     * @return length in Manhattan algorithm
     */
    public double lengthManhattan(){
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    /**
     * Find middle point of the line
     * @return middle point of the line
     */
    public Point middleOfLine() {
        return new Point((x1 + x2) / 2, (y1 + y2) / 2);
    }

    public double getXOfMiddle() {
        return (x1 + x2) / 2;
    }

    public double getYOfMiddle() {
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
        //  find middle between two points
        double middleX = (x1 + x2) / 2;

        //  if line is horizontal, then get vertical perpendicular
        if (y1 == y2) {
            return new Line(new Point(middleX, 0), new Point(middleX, Utils.yLimit));
        }

        double middleY = (y1 + y2) / 2;

        //  if line is vertical, then get horizontal perpendicular
        if (x1 == x2) {
            return new Line(new Point(0, middleY), new Point(Utils.xLimit, middleY));
        }

        //  find equation of the perpendicular (perpY = perpM * perpX + perpB)
        double perpM = -1 / m;
        double perpB = middleY - perpM * middleX;

        //  return the line that will be limited conform limits of x-axis
        return new Line(
                0, perpM * 0 + perpB,                               // first point coordinates
                Utils.xLimit, perpM * Utils.xLimit + perpB,             // second point coordinates
                perpM, perpB                                                // m and b coefficients of the line
        );
    }

    /**
     * finds perpendicular of line between two points without constructing the line itself
     * @param firstPoint first point of "virtual" line
     * @param secondPoint second point of "virtual" line
     * @return perpendicular for those points
     */
    public static Line getPerpendicularOfPoints(Point firstPoint, Point secondPoint) {
        //  find middle between two points
        double middleX = (firstPoint.x + secondPoint.x) / 2;

        //  if line is horizontal, then get vertical perpendicular
        if (firstPoint.y == secondPoint.y) {
            return new Line(new Point(middleX, 0), new Point(middleX, Utils.yLimit));
        }

        double middleY = (firstPoint.y + secondPoint.y) / 2;

        //  if line is vertical, then get horizontal perpendicular
        if (firstPoint.x == secondPoint.x) {
            return new Line(new Point(0, middleY), new Point(Utils.xLimit, middleY));
        }

        //  find equation of the perpendicular (perpY = perpM * perpX + perpB)
        double perpM = -1 / ((secondPoint.y - firstPoint.y) / (secondPoint.x - firstPoint.x));
        double perpB = middleY - perpM * middleX;

        //  return the line that will be limited conform limits of x-axis
        return new Line(
                0, perpM * 0 + perpB,
                Utils.xLimit, perpM * Utils.xLimit + perpB,
                perpM, perpB
        );
    }

    /**
     * Find out if current line contains point using first degree polynomial check: y = mx + b
     * @param point point presence of which must be checked
     * @return true if point is on the line, false if not
     */
    public boolean containsByEquation(Point point) {
        //  if line is vertical or horizontal -> find if it contains the point
        if (codeOfLine != Utils.SIMPLE_LINE_CODE) {
            return innerHorizontalAndVerticalContainsCheck(point);
        }

        // check if point is on the line
        return point.isEqual(point.x, m * point.x + b);
    }

    public boolean containsByEquation(double x, double y) {
        //  if line is vertical or horizontal -> find if it contains the point
        if (codeOfLine != Utils.SIMPLE_LINE_CODE) {
            return innerHorizontalAndVerticalContainsCheck(x, y);
        }

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
        if (codeOfLine == Utils.VERTICAL_LINE_CODE && x2 == point.x) {
            //  find which end of line is lower, which end is higher, and if point is between ends
            return (y1 < y2) ? (point.y > y1 || point.y < y2) : (point.y < y1 || point.y > y2);

            //  check if line is horizontal and if point has the same Y coordinate
        } else if (codeOfLine == Utils.HORIZONTAL_LINE_CODE && y1 == point.y) {
            //  find which end of line if left, which end is right, and if point is between ends
            return (x1 < x2) ? (point.x > x1 || point.x < x2) : (point.x < x1 || point.x > x2);
        }

        return false;
    }

    private boolean innerHorizontalAndVerticalContainsCheck(double x, double y) {
        //  check if line is vertical and if point has the same X coordinate
        if (codeOfLine == Utils.VERTICAL_LINE_CODE && x2 == x) {
            //  find which end of line is lower, which end is higher, and if point is between ends
            return (y1 < y2) ? (y > y1 || y < y2) : (y < y1 || y > y2);
        }
            //  check if line is horizontal and if point has the same Y coordinate
        else if (codeOfLine == Utils.HORIZONTAL_LINE_CODE && y1 == y) {
            //  find which end of line if left, which end is right, and if point is between ends
            return (x1 < x2) ? (x > x1 || x < x2) : (x < x1 || x > x2);
        }

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
            if (codeOfLine == Utils.SIMPLE_LINE_CODE) {
                //  if lines have the same formulas
                if (perpendicular.m == m && perpendicular.b == b) {
                    System.err.println("Perp = " + perpendicular + "; " +
                            "edge = " + this + "; " +
                            "perp = {m = " + perpendicular.m + ", b = " + perpendicular.b + "}; " +
                            "edge = {m = " + m + ", b = " + b + "}");
                    return null;
                    //  if lines have different formulas
                } else {
                    double intersectionX = Math.abs((perpendicular.b - this.b) / (this.m - perpendicular.m));
                    return new Point(intersectionX, this.m * intersectionX + this.b);
                }
                //  lines are both vertical or horizontal, no intersection possible
            } else {
                return null;
            }
        } else if (codeOfLine == Utils.HORIZONTAL_LINE_CODE) {
            if (perpendicular.codeOfLine == Utils.VERTICAL_LINE_CODE) {
                return new Point(perpendicular.x1, y1);
            } else {
                return new Point(Math.abs((perpendicular.b - y1) / perpendicular.m), y1);
            }
        } else if (codeOfLine == Utils.VERTICAL_LINE_CODE) {
            if (perpendicular.codeOfLine == Utils.HORIZONTAL_LINE_CODE) {
                return new Point(x1, perpendicular.y1);
            } else {
                return new Point(x1, perpendicular.m * x1 + perpendicular.b);
            }
        } else if (perpendicular.codeOfLine == Utils.HORIZONTAL_LINE_CODE) {
            return new Point(Math.abs((b - perpendicular.y1) / m), perpendicular.y1);
            //  perpendicular is vertical and another line is simple -> find intersection for this scenario
        }
        return new Point(perpendicular.x1, m * perpendicular.x1 + b);
    }

    //  getters

    public Point getFirstPoint() {
        return new Point(x1, y1);
    }

    public Point getSecondPoint() {
        return new Point(x2, y2);
    }

    @Override
    public String toString() {
        return "Line{" +
                "firstPoint=" + x1 + ", " + y1 +
                ", secondPoint=" + x2 + ", " + y2 +
                '}';
    }
}
