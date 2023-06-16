package main.boxes;

import main.videoprocessing.annotation.Box;
import main.videoprocessing.annotation.Order;
import main.videoprocessing.annotation.VariableArraySize;
import main.videoprocessing.annotation.VariableArraySizeProvider;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component()
@Scope("prototype")
@Box(type="stsc")
public class SampleToChunkBox extends FullBox {
    @Order(1)
    private int entryCount;

    @Order(2)
    @VariableArraySize
    private SampleChunk[] sampleChunks;

    @VariableArraySizeProvider
    private int getArraySize (String parameter){
        return entryCount;
    }

    private static class SampleChunk extends Printable {
        @Order(1)
        private int firstChunk;

        @Order(2)
        private int samplesPerChunk;

        @Order(3)
        private int sampleDescriptionIndex;

    }

}
