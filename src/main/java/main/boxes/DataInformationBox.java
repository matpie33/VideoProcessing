package main.boxes;

import main.videoprocessing.IBox;
import main.videoprocessing.annotation.Box;
import main.videoprocessing.annotation.Order;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component()
@Scope("prototype")
@Box(type="dinf")
public class DataInformationBox implements IBox {
    @Order(1)
    private DataReferenceBox dataReferenceBox;



}
