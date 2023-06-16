package main.videoprocessing;

import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

@Component
public class MethodGetter {

    public Method getMethodWithAnnotation (Class<?> classType, Class<? extends Annotation> annotationClass){
        return Arrays.stream(classType.getDeclaredMethods())
                .filter(m -> m.getDeclaredAnnotation(annotationClass) != null)
                .findFirst().orElseThrow(() -> new IllegalArgumentException(
                        "Method with annotation not found: "+annotationClass));
    }
}
