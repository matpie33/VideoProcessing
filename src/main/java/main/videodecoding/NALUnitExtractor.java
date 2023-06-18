package main.videodecoding;

import main.boxes.MediaDataBox;
import main.boxes.SampleSizeBox;
import main.boxes.codec.avc.AvcConfigurationBox;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@Component
public class NALUnitExtractor {

    public static final int TWO_BITS_MASK = 0b00000011;

    public List<byte []> extractNALUnits (MediaDataBox mediaData, AvcConfigurationBox avcConfigurationBox, SampleSizeBox sampleSizeBox){
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mediaData.getData());
        byte reservedAndLengthMinusOne = avcConfigurationBox.getReservedAndLengthMinusOne();
        int lengthMinus1 = reservedAndLengthMinusOne & TWO_BITS_MASK;
        List<byte []> nalUnits = new ArrayList<>();
        int pictureLength;
        byte [] nalUnitLength = new byte [lengthMinus1 + 1];
        for (int sampleIndex=0; sampleIndex< sampleSizeBox.getSampleCount(); sampleIndex++){
            pictureLength = sampleSizeBox.getSampleEntrySizes()[sampleIndex];
            for (int byteIndexInsideSample=0; byteIndexInsideSample< sampleSizeBox.getSampleEntrySizes()[sampleIndex]; byteIndexInsideSample++){
                int readedAmount = readBytesIntoArray(byteArrayInputStream, nalUnitLength);
                byte [] nalUnit = new byte [getValueFromBytes(nalUnitLength)];
                readedAmount = readBytesIntoArray(byteArrayInputStream, nalUnit);
                nalUnits.add(nalUnit);
                byteIndexInsideSample += nalUnitLength.length + nalUnit.length;
            }

        }
        nalUnits.forEach(unit-> System.out.println(Hex.encodeHex(unit)));
        return nalUnits;
    }

    private static int readBytesIntoArray(ByteArrayInputStream byteArrayInputStream, byte[] byteArray) {
        return byteArrayInputStream.read(byteArray, 0, byteArray.length);
    }

    private int getValueFromBytes (byte [] data){
        ByteBuffer wrapped = ByteBuffer.wrap(data);
        if (data.length == 4){
            return wrapped.getInt();
        }
        if (data.length == 2){
            return wrapped.getShort();
        }
        if (data.length == 1){
            return wrapped.get();
        }
        else{
            throw new IllegalArgumentException();
        }
    }

}
