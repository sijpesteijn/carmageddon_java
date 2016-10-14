package nl.carmageddon.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Gijs Sijpesteijn
 */
public class PwmMock implements Pwm {
    private static final Logger log = LoggerFactory.getLogger(PwmMock.class);

    @Override
    public void setPeriod(int period) {
        log.debug("Mock pwm set period: " + period);
    }

    @Override
    public void setPolarity(int polarity) {
        log.debug("Mock pwm set polarity: " + polarity);
    }

    @Override
    public void start() {
        log.debug("Mock pwm start");
    }

    @Override
    public void stop() {
        log.debug("Mock pwm stop");
    }

    @Override
    public void setDuty(int duty) {
        log.debug("Mock pwm set duty: " + duty);
    }
}
