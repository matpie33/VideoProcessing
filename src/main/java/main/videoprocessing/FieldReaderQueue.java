package main.videoprocessing;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@Component
public class FieldReaderQueue {

    private List<FieldReader> fieldReaders = new ArrayList<>();

    public void addReader (FieldReader fieldReader){
        fieldReaders.add(fieldReader);
    }

    public Object readField(FileInputStream fileInputStream, Class<?> classType, int availableBytes, Field field) throws IOException, NoSuchFieldException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        return fieldReaders.stream().filter(reader->reader.isApplicable(classType)).findFirst().orElse(null)
                .readField(fileInputStream, availableBytes, field);
    }
}
