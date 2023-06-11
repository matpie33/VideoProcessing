package main.videoprocessing;

import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

@Component
public class FileReader {

    private static final String FILE_NAME = "/screen-capture.mp4";

    private final BoxDataReader boxDataReader;


    public FileReader(BoxDataReader boxDataReader) {
        this.boxDataReader = boxDataReader;
    }

    public void readFile() throws URISyntaxException, IOException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        URI path = getClass().getResource(FILE_NAME).toURI();
        FileInputStream fileInputStream = new FileInputStream(Path.of(path).toFile());

        boxDataReader.readAllBoxes(fileInputStream);


    }


}
