package main.videoprocessing.fieldreaders;

import main.videoprocessing.*;
import main.videoprocessing.annotation.VariableObjectSize;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

@Component
public class PrimitiveFieldReader implements FieldReader {


    private final NumericFieldHandler numericFieldHandler;

    private final ElementSizeGetter elementSizeGetter;

    public PrimitiveFieldReader(NumericFieldHandler numericFieldHandler, ElementSizeGetter elementSizeGetter) {
        this.elementSizeGetter = elementSizeGetter;
        this.numericFieldHandler = numericFieldHandler;
    }

    @Override
    public FieldReadResult readField(FileInputStream fileInputStream, int availableBytes, Field field, Object objectInstance) throws IOException, NoSuchFieldException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        Class<?> fieldType = field.getType();
        Class<?> classType = fieldType.isArray()? fieldType.getComponentType(): fieldType;
        byte [] valueHolder = new byte [elementSizeGetter.getPrimitiveSize(classType)];
        int readed = fileInputStream.read(valueHolder, 0, valueHolder.length);
        Object fieldValue = numericFieldHandler.getNumericValueFromBytes(valueHolder, classType);
        return new FieldReadResult(readed,fieldValue);
    }

    @Override
    public boolean isApplicable( Field field) {
        Class<?> fieldType = field.getType();
        return fieldType.isArray()? fieldType.getComponentType().isPrimitive():
                fieldType.isPrimitive();
    }
}
