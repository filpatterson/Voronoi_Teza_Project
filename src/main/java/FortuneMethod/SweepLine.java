package FortuneMethod;

import Globals.Parameters;
import HalfPlaneIntersectionMethod.Point;

import java.awt.geom.Line2D;

public class SweepLine extends Line2D.Float {
    //  ending points of the line
    private final boolean isMovementLeftToRight;

    /**
     * sweep line constructor, preparing line for further movement
     * @param isMovementLeftToRight true if left to right, false if up to down
     */
    public SweepLine(boolean isMovementLeftToRight) {
        this.isMovementLeftToRight = isMovementLeftToRight;
        setFirstPoint(0, 0);

        if (isMovementLeftToRight)
            setSecondPoint(0, Parameters.yLimit);
        else
            setSecondPoint(Parameters.xLimit, 0);
    }

    /**
     * move line basing on direction of movement (left to right or up to down)
     */
    public boolean moveLine() {
        if (isMovementLeftToRight) {
            if (x1 < Parameters.xLimit) {
                x1++;
                x2++;
            } else
                return false;
        } else {
            if (y1 < Parameters.yLimit) {
                y1++;
                y2++;
            } else
                return false;
        }

        return true;
    }

    /**
     * find distance to the line from the point considering movement direction of the sweep line
     * @param point point distance from the line to which is required to find
     * @return distance from the line to the point
     */
    public float distanceToTheLine(Point point) {
        if (isMovementLeftToRight)
            return (float) Math.abs(point.getX() - x1);
        else
            return (float) Math.abs(point.getY() - y1);
    }

    /**
     * check if point is present on the line
     * @param point point presence of which is required to check
     * @return true if line is present, false if not
     */
    public boolean isPointOnTheLine(Point point) {
        if (isMovementLeftToRight)
            return point.getX() == x1;
        else
            return point.getY() == y1;
    }

    //  getters

    public void setFirstPoint(float x1, float y1) {
        this.x1 = x1;
        this.y1 = y1;
    }

    public void setSecondPoint(float x2, float y2) {
        this.x2 = x2;
        this.y2 = y2;
    }

    public boolean isMovementLeftToRight() {
        return isMovementLeftToRight;
    }
}
