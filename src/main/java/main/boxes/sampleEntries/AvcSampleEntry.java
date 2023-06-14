package main.boxes.sampleEntries;

import main.boxes.codec.avc.AvcConfigurationBox;
import main.videoprocessing.annotation.ArraySize;
import main.videoprocessing.annotation.Box;
import main.videoprocessing.annotation.Order;
import main.videoprocessing.annotation.SimpleTypeSize;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component()
@Scope("prototype")
@Box(type = "avc1")
public class AvcSampleEntry extends VisualSampleEntry {

    @Order(1)
    private AvcConfigurationBox avcConfigurationBox;

}