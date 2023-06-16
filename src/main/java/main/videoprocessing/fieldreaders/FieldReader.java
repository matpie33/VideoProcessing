package main.videoprocessing.fieldreaders;

import main.boxes.BasicBox;
import main.videoprocessing.FieldReadResult;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public interface FieldReader {

    FieldReadResult readField(FileInputStream fileInputStream, int availableBytes, Field field, Object objectInstance) throws IOException, NoSuchFieldException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException;

    boolean isApplicable (Field field);
}
