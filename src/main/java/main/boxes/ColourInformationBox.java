package main.boxes;

import main.videoprocessing.annotation.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component()
@Scope("prototype")
@Box(type="colr")
public class ColourInformationBox extends BasicBox {
    @Order(1)
    @ArraySize(4)
    @PrintAsString
    private byte[] colourType;

    @Conditional
    @Order(2)
    private OnScreenColors onScreenColors;

    @ConditionProvider
    public boolean isApplicable (String parameter){
        if (parameter.equals("onScreenColors")){
            return new String(colourType).equals("nclx");
        }
        else{
            throw new UnsupportedOperationException();
        }
    }

    private static class OnScreenColors extends Printable{
        @Order(1)
        private short colorPrimaries;
        @Order(2)
        private short transferCharacteristics;
        @Order(3)
        private short matrixCoefficients;
        @Order(4)
        private byte fullRangeFlagAndReserved;
    }

}
