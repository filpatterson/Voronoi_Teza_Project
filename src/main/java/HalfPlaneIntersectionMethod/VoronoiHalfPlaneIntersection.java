package HalfPlaneIntersectionMethod;

import Globals.Parameters;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Random;

/**
 * Voronoi diagram class that builds locus for each site using perpendicular method.
 */
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
        setSize(Parameters.xLimit, Parameters.yLimit);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.sites = sites;

        //  perform calculation of locuses for sites
        if (sites.size() > 0) {
            long startTime = System.currentTimeMillis();

            //  find locus for each site
            for (Site site : sites)
                site.findLocus(sites);

            long endTime = System.currentTimeMillis();
            System.out.println("Execution time is " + (endTime - startTime) + " ms.");
        } else
            throw new Exception("Empty list of sites was transmitted");
    }

    public void paint(Graphics g) {
        super.paint(g);  // fixes the immediate problem.
        Graphics2D g2 = (Graphics2D) g;

        //  iterate through all sites
        for (Site site : this.sites) {
            //  fill locus of the site with site color
            g2.setColor(Color.BLACK);
//            g2.fill(site.getLocus());
            g2.draw(site.getLocus());

            //  display each site as small black ellipsoid
            g2.setColor(Color.RED);
            g2.fill(new Ellipse2D.Float(site.x - 2.5f, site.y - 2.5f, 5, 5));
        }
    }


    public static void main(String[] args) throws Exception {
        //  store all sites in ArrayList
        ArrayList<Site> sites = new ArrayList<>();

        //  set random generator and generate 100 random points for constructing Voronoi diagram
        Random rand = new Random();
        for (int i = 0; i < 10000; i++)
            sites.add(new Site(rand.nextInt(Parameters.xLimit), rand.nextInt(Parameters.yLimit), Color.getColor("s" ,rand.nextInt(16777215))));

//          perform quick sort of all sites comparing by their "weight"
//        Site[] sitesArray = sites.toArray(new Site[0]);
//        QuickSort.performSort(sitesArray, 0, sitesArray.length - 1);
//        sites = new ArrayList<>(Arrays.asList(sitesArray));

        //  create voronoi diagram with perpendicular half planes approach
        VoronoiHalfPlaneIntersection voronoiHalfPlaneIntersection = new VoronoiHalfPlaneIntersection(sites);

        //  show it
        voronoiHalfPlaneIntersection.setVisible(true);
    }
}
