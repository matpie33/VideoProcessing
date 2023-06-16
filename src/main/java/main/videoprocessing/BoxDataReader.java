package main.videoprocessing;

import main.boxes.BasicBox;
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

    public List<BasicBox> readAllBoxes(FileInputStream fileInputStream) throws IOException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException, InstantiationException {
        List<BasicBox> boxes = new ArrayList<>();

        while (fileInputStream.available() > 0){
            int availableBytes = fileInputStream.available();
            Result result = readTypeAndSizeOfBox(fileInputStream);

            BasicBox box = readBox(fileInputStream, result.boxType, result.boxLength - BYTES_AMOUNT_BOX_TYPE_AND_SIZE);
            System.out.println(box);
            if (box != null){
                boxes.add(box);
            }
        }
        return boxes;
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

    private Method getMethodWithAnnotation (Class<?> classType, Class<? extends Annotation> annotationClass){
        return Arrays.stream(classType.getDeclaredMethods())
                .filter(m -> m.getDeclaredAnnotation(annotationClass) != null)
                .findFirst().orElseThrow(() -> new IllegalArgumentException(
                        "Method with annotation not found: "+annotationClass));
    }

    private int getArraySize (Object objectInstance, Field field, int availableBytes) throws InvocationTargetException, IllegalAccessException {
        ArraySize arraySize = field.getDeclaredAnnotation(ArraySize.class);
        VariableArraySize variableArraySize = field.getDeclaredAnnotation(VariableArraySize.class);
        if (arraySize != null){
            return arraySize.value();
        }
        else if (variableArraySize != null){
            Method method = getMethodWithAnnotation(field.getDeclaringClass(), VariableArraySizeProvider.class);
            method.setAccessible(true);
            return (int) method
                    .invoke(objectInstance, field.getName());
        }
        else return availableBytes / getFieldSize(field, objectInstance, availableBytes);
    }

    private BasicBox readBox(FileInputStream fileInputStream, String type, int availableBytes) throws IOException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException, InstantiationException {
        BasicBox box = getBoxByType(type);

        if (box == null){
            long skipped = fileInputStream.skip(availableBytes);
            return null;
        }
        SortedSet<Field> sortedFields = extractFields(box);
        fillFields(fileInputStream, availableBytes, box, sortedFields);
        return box;
    }

    private void fillFields(FileInputStream fileInputStream, int availableBytes, Object objectInstance, SortedSet<Field> sortedFields) throws IllegalAccessException, InvocationTargetException, IOException, ClassNotFoundException, NoSuchMethodException, InstantiationException {
        for (Field field : sortedFields) {
            Class<?> fieldType = field.getType();
            if (!fieldIsAvailable(objectInstance, field)) {
                continue;
            }
            if (fieldType.isArray() ){
                availableBytes = handleArrayField(fileInputStream, availableBytes, objectInstance, field, fieldType);
            }
            else {
                availableBytes = handleNonArrayField(fileInputStream, availableBytes, objectInstance, field, fieldType);
            }
        }
    }

    private int handleNonArrayField(FileInputStream fileInputStream, int availableBytes, Object objectInstance, Field field, Class<?> fieldType) throws IOException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException, InstantiationException {
        String type;
        Object fieldValue;
        if (BasicBox.class.isAssignableFrom(fieldType)){
            type = fieldType.getDeclaredAnnotation(Box.class).type();
            Result result = readTypeAndSizeOfBox(fileInputStream);
            availableBytes -= BYTES_AMOUNT_BOX_TYPE_AND_SIZE;
            fieldValue = readBox(fileInputStream, type, result.boxLength - BYTES_AMOUNT_BOX_TYPE_AND_SIZE);
        }
        else if (fieldType.isPrimitive()){
            int bytesToRead = getPrimitiveSize(fieldType);
            byte [] buffer = new byte[bytesToRead];
            int readedAmount = fileInputStream.read(buffer, 0, bytesToRead);
            availableBytes -= readedAmount;
            fieldValue = getNumericValueFromBytes(buffer, field.getType());
        }
        else if (fieldType.equals(String.class)){
            int bytesToRead = getFieldSize(field, objectInstance, availableBytes);
            byte [] buffer = new byte[bytesToRead];
            int readedAmount = fileInputStream.read(buffer, 0, bytesToRead);
            availableBytes -= readedAmount;
            fieldValue = new String(buffer);
        }
        else if (fieldType.equals(Number.class)){
            int bytesToRead = getFieldSize(field, objectInstance, availableBytes);
            byte [] buffer = new byte[bytesToRead];
            int readedAmount = fileInputStream.read(buffer, 0, bytesToRead);
            availableBytes -= readedAmount;
            fieldValue = getNumericValueFromBytes(buffer, Number.class);
        }
        else{
            fieldValue = readFieldAsObject(fileInputStream, availableBytes, field);

        }
        field.setAccessible(true);
        field.set(objectInstance, fieldValue);
        return availableBytes;
    }

    private int handleArrayField(FileInputStream fileInputStream, int availableBytes, Object objectInstance, Field field, Class<?> fieldType) throws InvocationTargetException, IllegalAccessException, IOException, ClassNotFoundException, NoSuchMethodException, InstantiationException {
        int arraySize = getArraySize(objectInstance, field, availableBytes);
        Class<?> elementClass = field.getType().getComponentType();
        Object array = Array.newInstance(elementClass, arraySize);
        for (int i=0; i<arraySize; i++){
            Object arrayElement;
            if (BasicBox.class.isAssignableFrom(fieldType.getComponentType())){
                Result result = readTypeAndSizeOfBox(fileInputStream);
                arrayElement = readBox(fileInputStream, result.boxType, result.boxLength - BYTES_AMOUNT_BOX_TYPE_AND_SIZE);
                availableBytes -= result.boxLength;
            }
            else if (field.getDeclaredAnnotation(VariableObjectSize.class)!=null){
                int varSize = (int)getMethodWithAnnotation(field.getDeclaringClass(), VariableObjectSizeProvider.class)
                        .invoke(objectInstance, field.getName());
                byte [] valueHolder = new byte [varSize];
                int readed = fileInputStream.read(valueHolder, 0, varSize);
                arrayElement = getNumericValueFromBytes(valueHolder, fieldType.getComponentType());
                availableBytes -= readed;

            }
            else if (field.getType().getComponentType().isPrimitive()){
                byte [] valueHolder = new byte [getPrimitiveSize(field.getType().getComponentType())];
                int readed = fileInputStream.read(valueHolder, 0, valueHolder.length);
                arrayElement = getNumericValueFromBytes(valueHolder, fieldType.getComponentType());
                availableBytes -= readed;
            }
            else if (fieldType.getComponentType().equals(String.class)){
                int bytesToRead = getFieldSize(field, objectInstance, availableBytes);
                byte [] buffer = new byte[bytesToRead];
                int readedAmount = fileInputStream.read(buffer, 0, bytesToRead);
                availableBytes -= readedAmount;
                arrayElement = new String(buffer);

            }
            else{
                arrayElement = readFieldAsObject(fileInputStream, availableBytes, field);
            }
            Array.set(array, i, arrayElement);
        }
        field.setAccessible(true);
        field.set(objectInstance, array);
        return availableBytes;
    }

    private boolean fieldIsAvailable(Object objectInstance, Field field) throws IllegalAccessException, InvocationTargetException {
        if (field.getDeclaredAnnotation(Conditional.class)!= null){
            return (boolean) getMethodWithAnnotation(field.getDeclaringClass(), ConditionProvider.class)
                    .invoke(objectInstance, field.getName());
        }
        return true;
    }

    private SortedSet<Field> extractFields(BasicBox box) {
        SortedSet<Field> sortedFields = new TreeSet<>(fieldsOrderComparator);
        Class<?> superclass = box.getClass().getSuperclass();
        while (!superclass.equals(Object.class)){
            Collections.addAll(sortedFields, superclass.getDeclaredFields());
            superclass = superclass.getSuperclass();
        }
        sortedFields.addAll(Arrays.asList(box.getClass().getDeclaredFields()));
        return sortedFields;
    }


    private Object readFieldAsObject(FileInputStream fileInputStream, int availableBytes, Field field) throws IOException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException, InstantiationException {
        SortedSet<Field> subFields = new TreeSet<>(fieldsOrderComparator);
        Class<?> fieldType = field.getType();
        Constructor<?> constructor = (fieldType.isArray()? fieldType.getComponentType():fieldType).getDeclaredConstructor();
        constructor.setAccessible(true);
        Object fieldInstance = constructor.newInstance();
        subFields.addAll(Arrays.asList(fieldType.isArray()? fieldType.getComponentType().getDeclaredFields(): fieldType.getDeclaredFields()));
        fillFields(fileInputStream, availableBytes, fieldInstance, subFields);
        return fieldInstance;
    }

    private Object getNumericValueFromBytes(byte[] byteData, Class<?> elementClass) {
        ByteBuffer wrapped = ByteBuffer.wrap(byteData);
        Object newValue;


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
                default:
                    throw new IllegalArgumentException("Not handled case byte array size: "+byteData.length);

            }
        }
        else{
            throw new IllegalArgumentException("Not handled case numeric type: "+elementClass);

        }
        return newValue;
    }


    private int getFieldSize(Field field, Object objectInstance, int availableBytes) throws InvocationTargetException, IllegalAccessException {
        SimpleTypeSize simpleTypeSize = field.getDeclaredAnnotation(SimpleTypeSize.class);
        VariableObjectSize variableObjectSize = field.getDeclaredAnnotation(VariableObjectSize.class);
        if (simpleTypeSize!=null){
            return simpleTypeSize.value();
        }
        else if (variableObjectSize != null){
            return (int)getMethodWithAnnotation(field.getDeclaringClass(), VariableObjectSizeProvider.class)
                    .invoke(objectInstance, field.getName());
        }
        else if (field.getType().isArray() && field.getType().getComponentType().isPrimitive()){
            return getPrimitiveSize(field.getType().getComponentType());
        }
        else if (field.getType().equals(String.class)){
            return availableBytes;
        }
        else{
            throw new IllegalArgumentException("Unknown array element type"+ objectInstance +"; " + field);
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

    private BasicBox getBoxByType(String type) {
        Map<String, BasicBox> beansOfType = applicationContext.getBeansOfType(BasicBox.class);
        BasicBox foundBox = null;
        for (BasicBox box : beansOfType.values()) {
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
