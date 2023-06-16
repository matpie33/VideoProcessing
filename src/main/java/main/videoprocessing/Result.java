package main.videoprocessing;

public class Result {
    private final int boxLength;
    private final String boxType;

    public Result(int boxLength, String boxType) {
        this.boxLength = boxLength;
        this.boxType = boxType;
    }

    public int getBoxLength() {
        return boxLength;
    }

    public String getBoxType() {
        return boxType;
    }
}
