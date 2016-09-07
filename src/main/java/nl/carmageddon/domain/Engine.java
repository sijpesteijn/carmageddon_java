package nl.carmageddon.domain;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.Observable;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
public class Engine extends Observable {
    private static final Logger log = LoggerFactory.getLogger(Engine.class);
//    private static int dutyMin= 800000;
//    private static int dutyMax= 2000000;
    private static int dutyMiddle = 1400000;
    private static int MAX_THROTTLE = 120;
    private static int SPEED_STEP = 5000;

    private Pwm pwm;
    private int throttle;
    private int throttleLimit = 20;
    private boolean connected;

    @Inject
    public Engine(@Named("PWM22") Pwm pwm) {
        this.pwm = pwm;
        if (this.pwm != null) {
            this.pwm.setPeriod(20000000);
            this.pwm.setPolarity(0);
            this.connected = true;
            this.setThrottle(0);
            this.connected = false;
            this.pwm.start();
        }
    }

    public int getThrottle() {
        return (throttle - dutyMiddle)/SPEED_STEP;
    }

    // TODO Check of stap niet te groot is. soms stopt de bb als je abrupt van max naar min gaat.
    // Misschien wordt er dan te veel vermogen gevraagd voor de motor en is er niet genoeg voor de bb.
    public void setThrottle(int throttle) {
        if (!connected) {
            log.error("I won't set the throttle, because no clients are connected.");
            return;
        }
        if (throttle < -throttleLimit) {
            throttle = -throttleLimit;
        }
        if (throttle > throttleLimit) {
            throttle = throttleLimit;
        }
        log.debug("Setting throttle: " + throttle);
        this.throttle = dutyMiddle + ( throttle * SPEED_STEP);
        this.pwm.setDuty(this.throttle);
        setChanged();
        notifyObservers();
    }

    public void speedUp() {
        int speedUp = this.getThrottle() - 1;
        setThrottle(speedUp);
    }

    public void slowDown() {
        int slowDown = this.getThrottle() + 1;
        setThrottle(slowDown);
    }

    public void setConnected(boolean connected) {
        if(!connected) {
            setThrottle(0);
        }
        this.connected = connected;
    }

    public int getThrottleLimit() {
        return throttleLimit;
    }

    public void setThrottleLimit(int throttleLimit) {
        if (throttleLimit <= MAX_THROTTLE)
            this.throttleLimit = throttleLimit;
    }
}
