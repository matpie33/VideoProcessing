package main.videodecoding;

import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

@Component
public class NALProcessor {

    public static final int UUID_ISO_IEC_LENGTH = 16;

    private final ExpGolombParser expGolombParser;

    private final BitReader bitReader;

    public NALProcessor(ExpGolombParser expGolombParser, BitReader bitReader) {
        this.expGolombParser = expGolombParser;
        this.bitReader = bitReader;
    }


    public void processNal (byte [] nalUnit) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(nalUnit);
        byte header = (byte)byteArrayInputStream.read();
        if (getForbiddenZeroBit(header)!=0){
            throw new IllegalArgumentException("Error");
        }
        int nalUnitType = getNalUnitType(header);
        processRbsp(byteArrayInputStream, nalUnitType);

    }

    private void processRbsp(ByteArrayInputStream byteBuffer, int nalUnitType) {
        NALUnitType type = NALUnitType.ofInt(nalUnitType);
        switch (type){
            case SEI:
                handleSEIMessage(byteBuffer);
                break;
            case CODED_SLICE_OF_IDR_PICTURE:
                handleCodedSlice(byteBuffer);
                break;
            case SEQUENCE_PARAMETER_SET:
                handleSequenceParameterSet(byteBuffer);
                break;
        }
    }

    private void handleSequenceParameterSet(ByteArrayInputStream byteStream) {
        int profileIdc =  byteStream.read();
        byte constraints6FlagsAnd2ReservedBits = (byte) byteStream.read();
        byte levelIdc = (byte) byteStream.read();
        long seqParameterSetId = expGolombParser.parseExpGolomb(byteStream);

        if (profileIdc == 100){
            long chromaFormatIdc = expGolombParser.parseExpGolomb(byteStream);
            if (chromaFormatIdc == 3){
                int separateColourPlaneFlag = bitReader.readNextNBits(byteStream, 1);
            }
            long bitDepthLumaMinus8 = expGolombParser.parseExpGolomb(byteStream);
            long bitDepthChromaMinus8 = expGolombParser.parseExpGolomb(byteStream);
            int qpPrimeYZeroTransformBypassFlag = bitReader.readNextNBits(byteStream, 1);
            int seqScalingMatrixPresentFlag = bitReader.readNextNBits(byteStream, 1);
            if (seqScalingMatrixPresentFlag==1){
                int[] seqScalingListPresentFlag = new int [(chromaFormatIdc!=3)? 8: 12];
                for (int i=0; i< seqScalingListPresentFlag.length ;i++){
                    seqScalingListPresentFlag[i] = bitReader.readNextNBits(byteStream, 1);
                    if (seqScalingListPresentFlag[i]==1){
                        if (i<6){

                        }
                        else{

                        }
                    }
                }
            }
        }

        long log2MaxFrameNumMinus4 = expGolombParser.parseExpGolomb(byteStream);
        long picOrderCountType = expGolombParser.parseExpGolomb(byteStream);
        if (picOrderCountType == 0){
            long log2MaxPicOrderCntLsbMinus4 = expGolombParser.parseExpGolomb(byteStream);
        }
        else if (picOrderCountType == 1){
            byte deltaPicOrderAlwaysZeroFlag = (byte)bitReader.readNextNBits(byteStream, 1);
            long offsetForNonRefPic = expGolombParser.parseExpGolomb(byteStream);
            long offsetForTopToBottomField = expGolombParser.parseExpGolomb(byteStream);
            long numRefFramesInPicOrderCntCycle = expGolombParser.parseExpGolomb(byteStream);
            long[] offsetForRefFrame = new long [(int)numRefFramesInPicOrderCntCycle];
            for (int i=0; i<numRefFramesInPicOrderCntCycle; i++){
                offsetForRefFrame[i] = expGolombParser.parseExpGolomb(byteStream);
            }
        }
        long maxNumRefFrames = expGolombParser.parseExpGolomb(byteStream);
        int gapsInFrameNumValueAllowedFlag = bitReader.readNextNBits(byteStream, 1);
        long picWidthInMbsMinus1 = expGolombParser.parseExpGolomb(byteStream);
        long picHeightInMapUnitsMinus1 = expGolombParser.parseExpGolomb(byteStream);
        int frameMbsOnlyFlag = bitReader.readNextNBits(byteStream, 1);
        if (frameMbsOnlyFlag==0){
            int mbAdaptiveFrameFieldFlag = bitReader.readNextNBits(byteStream, 1);
        }
        int direct8x8InferenceFlag = bitReader.readNextNBits(byteStream, 1);
        int frameCroppingFlag = bitReader.readNextNBits(byteStream, 1);
        if (frameCroppingFlag == 1){
            long frameCropLeftOffset = expGolombParser.parseExpGolomb(byteStream);
            long frameCropRightOffset = expGolombParser.parseExpGolomb(byteStream);
            long frameCropTopOffset = expGolombParser.parseExpGolomb(byteStream);
            long frameCropBottomOffset = expGolombParser.parseExpGolomb(byteStream);

        }
        int vuiParametersPresent = bitReader.readNextNBits(byteStream, 1);
        if (vuiParametersPresent==1){
            int aspectRationInfoPresentFlag = bitReader.readNextNBits(byteStream, 1);
            if (aspectRationInfoPresentFlag == 1){
                int aspectRatioIdc = bitReader.readNextNBits(byteStream, 8);
                if (aspectRatioIdc == 255){
                    int sarWidth = bitReader.readNextNBits(byteStream, 16);
                    int sarHeight = bitReader.readNextNBits(byteStream, 16);
                }
            }
            int overscanInfoPresentFlag = bitReader.readNextNBits(byteStream, 1);
            if (overscanInfoPresentFlag == 1){
                int overscanAppropriateFlag = bitReader.readNextNBits(byteStream, 1);
            }
            int videoSignalTypePresentFlag = bitReader.readNextNBits(byteStream, 1);
            if (videoSignalTypePresentFlag == 1){
                int videoFormat = bitReader.readNextNBits(byteStream, 3);
                int videoFullRangeFlag = bitReader.readNextNBits(byteStream, 1);
                int colorDescriptionPresentFlag = bitReader.readNextNBits(byteStream, 1);
                if (colorDescriptionPresentFlag ==1){
                    int colorPrimaries = bitReader.readNextNBits(byteStream, 8);
                    int transferCharacteristics = bitReader.readNextNBits(byteStream, 8);
                    int matrixCoefficients = bitReader.readNextNBits(byteStream, 8);
                }
            }
            int chromaLocInfoPresentFlag = bitReader.readNextNBits(byteStream, 1);
            if (chromaLocInfoPresentFlag == 1){
                long chromaSampleLocTypeTopField = expGolombParser.parseExpGolomb(byteStream);
                long chromaSampleLocTypeBottomField = expGolombParser.parseExpGolomb(byteStream);
            }
            int timingInfoPresentFlag = bitReader.readNextNBits(byteStream, 1);
            if (timingInfoPresentFlag == 1){
                int numUnitsInTick = bitReader.readNextNBits(byteStream, 32);
                int timeScale = bitReader.readNextNBits(byteStream, 32);
                int fixedFrameRateFlag = bitReader.readNextNBits(byteStream, 1);
            }
            int nalHrdParametersPresentFlag = bitReader.readNextNBits(byteStream, 1);
            if (nalHrdParametersPresentFlag == 1){
                hrdParameters(byteStream);
            }
            int vclHrdParametersPresentFlag = bitReader.readNextNBits(byteStream, 1);
            if (vclHrdParametersPresentFlag == 1){
                hrdParameters(byteStream);
            }
            if (nalHrdParametersPresentFlag==1 || vclHrdParametersPresentFlag==1){
                int lowDelayHrdFlag = bitReader.readNextNBits(byteStream, 1);
            }
            int picStructPresentFlag = bitReader.readNextNBits(byteStream, 1);
            int bitsStreamRestrictionFlag = bitReader.readNextNBits(byteStream, 1);
            if (bitsStreamRestrictionFlag == 1){
                int motionVectorsOverPicBoundariesFlag = bitReader.readNextNBits(byteStream, 1);
                long maxBytesOverPicDenom = expGolombParser.parseExpGolomb(byteStream);
                long maxBitsPerMbDenom = expGolombParser.parseExpGolomb(byteStream);
                long log2MaxMvLengthHorizontal = expGolombParser.parseExpGolomb(byteStream);
                long log2MaxMvLengthVertical = expGolombParser.parseExpGolomb(byteStream);
                long maxNumReorderFrames = expGolombParser.parseExpGolomb(byteStream);
                long maxDecFrameBuffering = expGolombParser.parseExpGolomb(byteStream);
            }



        }
    }

    private void hrdParameters(ByteArrayInputStream byteStream) {
        long cpbCntMinus1 = expGolombParser.parseExpGolomb(byteStream);
        int bitRateScale = bitReader.readNextNBits(byteStream, 4);
        int cpbSizeScale = bitReader.readNextNBits(byteStream, 4);
        long [] bitRateValueMinus1 = new long [(int)cpbCntMinus1];
        long [] cpbSizeValueMinus1 = new long [(int)cpbCntMinus1];
        int [] cbrFlag = new int [(int)cpbCntMinus1];
        for (int schedSelIdx = 0; schedSelIdx < cpbCntMinus1; schedSelIdx++){
            bitRateValueMinus1[schedSelIdx] = expGolombParser.parseExpGolomb(byteStream);
            cpbSizeValueMinus1[schedSelIdx] = expGolombParser.parseExpGolomb(byteStream);
            cbrFlag[schedSelIdx] = bitReader.readNextNBits(byteStream, 1);
        }
        int initialCpbRemovalDelayLengthMinus1 = bitReader.readNextNBits(byteStream, 5);
        int cpbRemovalDelayLengthMinus1 = bitReader.readNextNBits(byteStream, 5);
        int dpbOutputDelayLengthMinus1 = bitReader.readNextNBits(byteStream, 5);
        int timeOffsetLength = bitReader.readNextNBits(byteStream, 5);
    }

    private void handleCodedSlice(ByteArrayInputStream byteBuffer) {
        expGolombParser.parseExpGolomb(byteBuffer);
    }

    private void handleSEIMessage(ByteArrayInputStream byteBuffer) {
        int payloadType = getValueFromByteArray(byteBuffer);
        int payloadSize = getValueFromByteArray(byteBuffer);
        handleSEIPayload(byteBuffer, payloadType, payloadSize);
        System.out.println();

    }

    private void handleSEIPayload(ByteArrayInputStream byteBuffer, int payloadType, int payloadSize) {
        PayloadType type = PayloadType.of(payloadType);
        switch (type){
            case USER_DATA_UNREGISTERED:
                handleUserDataUnregisteredPayload(byteBuffer, payloadSize);
                break;
        }
    }

    private void handleUserDataUnregisteredPayload(ByteArrayInputStream byteBuffer, int payloadSize) {
        byte [] uuidIsoIec = new byte[UUID_ISO_IEC_LENGTH];
        byteBuffer.read(uuidIsoIec, 0, uuidIsoIec.length);
        byte [] payload = new byte [payloadSize];
        int i= UUID_ISO_IEC_LENGTH;
        while (i<payloadSize){
            payload[i] = (byte)byteBuffer.read();
            i++;
        }
        SEIMessage seiMessage = new SEIMessage(uuidIsoIec, new String(payload));
        int lastByte = byteBuffer.read();
        //TODO check
        if (lastByte != 0b10000000 || byteBuffer.available() >0){
            throw new IllegalArgumentException();
        }
    }

    private int getValueFromByteArray(ByteArrayInputStream byteBuffer) {
        int value = 0;
        int nextByte;
        while ((nextByte = byteBuffer.read()) == 0xFF){
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
