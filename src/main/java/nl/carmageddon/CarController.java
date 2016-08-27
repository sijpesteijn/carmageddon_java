package nl.carmageddon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Gijs Sijpesteijn
 */
@RestController
public class CarController {
    private static Logger logger = LoggerFactory.getLogger(CarController.class);

    private Car car;

    @Autowired
    public CarController(Car car) {
        this.car = car;
    }

    @RequestMapping(path = "/stop", method = RequestMethod.POST)
    public void stop() {
        logger.debug("Stop");
        if (car.getEngine().getThrottle() < 0) {
            car.getEngine().setThrottle(3);
            car.getEngine().setThrottle(0);
        } else {
            car.getEngine().setThrottle(-3);
            car.getEngine().setThrottle(0);
        }
        car.getSteer().setAngle(0);
    }


    @RequestMapping(path = "/steer/{angle}", method = RequestMethod.POST)
    public void setAngle(@PathVariable("angle") int angle) {
        logger.debug("Angle: " + angle);
        car.getSteer().setAngle(angle);
    }

    @RequestMapping(path = "/engine/{throttle}", method = RequestMethod.POST)
    public void setThrottle(@PathVariable("throttle") int throttle) {
        logger.debug("Throttle: " + throttle);
        car.getEngine().setThrottle(throttle);
    }
}
