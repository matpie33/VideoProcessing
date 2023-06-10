package main.boxes;

import main.videoprocessing.Box;
import main.videoprocessing.IBox;
import main.videoprocessing.Length;
import main.videoprocessing.Order;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component()
@Scope("prototype")
@Box(type="ftyp")
public class FileTypeBox implements IBox {
    @Order(value = 1)
    @Length(value = 4)
    private String majorBrand;
    @Order(value = 2)
    private int minorVersion;
    @Order(value = 3)
    @Length(value = 4)
    private String [] compatibleBrands;

}
