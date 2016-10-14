package nl.carmageddon.domain;

/**
 * @author Gijs Sijpesteijn
 */
public class CarState {
    private int throttle;
    private int angle;

    public int getThrottle() {
        return throttle;
    }

    public int getAngle() {
        return angle;
    }

    public void setThrottle(int throttle) {
        this.throttle = throttle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }
}
