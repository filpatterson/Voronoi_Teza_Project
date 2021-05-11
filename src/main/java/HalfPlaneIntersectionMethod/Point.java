package HalfPlaneIntersectionMethod;

import Globals.MapUtils;
import Globals.Utils;

import java.awt.geom.Point2D;
import java.math.BigDecimal;

/**
 * Custom class that stores location of the point on 2-dimensional space, supports convertion to Point2D from Swing
 */

/**
 *  Custom Point class that extends Swing-based Point class. Has custom methods that can be used by custom Line class.
 * Supports point change to geographical one and reverse.
 */
public class Point extends Point2D.Double {
    //  point geographical coordinates
    public double latitude;
    public double longitude;

    //  default constructor, testing purposes 
    public Point(){}

    /**
     * Constructor, simple point with X and Y coordinates
     * @param x X-axis coordinate
     * @param y Y-axis coordinate
     */
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Find if coordinates match coordinates of the point
     * @param x X-axis coordinate to check
     * @param y Y-axis coordinate to check
     * @return True if point has the same coordinates as transmitted ones, False if not
     */
    public boolean isEqual(double x, double y) {
        return this.x == x && this.y == y;
    }

    /**
     * Find if coordinates of this point match coordinates of another point
     * @param anotherPoint another point
     * @return True if points have the same coordinates, False if not
     */
    public boolean isEqual(Point anotherPoint) {
        return x == anotherPoint.x && y == anotherPoint.y;
    }

    /**
     * calculate point geographical coordinates from cartesian coordinates
     */
    public void toGeographical() {
        if (longitude == 0) {
            longitude = (MapUtils.centerLongitude - MapUtils.longitudeRadius) + x * MapUtils.longitudeResolution;
        }
        if (latitude == 0) {
            latitude = MapUtils.centerLatitude + MapUtils.latitudeRadius - y * MapUtils.latitudeResolution;
        }
    }

    /**
     * calculate point cartesian coordinates from geographical coordinates
     * @return true if point can be transformed to cartesian considering map parameters, false if not
     */
    public boolean toCartesian() {
        //  first check if point is inside of geographical bounds of the map (first latitude bounds, then longitude ones)
        if(latitude > (MapUtils.centerLatitude + MapUtils.latitudeRadius) || latitude < (MapUtils.centerLatitude - MapUtils.latitudeRadius)) {
            System.err.println("Point is out of latitude bounds: " + latitude + "out of " +
                    (MapUtils.centerLatitude + MapUtils.latitudeRadius) + " - " + (MapUtils.centerLatitude - MapUtils.latitudeRadius)
            );
            return false;
        } else if (longitude > MapUtils.centerLongitude + MapUtils.longitudeRadius || longitude < MapUtils.centerLongitude - MapUtils.longitudeRadius) {
            System.err.println("Point is out of longitude bounds");
            return false;
        }

        x = (longitude + MapUtils.longitudeRadius - MapUtils.centerLongitude) / MapUtils.longitudeResolution;
        y = (MapUtils.centerLatitude + MapUtils.latitudeRadius - latitude) / MapUtils.latitudeResolution;
        return true;
    }

    @Override
    public String toString() {
        return "Point{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
