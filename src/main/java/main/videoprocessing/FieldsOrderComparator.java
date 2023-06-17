package main.videoprocessing;

import main.videoprocessing.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Comparator;

@Component
public class FieldsOrderComparator implements Comparator<Field> {
    @Override
    public int compare(Field o1, Field o2) {
        int order1 = getOrder(o1);
        int order2 = getOrder(o2);
        Class<?> class1 = o1.getDeclaringClass();
        Class<?> class2 = o2.getDeclaringClass();
        if (!class1.equals(class2)){
            if (Modifier.isAbstract(class1.getModifiers())){
                return -1;
            }
            else{
                return +1;
            }
        }
        else{
            return Integer.compare(order1, order2);
        }
    }

    private static int getOrder(Field o1) {
        Order order = o1.getDeclaredAnnotation(Order.class);
        return order ==null? o1.hashCode(): order.value();
    }
}
