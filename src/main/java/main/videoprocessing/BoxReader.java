package main.videoprocessing;

import main.boxes.BasicBox;
import main.videoprocessing.annotation.Box;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.SortedSet;

@Component
public class BoxReader implements ApplicationContextAware, FieldReader{

    private ApplicationContext applicationContext;
    private static final int BYTES_AMOUNT_BOX_TYPE_AND_SIZE = 8;

    private final FieldsHandler fieldsHandler;

    private final FieldReaderQueue fieldReaderQueue;

    public BoxReader(FieldsHandler fieldsHandler, FieldReaderQueue fieldReaderQueue) {
        this.fieldsHandler = fieldsHandler;
        this.fieldReaderQueue = fieldReaderQueue;
    }

    @PostConstruct
    public void init (){
        fieldReaderQueue.addReader(this);
    }

    public BasicBox readBox(FileInputStream fileInputStream) throws IOException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException, InstantiationException, NoSuchFieldException {
        Result result = readTypeAndSizeOfBox(fileInputStream);
        BasicBox box = getBoxByType(result.getBoxType());

        int boxLength = result.getBoxLength();
        int availableBytes = boxLength - BYTES_AMOUNT_BOX_TYPE_AND_SIZE;
        if (box == null){
            long skipped = fileInputStream.skip(availableBytes);
            return null;
        }
        else{
            Field boxSize = BasicBox.class.getDeclaredField("boxLength");
            boxSize.setAccessible(true);
            boxSize.set(box, boxLength);
            SortedSet<Field> sortedFields = fieldsHandler.extractFields(box.getClass());
            fieldsHandler.fillFields(fileInputStream, availableBytes, box, sortedFields);
            return box;
        }

    }

    private BasicBox getBoxByType(String type) {
        Map<String, BasicBox> beansOfType = applicationContext.getBeansOfType(BasicBox.class);
        BasicBox foundBox = null;
        for (BasicBox box : beansOfType.values()) {
            if (box.getClass().getDeclaredAnnotation(Box.class).type().equals(type)){
                foundBox = box;
                break;
            }
        }
        if (foundBox == null){
            System.out.println("Warning: box of type: "+type + " not found.");
        }
        return foundBox;

    }

    public Result readTypeAndSizeOfBox(FileInputStream fileInputStream) throws IOException {
        byte[] boxSizeBuffer = new byte[4];
        byte[] boxTypeBuffer = new byte[4];
        int numberOfReadedBytes = fileInputStream.read(boxSizeBuffer, 0, 4);
        int boxLength = ByteBuffer.wrap(boxSizeBuffer).getInt();
        numberOfReadedBytes = fileInputStream.read(boxTypeBuffer, 0, 4);
        String boxType = new String(boxTypeBuffer);
        return new Result(boxLength, boxType);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object readField(FileInputStream fileInputStream, int availableBytes, Field field) throws IOException, NoSuchFieldException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        return readBox(fileInputStream);
    }

    @Override
    public boolean isApplicable(Class<?> classType) {
        return classType.isArray()? BasicBox.class.isAssignableFrom(classType.getComponentType()) : BasicBox.class
                .isAssignableFrom(classType);
    }
}
