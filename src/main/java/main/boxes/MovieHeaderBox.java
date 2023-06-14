package main.boxes;

import main.videoprocessing.annotation.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component()
@Scope("prototype")
@Box(type="mvhd")
public class MovieHeaderBox extends FullBox {

    @VariableObjectSize
    @Order(1)
    private Number creationTime;
    @Order(2)
    @VariableObjectSize
    private Number modificationTime;
    @Order(3)
    private int timeScale;
    @Order(4)
    @VariableObjectSize
    private Number duration;
    @Order(5)
    private int rate;
    @Order(6)
    private short volume;
    @Order(7)
    private final short reserved = 0;
    @Order(8)
    @ArraySize(2)
    private final int [] reserved2 = {0, 0};
    @Order(9)
    @ArraySize(9)
    private int [] matrix;
    @Order(10)
    @ArraySize(6)
    private final int [] preDefined = {0,0,0,0,0,0};
    @Order(11)
    private int nextTrackId;

    @VariableObjectSizeProvider
    public int getObjectSize(String parameterName){
        int length;

        switch (parameterName){
            case "creationTime":
            case "duration":
            case "modificationTime":
                length = version ==0? 4: 8;
                break;
            default:
                System.err.println("warning: unknown parameter in class : "+getClass() + " param: "+parameterName);
                length = 0;
        }
        return length;

    }

}
