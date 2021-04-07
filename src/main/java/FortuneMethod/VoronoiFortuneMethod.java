package FortuneMethod;

import Globals.Parameters;

import javax.swing.*;
import java.awt.*;

public class VoronoiFortuneMethod extends JFrame {
    SweepLine sweepLine;

    public VoronoiFortuneMethod() {
        JPanel panel = new JPanel();
        getContentPane().add(panel);
        setSize(Parameters.xLimit, Parameters.yLimit);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        sweepLine = new SweepLine(true);
    }

    public void paint(Graphics g) {
        super.paint(g);  // fixes the immediate problem.
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLACK);
        g2.draw(sweepLine);
    }

    public static void main(String[] args) {
        VoronoiFortuneMethod method = new VoronoiFortuneMethod();
        method.setVisible(true);
        while(true)
            if (method.sweepLine.moveLine())
                SwingUtilities.updateComponentTreeUI(method);
            else
                System.out.println("finished");
    }
}
