package main.ui;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

@Component
public class AppView {

    @PostConstruct
    public void initializeView (){

        EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setSize(new Dimension(500, 500));
            frame.setVisible(true);
            VideoView videoView = new VideoView();
            videoView.repaint();
            frame.setContentPane(videoView);
        });


    }


}
