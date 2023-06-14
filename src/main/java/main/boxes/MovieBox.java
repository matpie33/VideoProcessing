package main.boxes;

import main.videoprocessing.annotation.Box;
import main.videoprocessing.IBox;
import main.videoprocessing.annotation.Order;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component()
@Scope("prototype")
@Box(type="moov")
public class MovieBox extends BasicBox {
    @Order(1)
    private MovieHeaderBox movieHeaderBox;

}
