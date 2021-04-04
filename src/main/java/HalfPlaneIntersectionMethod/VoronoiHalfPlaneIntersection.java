package HalfPlaneIntersectionMethod;

import Globals.Parameters;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Random;

public class VoronoiHalfPlaneIntersection extends JFrame {
    //  reference to all sites of the map/area
    private final ArrayList<Site> sites;

    private static final Parameters parameters = new Parameters(1000, 1000);

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
            long startTime = System.currentTimeMillis();

            //  find locus for each site
            for (int i = 0; i < sites.size(); i++) {
                sites.get(i).findLocus(sites, parameters);
            }

            long endTime = System.currentTimeMillis();
            System.out.println("Execution time is " + (endTime - startTime) + " ms.");

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
        //  store all sites in ArrayList
        ArrayList<Site> sites = new ArrayList<>();

//        //  sites for performing test
//        Site firstSite = new Site(100, 100, Color.BLUE);
//        Site secondSite = new Site(200, 210, Color.CYAN);
//        Site thirdSite = new Site(460, 555, Color.GREEN);
//        Site fourthSite = new Site(822, 673, Color.ORANGE);
//        Site fifthSite = new Site(322, 111, Color.RED);
//        Site sixthSite = new Site(999, 222, Color.PINK);
//
//        //  append all sites to the ArrayList
//        sites.add(firstSite);
//        sites.add(secondSite);
//        sites.add(thirdSite);
//        sites.add(fourthSite);
//        sites.add(fifthSite);
//        sites.add(sixthSite);

        //  set random generator
        Random rand = new Random();
        for (int i = 0; i < 100; i++) {
            sites.add(new Site(rand.nextInt(1000), rand.nextInt(1000), Color.getColor("s" ,rand.nextInt(16777215))));
        }

        //  create voronoi diagram with perpendicular half planes approach
        VoronoiHalfPlaneIntersection voronoiHalfPlaneIntersection = new VoronoiHalfPlaneIntersection(sites);

        //  show it
        voronoiHalfPlaneIntersection.setVisible(true);
    }
}
