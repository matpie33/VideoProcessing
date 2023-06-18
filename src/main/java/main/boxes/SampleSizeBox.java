package main.boxes;

import main.videoprocessing.annotation.Box;
import main.videoprocessing.annotation.Order;
import main.videoprocessing.annotation.VariableArraySize;
import main.videoprocessing.annotation.VariableArraySizeProvider;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component()
@Scope("prototype")
@Box(type="stsz")
public class SampleSizeBox extends FullBox {
    @Order(1)
    private int sampleSize;

    @Order(2)
    private int sampleCount;

    @Order(3)
    @VariableArraySize
    private  int [] sampleEntrySizes;

    @VariableArraySizeProvider
    private int getArraySize (String parameter){
        return sampleCount;
    }

    public int getSampleSize() {
        return sampleSize;
    }

    public int getSampleCount() {
        return sampleCount;
    }

    public int[] getSampleEntrySizes() {
        return sampleEntrySizes;
    }
}
