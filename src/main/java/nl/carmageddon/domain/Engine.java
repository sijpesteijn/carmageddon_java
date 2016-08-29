package nl.carmageddon.domain;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Observable;

/**
 * @author Gijs Sijpesteijn
 */
public class Engine extends Observable {
    private static final Logger log = LoggerFactory.getLogger(Engine.class);
//    private static int dutyMin= 800000;
//    private static int dutyMax= 2000000;
    private static int dutyMiddle = 1400000;
    private static int SPEED_STEP =5000;

    private Pwm pwm;
    private int throttle;

    @Inject
    public Engine(@Named("PWM22") Pwm pwm) {
        this.pwm = pwm;
        if (this.pwm != null) {
            this.pwm.setPeriod(20000000);
            this.pwm.setPolarity(0);
            this.setThrottle(0);
            this.pwm.start();
        }
    }

    public int getThrottle() {
        return (throttle - dutyMiddle)/SPEED_STEP;
    }

    public void setThrottle(int throttle) {
        if (throttle < -120) {
            throttle = -120;
        }
        if (throttle > 120) {
            throttle = 120;
        }
        log.debug("setting throttle: " + throttle);
        this.throttle = dutyMiddle + ( throttle * SPEED_STEP);
        this.pwm.setDuty(this.throttle);
        notifyObservers(this.throttle);
    }

    public void speedUp() {
        int speedUp = this.getThrottle() - 1;
        setThrottle(speedUp);
    }

    public void slowDown() {
        int slowDown = this.getThrottle() + 1;
        setThrottle(slowDown);
    }
}
