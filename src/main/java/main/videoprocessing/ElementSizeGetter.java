package main.videoprocessing;

import main.videoprocessing.annotation.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Component
public class ElementSizeGetter {

    private final MethodGetter methodGetter;

    public ElementSizeGetter(MethodGetter methodGetter) {
        this.methodGetter = methodGetter;
    }

    public int getArraySize (Object objectInstance, Field field, int availableBytes) throws InvocationTargetException, IllegalAccessException {
        ArraySize arraySize = field.getDeclaredAnnotation(ArraySize.class);
        VariableArraySize variableArraySize = field.getDeclaredAnnotation(VariableArraySize.class);
        if (arraySize != null){
            return arraySize.value();
        }
        else if (variableArraySize != null){
            Method method = methodGetter.getMethodWithAnnotation(field.getDeclaringClass(), VariableArraySizeProvider.class);
            method.setAccessible(true);
            return (int) method
                    .invoke(objectInstance, field.getName());
        }
        else return availableBytes / getFieldSize(field, objectInstance, availableBytes);
    }



    public int getFieldSize(Field field, Object objectInstance, int availableBytes) throws InvocationTargetException, IllegalAccessException {
        SimpleTypeSize simpleTypeSize = field.getDeclaredAnnotation(SimpleTypeSize.class);
        VariableObjectSize variableObjectSize = field.getDeclaredAnnotation(VariableObjectSize.class);
        if (simpleTypeSize!=null){
            return simpleTypeSize.value();
        }
        else if (variableObjectSize != null){
            return (int)methodGetter.getMethodWithAnnotation(field.getDeclaringClass(), VariableObjectSizeProvider.class)
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

    public int getPrimitiveSize(Class fieldType) {
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

}
