package main.videoprocessing;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Comparator;

@Component
public class FieldsOrderComparator implements Comparator<Field> {
    @Override
    public int compare(Field o1, Field o2) {
        int order1 = getOrder(o1);
        int order2 = getOrder(o2);
        return Integer.compare(order1, order2);
    }

    private static int getOrder(Field o1) {
        return o1.getDeclaredAnnotation(Order.class).value();
    }
}
