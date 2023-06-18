package main.boxes;

import main.videoprocessing.annotation.Box;
import main.videoprocessing.annotation.Order;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component()
@Scope("prototype")
@Box(type="minf")
public class MediaInformationBox extends BasicBox {
    private VideoMediaHeaderBox videoMediaHeaderBox;

    private DataInformationBox dataInformationBox;

    private SampleTableBox sampleTableBox;

    public VideoMediaHeaderBox getVideoMediaHeaderBox() {
        return videoMediaHeaderBox;
    }

    public DataInformationBox getDataInformationBox() {
        return dataInformationBox;
    }

    public SampleTableBox getSampleTableBox() {
        return sampleTableBox;
    }
}
