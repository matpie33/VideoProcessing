package main.boxes.codec.avc;

import main.boxes.EditListBox;
import main.videoprocessing.IBox;
import main.videoprocessing.annotation.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component()
@Scope("prototype")
@Box(type="avcC")
public class AvcConfigurationBox implements IBox {
    @Order(1)
    private byte configurationVersion = 1;
    @Order(2)
    private byte avcProfileIndication;
    @Order(3)
    private byte profileCompatibility;
    @Order(4)
    private byte avcLevelIndication;
    @Order(5)
    private byte reservedAndLengthMinusOne;
    @Order(6)
    private byte reservedAndNumOfSequenceParameterSets;
    @Order(7)
    @VariableArraySize
    private SequenceParameter[] sequenceParameters;
    @Order(8)
    private byte numOfPictureParameterSets;

    @Order(9)
    @VariableArraySize
    private PictureParameter[] pictureParameters;

    @Order(10)
    @Conditional
    private ChromaParameters chromaParameters;

    @ConditionProvider
    public boolean shouldShowChromaParameters (String parameterName){
        if (parameterName.equals("chromaParameters")){

            return Arrays.asList(100, 110, 122, 144).contains((int)avcProfileIndication);
        }
        else{
            throw new IllegalArgumentException("Not found param: "+parameterName);
        }
    }

    @VariableArraySizeProvider
    public int getArraySize (String parameterName){
        if (parameterName.equals("sequenceParameters")){
            return reservedAndNumOfSequenceParameterSets & 0b00011111;
        }
        else if (parameterName.equals("pictureParameters")){
            return numOfPictureParameterSets;
        }
        else{
            throw new IllegalArgumentException("Unknown parameter");
        }
    }

    private static class ChromaParameters {
        @Order(1)
        private byte chromaFormatWithReserved;

        @Order(2)
        private byte bitDepthLumaMinus8;

        @Order(3)
        private byte bitDepthChromaMinus8;

        @Order(4)
        private byte numOfSequenceParameterSetExt;
        @Order(5)
        @VariableArraySize
        private SequenceParametersExt[] sequenceParametersExt;

        @VariableArraySizeProvider
        public int getSizeOfSequenceParametersExt (String parameterName){
            return numOfSequenceParameterSetExt;
        }

        private static class SequenceParametersExt {
            @Order(1)
            private short sequenceParameterSetExtLength;

            @Order(2)
            @VariableArraySize
            private byte [] sequenceParameterSetExtNALUnit;

            @VariableArraySizeProvider
            private int getSizeOfSequenceParameterSetExtNalUnit (String param){
                if (param.equals("sequenceParameterSetExtNALUnit")){
                    return sequenceParameterSetExtLength;
                }
                else{
                    throw new IllegalArgumentException("Not implemented");
                }
            }

            @Override
            public String toString() {
                return "SequenceParametersExt{" +
                        "sequenceParameterSetExtLength=" + sequenceParameterSetExtLength +
                        ", sequenceParameterSetExtNALUnit=" + Arrays.toString(sequenceParameterSetExtNALUnit) +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "ChromaParameters{" +
                    "chromaFormatWithReserved=" + chromaFormatWithReserved +
                    ", bitDepthLumaMinus8=" + bitDepthLumaMinus8 +
                    ", bitDepthChromaMinus8=" + bitDepthChromaMinus8 +
                    ", numOfSequenceParameterSetExt=" + numOfSequenceParameterSetExt +
                    ", sequenceParametersExt=" + Arrays.toString(sequenceParametersExt) +
                    '}';
        }
    }


    private static class SequenceParameter {
        @Order(1)
        private short sequenceParameterSetLength;
        @VariableArraySize
        @Order(2)
        private byte [] sequenceParameterSetNALUnit;

        @VariableArraySizeProvider
        public int getArraySize (String parameterName){
            if (parameterName.equals("sequenceParameterSetNALUnit")){
                return sequenceParameterSetLength;
            }
            else{
                throw new IllegalArgumentException("Unknown parameter");
            }
        }

        @Override
        public String toString() {
            return "SequenceParameter{" +
                    "sequenceParameterSetLength=" + sequenceParameterSetLength +
                    ", sequenceParameterSetNALUnit=" + Arrays.toString(sequenceParameterSetNALUnit) +
                    '}';
        }
    }

    private static class PictureParameter {
        @Order(1)
        private short pictureParameterSetLength;
        @VariableArraySize
        @Order(2)
        private byte [] pictureParameterSetNALUnit;

        @VariableArraySizeProvider
        public int getArraySize (String parameterName){
            if (parameterName.equals("pictureParameterSetNALUnit")){
                return pictureParameterSetLength;
            }
            else{
                throw new IllegalArgumentException("Unknown parameter");
            }
        }

        @Override
        public String toString() {
            return "PictureParameter{" +
                    "pictureParameterSetLength=" + pictureParameterSetLength +
                    ", pictureParameterSetNALUnit=" + Arrays.toString(pictureParameterSetNALUnit) +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "AvcConfigurationBox{" +
                "configurationVersion=" + configurationVersion +
                ", avcProfileIndication=" + avcProfileIndication +
                ", profileCompatibility=" + profileCompatibility +
                ", avcLevelIndication=" + avcLevelIndication +
                ", reservedAndLengthMinusOne=" + reservedAndLengthMinusOne +
                ", reservedAndNumOfSequenceParameterSets=" + reservedAndNumOfSequenceParameterSets +
                ", sequenceParameters=" + Arrays.toString(sequenceParameters) +
                ", numOfPictureParameterSets=" + numOfPictureParameterSets +
                ", pictureParameters=" + Arrays.toString(pictureParameters) +
                ", chromaParameters=" + chromaParameters +
                '}';
    }
}
