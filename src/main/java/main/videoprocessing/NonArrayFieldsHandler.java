package main.videoprocessing;

import main.boxes.BasicBox;
import main.videoprocessing.annotation.Order;
import main.videoprocessing.fieldreaders.FieldReaderQueue;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

@Component
public class NonArrayFieldsHandler {

    private final FieldReaderQueue fieldReaderQueue;

    public NonArrayFieldsHandler(FieldReaderQueue fieldReaderQueue) {
        this.fieldReaderQueue = fieldReaderQueue;
    }

    public int handleNonArrayField(FileInputStream fileInputStream, int availableBytes, Object objectInstance, Field field) throws IOException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException, InstantiationException, NoSuchFieldException {
        FieldReadResult readResult = fieldReaderQueue.readField(fileInputStream, availableBytes, field, objectInstance);

        if (field.getDeclaredAnnotation(Order.class)==null){
            field = Arrays.stream(objectInstance.getClass().getDeclaredFields()).filter(declaredField -> declaredField.getType().equals(readResult.getFieldValue().getClass())).findFirst().orElseThrow(() -> new IllegalArgumentException("not found"));

        }
        field.setAccessible(true);
        field.set(objectInstance, readResult.getFieldValue());
        availableBytes -= readResult.getReadedBytes();
        return availableBytes;
    }

}
