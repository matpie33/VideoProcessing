package main.boxes;

import main.videoprocessing.annotation.Box;
import main.videoprocessing.annotation.DoNotPrint;
import main.videoprocessing.annotation.Order;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component()
@Scope("prototype")
@Box(type="mdat")
public class MediaDataBox extends BasicBox {

    @Order(1)
    @DoNotPrint
    private byte [] data;

    public byte[] getData() {
        return data;
    }
}
