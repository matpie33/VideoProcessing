package main.boxes;

import main.boxes.sampleEntries.SampleEntry;
import main.videoprocessing.annotation.Box;
import main.videoprocessing.annotation.Order;
import main.videoprocessing.annotation.VariableArraySize;
import main.videoprocessing.annotation.VariableArraySizeProvider;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component()
@Scope("prototype")
@Box(type="stsd")
public class SampleDescriptionBox extends FullBox {

    @Order(1)
    private int entryCount;

    @Order(2)
    @VariableArraySize
    private SampleEntry[] sampleEntries;

    @VariableArraySizeProvider
    public int getSampleEntriesSize (String parameterName){
        return entryCount;
    }

    public int getEntryCount() {
        return entryCount;
    }

    public SampleEntry[] getSampleEntries() {
        return sampleEntries;
    }
}
