package Globals;

import HalfPlaneIntersectionMethod.Point;

//  class for storing application settings
public class Parameters {
    //  displayable/analyzable area size in X and Y
    private int xLimit;
    private int yLimit;

    //  corners of the displayable/analyzable area
    private final Point topLeftCorner;
    private final Point topRightCorner;
    private final Point bottomLeftCorner;
    private final Point bottomRightCorner;

    /**
     * construct global parameters
     * @param xLimit x area limit
     * @param yLimit y area limit
     */
    public Parameters(int xLimit, int yLimit) {
        this.xLimit = xLimit;
        this.yLimit = yLimit;

        this.topLeftCorner = new Point(0, 0);
        this.topRightCorner = new Point(xLimit, 0);
        this.bottomLeftCorner = new Point(0, yLimit);
        this.bottomRightCorner = new Point(xLimit, yLimit);
    }

    /**
     * change displayable/analyzable area in X and update corners
     * @param xLimit new X area limit
     */
    public void setxLimit(int xLimit) {
        this.xLimit = xLimit;
        this.topRightCorner.setX(xLimit);
        this.bottomRightCorner.setX(xLimit);
    }

    /**
     * change displayable/analyzable area in Y and update corners
     * @param yLimit new Y area limit
     */
    public void setyLimit(int yLimit) {
        this.yLimit = yLimit;
        this.bottomLeftCorner.setY(yLimit);
        this.bottomRightCorner.setY(yLimit);
    }

    //  getters

    public int getxLimit() {
        return xLimit;
    }

    public int getyLimit() {
        return yLimit;
    }

    public Point getTopLeftCorner() {
        return topLeftCorner;
    }

    public Point getTopRightCorner() {
        return topRightCorner;
    }

    public Point getBottomLeftCorner() {
        return bottomLeftCorner;
    }

    public Point getBottomRightCorner() {
        return bottomRightCorner;
    }
}
