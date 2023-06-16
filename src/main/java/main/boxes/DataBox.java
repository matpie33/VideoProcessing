package main.boxes;

import main.videoprocessing.annotation.Box;
import main.videoprocessing.annotation.Order;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component()
@Scope("prototype")
@Box(type="data")
public class DataBox extends BasicBox {

    @Order(1)
    private int type;

    @Order(2)
    private int locale;

    @Order(3)
    private String value;

}
