package main.ui;

import main.videoprocessing.FileReader;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

@Component
public class AppView {

    private final FileReader fileReader;

    private static final String FILE_NAME = "/screen-capture.mp4";


    public AppView(FileReader fileReader) {
        this.fileReader = fileReader;
    }

    @PostConstruct
    public void initializeView () throws URISyntaxException, IOException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException, InstantiationException, NoSuchFieldException {

        EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setSize(new Dimension(500, 500));
            frame.setVisible(true);
            VideoView videoView = new VideoView();
            videoView.repaint();
            frame.setContentPane(videoView);
        });
        fileReader.readFile(getClass().getResource(FILE_NAME).toURI());


    }


}
