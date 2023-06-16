package main.videoprocessing;

import main.boxes.BasicBox;
import main.videoprocessing.fieldreaders.FieldReaderQueue;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

@Component
public class NonArrayFieldsHandler {

    private final FieldReaderQueue fieldReaderQueue;

    public NonArrayFieldsHandler(FieldReaderQueue fieldReaderQueue) {
        this.fieldReaderQueue = fieldReaderQueue;
    }

    public int handleNonArrayField(FileInputStream fileInputStream, int availableBytes, Object objectInstance, Field field, Class<?> fieldType) throws IOException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException, InstantiationException, NoSuchFieldException {
        FieldReadResult readResult = fieldReaderQueue.readField(fileInputStream, availableBytes, field, objectInstance);
        field.setAccessible(true);
        field.set(objectInstance, readResult.getFieldValue());
        availableBytes -= readResult.getReadedBytes();
        return availableBytes;
    }

}
