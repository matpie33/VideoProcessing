package main.boxes;

import main.videoprocessing.annotation.Box;
import main.videoprocessing.annotation.Order;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component()
@Scope("prototype")
@Box(type="stbl")
public class SampleTableBox extends BasicBox{
    private SampleDescriptionBox sampleDescriptionBox;

    private TimeToSampleBox timeToSampleBox;

    private SyncSampleBox syncSampleBox;

    private CompositionOffsetBox compositionOffsetBox;

    private SampleToChunkBox sampleToChunkBox;

    private SampleSizeBox sampleSizeBox;

    private ChunkOffsetBox chunkOffsetBox;

    public SampleDescriptionBox getSampleDescriptionBox() {
        return sampleDescriptionBox;
    }

    public TimeToSampleBox getTimeToSampleBox() {
        return timeToSampleBox;
    }

    public SyncSampleBox getSyncSampleBox() {
        return syncSampleBox;
    }

    public CompositionOffsetBox getCompositionOffsetBox() {
        return compositionOffsetBox;
    }

    public SampleToChunkBox getSampleToChunkBox() {
        return sampleToChunkBox;
    }

    public SampleSizeBox getSampleSizeBox() {
        return sampleSizeBox;
    }

    public ChunkOffsetBox getChunkOffsetBox() {
        return chunkOffsetBox;
    }
}
