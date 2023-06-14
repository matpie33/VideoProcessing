package main.boxes;

import main.videoprocessing.annotation.Text;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class Printable {

    @Override
    public String toString() {
        return addClassNameOptionally() + Arrays.stream(getClass().getDeclaredFields()).map(field->{
            Object o;
            String toStringValue = "";
            try {
                field.setAccessible(true);
                o = field.get(this);
                if (BasicBox.class.isAssignableFrom(field.getType())){
                    toStringValue = "\n";
                }
                if (field.getType().isArray()){
                    if (field.getType().getComponentType().isPrimitive()){
                        if (field.getDeclaredAnnotation(Text.class)!=null){
                            return formatResult(field, new String((byte[])o));
                        }
                        String primitiveType = field.getType().getComponentType().getName();
                        switch (primitiveType){
                            case "int":
                                return formatResult(field,Arrays.toString((int[])o));
                            case "byte":
                                return formatResult(field, Arrays.toString((byte[])o));
                            case "short":
                                return formatResult(field, Arrays.toString((short[])o));
                            case "long":
                                return formatResult(field, Arrays.toString((long[])o));
                            default:
                                throw new UnsupportedOperationException("");

                        }
                    }
                    else{
                        toStringValue+= Arrays.toString((Object[]) o);
                    }

                }
                else{
                    toStringValue+= field.get(this).toString();
                }

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            return formatResult(field, toStringValue);
        }).collect(Collectors.joining(", "));
    }

    private String addClassNameOptionally() {
        return Modifier.isStatic(getClass().getModifiers()) ? "" : getClass().getSimpleName() + ": ";
    }

    private String formatResult (Field field, String fieldValues){
        return field.getName() + ": " + fieldValues;
    }

}
