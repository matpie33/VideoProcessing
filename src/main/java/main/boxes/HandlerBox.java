package main.boxes;

import main.videoprocessing.annotation.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component()
@Scope("prototype")
@Box(type="hdlr")
public class HandlerBox extends FullBox {
    @Order(1)
    private final int predefined = 0;
    @Order(2)
    @SimpleTypeSize(4)
    private String handlerType;
    @Order(3)
    @ArraySize(3)
    private final int[] reserved = {0, 0, 0};
    @Order(4)
    @Text
    private byte[] name;

}
