package main.videoprocessing.fieldreaders;

import main.videoprocessing.FieldReadResult;
import main.videoprocessing.MethodGetter;
import main.videoprocessing.NumericFieldHandler;
import main.videoprocessing.annotation.VariableObjectSize;
import main.videoprocessing.annotation.VariableObjectSizeProvider;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

@Component
public class VariableSizeNumericFieldReader implements FieldReader {

    private final MethodGetter methodGetter;

    private final NumericFieldHandler numericFieldHandler;


    public VariableSizeNumericFieldReader(MethodGetter methodGetter, NumericFieldHandler numericFieldHandler) {
        this.methodGetter = methodGetter;
        this.numericFieldHandler = numericFieldHandler;
    }

    @Override
    public FieldReadResult readField(FileInputStream fileInputStream, int availableBytes, Field field, Object objectInstance) throws IOException, NoSuchFieldException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        int varSize = (int)methodGetter.getMethodWithAnnotation(field.getDeclaringClass(), VariableObjectSizeProvider.class)
                .invoke(objectInstance, field.getName());
        byte [] valueHolder = new byte [varSize];
        int readed = fileInputStream.read(valueHolder, 0, varSize);
        Class<?> fieldType = field.getType();
        Class<?> classType = fieldType.isArray()? fieldType.getComponentType(): fieldType;
        return new FieldReadResult(readed, numericFieldHandler.getNumericValueFromBytes(valueHolder, classType));
    }

    @Override
    public boolean isApplicable( Field field) {
        return field.getDeclaredAnnotation(VariableObjectSize.class)!=null ;
    }
}
