package HalfPlaneIntersectionMethod;

import java.awt.geom.Line2D;

public class Line {
    //  ending points of the line
    private final Point firstPoint;
    private final Point secondPoint;

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
    public Line(int x1, int y1, int x2, int y2) {
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
    public double lineDistanceManhattan(){
        return Math.abs(firstPoint.getX() - secondPoint.getX()) + Math.abs(firstPoint.getY() - secondPoint.getY());
    }

    /**
     * Find middle point of the line
     * @return middle point of the line
     */
    public Point middleOfLine(){
        int x = (firstPoint.getX() + secondPoint.getX()) / 2;
        int y = (firstPoint.getY() + secondPoint.getY()) / 2;
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
     * Find perpendicular of the line
     * @param xLimit limit of X axis
     * @param yLimit limit of Y axis
     * @return perpendicular line
     */
    public Line getPerpendicularLine(int xLimit, int yLimit){
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
        while((perpendicularFirstPointX > 0 && perpendicularFirstPointX < xLimit) &&
                (perpendicularFirstPointY > 0 && perpendicularFirstPointY < yLimit)){
            perpendicularFirstPointX += difX;
            perpendicularFirstPointY += difY;
        }

        //  iterate in another direction until reaching axis limits
        while((perpendicularSecondPointX > 0 && perpendicularSecondPointX < xLimit) &&
                (perpendicularSecondPointY > 0 && perpendicularSecondPointY < yLimit)){
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
     * Convert line to the Swing graphics float line
     * @return swing graphics float line
     */
    public Line2D convertLineToGraphics(){
        return new Line2D.Float(firstPoint.getX(), firstPoint.getY(), secondPoint.getX(), secondPoint.getY());
    }

    /**
     * Find out if current line contains point
     * @param point point
     * @return True if point is on the line, False if not
     */
    public boolean contains(Point point) {
        //  check if line is vertical and if point has the same X coordinate
        if (this.getFirstPoint().getX() == this.getSecondPoint().getX() && this.getSecondPoint().getX() == point.getX()) {
            //  find which end of line is lower, which end is higher, and if point is between ends
            if(this.getFirstPoint().getY() < this.getSecondPoint().getY()) {
                return point.getY() > this.getFirstPoint().getY() || point.getY() < this.getSecondPoint().getY();
            } else {
                return point.getY() < this.getFirstPoint().getY() || point.getY() > this.getSecondPoint().getY();
            }

        //  check if line is horizontal and if point has the same Y coordinate
        } else if (this.getFirstPoint().getY() == this.getSecondPoint().getY() && this.getFirstPoint().getY() == point.getY()) {
            //  find which end of line if left, which end is right, and if point is between ends
            if(this.getFirstPoint().getX() < this.getSecondPoint().getX()) {
                return point.getX() > this.getFirstPoint().getX() || point.getX() < this.getSecondPoint().getX();
            } else {
                return point.getX() < this.getFirstPoint().getX() || point.getX() > this.getSecondPoint().getX();
            }

        //  if line is neither horizontal nor vertical then find if point is on the line via distances check
        } else {
            //  acceptable error
            double epsilon = 0.001d;

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
            if (firstToSecondDistance == firstToPointDistance + secondToPointDistance) {
                return true;
            } else return Math.abs(firstToSecondDistance - firstToPointDistance - secondToPointDistance) < epsilon;
        }
    }

    //  getters

    public Point getFirstPoint() {
        return firstPoint;
    }

    public Point getSecondPoint() {
        return secondPoint;
    }

    public Line2D getGraphicalRepresentation() {
        return new Line2D.Float(firstPoint.getX(), firstPoint.getY(), secondPoint.getX(), secondPoint.getY());
    }

    @Override
    public String toString() {
        return "Line{" +
                "firstPoint=" + firstPoint +
                ", secondPoint=" + secondPoint +
                '}';
    }
}
