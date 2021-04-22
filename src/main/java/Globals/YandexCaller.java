package Globals;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public class YandexCaller {
    public static void main(String[] args) throws Exception {
        URL imageURL = new URL(MapHandler.getCompleteRequestURL(47.024512, 28.832157, 0.1, 0.1, 450, 450));
        BufferedImage img = ImageIO.read(imageURL);
        System.out.println(img);

        JFrame frame = new JFrame();
        JLabel labelImage = new JLabel(new ImageIcon(img));
        frame.getContentPane().add(labelImage, BorderLayout.CENTER);
        frame.setSize(img.getWidth() + 50, img.getHeight() + 50);
        frame.setVisible(true);
    }
}
