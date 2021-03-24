package HalfPlaneIntersectionMethod;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

public class VoronoiHalfPlaneIntersection extends JFrame {
    //  reference to all sites of the map/area
    private final ArrayList<Site> sites;

    /**
     * Constructor, automatically creates locuses for all sites
     * @param sites reference to sites ArrayList for which is required locuses estimation
     * @throws Exception error of sending empty list of sites or any another
     */
    public VoronoiHalfPlaneIntersection(ArrayList<Site> sites) throws Exception {
        //  initialize panel for sites and locuses drawing, setting window size and how to close program
        JPanel panel = new JPanel();
        getContentPane().add(panel);
        setSize(1000, 1000);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        if (sites.size() > 0) {
            //  find locus for each site
            for (int i = 0; i < sites.size(); i++) {
                sites.get(i).findLocus(sites, 1000, 1000);
            }

            this.sites = sites;
        } else {
            throw new Exception("Empty list of sites was transmitted");
        }
    }

    public void paint(Graphics g) {
        super.paint(g);  // fixes the immediate problem.
        Graphics2D g2 = (Graphics2D) g;

        //  iterate through all sites
        for (Site site : this.sites) {
            //  fill locus of the site with site color
            g2.setColor(site.getColor());
            g2.fill(site.getLocus());

            //  display each site as small black ellipsoid
            g2.setColor(Color.BLACK);
            g2.fill(new Ellipse2D.Double(site.getX() - 2.5, site.getY() - 2.5, 6, 6));
        }
    }


    public static void main(String[] args) throws Exception {

        //  sites for performing test
        Site firstSite = new Site(100, 100, Color.BLUE);
        Site secondSite = new Site(200, 210, Color.CYAN);
        Site thirdSite = new Site(460, 555, Color.GREEN);
        Site fourthSite = new Site(822, 673, Color.ORANGE);
        Site fifthSite = new Site(322, 111, Color.RED);
        Site sixthSite = new Site(999, 222, Color.PINK);

        //  store all sites in ArrayList
        ArrayList<Site> sites = new ArrayList<>();

        //  append all sites to the ArrayList
        sites.add(firstSite);
        sites.add(secondSite);
        sites.add(thirdSite);
        sites.add(fourthSite);
        sites.add(fifthSite);
        sites.add(sixthSite);

        //  create voronoi diagram with perpendicular half planes approach
        VoronoiHalfPlaneIntersection voronoiHalfPlaneIntersection = new VoronoiHalfPlaneIntersection(sites);

        //  show it
        voronoiHalfPlaneIntersection.setVisible(true);
    }
}
