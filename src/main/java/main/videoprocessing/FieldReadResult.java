package main.videoprocessing;

public class FieldReadResult {

    private int readedBytes;

    private Object fieldValue;

    public FieldReadResult(int readedBytes, Object fieldValue) {
        this.readedBytes = readedBytes;
        this.fieldValue = fieldValue;
    }

    public int getReadedBytes() {
        return readedBytes;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}
