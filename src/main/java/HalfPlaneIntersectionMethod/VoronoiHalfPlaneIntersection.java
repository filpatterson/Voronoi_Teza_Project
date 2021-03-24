package HalfPlaneIntersectionMethod;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

public class VoronoiHalfPlaneIntersection extends JFrame {
//    private Line line;
//    private Line perp;
    private final ArrayList<Site> sites;

//    public VoronoiHalfPlaneIntersection(Line line, Line perp) {
//        JPanel panel = new JPanel();
//        getContentPane().add(panel);
//        setSize(450, 450);
//
//        this.line = line;
//        this.perp = perp;
//    }

    public VoronoiHalfPlaneIntersection(ArrayList<Site> sites) throws Exception {
        JPanel panel = new JPanel();
        getContentPane().add(panel);
        setSize(1000, 1000);
        //  set how program should be finished
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        if (sites.size() > 0) {
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
//        g2.draw(line.convertLineToGraphics());
//        g2.draw(perp.convertLineToGraphics());
        for (int i = 0; i < this.sites.size(); i++) {
            g2.setColor(sites.get(i).getColor());
            g2.fill(sites.get(i).getLocus());
//            for (int j = 0; j < sites.get(i).getPerps().size(); j++) {
//                g2.setColor(Color.BLACK);
//                if (sites.get(i).getColor().equals(Color.RED)) {
//                    g2.draw(sites.get(i).getPerps().get(j).convertLineToGraphics());
//                }
//            }
            g2.setColor(Color.BLACK);
            g2.fill(new Ellipse2D.Double(this.sites.get(i).getX() - 2.5, this.sites.get(i).getY() - 2.5, 5, 5));
        }
    }


    public static void main(String[] args) throws Exception {
//        Line line = new Line(new Point(12, 14), new Point(350, 411));
//        Line perp = line.getPerpendicularLine(450, 450);

        Site firstSite = new Site(100, 100, Color.BLUE);
        Site secondSite = new Site(200, 210, Color.CYAN);
        Site thirdSite = new Site(460, 555, Color.GREEN);
        Site fourthSite = new Site(822, 673, Color.ORANGE);
        Site fifthSite = new Site(322, 111, Color.RED);
        Site sixthSite = new Site(999, 222, Color.PINK);

        ArrayList<Site> sites = new ArrayList<>();

        sites.add(firstSite);
        sites.add(secondSite);
        sites.add(thirdSite);
        sites.add(fourthSite);
        sites.add(fifthSite);
        sites.add(sixthSite);

//        firstSite.findLocus(sites, 450, 450);

        VoronoiHalfPlaneIntersection voronoiHalfPlaneIntersection = new VoronoiHalfPlaneIntersection(sites);

        voronoiHalfPlaneIntersection.setVisible(true);

//        VoronoiHalfPlaneIntersection voronoi = new VoronoiHalfPlaneIntersection(line, perp);
//
//
////        System.out.println("Line distance Euclidean = " + line.lineDistanceEuclidean());
////        System.out.println("Line distance Manhattan = " + line.lineDistanceManhattan());
////        System.out.println("Line angle in degrees = " + line.angleOfLineInDegrees());
////        System.out.println("Line angle in radians = " + line.angleOfLineInRadians());
//        System.out.println("Original line = " + line);
//        System.out.println("Middle = " + line.middleOfLine());
//        System.out.println("angle with the original line = " + line.angleOfPerpendicularToLine());
//        System.out.println("Perp incremental X = " + Math.sin(line.angleOfLineInRadians()));
//        System.out.println("Perp incremental Y = " + Math.cos(line.angleOfLineInRadians()));
//        System.out.println("Perp points: " + perp);
//        voronoi.setVisible(true);

    }
}
