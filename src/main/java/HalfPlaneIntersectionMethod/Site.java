package HalfPlaneIntersectionMethod;

import java.awt.*;
import java.util.ArrayList;

//  site is a point for which is required to find locus
public class Site extends Point {
    private Color color;
    private ArrayList<Point> locusPoints;

    public Site(int x, int y) {
        super(x, y);
        locusPoints = new ArrayList<>();
    }

    public Site(int x, int y, Color color) {
        super(x, y);
        locusPoints = new ArrayList<>();
        this.color = color;
    }

    /**
     * finding locus points for the current site
     * @param sites array of all sites on the sector
     */
    public void findLocusPoints(ArrayList<Site> sites, int xLimit, int yLimit) {
        //  initialize array of all halfplanes calculated for the current site
        ArrayList<Polygon> halfPlanes = new ArrayList<>();

        //  iterate through all of the sites
        for(int index = 0; index < sites.size(); index++) {
            Site anotherSite = sites.get(index);

            if(!anotherSite.equals(this)){
                Line line = new Line(this, anotherSite);
                Line perpendicular = line.getPerpendicularLine(xLimit, yLimit);

                Polygon halfPlane = new Polygon();
                halfPlane.addPoint(perpendicular.getFirstPoint().getX(), perpendicular.getFirstPoint().getY());
                halfPlane.addPoint(perpendicular.getSecondPoint().getX(), perpendicular.getSecondPoint().getY());

                double halfPlaneSide = findHalfPlaneSide(perpendicular.getFirstPoint(), perpendicular.getSecondPoint(), this);

                if(halfPlaneSide < 0) {
                    
                }
            }
        }
    }

    public double findHalfPlaneSide(Point a, Point b, Point site){
        return (site.getX() - a.getX()) * ((b.getY() - a.getY())) - (site.getY() - a.getY()) * (b.getX() - a.getX());
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Site{" +
                "color=" + color +
                ", " + super.toString() +
                '}';
    }
}
