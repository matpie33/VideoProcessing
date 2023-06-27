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
        if (numberOfBitsToRead ==0){
            return 0;
        }
        int result;
        int totalBitsReaded;
        checkInitialize(inputStream);
        int remainingBitsToCheckFromLastByte = BYTE_SIZE - currentBitToCheck;
        int localBitsToRead = Math.min(remainingBitsToCheckFromLastByte, numberOfBitsToRead);
        int bitsReaded = readBitsFromByte(localBitsToRead);
        incrementBitToCheckByAmountOfReadedBits(localBitsToRead, inputStream);
        result = bitsReaded << (INT_SIZE - localBitsToRead);
        totalBitsReaded = localBitsToRead;
        numberOfBitsToRead -=localBitsToRead;
        while (numberOfBitsToRead > BYTE_SIZE && inputStream.available()>0){
            totalBitsReaded += BYTE_SIZE;
            result |= (currentByteToCheck << INT_SIZE- totalBitsReaded);
            numberOfBitsToRead -= BYTE_SIZE;
            currentByteToCheck = (byte)inputStream.read();
        }
        if (numberOfBitsToRead > 0){
            bitsReaded = readBitsFromByte(numberOfBitsToRead);
            totalBitsReaded += numberOfBitsToRead;
            result |= (bitsReaded << INT_SIZE- totalBitsReaded);
            incrementBitToCheckByAmountOfReadedBits(numberOfBitsToRead, inputStream);
        }
        result >>>= (INT_SIZE - totalBitsReaded);

        return result;
    }

    private void incrementBitToCheckByAmountOfReadedBits(int numberOfBitsToRead, ByteArrayInputStream inputStream) {
        currentBitToCheck += numberOfBitsToRead ;
        if (currentBitToCheck == 8){
            currentBitToCheck =0;
            currentByteToCheck = (byte)inputStream.read();
        }
    }

    int readBitsFromByte(int numberOfBits){
        int lastReadedBitIndex = currentBitToCheck + numberOfBits -1;
        int shiftAmount = BYTE_SIZE - lastReadedBitIndex -1;

        int shiftedRight = currentByteToCheck >> shiftAmount;
        int mask = ALL_ONES_BYTE >>> (BYTE_SIZE-numberOfBits);

        return shiftedRight & mask;

    }

    private void checkInitialize(ByteArrayInputStream inputStream) {
        if (currentByteToCheck == null){
            currentByteToCheck = (byte) inputStream.read();
            currentBitToCheck = 0;
        }
    }

}
