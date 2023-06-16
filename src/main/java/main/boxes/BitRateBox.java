package main.boxes;

import main.videoprocessing.annotation.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component()
@Scope("prototype")
@Box(type="btrt")
public class BitRateBox extends BasicBox {
    @Order(1)
    private int bufferSizeDB;

    @Order(2)
    private int maxBitrate;

    @Order(3)
    private int avgBitrate;


}
