package Globals;

import HalfPlaneIntersectionMethod.Point;

//  class for storing application settings
public class Parameters {
    //  displayable/analyzable area size in X and Y
    public static short xLimit = 700;
    public static short yLimit = 700;

    public static final byte HORIZONTAL_LINE_CODE = 1;
    public static final byte VERTICAL_LINE_CODE = 2;
    public static final byte SIMPLE_LINE_CODE = 0;

    //  corners of the displayable/analyzable area
    public static final Point topLeftCorner = new Point(0, 0);
    public static final Point topRightCorner = new Point(xLimit, 0);
    public static final Point bottomLeftCorner = new Point(0, yLimit);
    public static final Point bottomRightCorner = new Point(xLimit, yLimit);
}
