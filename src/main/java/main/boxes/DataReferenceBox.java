package main.boxes;

import main.videoprocessing.annotation.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component()
@Scope("prototype")
@Box(type="dref")
public class DataReferenceBox extends FullBox {
    @Order(1)
    private int entryCount;

    @Order(2)
    @VariableArraySize
    private DataEntryUrlBox[] dataEntries;

    @VariableArraySizeProvider
    public int getEntryUrlBoxesCount (String parameterName){
        return entryCount;
    }


}
