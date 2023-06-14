package main.boxes;

import main.videoprocessing.annotation.Box;
import main.videoprocessing.annotation.Order;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component()
@Scope("prototype")
@Box(type="dinf")
public class DataInformationBox extends BasicBox {
    @Order(1)
    private DataReferenceBox dataReferenceBox;



}
