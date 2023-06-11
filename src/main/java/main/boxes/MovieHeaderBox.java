package main.boxes;

import main.videoprocessing.annotation.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component()
@Scope("prototype")
@Box(type="mvhd")
public class MovieHeaderBox extends FullBox {

    @VariableSize
    @Order(1)
    private Number creationTime;
    @Order(2)
    @VariableSize
    private Number modificationTime;
    @Order(3)
    @VariableSize
    private Number timeScale;
    @Order(4)
    @VariableSize
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

    @VariableSizeProvider
    public int getVariableLength (String parameterName){
        int length;

        switch (parameterName){
            case "creationTime":
            case "duration":
            case "modificationTime":
                length = version ==0? 4: 8;
                break;
            case "timeScale":
                length = 4;
                break;
            default:
                System.err.println("warning: unknown parameter in class : "+getClass() + " param: "+parameterName);
                length = 0;
        }
        return length;

    }

    @Override
    public String toString() {
        return "MovieHeaderBox{" +
                "creationTime=" + creationTime +
                ", modificationTime=" + modificationTime +
                ", timeScale=" + timeScale +
                ", duration=" + duration +
                ", rate=" + rate +
                ", volume=" + volume +
                ", reserved=" + reserved +
                ", reserved2=" + Arrays.toString(reserved2) +
                ", matrix=" + Arrays.toString(matrix) +
                ", preDefined=" + Arrays.toString(preDefined) +
                ", nextTrackId=" + nextTrackId +
                ", version=" + version +
                ", flags=" + Arrays.toString(flags) +
                '}';
    }
}
