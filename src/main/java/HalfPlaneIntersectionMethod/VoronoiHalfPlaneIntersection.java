package HalfPlaneIntersectionMethod;

import javax.swing.*;
import java.awt.*;

public class VoronoiHalfPlaneIntersection extends JFrame {
    private Line line;

    public VoronoiHalfPlaneIntersection(Line line) {
        JPanel panel = new JPanel();
        getContentPane().add(panel);
        setSize(450, 450);

        JButton button =new JButton("press");
        panel.add(button);
        this.line = line;
    }

    public void paint(Graphics g) {
        super.paint(g);  // fixes the immediate problem.
        Graphics2D g2 = (Graphics2D) g;
        g2.draw(line.convertLineToGraphics());
    }


    public static void main(String[] args) {
        Line line = new Line(new Point(1, 1), new Point(300, 300));
        VoronoiHalfPlaneIntersection voronoi = new VoronoiHalfPlaneIntersection(line);

        System.out.println("Line distance Euclidean = " + line.lineDistanceEuclidean());
        System.out.println("Line distance Manhattan = " + line.lineDistanceManhattan());
        System.out.println("Line angle in degrees = " + line.angleOfLineInDegrees());
        System.out.println("Line angle in radians = " + line.angleOfLineInRadians());
        voronoi.setVisible(true);

    }
}
