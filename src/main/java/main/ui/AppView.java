package main.ui;

import main.boxes.BasicBox;
import main.videodecoding.VideoDecoder;
import main.videoprocessing.FileReader;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.List;

@Component
public class AppView {

    private final FileReader fileReader;

    private final VideoDecoder videoDecoder;

    private static final String FILE_NAME = "/screen-capture.mp4";


    public AppView(FileReader fileReader, VideoDecoder videoDecoder) {
        this.fileReader = fileReader;
        this.videoDecoder = videoDecoder;
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
        List<BasicBox> boxes = fileReader.readFile(getClass().getResource(FILE_NAME).toURI());
        videoDecoder.decode(boxes);


    }


}
