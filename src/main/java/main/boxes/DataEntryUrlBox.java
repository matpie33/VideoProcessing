package main.boxes;

import main.videoprocessing.annotation.Box;
import main.videoprocessing.annotation.Order;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component()
@Scope("prototype")
@Box(type="url ")
public class DataEntryUrlBox extends FullBox {
    @Order(1)
    private String location;
    



}
