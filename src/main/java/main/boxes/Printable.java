package main.boxes;

import main.videoprocessing.FieldsOrderComparator;
import main.videoprocessing.annotation.DoNotPrint;
import main.videoprocessing.annotation.PrintAsString;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Printable {

    @Override
    public String toString() {
        return addClassNameOptionally() + getDeclaredFields().map(field->{
            Object o;
            String toStringValue = "";
            if (field.getDeclaredAnnotation(DoNotPrint.class) != null){
                return "[not displayed]";
            }
            try {
                field.setAccessible(true);
                o = field.get(this);
                if (BasicBox.class.isAssignableFrom(field.getType())){
                    toStringValue = "\n";
                }
                if (field.getType().isArray()){
                    if (field.getType().getComponentType().isPrimitive()){
                        if (field.getDeclaredAnnotation(PrintAsString.class)!=null){
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

    private Stream<Field> getDeclaredFields() {
        Set<Field> declaredFields = new TreeSet<>(new FieldsOrderComparator());
        Class parent = getClass();
        while (!parent.equals(Object.class)){
            Collections.addAll(declaredFields, parent.getDeclaredFields());
            parent = parent.getSuperclass();
        }
        return declaredFields.stream();
    }


    private String addClassNameOptionally() {
        return Modifier.isStatic(getClass().getModifiers()) ? "" : getClass().getSimpleName() + ": ";
    }

    private String formatResult (Field field, String fieldValues){
        return field.getName() + ": " + fieldValues;
    }

}
