package main.videoprocessing;

import main.boxes.BasicBox;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.SortedSet;

@Component
public class ObjectFieldReader implements FieldReader {

    private final FieldsHandler fieldsHandler;


    public ObjectFieldReader(FieldsHandler fieldsHandler) {
        this.fieldsHandler = fieldsHandler;
    }



    public Object readFieldAsObject(FileInputStream fileInputStream, int availableBytes, Field field) throws IOException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException, InstantiationException, NoSuchFieldException {
        Class<?> fieldType = field.getType();
        Constructor<?> constructor = (fieldType.isArray()? fieldType.getComponentType():fieldType).getDeclaredConstructor();
        constructor.setAccessible(true);
        Object fieldInstance = constructor.newInstance();
        SortedSet<Field> fields = fieldsHandler.extractFields(fieldType.isArray() ? fieldType.getComponentType() : fieldType);
        fieldsHandler.fillFields(fileInputStream, availableBytes, fieldInstance, fields);
        return fieldInstance;
    }

    @Override
    public Object readField(FileInputStream fileInputStream, int availableBytes, Field field) throws IOException, NoSuchFieldException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        return readFieldAsObject(fileInputStream, availableBytes, field);
    }

    @Override
    public boolean isApplicable(Class<?> classType) {
        return classType.isArray()?  !BasicBox.class.isAssignableFrom(classType.getComponentType()):
                !BasicBox.class.isAssignableFrom(classType) ;
    }
}
