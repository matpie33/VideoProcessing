package main.boxes;

import main.videoprocessing.annotation.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component()
@Scope("prototype")
@Box(type="trak")
public class TrackBox extends BasicBox {
    private TrackHeaderBox trackHeaderBox;
    @OptionalField
    private EditBox editBox;
    private MediaBox mediaBox;

    public TrackHeaderBox getTrackHeaderBox() {
        return trackHeaderBox;
    }

    public EditBox getEditBox() {
        return editBox;
    }

    public MediaBox getMediaBox() {
        return mediaBox;
    }
}
