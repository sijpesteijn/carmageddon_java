package nl.carmageddon.domain;

import javax.inject.Singleton;

/**
 * @author Gijs Sijpesteijn
 *
 */
@Singleton
public class Engine {
    private int throttle;
    private int throttleLimit;

    public int getThrottle() {
        return throttle;
    }

    public void setThrottle(int throttle) {
        this.throttle = throttle;
    }

    public int getThrottleLimit() {
        return throttleLimit;
    }

    public void setThrottleLimit(int throttleLimit) {
        this.throttleLimit = throttleLimit;
    }
}
