package nl.carmageddon.domain;

/**
 * @author Gijs Sijpesteijn
 */
public class AutonomousStatus {
    private final boolean connected;
    private boolean racing;
    private final String message;

    public AutonomousStatus(boolean connected, boolean racing, String message) {
        this.connected = connected;
        this.racing = racing;
        this.message = message;
    }

    public boolean getConnected() {
        return connected;
    }

    public String getMessage() {
        return message;
    }

    public boolean isRacing() {
        return racing;
    }
}
