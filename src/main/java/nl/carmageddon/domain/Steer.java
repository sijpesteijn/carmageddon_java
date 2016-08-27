package nl.carmageddon.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Gijs Sijpesteijn
 */
public class Steer {
    private static final Logger log = LoggerFactory.getLogger(Steer.class);
    private static int dutyMax = 1810000;
    private static int dutyMiddle = 1480000;
    private static int dutyMin = 1150000;
    private static int ONE_DEGREE =16500;

    private Pwm pwm;
    private int angle;

    public Steer(Pwm pwm) {
        try {
        this.pwm = pwm;
            if(this.pwm != null) {
                this.pwm.setPeriod(20000000);
                this.pwm.setPolarity(0);
                this.setAngle(0);
                this.pwm.start();
            }
        } catch (IOException ioe) {
            log.error("Could not set pwm properties");
        }
    }

    public int getAngle() {
        return (angle - dutyMiddle)/ONE_DEGREE;
    }

    public void setAngle(int angle) {
        if (angle < -20) {
            angle = -20;
        }
        if (angle > 20) {
            angle = 20;
        }
        log.debug("setting angle: " + angle);
        this.angle = dutyMiddle + ( angle * ONE_DEGREE);
        try {
            this.pwm.setDuty(this.angle);
        } catch (IOException e) {
            log.error("Could not set pwm duty.");
        }
    }

    public void left() {
        int left = this.getAngle() - 1;
        setAngle(left);
    }

    public void right() {
        int right = this.getAngle() + 1;
        setAngle(right);
    }
}
