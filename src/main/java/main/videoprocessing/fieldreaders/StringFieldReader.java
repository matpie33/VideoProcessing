package main.videoprocessing.fieldreaders;

import main.videoprocessing.*;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

@Component
public class StringFieldReader implements FieldReader {

    private final ElementSizeGetter elementSizeGetter;

    public StringFieldReader(ElementSizeGetter elementSizeGetter) {
        this.elementSizeGetter = elementSizeGetter;
    }

    @Override
    public FieldReadResult readField(FileInputStream fileInputStream, int availableBytes, Field field, Object objectInstance) throws IOException, NoSuchFieldException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        int bytesToRead = elementSizeGetter.getFieldSize(field, objectInstance, availableBytes);
        byte [] buffer = new byte[bytesToRead];
        int readed = fileInputStream.read(buffer, 0, bytesToRead);
        return new FieldReadResult(readed, new String(buffer));
    }

    @Override
    public boolean isApplicable( Field field) {
        Class<?> classType = field.getType();
        return classType.isArray()? classType.getComponentType().equals(String.class):
                classType.equals(String.class);
    }
}
