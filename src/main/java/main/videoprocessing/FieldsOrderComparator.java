package main.videoprocessing;

import main.videoprocessing.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

@Component
public class FieldsOrderComparator implements Comparator<Field> {
    @Override
    public int compare(Field o1, Field o2) {

        Class<?> class1 = o1.getDeclaringClass();
        Class<?> class2 = o2.getDeclaringClass();
        if (getParents(class1).contains(class2)){
            return 1;
        }
        else if (getParents(class2).contains(class1)){
            return -1;
        }
        int classCompare = class1.getSimpleName().compareTo(class2.getSimpleName());
        if (classCompare != 0){
            return classCompare;
        }

        int order1 = getOrder(o1);
        int order2 = getOrder(o2);

        return Integer.compare(order1, order2);

    }

    private Set<Class<?>> getParents (Class<?> classType ){
        Set<Class<?>> parents = new HashSet<>();
        Class<?> parent = classType.getSuperclass();
        while (!parent.equals(Object.class)){
            parents.add(parent);
            parent = parent.getSuperclass();
        }
        return parents;
    }

    private static int getOrder(Field o1) {
        Order order = o1.getDeclaredAnnotation(Order.class);
        return order ==null? o1.hashCode(): order.value();
    }
}
