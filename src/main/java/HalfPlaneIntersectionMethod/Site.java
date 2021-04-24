package HalfPlaneIntersectionMethod;

import Globals.Utils;

import java.awt.*;

import java.util.ArrayList;


/**
 *  Class with specification of either service point, or point of interest. Extends Point class and has specifications
 * of color for further graphical representation and locus that describes shape of area, where all points are closer to
 * this site than to any another one (locus). Supports locus estimation via perpendicular method, has several approaches
 * for finding locus.
 */
public class Site extends Point {

    //  color of the site that will be applied for drawing PixelByPixelMethod.Voronoi diagram by coloring locus
    private Color color;
    private String name;

    //  locus - area each point of which is closer to this site than to any another one
    private VoronoiPolygon locus;

    private static ArrayList<Line> allLines = new ArrayList<>();

    //  corners of the analyzable area
    private static final ArrayList<Point> corners = new ArrayList<>();
    static {
        corners.add(Utils.topLeftCorner);
        corners.add(Utils.topRightCorner);
        corners.add(Utils.bottomRightCorner);
        corners.add(Utils.bottomLeftCorner);
    }

    public static final VoronoiPolygon areaBorders = new VoronoiPolygon(corners);

    public Site() {}

    public Site(double latitude, double longitude, Color color, String name,boolean isCartesianTransformRequired) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.color = color;
        this.name = name;

        if (isCartesianTransformRequired) {
            this.toCartesian();
        }
    }

    /**
     * Constructor, create site with coordinates and color specification
     * @param x X-axis coordinate
     * @param y Y-axis coordinate
     * @param color Color that will be used for coloring locus of this site
     */
    public Site(double x, double y, Color color, String name) {
        super(x, y);
        this.color = color;
        this.name = name;
    }

    /**
     * Find locus area for this site
     */
    public void findLocus() {
        locus = areaBorders;
        Line currentPerpendicular;

        if (this.toCartesian()) {
            for (Site anotherSite : Utils.sitesStorage) {
                if (anotherSite.toCartesian() && !anotherSite.isEqual(this)) {
                    currentPerpendicular = Line.getPerpendicularOfPoints(this, anotherSite);
                    if (locus.checkSliceByLine(currentPerpendicular))
                        locus = locus.findHalfPolygon(currentPerpendicular, this);
                }
            }
        }

        return;
    }

    //  getters

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public VoronoiPolygon getLocus() {
        locus.setDrawable();
        return locus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Site{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", color=" + color +
                ", name='" + name + '\'' +
                ", locus=" + locus +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
