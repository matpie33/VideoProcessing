package main.videoprocessing;

import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

@Component
public class NumericFieldHandler {

    public Object getNumericValueFromBytes(byte[] byteData, Class<?> elementClass) {
        ByteBuffer wrapped = ByteBuffer.wrap(byteData);
        Object newValue;


        if (elementClass.equals(byte.class)){
            newValue= wrapped.get();
        }
        else if (elementClass.equals(short.class)){
            newValue= wrapped.getShort();
        }
        else if (elementClass.equals(int.class)){
            newValue=wrapped.getInt();
        }
        else if (elementClass.equals(long.class)){
            newValue=wrapped.getLong();
        }
        else if (elementClass.equals(Number.class)){
            switch (byteData.length){
                case 1:
                    newValue=wrapped.get();
                    break;
                case 2:
                    newValue=wrapped.getShort();
                    break;
                case 4:
                    newValue=wrapped.getInt();
                    break;
                case 8:
                    newValue=wrapped.getLong();
                    break;
                default:
                    throw new IllegalArgumentException("Not handled case byte array size: "+byteData.length);

            }
        }
        else{
            throw new IllegalArgumentException("Not handled case numeric type: "+elementClass);

        }
        return newValue;
    }

}
