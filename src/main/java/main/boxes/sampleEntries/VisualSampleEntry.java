package main.boxes.sampleEntries;

import main.boxes.FullBox;
import main.videoprocessing.annotation.ArraySize;
import main.videoprocessing.annotation.Box;
import main.videoprocessing.annotation.Order;
import main.videoprocessing.annotation.SimpleTypeSize;
import org.apache.commons.codec.binary.Hex;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;

public abstract class VisualSampleEntry extends SampleEntry {
    @Order(1)
    private final short predefined=0;

    @Order(2)
    private final short reserved =0;

    @Order(3)
    @ArraySize(3)
    private final int [] predefined2 = {0, 0, 0};

    @Order(4)
    private short width;

    @Order(5)
    private short height;

    @Order(6)
    private int horizontalResolution = 0x00480000;
    @Order(7)
    private int verticalResolution = 0x00480000;

    @Order(8)
    private final int reserved2 = 0;


    @Order(9)
    private short frameCount = 1;

    @Order(10)
    @SimpleTypeSize(32)
    private String compressorName;

    @Order(11)
    private final short depth = 0x0018;

    @Order(12)
    private final short predefined3= -1;


    @Override
    public String toString() {
        return "VisualSampleEntry{" +
                "predefined=" + predefined +
                ", reserved=" + reserved +
                ", predefined2=" + Arrays.toString(predefined2) +
                ", width=" + width +
                ", height=" + height +
                ", horizontalResolution=" + Integer.toHexString(horizontalResolution) +
                ", verticalResolution=" + Integer.toHexString(verticalResolution) +
                ", reserved2=" + reserved2 +
                ", compressorName='" + compressorName + '\'' +
                ", frameCount='" + frameCount + '\'' +
                ", depth=" + depth +
                ", predefined3=" + predefined3 +
                '}';
    }
}
