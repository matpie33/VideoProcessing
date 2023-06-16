package main.boxes;

import main.videoprocessing.annotation.Order;
import main.videoprocessing.annotation.Skip;

public class BasicBox extends Printable {

    @Order(1)
    @Skip
    private int boxLength;

    public int getBoxLength() {
        return boxLength;
    }
}
