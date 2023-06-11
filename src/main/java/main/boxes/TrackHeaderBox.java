package main.boxes;

import main.videoprocessing.annotation.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component()
@Scope("prototype")
@Box(type="tkhd")
public class TrackHeaderBox extends FullBox {

    @Order(1)
    @VariableObjectSize
    private Number creationTime;
    @Order(2)
    @VariableObjectSize
    private Number modificationTime;
    @Order(3)
    private int trackId;

    @Order(4)
    private int reserved;
    @Order(5)
    @VariableObjectSize
    private int duration;

    @Order(6)
    @ArraySize(2)
    private final int[] reserved2 = {0, 0};

    @Order(7)
    private short layer = 0;

    @Order(8)
    private short alternateGroup =0;

    @Order(9)
    private short volume;
    @Order(10)
    private short reserved3 = 0;
    @Order(11)
    @ArraySize(9)
    private int[] matrix;
    @Order(12)
    private int width;
    @Order(13)
    private int height;

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

    @Override
    public String toString() {
        return "TrackHeaderBox{" +
                "creationTime=" + creationTime +
                ", modificationTime=" + modificationTime +
                ", trackId=" + trackId +
                ", reserved=" + reserved +
                ", duration=" + duration +
                ", reserved2=" + Arrays.toString(reserved2) +
                ", layer=" + layer +
                ", alternateGroup=" + alternateGroup +
                ", volume=" + volume +
                ", reserved3=" + reserved3 +
                ", matrix=" + Arrays.toString(matrix) +
                ", width=" + width +
                ", height=" + height +
                ", version=" + version +
                ", flags=" + Arrays.toString(flags) +
                '}';
    }
}
