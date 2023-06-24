package main.videodecoding;

import main.utilities.BitUtils;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

@Component
public class LeadingZerosCounter {

    private final BitUtils bitUtils;
    private Byte currentByteToCheck;
    private int currentBitToCheck;

    public LeadingZerosCounter(BitUtils bitUtils) {
        this.bitUtils = bitUtils;
    }

    public LeadingZerosCheckResultDTO countLeadingBits(ByteArrayInputStream inputStream) {
        int leadingZeroBits = 0;

        if (currentByteToCheck == null){
            currentByteToCheck = (byte) inputStream.read();
            currentBitToCheck = 0;
        }
        else if (currentBitToCheck == 7){
            currentBitToCheck =0;
            currentByteToCheck = (byte) inputStream.read();
        }
        else{
            currentBitToCheck++;
        }

        while (currentByteToCheck != -1){

            while (currentBitToCheck <8) {
                if (!bitUtils.isBitSet(currentByteToCheck, currentBitToCheck)){
                    leadingZeroBits++;
                }
                else{
                    return new LeadingZerosCheckResultDTO(currentByteToCheck, currentBitToCheck, leadingZeroBits);
                }
                currentBitToCheck++;
            }
            currentBitToCheck = 0 ;
            currentByteToCheck = (byte)inputStream.read();

        }
        return new LeadingZerosCheckResultDTO(currentByteToCheck, currentBitToCheck, leadingZeroBits);
    }
}
