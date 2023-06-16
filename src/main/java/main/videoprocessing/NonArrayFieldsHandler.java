package main.videoprocessing;

import main.boxes.BasicBox;
import main.videoprocessing.annotation.Box;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

@Component
public class NonArrayFieldsHandler {

    private final ElementSizeGetter elementSizeGetter;


    private final NumericFieldHandler numericFieldHandler;



    private final FieldReaderQueue fieldReaderQueue;



    public NonArrayFieldsHandler(ElementSizeGetter elementSizeGetter,  NumericFieldHandler numericFieldHandler,  FieldReaderQueue fieldReaderQueue) {
        this.elementSizeGetter = elementSizeGetter;
        this.numericFieldHandler = numericFieldHandler;
        this.fieldReaderQueue = fieldReaderQueue;
    }

    public int handleNonArrayField(FileInputStream fileInputStream, int availableBytes, Object objectInstance, Field field, Class<?> fieldType) throws IOException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException, InstantiationException, NoSuchFieldException {
        Object fieldValue;
        if (BasicBox.class.isAssignableFrom(fieldType)){
            fieldValue = fieldReaderQueue.readField(fileInputStream, fieldType, availableBytes, field);
            availableBytes -= ((BasicBox)fieldValue).getBoxLength();
        }
        else if (fieldType.isPrimitive()){
            int bytesToRead = elementSizeGetter.getPrimitiveSize(fieldType);
            byte [] buffer = new byte[bytesToRead];
            int readedAmount = fileInputStream.read(buffer, 0, bytesToRead);
            availableBytes -= readedAmount;
            fieldValue = numericFieldHandler.getNumericValueFromBytes(buffer, field.getType());
        }
        else if (fieldType.equals(String.class)){
            int bytesToRead = elementSizeGetter.getFieldSize(field, objectInstance, availableBytes);
            byte [] buffer = new byte[bytesToRead];
            int readedAmount = fileInputStream.read(buffer, 0, bytesToRead);
            availableBytes -= readedAmount;
            fieldValue = new String(buffer);
        }
        else if (fieldType.equals(Number.class)){
            int bytesToRead = elementSizeGetter.getFieldSize(field, objectInstance, availableBytes);
            byte [] buffer = new byte[bytesToRead];
            int readedAmount = fileInputStream.read(buffer, 0, bytesToRead);
            availableBytes -= readedAmount;
            fieldValue = numericFieldHandler.getNumericValueFromBytes(buffer, Number.class);
        }
        else{
            fieldValue = fieldReaderQueue.readField(fileInputStream, fieldType, availableBytes, field);


        }
        field.setAccessible(true);
        field.set(objectInstance, fieldValue);
        return availableBytes;
    }

}
