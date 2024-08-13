package net.minecraft.client.main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class LaunchWrapper {
    public static void main(String[] args) {
        ImageIcon imageIcon;
        try {
            imageIcon = new ImageIcon(ImageIO.read(Main.class.getResourceAsStream("/szy-min.png")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JLabel splashLabel = new JLabel(imageIcon);
        JWindow splashScreen = new JWindow();
        splashScreen.getContentPane().add(splashLabel);

        splashScreen.setSize(imageIcon.getIconWidth(), imageIcon.getIconHeight());
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - splashScreen.getWidth()) / 2;
        int y = (screenSize.height - splashScreen.getHeight()) / 2;
        splashScreen.setLocation(x, y);

        splashScreen.setVisible(true);

        Main.doMain(splashScreen, args);
    }

}
