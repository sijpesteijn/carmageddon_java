package nl.carmageddon.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Gijs Sijpesteijn
 */
@Component
public class Engine {
    private static final Logger log = LoggerFactory.getLogger(Engine.class);
    private static int dutyMax= 2000000;
    private static int dutyMiddle = 1400000;
    private static int dutyMin= 800000;
    private static int SPEED_STEP =10000;

    private Pwm pwm;
    private int throttle;

    @Autowired
    public Engine(@Qualifier("pwm22") Pwm pwm) {
        try {
            this.pwm = pwm;
            if (this.pwm != null) {
                this.pwm.setPeriod(20000000);
                this.pwm.setPolarity(0);
                this.setThrottle(0);
                this.pwm.start();
            }
        } catch (IOException ioe) {
            log.error("Could not set pwm properties");
        }
    }

    public int getThrottle() {
        return (throttle - dutyMiddle)/SPEED_STEP;
    }

    public void setThrottle(int throttle) {
        if (throttle < -60) {
            throttle = -60;
        }
        if (throttle > 60) {
            throttle = 60;
        }
        log.debug("setting throttle: " + throttle);
        this.throttle = dutyMiddle + ( throttle * SPEED_STEP);
        try {
            this.pwm.setDuty(this.throttle);
        } catch (IOException e) {
            log.error("Could not set pwm duty.");
        }
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
