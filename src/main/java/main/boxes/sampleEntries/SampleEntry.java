package main.boxes.sampleEntries;

import main.boxes.BasicBox;
import main.videoprocessing.annotation.ArraySize;
import main.videoprocessing.annotation.Order;

public abstract class SampleEntry extends BasicBox {
    @Order(1)
    @ArraySize(6)
    private final byte[] reserved = {0,0,0,0,0,0};

    @Order(2)
    private short dataReferenceIndex;
}
