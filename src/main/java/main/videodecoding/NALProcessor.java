package main.videodecoding;

import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

@Component
public class NALProcessor {

    public static final int UUID_ISO_IEC_LENGTH = 16;

    public void processNal (byte [] nalUnit){
        byte header = nalUnit[0];
        if (getForbiddenZeroBit(header)!=0){
            throw new IllegalArgumentException("Error");
        }
        int nalUnitType = getNalUnitType(header);
        processRbsp(nalUnit, nalUnitType);

    }

    private void processRbsp(byte[] nalUnit, int nalUnitType) {
        NALUnitType type = NALUnitType.ofInt(nalUnitType);
        switch (type){
            case SEI:
                handleSEIMessage(nalUnit);
                break;
        }
    }

    private void handleSEIMessage(byte[] nalUnit) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(nalUnit);
        long skipped = inputStream.skip(1);
        int payloadType = getValueFromByteArray(inputStream);
        int payloadSize = getValueFromByteArray(inputStream);
        handleSEIPayload(inputStream, payloadType, payloadSize);
        System.out.println();

    }

    private void handleSEIPayload(ByteArrayInputStream inputStream, int payloadType, int payloadSize) {
        PayloadType type = PayloadType.of(payloadType);
        switch (type){
            case USER_DATA_UNREGISTERED:
                handleUserDataUnregisteredPayload(inputStream, payloadSize);
                break;
        }
    }

    private void handleUserDataUnregisteredPayload(ByteArrayInputStream inputStream, int payloadSize) {
        byte [] uuidIsoIec = new byte[UUID_ISO_IEC_LENGTH];
        int readed = inputStream.read(uuidIsoIec, 0, uuidIsoIec.length);
        byte [] payload = new byte [payloadSize];
        int i= UUID_ISO_IEC_LENGTH;
        while (i<payloadSize){
            payload[i] = (byte)inputStream.read();
            i++;
        }
        SEIMessage seiMessage = new SEIMessage(uuidIsoIec, new String(payload));
        int lastByte = inputStream.read();
        if (lastByte != 0b10000000 || inputStream.available() >0){
            throw new IllegalArgumentException();
        }
    }

    private int getValueFromByteArray(ByteArrayInputStream inputStream) {
        int value = 0;
        int nextByte;
        while ((nextByte = inputStream.read()) == 0xFF){
            value += nextByte;
        }
        value+=nextByte;
        return value;
    }

    private int getForbiddenZeroBit(byte header){
        return header & 0b10000000;
    }
    private int getNalRefIdc(byte header){
        return (header & 0b01100000)>>5;
    }

    private int getNalUnitType(byte header){
        return header & 0b00011111;
    }


}
