package nl.carmageddon.domain;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Observable;

/**
 * @author Gijs Sijpesteijn
 */
public class Steer extends Observable {
    private static final Logger log = LoggerFactory.getLogger(Steer.class);
//    private static int dutyMin = 1150000;
//    private static int dutyMax = 1810000;
    private static int dutyMiddle = 1480000;
    private static int ONE_DEGREE =8250;

    private Pwm pwm;
    private int angle;

    @Inject
    public Steer(@Named("PWM42") Pwm pwm) {
        log.debug("PWM Name: " + pwm.getClass().getTypeName());
        this.pwm = pwm;
        if(this.pwm != null) {
            this.pwm.setPeriod(20000000);
            this.pwm.setPolarity(0);
            this.setAngle(0);
            this.pwm.start();
        }
    }

    public int getAngle() {
        return (angle - dutyMiddle)/ONE_DEGREE;
    }

    public void setAngle(int angle) {
        if (angle < -40) {
            angle = -40;
        }
        if (angle > 40) {
            angle = 40;
        }
        log.debug("setting angle: " + angle);
        this.angle = dutyMiddle + ( angle * ONE_DEGREE);
        this.pwm.setDuty(this.angle);
        notifyObservers(this.angle);
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
