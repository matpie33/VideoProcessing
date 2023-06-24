package main.videodecoding;

public class LeadingZerosCheckResultDTO {

    private byte previouslyCheckedByte;
    private int previouslyCheckedBitIndex;
    private int leadingZeroBits;

    public LeadingZerosCheckResultDTO(byte previouslyCheckedByte, int previouslyCheckedBitIndex, int leadingZeroBits) {
        this.previouslyCheckedByte = previouslyCheckedByte;
        this.previouslyCheckedBitIndex = previouslyCheckedBitIndex;
        this.leadingZeroBits = leadingZeroBits;
    }

    public byte getPreviouslyCheckedByte() {
        return previouslyCheckedByte;
    }

    public int getPreviouslyCheckedBitIndex() {
        return previouslyCheckedBitIndex;
    }

    public int getLeadingZeroBits() {
        return leadingZeroBits;
    }
}
