package main.videoprocessing;

import main.boxes.BasicBox;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public interface FieldReader {

    Object readField(FileInputStream fileInputStream, int availableBytes, Field field) throws IOException, NoSuchFieldException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException;

    boolean isApplicable (Class<?> classType);
}
