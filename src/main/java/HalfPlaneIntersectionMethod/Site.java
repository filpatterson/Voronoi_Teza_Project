package HalfPlaneIntersectionMethod;

import Globals.Parameters;

import java.awt.*;

import java.awt.geom.Area;
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
    private Area locus;

    //  borders of the reviewed area, common for all sites
    private static final ArrayList<Line> borders = new ArrayList<>();
    static {
        //  form borders of the sector in clockwise direction
        borders.add(new Line(Parameters.topLeftCorner, Parameters.topRightCorner));     //  top border
        borders.add(new Line(Parameters.topRightCorner, Parameters.bottomRightCorner));   //  right border
        borders.add(new Line(Parameters.bottomRightCorner, Parameters.bottomLeftCorner));   //  bottom border
        borders.add(new Line(Parameters.bottomLeftCorner, Parameters.topLeftCorner));     //  left border
    }

    /**
     * Constructor, create site with coordinates, no color attached
     * @param x X-axis coordinate
     * @param y Y-axis coordinate
     */
    public Site(float x, float y) {
        super(x, y);
    }

    /**
     * Constructor, create site with coordinates and color specification
     * @param x X-axis coordinate
     * @param y Y-axis coordinate
     * @param color Color that will be used for coloring locus of this site
     */
    public Site(float x, float y, Color color) {
        super(x, y);
        this.color = color;
    }

    /**
     * Find locus area for this site
     * @param sites array of all sites presented on this sector
     */
    public void findLocus(ArrayList<Site> sites) {
        //  initialize storage for all half planes that were calculated referring to other sites
        ArrayList<Area> halfPlanes = new ArrayList<>();

        //  iterate through each site
        for (Site anotherSite : sites)
            //  if current site is the same as this one
            if (!anotherSite.equals(this)) {
                //  calculate half plane between current site and another one using estimated perpendicular for line
                // connecting those sites
                halfPlanes.add(findHalfPlane(new Line(this, anotherSite).getPerpendicularByEquation(), true));
            }

        //  initialize locus as first calculated half plane
        locus = new Area(halfPlanes.get(0));

        //  if half planes value is bigger than one then calculate intersection of all half planes
        if (halfPlanes.size() > 1)
            for (Area halfPlane : halfPlanes)
                locus.intersect(new Area(halfPlane));
    }

    /**
     * Find half plane of this site using perpendicular estimated with another site
     * @param perpendicular perpendicular that was calculated between this site and another one
     * @param useCustomSecondHalfEstimation true if use of custom algorithm is required, false if required area negation
     * @return Half plane area containing this site
     */
    private Area findHalfPlane(Line perpendicular, boolean useCustomSecondHalfEstimation) {
        //  iterate through sector borders in clockwise direction
        ArrayList<Point> halfplaneCorners = findCornersOfHalfplane(borders, perpendicular, false);

        //  form polygon out of estimated corners
        Polygon halfPlane = new Polygon();
        for (Point corner : halfplaneCorners)
            halfPlane.addPoint((int) corner.getX(), (int) corner.getY());

        //  if half plane contains site then return this area
        if (halfPlane.contains(this.getX(), this.getY()))
            return new Area(halfPlane);

        //  if half plane does not have site then return another half plane from this sector
        else {
            if (useCustomSecondHalfEstimation) {
                //  use algorithm of finding corners of the halfplane with specified flag, defining which half plane is
                // required and reset original halfplane
                halfplaneCorners = findCornersOfHalfplane(borders, perpendicular, true);
                halfPlane.reset();
                for (Point corner : halfplaneCorners)
                    halfPlane.addPoint((int) corner.getX(), (int) corner.getY());

                return new Area(halfPlane);
            } else {
                //  take area of sector and subtract from it area of the half plane that does not have site
                Area siteArea = new Area(new Rectangle(0, 0, Parameters.xLimit, Parameters.yLimit));
                siteArea.subtract(new Area(halfPlane));
                return siteArea;
            }
        }
    }

    /**
     * finds all corners of the halfplane by iterating through area borders (requires setting borders in clockwise direction)
     * @param borders borders of the area defined in clockwise direction
     * @param perpendicular perpendicular of the line
     * @return list of points defining halfplane corners
     */
    private ArrayList<Point> findCornersOfHalfplane(ArrayList<Line> borders, Line perpendicular, boolean isSecondOneRequired) {
        //  flag that will detect if half plane was completely found
        int perpendicularPointsMet = 0;

        //  half plane corners array
        ArrayList<Point> halfplaneCorners = new ArrayList<>();

        if (!isSecondOneRequired)
            //  iterate through sector borders (works with either clockwise or anti-clockwise direction)
            for (Line border : borders) {
                //  if any of the perpendicular is met -> append it to half plane corners -> show this to the flag
                if (border.containsByEquation(perpendicular.getFirstPoint())) {
                    perpendicularPointsMet++;
                    halfplaneCorners.add(perpendicular.getFirstPoint());
                } else if (border.containsByEquation(perpendicular.getSecondPoint())) {
                    perpendicularPointsMet++;
                    halfplaneCorners.add(perpendicular.getSecondPoint());
                }

                //  if both ends of perpendicular was checked -> half plane is found
                if (perpendicularPointsMet == 2)
                    break;

                    //  if corner is a part of half plane -> append it to the half plane corners list
                else if (perpendicularPointsMet == 1)
                    halfplaneCorners.add(border.getSecondPoint());
            }
        else
            for (Line border : borders) {
                //  if corner is a part of half plane -> append it to the half plane corners list
                if (perpendicularPointsMet == 0 || perpendicularPointsMet == 2)
                    halfplaneCorners.add(border.getFirstPoint());

                //  if any of the perpendicular is met -> append it to half plane corners -> show this to the flag
                if (border.containsByEquation(perpendicular.getFirstPoint())) {
                    perpendicularPointsMet++;
                    halfplaneCorners.add(perpendicular.getFirstPoint());
                } else if (border.containsByEquation(perpendicular.getSecondPoint())) {
                    perpendicularPointsMet++;
                    halfplaneCorners.add(perpendicular.getSecondPoint());
                }
            }

        return halfplaneCorners;
    }

    //  getters

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Area getLocus() {
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
