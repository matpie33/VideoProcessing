package main.boxes.sampleEntries;

import main.videoprocessing.IBox;
import main.videoprocessing.annotation.ArraySize;
import main.videoprocessing.annotation.Box;
import main.videoprocessing.annotation.Order;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

public abstract class SampleEntry implements IBox {
    @Order(1)
    @ArraySize(6)
    private final byte[] reserved = {0,0,0,0,0,0};

    @Order(2)
    private short dataReferenceIndex;
}
