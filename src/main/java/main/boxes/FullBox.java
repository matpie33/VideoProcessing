package main.boxes;

import main.videoprocessing.annotation.ArraySize;
import main.videoprocessing.IBox;
import main.videoprocessing.annotation.Order;

public abstract class FullBox implements IBox {

    @Order(1)
    protected byte version;

    @Order(2)
    @ArraySize(3)
    protected byte [] flags;
}
