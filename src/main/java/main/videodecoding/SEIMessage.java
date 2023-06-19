package main.videodecoding;

public class SEIMessage {

    private byte [] uuid;

    private String message;

    public SEIMessage(byte[] uuid, String message) {
        this.uuid = uuid;
        this.message = message;
    }

    public byte[] getUuid() {
        return uuid;
    }

    public String getMessage() {
        return message;
    }
}
