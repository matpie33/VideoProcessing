package main.boxes;

import main.videoprocessing.annotation.Box;
import main.videoprocessing.annotation.Order;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component()
@Scope("prototype")
@Box(type="minf")
public class MediaInformationBox extends BasicBox {
    @Order(1)
    private VideoMediaHeaderBox videoMediaHeaderBox;

    @Order(2)
    private DataInformationBox dataInformationBox;

    @Order(3)
    private SampleTableBox sampleTableBox;

}
