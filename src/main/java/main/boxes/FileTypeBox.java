package main.boxes;

import main.videoprocessing.annotation.Box;
import main.videoprocessing.annotation.SimpleTypeSize;
import main.videoprocessing.annotation.Order;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component()
@Scope("prototype")
@Box(type="ftyp")
public class FileTypeBox extends BasicBox {
    @Order(1)
    @SimpleTypeSize(4)
    private String majorBrand;
    @Order(2)
    private int minorVersion;
    @Order(3)
    @SimpleTypeSize(4)
    private String [] compatibleBrands;

}
