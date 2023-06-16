package main.boxes;

import main.videoprocessing.annotation.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component()
@Scope("prototype")
@Box(type="meta")
public class MetaBox extends FullBox {
    @Order(1)
    private HandlerBox handlerBox;

    @Order(2)
    private MetaDataItemListBox metaDataItemListBox;

}
