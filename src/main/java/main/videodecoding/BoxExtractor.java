package main.videodecoding;

import main.boxes.BasicBox;
import main.boxes.MediaDataBox;
import main.boxes.SampleSizeBox;
import main.boxes.TrackBox;
import main.boxes.codec.avc.AvcConfigurationBox;
import main.boxes.sampleEntries.AvcSampleEntry;
import main.boxes.sampleEntries.SampleEntry;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;

@Component
public class BoxExtractor {

    public MediaDataBox extractMediaData (Collection<BasicBox> boxes){
        return boxes.stream().filter(box->box instanceof MediaDataBox).map(MediaDataBox.class::cast)
                .findFirst().orElseThrow(()->new IllegalArgumentException("No media data box found"));
    }
    public SampleSizeBox extractSampleSizeBox (Collection<BasicBox> boxes){
        TrackBox trackBox = extractTrackBox(boxes);

        return trackBox.getMediaBox().getMediaInformationBox().getSampleTableBox().getSampleSizeBox();
    }

    private static TrackBox extractTrackBox(Collection<BasicBox> boxes) {
        return boxes.stream().filter(box -> box instanceof TrackBox).map(TrackBox.class::cast)
                .findFirst().orElseThrow(() -> new IllegalArgumentException("No track box found"));
    }

    public AvcConfigurationBox extractAvcConfigurationBox (Collection<BasicBox> boxes){
        TrackBox trackBox = extractTrackBox(boxes);

        SampleEntry[] sampleEntries = trackBox.getMediaBox().getMediaInformationBox().getSampleTableBox().getSampleDescriptionBox()
                .getSampleEntries();
        AvcSampleEntry avcSampleEntry = Arrays.stream(sampleEntries).filter(sampleEntry -> sampleEntry instanceof AvcSampleEntry).map(AvcSampleEntry.class::cast)
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Avc entry not found"));

        return avcSampleEntry.getAvcConfigurationBox();
    }

}
