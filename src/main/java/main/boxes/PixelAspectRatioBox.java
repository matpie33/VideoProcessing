package main.boxes;

import main.videoprocessing.annotation.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component()
@Scope("prototype")
@Box(type="pasp")
public class PixelAspectRatioBox extends BasicBox {
    @Order(1)
    private int hSpacing;

    @Order(2)
    private int vSpacing;


}
