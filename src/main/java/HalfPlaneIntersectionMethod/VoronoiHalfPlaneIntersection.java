package HalfPlaneIntersectionMethod;

import Globals.ConsoleColors;
import Globals.Utils;
import Globals.MapUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

/**
 * Voronoi diagram class that builds locus for each site using perpendicular method.
 */
public class VoronoiHalfPlaneIntersection extends JFrame {
    //  reference to all sites of the map/area
    private Image image;

    public boolean isMapRequired;

    private int siteStoragePrevSize = 0;

    /**
     * Constructor, automatically creates loci for all sites
     * @throws Exception error of sending empty list of sites or any another
     */
    public VoronoiHalfPlaneIntersection(boolean isMapRequired) throws Exception {
        //  if there is need to load map data
        this.isMapRequired = isMapRequired;

        URL imageURL = new URL(MapUtils.getCompleteRequestURL());
        BufferedImage img = ImageIO.read(imageURL);
        //this.image = img.getScaledInstance(Utils.xLimit, Utils.yLimit, Image.SCALE_AREA_AVERAGING);
        this.image = img;
        Utils.xLimit = (short) img.getWidth();
        Utils.yLimit = (short) img.getHeight();

        JPanel panel = new JPanel();
        getContentPane().add(panel);
        setSize(Utils.xLimit + 8, Utils.yLimit + 8);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void paint(Graphics g) {
        super.paint(g);  // fixes the immediate problem.
        Graphics2D g2 = (Graphics2D) g.create();

        //  apply additional enhancements for graphics
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        if (isMapRequired) {
            g.drawImage(image, 0, 0, null);
        }

        if (Utils.sitesStorage.size() == 0) {
            System.out.println(ConsoleColors.RED +
                    "\tthere are no sites to show" +
                    ConsoleColors.RESET);
            return;
        }

        if (siteStoragePrevSize != Utils.sitesStorage.size()) {
            for (Site site : Utils.sitesStorage) {
                site.findLocus();
            }
            siteStoragePrevSize = Utils.sitesStorage.size();
        }

        //  iterate through all sites
        for (Site site : Utils.sitesStorage) {
            //  fill locus of the site with site color
            g2.setColor(Color.BLACK);
//            g2.fill(site.getLocus());
            g2.draw(site.getLocus());

            //  display each site as small black ellipsoid
            g2.setColor(Color.RED);
            g2.fill(new Ellipse2D.Double(site.x - 5, site.y - 5, 5, 5));
        }
    }


    public static void main(String[] args) throws Exception {
        //  coordinates of the Chisinau are considered as default values
        MapUtils.setMapHandlerParameters(47.024512, 28.832157, 0.1);

        //  set random generator and generate 100 random points for constructing Voronoi diagram
        Random rand = new Random();
        for (int i = 0; i < 50; i++) {
            Site newPoint = new Site(rand.nextDouble() * Utils.xLimit, rand.nextDouble() * Utils.yLimit, Color.getColor("s", rand.nextInt(16777215)), "name" + rand.nextDouble());
            newPoint.toGeographical();
            Utils.sitesStorage.add(newPoint);
        }

//          perform quick sort of all sites comparing by their "weight"
//        Site[] sitesArray = sites.toArray(new Site[0]);
//        QuickSort.performSort(sitesArray, 0, sitesArray.length - 1);
//        sites = new ArrayList<>(Arrays.asList(sitesArray));

        //  create voronoi diagram with perpendicular half planes approach
        VoronoiHalfPlaneIntersection voronoiHalfPlaneIntersection = new VoronoiHalfPlaneIntersection(true);

        //  show it
        voronoiHalfPlaneIntersection.setVisible(true);
    }
}
