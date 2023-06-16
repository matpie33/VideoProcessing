package main.boxes;

import main.videoprocessing.annotation.*;

public class MetadataItem extends Printable{
    @Order(1)
    private int size;

    @Order(2)
    @ArraySize(4)
    @PrintAsString
    private byte[] name;

    @Order(3)
    private DataBox dataBox;

}
