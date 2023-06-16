package main.videoprocessing.fieldreaders;

import main.videoprocessing.FieldReadResult;
import main.videoprocessing.FileReader;
import main.videoprocessing.fieldreaders.FieldReader;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@Component
public class FieldReaderQueue {

    private List<FieldReader> fieldReaders = new ArrayList<>();

    private FieldReader elseReader;

    public void addReader (FieldReader fieldReader){
        fieldReaders.add(fieldReader);
    }

    public void setElseReader(FieldReader fieldReader){
        elseReader = fieldReader;
    }

    public FieldReadResult readField(FileInputStream fileInputStream, int availableBytes, Field field, Object objectInstance) throws IOException, NoSuchFieldException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        return fieldReaders.stream().filter(reader->reader.isApplicable(field)).findFirst().orElse(elseReader)
                .readField(fileInputStream, availableBytes, field, objectInstance);
    }
}
