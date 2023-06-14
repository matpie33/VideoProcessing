package main.boxes;

import main.videoprocessing.IBox;
import main.videoprocessing.annotation.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component()
@Scope("prototype")
@Box(type="trak")
public class TrackBox extends BasicBox {
    @Order(1)
    private TrackHeaderBox trackHeaderBox;
    @OptionalField
    @Order(2)
    private EditBox editBox;
    @Order(3)
    private MediaBox mediaBox;

}
