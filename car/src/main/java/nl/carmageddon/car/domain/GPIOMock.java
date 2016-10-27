package nl.carmageddon.car.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Gijs Sijpesteijn
 */
public class GPIOMock implements GPIO {
    private static final Logger logger = LoggerFactory.getLogger(GPIOMock.class);

    @Override
    public void start() {
        logger.debug("Mock gpio start");
    }

    @Override
    public void stop() {
        logger.debug("Mock gpio stop");
    }

    @Override
    public void setDirection(String direction) {
        logger.debug("Mock gpio set direction " + direction);
    }

}
