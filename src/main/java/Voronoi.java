import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Voronoi extends JFrame {
    //  image
    static BufferedImage image;

    //  arrays for coordinates of interest points
    static int[] px;
    static int[] py;

    //  array of colors for interest points
    static int[] color;

    //  amount of interest points
    static int amountOfInterestPoints = 100;

    //  set size of image in pixels
    static int size = 1000;

    public Voronoi() {
        //  set name of the graph
        super("Voronoi Diagram");

        //  set window size and position relatively to the screen
        setBounds(0, 0, size, size);

        //  set how program should be finished
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //  constructor of the image
        image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);

        //  set arrays for storing X and Y coordinates of interest points
        px = new int[amountOfInterestPoints];
        py = new int[amountOfInterestPoints];

        //  set array of colors for interest points
        color = new int[amountOfInterestPoints];

        //  randomize all coordinates and colors for interest points
        interestPointsRandomize(px, py, amountOfInterestPoints, color);

        //  find and calculate locusts for interest points pixel-by-pixel
        voronoiLocustsIdentification(px, py, amountOfInterestPoints, size, color);

        //  apply graphics of the image with all parameters
        Graphics2D graphics2D = image.createGraphics();

        //  set background color to be black
        graphics2D.setColor(Color.BLACK);

        //  color all areas matched conform parameters
        for (int i = 0; i < amountOfInterestPoints; i++) {
            graphics2D.fill(new Ellipse2D .Double(px[i] - 2.5, py[i] - 2.5, 5, 5));
        }

        //  try to create image
        try {
            ImageIO.write(image, "png", new File("voronoi.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Paint image on the screen using graphics by setting offset and observer that will handle displayable elements
     * @param graphics graphics module
     */
    public void paint(Graphics graphics) {
        graphics.drawImage(image, 0, 0, this);
    }

    /**
     * find distance between points on the two-dimensional space
     * @param x1 position on X-axis of the first point
     * @param x2 position on X-axis of the second point
     * @param y1 position on Y-axis of the first point
     * @param y2 position on Y-axis of the second point
     * @return distance between two points on the two-dimensional array
     */
    static double distance(int x1, int x2, int y1, int y2) {
        //  euclidian distance calculation between two points in two-dimensional space
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));

//        //  manhattan algorithm for estimating two-points distance in two-dimensional space
//        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    /**
     * generate locusts for all interest points (find nearest points) using colorization via pixel-by-pixel calculation
     * @param interestPointsX x coordinates of all interest points
     * @param interestPointsY y coordinates of all interest points
     * @param interestPointsValue how many interest points
     * @param imageSize size of sector for analysis
     * @param interestPointsColors colors of all interest points
     */
    static void voronoiLocustsIdentification(int[] interestPointsX, int[] interestPointsY, int interestPointsValue, int imageSize, int[] interestPointsColors) {
        //  iterate through all "pixels" of the image
        for (int x = 0; x < imageSize; x++) {
            for (int y = 0; y < imageSize; y++) {
                int n = 0;

                //  iterate through all cells of the image
                for (byte i = 0; i < interestPointsValue; i++) {
                    //  check to which cell current pixel is correlated by finding cell center of which is least distanced
                    if (distance(interestPointsX[i], x, interestPointsY[i], y) < distance(interestPointsX[n], x, interestPointsY[n], y)) {
                        n = i;
                    }
                }

                //  apply color of the cell to current "pixel"
                image.setRGB(x, y, interestPointsColors[n]);
            }
        }
    }

    /**
     * randomizes coordinates of interest points and sets random colors to those points
     * @param interestPointsX x coordinates array of all points of interest
     * @param interestPointsY x coordinates array of all points of interest
     * @param interestPointsValue how many interest points there are
     * @param interestPointsColors colors array for all points of interest
     */
    static void interestPointsRandomize(int[] interestPointsX, int[] interestPointsY, int interestPointsValue, int[] interestPointsColors) {
        //  set random generator
        Random rand = new Random();

        //  iterate through all points of interest of the field
        for (int i = 0; i < interestPointsValue; i++) {
            //  generate random position for the interest point on X-axis
            interestPointsX[i] = rand.nextInt(size);

            //  generate random position for the interest point on Y-axis
            interestPointsY[i] = rand.nextInt(size);

            //  generate random color for the interest point
            interestPointsColors[i] = rand.nextInt(16777215);
        }
    }

    public static void main(String[] args) {
        new Voronoi().setVisible(true);
    }
}
