package main.videodecoding;

import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

@Component
public class ExpGolombParser {

    private BitReader bitReader;

    public ExpGolombParser(BitReader bitReader) {
        this.bitReader = bitReader;
    }

    public int countLeadingBits(ByteArrayInputStream inputStream) {
        int leadingZeroBits = -1;

        boolean isCurrentBitZero = true;
        while (isCurrentBitZero){
            leadingZeroBits ++;
            int bit = bitReader.readNextNBits(inputStream, 1);
            isCurrentBitZero = bit == 0;
        }
        return leadingZeroBits;

    }

    public long parseExpGolomb (ByteArrayInputStream inputStream){
        int leadingZeroBits = countLeadingBits(inputStream);
        return 2^leadingZeroBits -1 + bitReader.readNextNBits( inputStream, leadingZeroBits);
    }

}
