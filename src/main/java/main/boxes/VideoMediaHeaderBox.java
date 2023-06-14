package main.boxes;

import main.videoprocessing.annotation.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component()
@Scope("prototype")
@Box(type="vmhd")
public class VideoMediaHeaderBox extends FullBox {
    @Order(1)
    private final short graphicsMode = 0;

    @Order(2)
    @ArraySize(3)
    private short[] opColor = {0, 0, 0};

}
