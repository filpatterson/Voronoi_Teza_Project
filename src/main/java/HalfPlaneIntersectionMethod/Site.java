package HalfPlaneIntersectionMethod;

import Globals.Parameters;

import java.awt.*;

import java.awt.geom.Area;
import java.util.ArrayList;


//  site is a point for which is required to find locus
public class Site extends Point {
    //  color of the site that will be applied for drawing PixelByPixelMethod.Voronoi diagram by coloring locus
    private Color color;

    //  locus - area each point of which is closer to this site than to any another one
    private Area locus;

    //  perpendiculars referring from this site to other ones
    private final ArrayList<Line> perpendiculars;

    /**
     * Constructor, create site with coordinates, no color attached
     * @param x X-axis coordinate
     * @param y Y-axis coordinate
     */
    public Site(int x, int y) {
        super(x, y);
        perpendiculars = new ArrayList<>();
    }

    /**
     * Constructor, create site with coordinates and color specification
     * @param x X-axis coordinate
     * @param y Y-axis coordinate
     * @param color Color that will be used for coloring locus of this site
     */
    public Site(int x, int y, Color color) {
        super(x, y);
        this.color = color;
        perpendiculars = new ArrayList<>();
    }

    /**
     * Find locus area for this site
     * @param sites array of all sites presented on this sector
     * @param parameters parameters of the application
     */
    public void findLocus(ArrayList<Site> sites, Parameters parameters) {
        //  initialize storage for all half planes that were calculated referring to other sites
        ArrayList<Area> halfPlanes = new ArrayList<>();

        //  iterate through each site
        for (Site anotherSite : sites) {
            //  if current site is the same as this one
            if (!anotherSite.equals(this)) {
                //  create a line between current site and this one, specify perpendicular
                Line line = new Line(this, anotherSite);
                Line perpendicular = line.getPerpendicularByEquation(parameters.getxLimit(), parameters.getyLimit());

                //  save perpendicular that was found
                perpendiculars.add(perpendicular);

                //  calculate half plane between current site and this one, save it to this site
                halfPlanes.add(findHalfPlane(perpendicular, parameters));
            }
        }

        //  initialize locus as first calculated half plane
        locus = new Area(halfPlanes.get(0));

        //  if half planes value is bigger than one then calculate intersection of all half planes
        if (halfPlanes.size() > 1) {
            for (Area halfPlane : halfPlanes) {
                locus.intersect(new Area(halfPlane));
            }
        }
    }

    /**
     * Find half plane of this site using perpendicular estimated with another site
     * @param perpendicular perpendicular that was calculated between this site and another one
     * @param parameters parameters of the application
     * @return Half plane area containing this site
     */
    private Area findHalfPlane(Line perpendicular, Parameters parameters) {
        //  initialize borders and corners lists
        ArrayList<Line> borders = new ArrayList<>();

        //  form borders of the sector in clockwise direction
        borders.add(new Line(parameters.getTopLeftCorner(), parameters.getTopRightCorner()));     //  top border
        borders.add(new Line(parameters.getTopRightCorner(), parameters.getBottomRightCorner()));   //  right border
        borders.add(new Line(parameters.getBottomRightCorner(), parameters.getBottomLeftCorner()));   //  bottom border
        borders.add(new Line(parameters.getBottomLeftCorner(), parameters.getTopLeftCorner()));     //  left border

        //  iterate through sector borders in clockwise direction
        ArrayList<Point> halfplaneCorners = findCornersOfHalfplane(borders, perpendicular);

        //  form polygon out of estimated corners
        Polygon halfPlane = new Polygon();
        for (Point corner : halfplaneCorners) {
            halfPlane.addPoint(corner.getX(), corner.getY());
        }

        //  if half plane contains site then return this area
        if (halfPlane.contains(this.getX(), this.getY())) {
            return new Area(halfPlane);

        //  if half plane does not have site then return another half plane from this sector
        } else {
            //  take area of sector and subtract from it area of the half plane that does not have site
            Area siteArea = new Area(new Rectangle(0, 0, parameters.getxLimit(), parameters.getyLimit()));
            siteArea.subtract(new Area(halfPlane));
            System.out.println("alternate for " + this);
            return siteArea;
        }
    }

    private ArrayList<Point> findCornersOfHalfplane(ArrayList<Line> borders, Line perpendicular) {
        //  flag that will detect if half plane was completely found
        int perpendicularPointsMet = 0;

        //  half plane corners array
        ArrayList<Point> halfplaneCorners = new ArrayList<>();

        //  iterate through sector borders (works with either clockwise or anti-clockwise direction)
        for (Line border : borders) {
            //  if any of the perpendicular is met -> append it to half plane corners -> show this to the flag
            if (border.contains(perpendicular.getFirstPoint())) {
                perpendicularPointsMet++;
                halfplaneCorners.add(perpendicular.getFirstPoint());
            } else if (border.contains(perpendicular.getSecondPoint())) {
                perpendicularPointsMet++;
                halfplaneCorners.add(perpendicular.getSecondPoint());
            }

            //  if both ends of perpendicular was checked -> half plane is found
            if (perpendicularPointsMet == 2) {
                break;

                //  if corner is a part of half plane -> append it to the half plane corners list
            } else if (perpendicularPointsMet > 0) {
                halfplaneCorners.add(border.getSecondPoint());
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

    public ArrayList<Line> getPerps() {
        return perpendiculars;
    }

    @Override
    public String toString() {
        return "Site{" +
                "color=" + color +
                ", " + super.toString() +
                '}';
    }
}
