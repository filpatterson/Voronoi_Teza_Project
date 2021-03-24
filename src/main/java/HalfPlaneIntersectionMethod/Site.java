package HalfPlaneIntersectionMethod;

import java.awt.*;

import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Arrays;


//  site is a point for which is required to find locus
public class Site extends Point {
    private Color color;
    private Area locus;
    private final ArrayList<Line> perps;

    public Site(int x, int y) {
        super(x, y);
        perps = new ArrayList<>();
    }

    public Site(int x, int y, Color color) {
        super(x, y);
        this.color = color;
        perps = new ArrayList<>();
    }

    /**
     * finding locus points for the current site
     * @param sites array of all sites on the sector
     */
    public void findLocus(ArrayList<Site> sites, int xLimit, int yLimit) {
        //  initialize array of all halfplanes calculated for the current site
        ArrayList<Polygon> halfPlanes = new ArrayList<>();

        //  iterate through all of the sites
        for (Site anotherSite : sites) {
            if (!anotherSite.equals(this)) {
                Line line = new Line(this, anotherSite);
                Line perpendicular = line.getPerpendicularLine(xLimit, yLimit);

                if (this.color.equals(Color.RED)) {
                    System.out.println("test");
                }

                perps.add(perpendicular);
                halfPlanes.add(findHalfPlane(perpendicular, xLimit, yLimit));
            }
        }

        if (this.color.equals(Color.RED)) {
            for (Polygon halfPlane : halfPlanes) {
                System.out.println(this.color + "\n---------------------------");
                System.out.println("x = [" + Arrays.toString(halfPlane.xpoints) + "];");
                System.out.println("y = [" + Arrays.toString(halfPlane.ypoints) + "];");
            }
        }
        locus = new Area(halfPlanes.get(0));
        if (halfPlanes.size() > 1) {
            for (Polygon halfPlane : halfPlanes) {
                locus.intersect(new Area(halfPlane));
            }
        }


    }

    private Polygon findHalfPlane(Line perpendicular, int xLimit, int yLimit) {
        //  initialize lists of borders and corners of the area
        ArrayList<Line> borders = new ArrayList<>();
        ArrayList<Point> corners = new ArrayList<>();

        Point topLeftCorner = new Point(0, 0);
        Point topRightCorner = new Point(xLimit, 0);
        Point bottomLeftCorner = new Point(0, yLimit);
        Point bottomRightCorner = new Point(xLimit, yLimit);

        //  add all borders to array
        borders.add(new Line(topLeftCorner, topRightCorner));     //  top border
        borders.add(new Line(topRightCorner, bottomRightCorner));   //  right border
        borders.add(new Line(bottomRightCorner, bottomLeftCorner));   //  bottom border
        borders.add(new Line(bottomLeftCorner, topLeftCorner));     //  left border

        //  flag for detecting if full halfplane was found
        int perpendicularPointsMet = 0;

        //  iterate through area corners and perpendicular points in clockwise manner to find possible half plane
        for (Line border : borders) {
            //  if any point of the perpendicular is met -> append it to corners of half plane and show to the flag that
            // perpendicular point is met
            if (border.contains(perpendicular.getFirstPoint())) {
                perpendicularPointsMet++;
                corners.add(perpendicular.getFirstPoint());
            } else if (border.contains(perpendicular.getSecondPoint())) {
                perpendicularPointsMet++;
                corners.add(perpendicular.getSecondPoint());
            }

            //  if half plane was detected -> stop checking next points
            if (perpendicularPointsMet == 2) {
                break;
                //  append corner of the area that is inside half plane
            } else if (perpendicularPointsMet > 0) {
                corners.add(border.getSecondPoint());
            }
        }

        Polygon halfPlane = new Polygon();
        for (Point corner : corners) {
            halfPlane.addPoint(corner.getX(), corner.getY());
        }

        if (halfPlane.contains(this.getX(), this.getY())) {
            int[] xpoints = new int[halfPlane.npoints];
            int[] ypoints = new int[halfPlane.npoints];
            int npoints = halfPlane.npoints;

            for (int i = 0; i < halfPlane.npoints; i++) {
                xpoints[i] = halfPlane.xpoints[i];
                ypoints[i] = halfPlane.ypoints[i];
            }
            return new Polygon(xpoints, ypoints, npoints);
        } else {
            halfPlane = new Polygon();
            halfPlane.addPoint(perpendicular.getFirstPoint().getX(), perpendicular.getFirstPoint().getY());
            halfPlane.addPoint(perpendicular.getSecondPoint().getX(), perpendicular.getSecondPoint().getY());

            ArrayList<Point> anotherCorners = new ArrayList<>();
            anotherCorners.add(topLeftCorner);
            anotherCorners.add(topRightCorner);
            anotherCorners.add(bottomLeftCorner);
            anotherCorners.add(bottomRightCorner);
            anotherCorners.removeAll(corners);

            for (Point anotherCorner : anotherCorners) {
                halfPlane.addPoint(anotherCorner.getX(), anotherCorner.getY());
            }

            int[] xpoints = new int[halfPlane.npoints];
            int[] ypoints = new int[halfPlane.npoints];
            int npoints = halfPlane.npoints;

            for (int i = 0; i < halfPlane.npoints; i++) {
                xpoints[i] = halfPlane.xpoints[i];
                ypoints[i] = halfPlane.ypoints[i];
            }
            return new Polygon(xpoints, ypoints, npoints);
        }
    }

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
        return perps;
    }

    @Override
    public String toString() {
        return "Site{" +
                "color=" + color +
                ", " + super.toString() +
                '}';
    }
}
