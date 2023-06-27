package main.videodecoding;

import java.util.Arrays;

public enum NALUnitType {
    SEI(6), CODED_SLICE_OF_IDR_PICTURE(5), SEQUENCE_PARAMETER_SET(7);

    private int value;

    NALUnitType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static NALUnitType ofInt(int value){
        return Arrays.stream(values()).filter(t->t.getValue()==value).findFirst().orElseThrow(IllegalArgumentException::new);
    }
}
