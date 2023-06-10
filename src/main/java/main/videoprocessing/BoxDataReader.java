package main.videoprocessing;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.*;

@Component
public class BoxDataReader implements ApplicationContextAware {

    private final FieldsOrderComparator fieldsOrderComparator;
    private ApplicationContext applicationContext;

    public BoxDataReader(FieldsOrderComparator fieldsOrderComparator) {
        this.fieldsOrderComparator = fieldsOrderComparator;
    }

    public void readBytes (FileInputStream fileInputStream, String type, int availableBytes) throws IOException, IllegalAccessException {
        IBox box = getBoxByType(type);
        if (box == null){
            return;
        }
        SortedSet<Field> sortedFields = new TreeSet<>(fieldsOrderComparator);
        sortedFields.addAll(Arrays.asList(box.getClass().getDeclaredFields()));
        for (Field field : sortedFields) {
            Class<?> fieldType = field.getType();
            Integer bytesToRead = getAmountOfBytesToRead(field, availableBytes);
            byte [] buffer = new byte[bytesToRead];
            int readedAmount;
            field.setAccessible(true);
            if (fieldType.isArray()){
                int primitiveSize = getArrayElementSize(field);
                int arraySize = availableBytes/ primitiveSize;
                byte [] singleValue = new byte [primitiveSize];
                String [] array = new String [arraySize];

                for (int i=0; i< arraySize; i++){
                    readedAmount = fileInputStream.read(singleValue, 0, bytesToRead);
                    array[i] = new String(singleValue);
                }
                field.set(box, array);
            }
            else{
                readedAmount = fileInputStream.read(buffer, 0, bytesToRead);
                setField(box, field, buffer);
                availableBytes-= readedAmount;

            }

        }
    }

    private int getArrayElementSize (Field field){
        int bytesToRead = 0;
        if (Arrays.stream(field.getDeclaredAnnotations()).anyMatch(annotation -> annotation.annotationType().equals(Length.class))){
            bytesToRead = field.getDeclaredAnnotation(Length.class).value();
        }
        return bytesToRead;
    }

    private static void setField(IBox box, Field field, byte[] byteData) throws IllegalAccessException {
        ByteBuffer wrapped = ByteBuffer.wrap(byteData);
        switch (field.getType().getSimpleName()){
            case "int":
                field.setInt(box, wrapped.getInt());
                break;
            case "byte":
                field.setByte(box, wrapped.get());
                break;
            case "short":
                field.setShort(box, wrapped.getShort());
                break;
            case "long":
                field.setLong(box, wrapped.getLong());
                break;
            case "String":
                field.set(box, new String(byteData));
        }
    }

    private Integer getAmountOfBytesToRead(Field field, int availableBytes) {
        int bytesToRead = 0;
        Class<?> fieldType = field.getType();
        if (Arrays.stream(field.getDeclaredAnnotations()).anyMatch(annotation -> annotation.annotationType().equals(Length.class))){
            bytesToRead = field.getDeclaredAnnotation(Length.class).value();
        }
        else if (fieldType.isArray()){

            bytesToRead = availableBytes;
        }
        else {
            bytesToRead = getPrimitiveSize(fieldType.getSimpleName());
        }

        return bytesToRead;
    }

    private int getPrimitiveSize(String fieldType) {
        int size;
        switch (fieldType){
            case "int":
                size =4;
                break;
            case "byte":
                size =1;
                break;
            case "short":
                size =2;
                break;
            case "long":
                size =8;
                break;
            default:
                System.out.println("WARNING: unhandled type: "+fieldType);
                size = 0;
        }
        return size;
    }

    private IBox getBoxByType(String type) {
        Map<String, IBox> beansOfType = applicationContext.getBeansOfType(IBox.class);
        IBox foundBox = null;
        for (IBox box : beansOfType.values()) {
            if (box.getClass().getDeclaredAnnotation(Box.class).type().equals(type)){
                foundBox = box;
                break;
            }
        }
        if (foundBox == null){
            System.out.println("Warning: box of type: "+type + " not found.");
        }
        return foundBox;

    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
