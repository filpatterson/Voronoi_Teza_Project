package HalfPlaneIntersectionMethod;

import Globals.Parameters;

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

    //  locus - area each point of which is closer to this site than to any another one
    private VoronoiPolygon locus;

    private static ArrayList<Line> allLines = new ArrayList<>();

    //  corners of the analyzable area
    private static final ArrayList<Point> corners = new ArrayList<>();
    static {
        corners.add(Parameters.topLeftCorner);
        corners.add(Parameters.topRightCorner);
        corners.add(Parameters.bottomRightCorner);
        corners.add(Parameters.bottomLeftCorner);
    }

    public static final VoronoiPolygon areaBorders = new VoronoiPolygon(corners);

    public Site() {}

    /**
     * Constructor, create site with coordinates, no color attached
     * @param x X-axis coordinate
     * @param y Y-axis coordinate
     */
    public Site(double x, double y) {
        super(x, y);
    }

    /**
     * Constructor, create site with coordinates and color specification
     * @param x X-axis coordinate
     * @param y Y-axis coordinate
     * @param color Color that will be used for coloring locus of this site
     */
    public Site(double x, double y, Color color) {
        super(x, y);
        this.color = color;
    }

    /**
     * Find locus area for this site
     * @param sites array of all sites presented on this sector
     */
    public void findLocus(ArrayList<Site> sites) {
        locus = areaBorders;
        Line currentPerpendicular;

        for (Site anotherSite : sites)
            if (!anotherSite.isEqual(this)) {
                currentPerpendicular = Line.getPerpendicularOfPoints(this, anotherSite);
                if (locus.checkSliceByLine(currentPerpendicular))
                    locus = locus.findHalfPolygon(currentPerpendicular, this);
            }
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

    @Override
    public String toString() {
        return "Site{" +
                "color=" + color +
                ", " + super.toString() +
                '}';
    }
}
