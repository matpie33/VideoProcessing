package main.boxes;

import main.videoprocessing.annotation.Box;
import main.videoprocessing.annotation.Order;
import main.videoprocessing.annotation.VariableObjectSize;
import main.videoprocessing.annotation.VariableObjectSizeProvider;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component()
@Scope("prototype")
@Box(type="mdhd")
public class MediaHeaderBox extends FullBox {
    @Order(1)
    @VariableObjectSize
    private Number creationTime;
    @Order(2)
    @VariableObjectSize
    private Number modificationTime;
    @Order(3)
    private int timeScale;
    @Order(4)
    @VariableObjectSize
    private Number duration;

    @Order(5)
    private short languageWithPadding;
    @Order(6)
    private short predefined;

    @VariableObjectSizeProvider
    public int getVariableSize (String parameterName){
        int length;

        switch (parameterName){
            case "creationTime":
            case "duration":
            case "modificationTime":
                length = version ==0? 4: 8;
                break;
            default:
                System.err.println("warning: unknown parameter in class : "+getClass() + " param: "+parameterName);
                length = 0;
        }
        return length;
    }

}
