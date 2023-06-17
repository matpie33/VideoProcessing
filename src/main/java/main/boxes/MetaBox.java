package main.boxes;

import main.videoprocessing.annotation.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component()
@Scope("prototype")
@Box(type="meta")
public class MetaBox extends FullBox {
    private HandlerBox handlerBox;

    private MetaDataItemListBox metaDataItemListBox;

}
