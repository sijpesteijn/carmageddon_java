package nl.carmageddon;

import nl.carmageddon.domain.Car;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 * @author Gijs Sijpesteijn
 */
@Path("car")
public class CarController {
    private static Logger logger = LoggerFactory.getLogger(CarController.class);

    private Car car;
    private boolean panic = false;

    @Inject
    public CarController(Car car) {
        this.car = car;
    }

    @POST
    @Path(value = "/panic")
    public void stop() {
        logger.debug("Panic");
        car.getEngine().setThrottle(0);
        car.getSteer().setAngle(0);
        this.panic = true;
    }

    @POST
    @Path(value = "/everythingcool")
    public void wakeup() {
        logger.debug("Every thing is cool again. Let race.");
        this.panic = false;
    }

    @POST
    @Path(value = "/steer/{angle}")
    public Response setAngle(@PathParam("angle") int angle) {
        if (!panic) {
            car.getSteer().setAngle(angle);
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.PRECONDITION_FAILED).build();
        }
    }

    @POST
    @Path(value = "/engine/{throttle}")
    public Response setThrottle(@PathParam("throttle") int throttle) {
        if (!panic) {
            car.getEngine().setThrottle(throttle);
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.PRECONDITION_FAILED).build();
        }
    }
}
