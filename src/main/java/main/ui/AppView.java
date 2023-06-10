package main.ui;

import main.videoprocessing.BoxDataReader;
import main.videoprocessing.FileReader;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;

@Component
public class AppView {

    private final FileReader fileReader;

    public AppView(FileReader fileReader, BoxDataReader boxDataReader) {
        this.fileReader = fileReader;
    }

    @PostConstruct
    public void initializeView () throws URISyntaxException, IOException, IllegalAccessException {

        EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setSize(new Dimension(500, 500));
            frame.setVisible(true);
            VideoView videoView = new VideoView();
            videoView.repaint();
            frame.setContentPane(videoView);
        });
        fileReader.readBoxes();


    }


}
