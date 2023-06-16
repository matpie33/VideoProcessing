package main.boxes;

import main.videoprocessing.annotation.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component()
@Scope("prototype")
@Box(type="udta")
public class UserDataBox extends BasicBox {
    @Order(1)
    private MetaBox metaBox;

}
