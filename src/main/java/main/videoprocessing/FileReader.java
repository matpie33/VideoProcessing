package main.videoprocessing;

import main.boxes.BasicBox;
import main.videoprocessing.fieldreaders.BoxReader;
import main.videoprocessing.fieldreaders.FieldReader;
import main.videoprocessing.fieldreaders.FieldReaderQueue;
import main.videoprocessing.fieldreaders.ObjectFieldReader;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class FileReader {

    private Set<FieldReader> fieldReaders;

    private FieldReaderQueue fieldReaderQueue;

    private BoxReader boxReader;

    @PostConstruct
    public void init (){
        fieldReaders.forEach(fieldReader -> {
            if (fieldReader instanceof ObjectFieldReader){
                fieldReaderQueue.setElseReader(fieldReader);
            }
            else{
                fieldReaderQueue.addReader(fieldReader);

            }
        });
    }

    public FileReader(Set<FieldReader> fieldReaders, FieldReaderQueue fieldReaderQueue, BoxReader boxReader) {
        this.fieldReaders = fieldReaders;
        this.fieldReaderQueue = fieldReaderQueue;
        this.boxReader = boxReader;
    }

    public List<BasicBox> readFile(URI path) throws IOException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException, InstantiationException, NoSuchFieldException {
        FileInputStream fileInputStream = new FileInputStream(Path.of(path).toFile());
        return readAllBoxes(fileInputStream);


    }

    private List<BasicBox> readAllBoxes(FileInputStream fileInputStream) throws IOException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException, InstantiationException, NoSuchFieldException {
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
