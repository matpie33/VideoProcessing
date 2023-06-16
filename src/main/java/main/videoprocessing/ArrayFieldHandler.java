package main.videoprocessing;

import main.boxes.BasicBox;
import main.videoprocessing.annotation.VariableObjectSize;
import main.videoprocessing.annotation.VariableObjectSizeProvider;
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

    public int handleArrayField(FileInputStream fileInputStream, int availableBytes, Object objectInstance, Field field, Class<?> fieldType) throws InvocationTargetException, IllegalAccessException, IOException, ClassNotFoundException, NoSuchMethodException, InstantiationException, NoSuchFieldException {
        int arraySize = elementSizeGetter.getArraySize(objectInstance, field, availableBytes);
        Class<?> elementClass = field.getType().getComponentType();
        Object array = Array.newInstance(elementClass, arraySize);
        for (int i=0; i<arraySize; i++){
            Object arrayElement;
            if (BasicBox.class.isAssignableFrom(fieldType.getComponentType())){
                arrayElement = fieldReaderQueue.readField(fileInputStream, fieldType, availableBytes, field);
                availableBytes -= ((BasicBox)arrayElement).getBoxLength();
            }
            else if (field.getDeclaredAnnotation(VariableObjectSize.class)!=null){
                int varSize = (int)methodGetter.getMethodWithAnnotation(field.getDeclaringClass(), VariableObjectSizeProvider.class)
                        .invoke(objectInstance, field.getName());
                byte [] valueHolder = new byte [varSize];
                int readed = fileInputStream.read(valueHolder, 0, varSize);
                arrayElement = numericFieldHandler.getNumericValueFromBytes(valueHolder, fieldType.getComponentType());
                availableBytes -= readed;

            }
            else if (field.getType().getComponentType().isPrimitive()){
                byte [] valueHolder = new byte [elementSizeGetter.getPrimitiveSize(field.getType().getComponentType())];
                int readed = fileInputStream.read(valueHolder, 0, valueHolder.length);
                arrayElement = numericFieldHandler.getNumericValueFromBytes(valueHolder, fieldType.getComponentType());
                availableBytes -= readed;
            }
            else if (fieldType.getComponentType().equals(String.class)){
                int bytesToRead = elementSizeGetter.getFieldSize(field, objectInstance, availableBytes);
                byte [] buffer = new byte[bytesToRead];
                int readedAmount = fileInputStream.read(buffer, 0, bytesToRead);
                availableBytes -= readedAmount;
                arrayElement = new String(buffer);

            }
            else{
                arrayElement = fieldReaderQueue.readField(fileInputStream, fieldType, availableBytes, field);

            }
            Array.set(array, i, arrayElement);
        }
        field.setAccessible(true);
        field.set(objectInstance, array);
        return availableBytes;
    }

}
