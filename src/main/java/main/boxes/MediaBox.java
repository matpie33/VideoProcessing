package main.boxes;

import main.videoprocessing.IBox;
import main.videoprocessing.annotation.Box;
import main.videoprocessing.annotation.Order;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component()
@Scope("prototype")
@Box(type="mdia")
public class MediaBox extends BasicBox {
    @Order(1)
    private MediaHeaderBox mediaHeaderBox;

    @Order(2)
    private HandlerBox handlerBox;

    @Order(3)
    private MediaInformationBox mediaInformationBox;


}
