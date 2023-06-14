package main.boxes;

import main.videoprocessing.annotation.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component()
@Scope("prototype")
@Box(type="elst")
public class EditListBox extends FullBox {
    @Order(1)
    private int entryCount;
    @Order(2)
    @VariableObjectSize
    @VariableArraySize
    private Number[] segmentDurations;
    @Order(3)
    @VariableObjectSize
    @VariableArraySize
    private Number [] mediaTimes;
    @Order(4)
    @VariableArraySize
    private short[] mediaRateInteger;
    @Order(5)
    @VariableArraySize
    private short[] mediaRateFraction;

    @VariableArraySizeProvider
    public int getArraySize (String parameterName){
        int size;
        switch (parameterName){
            case "segmentDurations":
            case "mediaTimes":
            case "mediaRateInteger":
            case "mediaRateFraction":
                size = entryCount;
                break;
            default:
                System.err.println("warning: unknown parameter in class : "+getClass() + " param: "+parameterName);
                size = 0;
        }
        return size;
    }

    @VariableObjectSizeProvider
    public int getObjectSize(String parameterName){
        int length;

        switch (parameterName){
            case "segmentDurations":
            case "mediaTimes":
                length = version ==0? 4: 8;
                break;
            default:
                System.err.println("warning: unknown parameter in class : "+getClass() + " param: "+parameterName);
                length = 0;
        }
        return length;

    }

}
