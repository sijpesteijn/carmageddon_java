package nl.carmageddon.domain;

/**
 * @author Gijs Sijpesteijn
 */
public class AutonoumousEvent {
    private final AutonoumousEventType eventType;
    private final byte[] imgBytes;
    private final boolean connected;

    public AutonoumousEvent(AutonoumousEventType eventType, byte[] imgBytes, boolean connected) {
        this.eventType = eventType;
        this.imgBytes = imgBytes;
        this.connected = connected;
    }

    public AutonoumousEventType getEventType() {
        return eventType;
    }

    public byte[] getImgBytes() {
        return imgBytes;
    }

    public boolean isConnected() {
        return connected;
    }
}
