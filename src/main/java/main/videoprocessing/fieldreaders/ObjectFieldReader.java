package main.videoprocessing.fieldreaders;

import main.boxes.BasicBox;
import main.videoprocessing.FieldReadResult;
import main.videoprocessing.FieldsHandler;
import main.videoprocessing.fieldreaders.FieldReader;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;

@Component
public class ObjectFieldReader implements FieldReader {

    private final FieldsHandler fieldsHandler;


    public ObjectFieldReader(FieldsHandler fieldsHandler) {
        this.fieldsHandler = fieldsHandler;
    }



    public FieldReadResult readFieldAsObject(FileInputStream fileInputStream, int availableBytes, Field field) throws IOException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException, InstantiationException, NoSuchFieldException {
        Class<?> fieldType = field.getType();
        Constructor<?> constructor = (fieldType.isArray()? fieldType.getComponentType():fieldType).getDeclaredConstructor();
        constructor.setAccessible(true);
        Object fieldInstance = constructor.newInstance();
        SortedSet<Field> fields = fieldsHandler.extractFields(fieldType.isArray() ? fieldType.getComponentType() : fieldType);
        int readedBytes = fieldsHandler.fillFields(fileInputStream, availableBytes, fieldInstance, fields);
        return new FieldReadResult(readedBytes, fieldInstance);
    }

    @Override
    public FieldReadResult readField(FileInputStream fileInputStream, int availableBytes, Field field, Object objectInstance) throws IOException, NoSuchFieldException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        return  readFieldAsObject(fileInputStream, availableBytes, field);
    }

    @Override
    public boolean isApplicable(Field field) {
        Class<?> classType = field.getType();
        Set<Class<?>> excludedClasses = new HashSet<>();
        excludedClasses.add(String.class);
        excludedClasses.add(BasicBox.class);
        excludedClasses.add(Number.class);
        return classType.isArray()? !excludedClasses.contains(classType.getComponentType()):
                !excludedClasses.contains(classType) ;
    }
}
