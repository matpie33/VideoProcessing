package main.videoprocessing;

import main.boxes.FullBox;
import main.videoprocessing.annotation.*;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.*;

@Component
public class BoxDataReader implements ApplicationContextAware {

    private final FieldsOrderComparator fieldsOrderComparator;
    private ApplicationContext applicationContext;

    private static final int BYTES_AMOUNT_BOX_TYPE_AND_SIZE = 8;


    public BoxDataReader(FieldsOrderComparator fieldsOrderComparator) {
        this.fieldsOrderComparator = fieldsOrderComparator;
    }

    public void readAllBoxes(FileInputStream fileInputStream) throws IOException, IllegalAccessException, InvocationTargetException, ClassNotFoundException{


        while (fileInputStream.available() > 0){
            int availableBytes = fileInputStream.available();
            Result result = readTypeAndSizeOfBox(fileInputStream);
            System.out.println("box : "+ result.boxType + " len "+ result.boxLength);

            readBox(fileInputStream, result.boxType, result.boxLength - BYTES_AMOUNT_BOX_TYPE_AND_SIZE);
        }
    }

    private Result readTypeAndSizeOfBox(FileInputStream fileInputStream) throws IOException {
        byte[] boxSizeBuffer = new byte[4];
        byte[] boxTypeBuffer = new byte[4];
        int numberOfReadedBytes = fileInputStream.read(boxSizeBuffer, 0, 4);
        int boxLength = ByteBuffer.wrap(boxSizeBuffer).getInt();
        numberOfReadedBytes = fileInputStream.read(boxTypeBuffer, 0, 4);
        String boxType = new String(boxTypeBuffer);
        return new Result(boxLength, boxType);
    }

    private static class Result {
        public final int boxLength;
        public final String boxType;

        public Result(int boxLength, String boxType) {
            this.boxLength = boxLength;
            this.boxType = boxType;
        }
    }

    private void readBox(FileInputStream fileInputStream, String type, int availableBytes) throws IOException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        IBox box = getBoxByType(type);
        if (box == null){
            fileInputStream.skip(availableBytes);
            return;
        }
        SortedSet<Field> sortedFields = new TreeSet<>(fieldsOrderComparator);
        if (box instanceof FullBox){
            Collections.addAll(sortedFields, FullBox.class.getDeclaredFields());
        }
        sortedFields.addAll(Arrays.asList(box.getClass().getDeclaredFields()));
        for (Field field : sortedFields) {
            Class<?> fieldType = field.getType();
            if (IBox.class.isAssignableFrom(fieldType)){
                type = fieldType.getDeclaredAnnotation(Box.class).type();
                Result result = readTypeAndSizeOfBox(fileInputStream);
                availableBytes -= BYTES_AMOUNT_BOX_TYPE_AND_SIZE;
                readBox(fileInputStream, type, availableBytes);
            }
            else{
                availableBytes = readSimpleParameter(fileInputStream, availableBytes, box, field, fieldType);
            }


        }
        System.out.println(box);
    }

    private int readSimpleParameter(FileInputStream fileInputStream, int availableBytes, IBox box, Field field, Class<?> fieldType) throws IOException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        Integer bytesToRead = getAmountOfBytesToRead(field,box, availableBytes);
        byte [] buffer = new byte[bytesToRead];
        int readedAmount;
        field.setAccessible(true);
        if (fieldType.isArray()){
            int simpleElementSize = getArrayElementSize(field);
            int arraySize = bytesToRead / simpleElementSize;
            byte [] singleValue = new byte [simpleElementSize];
            Class elementClass = field.getType().getComponentType();
            Object array = Array.newInstance(elementClass, arraySize);
            for (int i=0; i< arraySize; i++){
                readedAmount = fileInputStream.read(singleValue, 0, simpleElementSize);
                Object valueFromBytes = getValueFromBytes(singleValue, elementClass);
                Array.set(array, i, valueFromBytes);
            }
            field.set(box, array);

        }
        else{
            readedAmount = fileInputStream.read(buffer, 0, bytesToRead);
            Object valueFromBytes = getValueFromBytes(buffer, field.getType());
            field.set(box, valueFromBytes);
            availableBytes -= readedAmount;

        }
        return availableBytes;
    }





    private Object getValueFromBytes(byte[] byteData, Class elementClass) throws IllegalAccessException {
        ByteBuffer wrapped = ByteBuffer.wrap(byteData);
        Object newValue = "no value";


        if (elementClass.equals(byte.class)){
            newValue= wrapped.get();
        }
        else if (elementClass.equals(short.class)){
            newValue= wrapped.getShort();
        }
        else if (elementClass.equals(int.class)){
            newValue=wrapped.getInt();
        }
        else if (elementClass.equals(long.class)){
            newValue=wrapped.getLong();
        }
        else if (elementClass.equals(Number.class)){
            switch (byteData.length){
                case 1:
                    newValue=wrapped.get();
                    break;
                case 2:
                    newValue=wrapped.getShort();
                    break;
                case 4:
                    newValue=wrapped.getInt();
                    break;
                case 8:
                    newValue=wrapped.getLong();
                    break;
            }
        }
        else if (elementClass.equals(String.class)){
            newValue=new String(byteData);
        }
        else{
            System.err.println("Not handled case");

        }
        return newValue;
    }

    private Integer getAmountOfBytesToRead(Field field, IBox box, int availableBytes) throws InvocationTargetException, IllegalAccessException {
        int bytesToRead;
        Class<?> fieldType = field.getType();
        SimpleTypeSize simpleTypeSize = field.getDeclaredAnnotation(SimpleTypeSize.class);
        ArraySize arraySize = field.getDeclaredAnnotation(ArraySize.class);
        Optional<Method> variableLengthProvider = Arrays.stream(field.getDeclaringClass().getDeclaredMethods()).filter(m -> m.getDeclaredAnnotation(VariableSizeProvider.class)!=null).findFirst();

        if (fieldType.isArray()) {
            if (arraySize != null) {
                bytesToRead = arraySize.value();
                int arrayElementSize = getArrayElementSize(field);
                bytesToRead *= arrayElementSize;
            }

            else{
                bytesToRead = availableBytes;
            }
        }
        else {
            if (simpleTypeSize !=null){
                bytesToRead = field.getDeclaredAnnotation(SimpleTypeSize.class).value();
            }
            else if (field.getDeclaredAnnotation(VariableSize.class) != null){
                bytesToRead = (int) variableLengthProvider.orElseThrow(()->
                        new IllegalArgumentException("Variable size provider not provided for class: "+field.getDeclaringClass()))
                        .invoke(box, field.getName());
            }
            else{
                bytesToRead = getPrimitiveSize(fieldType);
            }
        }

        return bytesToRead;
    }

    private int getArrayElementSize (Field field){
        SimpleTypeSize simpleTypeSize = field.getDeclaredAnnotation(SimpleTypeSize.class);
        if (simpleTypeSize!=null){
            return simpleTypeSize.value();
        }
        else{
            return getPrimitiveSize(field.getType().getComponentType());
        }
    }

    private int getPrimitiveSize(Class fieldType) {
        int size;
        if (fieldType.equals(byte.class)){
            size = 1;
        }
        else if (fieldType.equals(short.class)){
            size = 2;
        }
        else if (fieldType.equals(int.class)){
            size = 4;
        }
        else if (fieldType.equals(long.class)){
            size = 8;
        }
        else{
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
