package HalfPlaneIntersectionMethod;

import javax.swing.*;
import java.awt.*;

public class VoronoiHalfPlaneIntersection extends JFrame {
    private Line line;
    private Line perp;

    public VoronoiHalfPlaneIntersection(Line line, Line perp) {
        JPanel panel = new JPanel();
        getContentPane().add(panel);
        setSize(450, 450);

        JButton button =new JButton("press");
        panel.add(button);
        this.line = line;
        this.perp = perp;
    }

    public void paint(Graphics g) {
        super.paint(g);  // fixes the immediate problem.
        Graphics2D g2 = (Graphics2D) g;
        g2.draw(line.convertLineToGraphics());
        g2.draw(perp.convertLineToGraphics());
    }


    public static void main(String[] args) {
        Line line = new Line(new Point(12, 14), new Point(350, 411));
        Line perp = line.getPerpendicularLine(450, 450);
        VoronoiHalfPlaneIntersection voronoi = new VoronoiHalfPlaneIntersection(line, perp);


//        System.out.println("Line distance Euclidean = " + line.lineDistanceEuclidean());
//        System.out.println("Line distance Manhattan = " + line.lineDistanceManhattan());
//        System.out.println("Line angle in degrees = " + line.angleOfLineInDegrees());
//        System.out.println("Line angle in radians = " + line.angleOfLineInRadians());
        System.out.println("Original line = " + line);
        System.out.println("Middle = " + line.middleOfLine());
        System.out.println("angle with the original line = " + line.angleOfPerpendicularToLine());
        System.out.println("Perp incremental X = " + Math.sin(line.angleOfLineInRadians()));
        System.out.println("Perp incremental Y = " + Math.cos(line.angleOfLineInRadians()));
        System.out.println("Perp points: " + perp);
        voronoi.setVisible(true);

    }
}
