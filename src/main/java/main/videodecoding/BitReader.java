package main.videodecoding;

import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

@Component
public class BitReader {

    public static final int INT_SIZE = 32;
    public static final int BYTE_SIZE = 8;
    private Byte currentByteToCheck;
    private int currentBitToCheck;

    private static final int ALL_ONES_BYTE = 0b11111111;

    public int readNextNBits (ByteArrayInputStream inputStream, int numberOfBitsToRead){
        if (numberOfBitsToRead > INT_SIZE){
            throw new UnsupportedOperationException("only supporting reading up to 32 bits at a time");
        }
        int result;
        int totalBitsReaded;
        incrementBitToCheck(inputStream);
        int remainingBitsToCheckFromLastByte = BYTE_SIZE - currentBitToCheck;
        int localBitsToRead = Math.min(remainingBitsToCheckFromLastByte, numberOfBitsToRead);
        int bitsReaded = readBitsFromByte(localBitsToRead);
        incrementBitToCheckByAmountOfReadedBits(localBitsToRead);
        result = bitsReaded << (INT_SIZE - localBitsToRead);
        totalBitsReaded = localBitsToRead;
        numberOfBitsToRead -=localBitsToRead;
        int shiftValue = localBitsToRead;
        while (numberOfBitsToRead > BYTE_SIZE && inputStream.available()>0){
            bitsReaded = inputStream.read();
            result |= bitsReaded << shiftValue;
            shiftValue += BYTE_SIZE;
            numberOfBitsToRead -= BYTE_SIZE;
            totalBitsReaded += BYTE_SIZE;
        }
        if (numberOfBitsToRead > 0  && inputStream.available()>0){
            currentByteToCheck = (byte)inputStream.read();
            bitsReaded = readBitsFromByte(numberOfBitsToRead);
            totalBitsReaded += numberOfBitsToRead;
            result |= (bitsReaded << INT_SIZE- totalBitsReaded);
            incrementBitToCheckByAmountOfReadedBits(numberOfBitsToRead);
        }
        result >>>= (INT_SIZE - totalBitsReaded);

        return result;
    }

    private void incrementBitToCheckByAmountOfReadedBits(int numberOfBitsToRead) {
        currentBitToCheck += numberOfBitsToRead -1;
        if (currentBitToCheck == 7){
            currentBitToCheck =0;
        }
    }

    int readBitsFromByte(int numberOfBits){
        int lastReadedBitIndex = currentBitToCheck + numberOfBits -1;
        int shiftAmount = BYTE_SIZE - lastReadedBitIndex -1;

        int shiftedRight = currentByteToCheck >> shiftAmount;
        int mask = ALL_ONES_BYTE >>> (BYTE_SIZE-numberOfBits);

        return shiftedRight & mask;

    }

    private void incrementBitToCheck(ByteArrayInputStream inputStream) {
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
    }

}
