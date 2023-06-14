package main.videoprocessing;

import main.boxes.FullBox;
import main.boxes.sampleEntries.SampleEntry;
import main.boxes.sampleEntries.VisualSampleEntry;
import main.videoprocessing.annotation.*;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
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

    public void readAllBoxes(FileInputStream fileInputStream) throws IOException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException {


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

    private IBox readBox(FileInputStream fileInputStream, String type, int availableBytes) throws IOException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException {
        IBox box = getBoxByType(type);
        if (box == null){
            fileInputStream.skip(availableBytes);
            return null;
        }
        SortedSet<Field> sortedFields = new TreeSet<>(fieldsOrderComparator);
        Class<?> superclass = box.getClass().getSuperclass();
        while (!superclass.equals(Object.class)){
            Collections.addAll(sortedFields, superclass.getDeclaredFields());
            superclass = superclass.getSuperclass();
        }
        sortedFields.addAll(Arrays.asList(box.getClass().getDeclaredFields()));
        for (Field field : sortedFields) {
            Class<?> fieldType = field.getType();
            if (fieldType.isArray() && IBox.class.isAssignableFrom(fieldType.getComponentType()) && field.getDeclaredAnnotation(VariableArraySize.class)!=null){
                int arraySize = getVariableArraySize(box, field);
                Class<?> elementClass = field.getType().getComponentType();
                Object array = Array.newInstance(elementClass, arraySize);
                for (int i=0; i<arraySize; i++){
                    Result result = readTypeAndSizeOfBox(fileInputStream);
                    IBox subBox = readBox(fileInputStream, result.boxType, result.boxLength - BYTES_AMOUNT_BOX_TYPE_AND_SIZE);
                    Array.set(array, i, subBox);
                }
                field.setAccessible(true);
                field.set(box, array);
                System.out.println();
            }
            else if (IBox.class.isAssignableFrom(fieldType)){
                type = fieldType.getDeclaredAnnotation(Box.class).type();
                Result result = readTypeAndSizeOfBox(fileInputStream);
                availableBytes -= BYTES_AMOUNT_BOX_TYPE_AND_SIZE;
                IBox subBox = readBox(fileInputStream, type, result.boxLength - BYTES_AMOUNT_BOX_TYPE_AND_SIZE);
                field.setAccessible(true);
                field.set(box, subBox);
            }
            else{
                availableBytes = readSimpleParameter(fileInputStream, availableBytes, box, field, fieldType);
            }


        }
        System.out.println(box);
        return box;
    }

    private static int getVariableArraySize(IBox box, Field field) throws IllegalAccessException, InvocationTargetException {
        Method m = Arrays.stream(field.getDeclaringClass().getDeclaredMethods())
                .filter(method -> method.getDeclaredAnnotation(VariableArraySizeProvider.class) != null).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Variable array size provider not found"));
        int arraySize = (int)m.invoke(box, field.getName());
        return arraySize;
    }

    private int readSimpleParameter(FileInputStream fileInputStream, int availableBytes, IBox box, Field field, Class<?> fieldType) throws IOException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException {
        Integer bytesToRead = getAmountOfBytesToRead(field,box, availableBytes);
        byte [] buffer = new byte[bytesToRead];
        int readedAmount;
        field.setAccessible(true);
        if (fieldType.isArray()){
            int simpleElementSize = getArrayElementSize(field, box);
            int arraySize = bytesToRead / simpleElementSize;
            byte [] singleValue = new byte [simpleElementSize];
            Class elementClass = field.getType().getComponentType();
            Object array = Array.newInstance(elementClass, arraySize);
            for (int i=0; i< arraySize; i++){
                readedAmount = fileInputStream.read(singleValue, 0, simpleElementSize);
                availableBytes -= readedAmount;
                Object valueFromBytes = getValueFromBytes(singleValue, elementClass);
                Array.set(array, i, valueFromBytes);
            }
            if (Modifier.isFinal(field.getModifiers())){
                Method equals = Arrays.class.getMethod("equals", array.getClass(), array.getClass());
                if (!(boolean) equals.invoke(null, array, field.get(box))){
                    Method toString = Arrays.class.getMethod("toString", array.getClass());
                    throw new IllegalStateException("Difference on final variable: "+box.getClass() + " field: "+toString.invoke(null,field.get(box)) + " , "+
                            toString.invoke(null, array));
                }
            }
            else{
                field.set(box, array);
            }

        }
        else{
            readedAmount = fileInputStream.read(buffer, 0, bytesToRead);
            availableBytes -= readedAmount;
            Object valueFromBytes = getValueFromBytes(buffer, field.getType());
            if (Modifier.isFinal(field.getModifiers())){

                if (!valueFromBytes.equals(field.get(box))){
                    throw new IllegalStateException("Difference on final variable: "+box.getClass() + " field: "+field.get(box) + " , "+valueFromBytes);
                }
            }
            else{
                field.set(box, valueFromBytes);
            }


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
        Optional<Method> variableObjectSizeProvider = getVariableObjectSizeProvider(field, VariableObjectSizeProvider.class);
        Optional<Method> variableArraySizeProvider = getVariableObjectSizeProvider(field, VariableArraySizeProvider.class);

        if (fieldType.isArray()) {
            if (arraySize != null) {
                bytesToRead = arraySize.value();
                int arrayElementSize = getArrayElementSize(field, box);
                bytesToRead *= arrayElementSize;
            }
            else if (field.getDeclaredAnnotation(VariableArraySize.class)!=null){
                bytesToRead = (int) variableArraySizeProvider.orElseThrow(()->
                                new IllegalArgumentException("Variable size provider not provided for class: "+field.getDeclaringClass()))
                        .invoke(box, field.getName());
                int arrayElementSize = getArrayElementSize(field, box);
                bytesToRead *= arrayElementSize;
            }

            else{
                bytesToRead = availableBytes;
            }
        }
        else {
            if (arraySize != null){
                throw new IllegalArgumentException("Field is not array, but is annotated with @ArraySize" + field);
            }
            if (simpleTypeSize !=null){
                bytesToRead = field.getDeclaredAnnotation(SimpleTypeSize.class).value();
            }
            else if (field.getDeclaredAnnotation(VariableObjectSize.class) != null){
                bytesToRead = (int) variableObjectSizeProvider.orElseThrow(()->
                        new IllegalArgumentException("Variable size provider not provided for class: "+field.getDeclaringClass()))
                        .invoke(box, field.getName());
            }
            else if (fieldType.isPrimitive()){
                bytesToRead = getPrimitiveSize(fieldType);
            }
            else{
                bytesToRead = availableBytes;
            }
        }

        return bytesToRead;
    }

    private static Optional<Method> getVariableObjectSizeProvider(Field field, Class<? extends Annotation> annotationClass) {
        return Arrays.stream(field.getDeclaringClass().getDeclaredMethods()).filter(m -> m.getDeclaredAnnotation(annotationClass) != null).findFirst();
    }

    private int getArrayElementSize (Field field, IBox box) throws InvocationTargetException, IllegalAccessException {
        SimpleTypeSize simpleTypeSize = field.getDeclaredAnnotation(SimpleTypeSize.class);
        VariableObjectSize variableObjectSize = field.getDeclaredAnnotation(VariableObjectSize.class);
        if (simpleTypeSize!=null){
            return simpleTypeSize.value();
        }
        else if (variableObjectSize != null){
            return (int)getVariableObjectSizeProvider(field, VariableObjectSizeProvider.class).orElseThrow(()->new IllegalArgumentException("No variable object size provider")).invoke(box, field.getName());
        }
        else if (field.getType().getComponentType().isPrimitive()){
            return getPrimitiveSize(field.getType().getComponentType());
        }
        else{
            throw new IllegalArgumentException("Unknown array element type"+ box +"; " + field);
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
            throw new IllegalArgumentException("Field is not primitive type. "+fieldType);
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
