package main.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class VideoView extends JPanel {


    private BufferedImage bufferedImage;

    public VideoView() {
        bufferedImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
        for (int i=0; i<100; i++){
            bufferedImage.setRGB(i, 0, Color.red.getRGB());
        }

    }

    @Override
    protected void paintComponent(Graphics g) {

        g.drawImage(bufferedImage, 10, 10, null);
    }
}
