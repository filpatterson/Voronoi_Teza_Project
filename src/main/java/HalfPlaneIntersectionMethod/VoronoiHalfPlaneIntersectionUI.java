package HalfPlaneIntersectionMethod;

import Globals.Utils;
import Globals.MapUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

/**
 * Voronoi diagram class that builds locus for each site using perpendicular method (version for UI program part)
 */
public class VoronoiHalfPlaneIntersectionUI extends JLabel {
    //  reference to all sites of the map/area
    private Image image;
    private double origLat;
    private double origLong;

    public boolean isMapRequired;

    private int siteStoragePrevSize = 0;

    /**
     * Constructor, automatically creates loci for all sites
     * @throws Exception error of sending empty list of sites or any another
     */
    public VoronoiHalfPlaneIntersectionUI(boolean isMapRequired) throws Exception {
        //  if there is need to load map data
        this.isMapRequired = isMapRequired;
        origLat = MapUtils.centerLatitude;
        origLong = MapUtils.centerLongitude;

        URL imageURL = new URL(MapUtils.getCompleteRequestURL());
        BufferedImage img = ImageIO.read(imageURL);

        this.image = img;
        Utils.xLimit = (short) img.getWidth();
        Utils.yLimit = (short) img.getHeight();

        setSize(Utils.xLimit + 8, Utils.yLimit + 8);
    }

    public void paint(Graphics g) {
        super.paint(g);  // fixes the immediate problem.
        Graphics2D g2 = (Graphics2D) g.create();

        if (origLat != MapUtils.centerLatitude || origLong != MapUtils.centerLongitude) {
            URL imageURL = null;
            try {
                imageURL = new URL(MapUtils.getCompleteRequestURL());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            BufferedImage img = null;
            try {
                img = ImageIO.read(imageURL);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.image = img;
            Utils.xLimit = (short) img.getWidth();
            Utils.yLimit = (short) img.getHeight();

            setSize(Utils.xLimit + 8, Utils.yLimit + 8);

            origLat = MapUtils.centerLatitude;
            origLong = MapUtils.centerLongitude;

            ArrayList<Site> newSitesStorage = new ArrayList<>();

            for (Site site : Utils.sitesStorage) {
                if (site.toCartesian()) {
                    newSitesStorage.add(site);
                }
            }

            Utils.sitesStorage = newSitesStorage;
            System.out.println(Utils.sitesStorage);
            Utils.isModified = true;
        }

        //  apply additional enhancements for graphics
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        if (isMapRequired) {
            g.drawImage(image, 0, 0, null);
        }

        if ((Utils.sitesStorage.size() != 0 && siteStoragePrevSize != Utils.sitesStorage.size()) || Utils.isModified) {
            for (Site site : Utils.sitesStorage) {
                site.findLocus();
            }
            siteStoragePrevSize = Utils.sitesStorage.size();
            Utils.isModified = false;
        }

        //  iterate through all sites
        for (Site site : Utils.sitesStorage) {
            //  fill locus of the site with site color
            g2.setColor(Color.BLACK);
//            g2.fill(site.getLocus());
            g2.draw(site.getLocus());

            //  display each site as small black ellipsoid
            g2.setColor(site.getColor());
            g2.fill(new Ellipse2D.Double(site.x - 10, site.y - 10, 10, 10));
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

        //  create voronoi diagram with perpendicular half planes approach
        VoronoiHalfPlaneIntersectionUI voronoiHalfPlaneIntersectionUI = new VoronoiHalfPlaneIntersectionUI(true);

        //  show it
        voronoiHalfPlaneIntersectionUI.setVisible(true);
    }
}