package main.videoprocessing.annotation;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;

@Target(value=FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Skip {
}
