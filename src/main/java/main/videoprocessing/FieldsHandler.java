package main.videoprocessing;

import main.videoprocessing.annotation.ConditionProvider;
import main.videoprocessing.annotation.Conditional;
import main.videoprocessing.annotation.Skip;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

@Component
public class FieldsHandler {

    private final ArrayFieldHandler arrayFieldHandler;

    private final NonArrayFieldsHandler nonArrayFieldsHandler;

    private final MethodGetter methodGetter;

    private final FieldsOrderComparator fieldsOrderComparator;

    public FieldsHandler(ArrayFieldHandler arrayFieldHandler, NonArrayFieldsHandler nonArrayFieldsHandler, MethodGetter methodGetter, FieldsOrderComparator fieldsOrderComparator) {
        this.arrayFieldHandler = arrayFieldHandler;
        this.nonArrayFieldsHandler = nonArrayFieldsHandler;
        this.methodGetter = methodGetter;
        this.fieldsOrderComparator = fieldsOrderComparator;
    }

    public int fillFields(FileInputStream fileInputStream, int availableBytes, Object objectInstance, SortedSet<Field> sortedFields) throws IllegalAccessException, InvocationTargetException, IOException, ClassNotFoundException, NoSuchMethodException, InstantiationException, NoSuchFieldException {
        int availableBytesStart = availableBytes;
        for (Field field : sortedFields) {
            Class<?> fieldType = field.getType();
            if (field.getDeclaredAnnotation(Skip.class)!=null || !isFieldAvailable(objectInstance, field)) {
                continue;
            }
            if (fieldType.isArray() ){
                availableBytes = arrayFieldHandler.handleArrayField(fileInputStream, availableBytes, objectInstance, field);
            }
            else {
                availableBytes = nonArrayFieldsHandler.handleNonArrayField(fileInputStream, availableBytes, objectInstance, field);
            }
        }
        return availableBytesStart - availableBytes;
    }

    private boolean isFieldAvailable(Object objectInstance, Field field) throws IllegalAccessException, InvocationTargetException {
        if (field.getDeclaredAnnotation(Conditional.class)!= null){
            return (boolean) methodGetter.getMethodWithAnnotation(field.getDeclaringClass(), ConditionProvider.class)
                    .invoke(objectInstance, field.getName());
        }
        return true;
    }

    public SortedSet<Field> extractFields(Class<?> classWithFields) {
        SortedSet<Field> sortedFields = new TreeSet<>(fieldsOrderComparator);
        Class<?> superclass = classWithFields.getSuperclass();
        while (!superclass.equals(Object.class)){
            Collections.addAll(sortedFields, superclass.getDeclaredFields());
            superclass = superclass.getSuperclass();
        }
        sortedFields.addAll(Arrays.asList(classWithFields.getDeclaredFields()));
        return sortedFields;
    }

}
