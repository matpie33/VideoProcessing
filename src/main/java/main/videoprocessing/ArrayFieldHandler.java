package main.videoprocessing;

import main.boxes.BasicBox;
import main.videoprocessing.annotation.VariableObjectSize;
import main.videoprocessing.annotation.VariableObjectSizeProvider;
import main.videoprocessing.fieldreaders.FieldReaderQueue;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

@Component
public class ArrayFieldHandler {

    private  final ElementSizeGetter elementSizeGetter;


    private final NumericFieldHandler numericFieldHandler;

    private final MethodGetter methodGetter;

    private final FieldReaderQueue fieldReaderQueue;

    public ArrayFieldHandler(ElementSizeGetter elementSizeGetter,  NumericFieldHandler numericFieldHandler, MethodGetter methodGetter, FieldReaderQueue fieldReaderQueue) {
        this.elementSizeGetter = elementSizeGetter;
        this.numericFieldHandler = numericFieldHandler;
        this.methodGetter = methodGetter;
        this.fieldReaderQueue = fieldReaderQueue;
    }

    public int handleArrayField(FileInputStream fileInputStream, int availableBytes, Object objectInstance, Field field) throws InvocationTargetException, IllegalAccessException, IOException, ClassNotFoundException, NoSuchMethodException, InstantiationException, NoSuchFieldException {
        int arraySize = elementSizeGetter.getArraySize(objectInstance, field, availableBytes);
        Class<?> elementClass = field.getType().getComponentType();
        Object array = Array.newInstance(elementClass, arraySize);
        for (int i=0; i<arraySize; i++){
            FieldReadResult readResult = fieldReaderQueue.readField(fileInputStream, availableBytes, field, objectInstance);
            availableBytes -= readResult.getReadedBytes();
            Array.set(array, i, readResult.getFieldValue());
        }
        field.setAccessible(true);
        field.set(objectInstance, array);
        return availableBytes;
    }

}
