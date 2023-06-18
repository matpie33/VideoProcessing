package main.videodecoding;

import main.boxes.BasicBox;
import main.boxes.MediaDataBox;
import main.boxes.SampleSizeBox;
import main.boxes.codec.avc.AvcConfigurationBox;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class VideoDecoder {

    private BoxExtractor boxExtractor;

    private NALUnitExtractor nalUnitExtractor;

    public VideoDecoder(BoxExtractor boxExtractor, NALUnitExtractor nalUnitExtractor) {
        this.boxExtractor = boxExtractor;
        this.nalUnitExtractor = nalUnitExtractor;
    }

    public void decode (Collection<BasicBox> boxes){
        MediaDataBox mediaDataBox = boxExtractor.extractMediaData(boxes);
        AvcConfigurationBox avcConfigurationBox = boxExtractor.extractAvcConfigurationBox(boxes);
        SampleSizeBox sampleSizeBox = boxExtractor.extractSampleSizeBox(boxes);
        nalUnitExtractor.extractNALUnits(mediaDataBox, avcConfigurationBox, sampleSizeBox);
    }

}
