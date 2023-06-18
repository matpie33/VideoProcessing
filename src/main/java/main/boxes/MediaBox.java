package main.boxes;

import main.videoprocessing.annotation.Box;
import main.videoprocessing.annotation.Order;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component()
@Scope("prototype")
@Box(type="mdia")
public class MediaBox extends BasicBox {

    private HandlerBox handlerBox;
    private MediaHeaderBox mediaHeaderBox;

    private MediaInformationBox mediaInformationBox;

    public HandlerBox getHandlerBox() {
        return handlerBox;
    }

    public MediaHeaderBox getMediaHeaderBox() {
        return mediaHeaderBox;
    }

    public MediaInformationBox getMediaInformationBox() {
        return mediaInformationBox;
    }
}
