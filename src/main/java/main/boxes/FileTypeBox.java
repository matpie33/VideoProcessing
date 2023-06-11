package main.boxes;

import main.videoprocessing.annotation.Box;
import main.videoprocessing.IBox;
import main.videoprocessing.annotation.SimpleTypeSize;
import main.videoprocessing.annotation.Order;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component()
@Scope("prototype")
@Box(type="ftyp")
public class FileTypeBox implements IBox {
    @Order(1)
    @SimpleTypeSize(4)
    private String majorBrand;
    @Order(2)
    private int minorVersion;
    @Order(3)
    @SimpleTypeSize(4)
    private String [] compatibleBrands;

    @Override
    public String toString() {
        return "FileTypeBox{" +
                "majorBrand='" + majorBrand + '\'' +
                ", minorVersion=" + minorVersion +
                ", compatibleBrands=" + Arrays.toString(compatibleBrands) +
                '}';
    }
}
