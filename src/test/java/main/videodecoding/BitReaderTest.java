package main.videodecoding;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;

public class BitReaderTest {

    private final BitReader bitReader = new BitReader();

    @Test
    public void testReadingBits (){
        byte[] b = {  (byte)0b10110111};
        ByteArrayInputStream input = new ByteArrayInputStream(b);
        long result = bitReader.readNextNBits(input, 3);
        Assertions.assertThat(result).isEqualTo(0b00000101);
        long result2 = bitReader.readNextNBits(input, 4);
        Assertions.assertThat(result2).isEqualTo(0b00001011);

    }

    @Test
    public void testReadingBitsFrom2Bytes (){
        byte[] b = {  (byte)0b10110111, (byte)0b01010000};
        ByteArrayInputStream input = new ByteArrayInputStream(b);
        long result = bitReader.readNextNBits(input, 6);
        Assertions.assertThat(result).isEqualTo(0b00101101);
        long result2 = bitReader.readNextNBits(input, 4);
        Assertions.assertThat(result2).isEqualTo(0b00001101);

    }

    @Test
    public void testReadingFullByte (){
        int byteToCheck = 0b10110111;
        byte[] b = {  (byte) byteToCheck};
        ByteArrayInputStream input = new ByteArrayInputStream(b);
        long result = bitReader.readNextNBits(input, 8);
        Assertions.assertThat(result).isEqualTo(byteToCheck);

    }

    @Test
    public void testReadingFullByteAndSomeMoreBits (){
        byte[] b = {  (byte) 0b10110111, (byte) 0b11000100, (byte) 0b10100111};
        ByteArrayInputStream input = new ByteArrayInputStream(b);
        long result = bitReader.readNextNBits(input, 6);
        Assertions.assertThat(result).isEqualTo(0b00101101);
        long result2 = bitReader.readNextNBits(input, 12);
        Assertions.assertThat(result2).isEqualTo(0b000000111100010010);

    }

}