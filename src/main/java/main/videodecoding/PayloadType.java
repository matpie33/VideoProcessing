package main.videodecoding;

import java.util.Arrays;

public enum PayloadType {
    USER_DATA_UNREGISTERED(5);

    int value;

    PayloadType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static PayloadType of(int value){
        return Arrays.stream(values()).filter(p->p.getValue() == value).findFirst().orElseThrow(IllegalArgumentException::new);
    }
}
