package HalfPlaneIntersectionOld;

import Globals.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Random;

/**
 * Voronoi diagram class that builds locus for each site using perpendicular method.
 */
public class VoronoiHalfPlaneIntersectionOld extends JFrame {
    //  reference to all sites of the map/area
    private final ArrayList<SiteOld> siteOlds;

    /**
     * Constructor, automatically creates locuses for all sites
     * @param siteOlds reference to sites ArrayList for which is required locuses estimation
     * @throws Exception error of sending empty list of sites or any another
     */
    public VoronoiHalfPlaneIntersectionOld(ArrayList<SiteOld> siteOlds) throws Exception {
        //  initialize panel for sites and locuses drawing, setting window size and how to close program
        JPanel panel = new JPanel();
        getContentPane().add(panel);
        setSize(Utils.xLimit, Utils.yLimit);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.siteOlds = siteOlds;

        if (siteOlds.size() > 0) {
            long startTime = System.currentTimeMillis();

            //  find locus for each site
            for (SiteOld siteOld : siteOlds)
                siteOld.findLocus(siteOlds);

            long endTime = System.currentTimeMillis();
            System.out.println("Execution time is " + (endTime - startTime) + " ms.");
        } else
            throw new Exception("Empty list of sites was transmitted");
    }

    public void paint(Graphics g) {
        super.paint(g);  // fixes the immediate problem.
        Graphics2D g2 = (Graphics2D) g;

        //  iterate through all sites
        for (SiteOld siteOld : this.siteOlds) {
            //  fill locus of the site with site color
            g2.setColor(Color.BLACK);
//            g2.fill(site.getLocus());
            g2.draw(siteOld.getLocus());

            //  display each site as small black ellipsoid
            g2.setColor(Color.RED);
            g2.fill(new Ellipse2D.Double(siteOld.getX() - 2.5, siteOld.getY() - 2.5, 6, 6));
        }
    }


    public static void main(String[] args) throws Exception {
        //  store all sites in ArrayList
        ArrayList<SiteOld> siteOlds = new ArrayList<>();

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
        for (int i = 0; i < 100; i++)
            siteOlds.add(new SiteOld(rand.nextInt(Utils.xLimit), rand.nextInt(Utils.yLimit), Color.getColor("s" ,rand.nextInt(16777215))));

        //  create voronoi diagram with perpendicular half planes approach
        VoronoiHalfPlaneIntersectionOld voronoiHalfPlaneIntersectionOld = new VoronoiHalfPlaneIntersectionOld(siteOlds);

        //  show it
        voronoiHalfPlaneIntersectionOld.setVisible(true);
    }
}
