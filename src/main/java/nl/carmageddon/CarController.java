package nl.carmageddon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * @author Gijs Sijpesteijn
 */
@Path("car")
public class CarController {
    private static Logger logger = LoggerFactory.getLogger(CarController.class);

    private Car car;

    @Inject
    public CarController(Car car) {
        this.car = car;
    }

    @POST
    @Path(value = "/stop")
    public void stop() {
        System.out.println("Stop");
        if (car.getEngine().getThrottle() < 0) {
            car.getEngine().setThrottle(0);
            car.getEngine().setThrottle(3);
        } else {
            car.getEngine().setThrottle(0);
            car.getEngine().setThrottle(-3);
        }
        car.getSteer().setAngle(0);
    }

    @POST
    @Path(value = "/steer/{angle}")
    public void setAngle(@PathParam("angle") int angle) {
        System.out.println("Angle: " + angle);
        car.getSteer().setAngle(angle);
    }

    @POST
    @Path(value = "/engine/{throttle}")
    public void setThrottle(@PathParam("throttle") int throttle) {
        System.out.println("Throttle: " + throttle);
        car.getEngine().setThrottle(throttle);
    }
}
