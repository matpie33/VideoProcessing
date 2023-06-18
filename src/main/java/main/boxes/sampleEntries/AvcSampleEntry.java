package main.boxes.sampleEntries;

import main.boxes.BitRateBox;
import main.boxes.ColourInformationBox;
import main.boxes.PixelAspectRatioBox;
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

    private AvcConfigurationBox avcConfigurationBox;

    private ColourInformationBox colourInformationBox;

    private PixelAspectRatioBox pixelAspectRatioBox;

    private BitRateBox bitRateBox;

    public AvcConfigurationBox getAvcConfigurationBox() {
        return avcConfigurationBox;
    }

    public ColourInformationBox getColourInformationBox() {
        return colourInformationBox;
    }

    public PixelAspectRatioBox getPixelAspectRatioBox() {
        return pixelAspectRatioBox;
    }

    public BitRateBox getBitRateBox() {
        return bitRateBox;
    }
}
