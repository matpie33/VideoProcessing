package main.boxes;

import main.videoprocessing.annotation.Box;
import main.videoprocessing.annotation.Order;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component()
@Scope("prototype")
@Box(type="stbl")
public class SampleTableBox extends BasicBox{
    @Order(1)
    private SampleDescriptionBox sampleDescriptionBox;

    @Order(2)
    private TimeToSampleBox timeToSampleBox;

    @Order(3)
    private SyncSampleBox syncSampleBox;

    @Order(4)
    private CompositionOffsetBox compositionOffsetBox;

    @Order(5)
    private SampleToChunkBox sampleToChunkBox;

    @Order(6)
    private SampleSizeBox sampleSizeBox;

    @Order(7)
    private ChunkOffsetBox chunkOffsetBox;
}
