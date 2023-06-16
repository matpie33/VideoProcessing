package main.boxes;

import main.videoprocessing.annotation.Box;
import main.videoprocessing.annotation.Order;
import main.videoprocessing.annotation.VariableArraySize;
import main.videoprocessing.annotation.VariableArraySizeProvider;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component()
@Scope("prototype")
@Box(type="stts")
public class TimeToSampleBox extends FullBox {
    @Order(1)
    private int entryCount;

    @Order(2)
    @VariableArraySize
    private SampleData[] sampleData;

    @VariableArraySizeProvider
    private int getArraySize (String parameter){
        return entryCount;
    }

    private static class SampleData extends Printable {
        @Order(1)
        private int sampleCount;

        @Order(2)
        private int sampleDelta;
    }


}
