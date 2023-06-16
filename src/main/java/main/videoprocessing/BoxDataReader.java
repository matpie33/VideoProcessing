package main.videoprocessing;

import main.boxes.BasicBox;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;


@Component
public class BoxDataReader {

    private final BoxReader boxReader;


    public BoxDataReader(BoxReader boxReader, ObjectFieldReader objectFieldReader) {
        this.boxReader = boxReader;
    }

    public List<BasicBox> readAllBoxes(FileInputStream fileInputStream) throws IOException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException, InstantiationException, NoSuchFieldException {
        List<BasicBox> boxes = new ArrayList<>();

        while (fileInputStream.available() > 0){
            int availableBytes = fileInputStream.available();
            BasicBox box = boxReader.readBox(fileInputStream);
            System.out.println(box);
            if (box != null){
                boxes.add(box);
            }
        }
        return boxes;
    }

}
