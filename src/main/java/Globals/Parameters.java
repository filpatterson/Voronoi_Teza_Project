package Globals;

import HalfPlaneIntersectionMethod.Point;

//  class for storing application settings
public class Parameters {
    //  displayable/analyzable area size in X and Y
    public static int xLimit = 1000;
    public static int yLimit = 1000;

    //  corners of the displayable/analyzable area
    public static final Point topLeftCorner = new Point(0, 0);
    public static final Point topRightCorner = new Point(xLimit, 0);
    public static final Point bottomLeftCorner = new Point(0, yLimit);
    public static final Point bottomRightCorner = new Point(xLimit, yLimit);
}
