package main.videoprocessing;

import main.utilities.HexUtils;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.file.Path;

@Component
public class FileReader {

    private static final String FILE_NAME = "/screen-capture.mp4";

    private final BoxDataReader boxDataReader;

    private static final int BYTES_AMOUNT_BOX_TYPE_AND_SIZE = 8;

    public FileReader(BoxDataReader boxDataReader) {
        this.boxDataReader = boxDataReader;
    }

    public void readBoxes () throws URISyntaxException, IOException, IllegalAccessException {
        URI path = getClass().getResource(FILE_NAME).toURI();
        FileInputStream fileInputStream = new FileInputStream(Path.of(path).toFile());
        byte[] boxSizeBuffer = new byte[4];
        byte[] boxTypeBuffer = new byte[4];
        while (fileInputStream.available() > 0){
            int availableBytes = fileInputStream.available();

            int numberOfReadedBytes = fileInputStream.read(boxSizeBuffer, 0, 4);
            int boxLength = ByteBuffer.wrap(boxSizeBuffer).getInt();
            numberOfReadedBytes = fileInputStream.read(boxTypeBuffer, 0, 4);
            String boxType = new String(boxTypeBuffer);
            boxDataReader.readBytes(fileInputStream, boxType, boxLength - BYTES_AMOUNT_BOX_TYPE_AND_SIZE);
        }

    }


}
