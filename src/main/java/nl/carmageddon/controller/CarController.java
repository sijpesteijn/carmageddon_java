package nl.carmageddon.controller;

import nl.carmageddon.domain.Car;
import nl.carmageddon.domain.Mode;
import nl.carmageddon.service.AutonomousService;
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
    private AutonomousService autonomousService;

    @Inject
    public CarController(Car car, AutonomousService autonomousService) {
        this.car = car;
        this.autonomousService = autonomousService;
    }

    @POST
    @Path(value = "/panic")
    public void stop() {
        logger.debug("Panic");
        autonomousService.stopRace();
        car.getEngine().setThrottle(0);
        car.getSteer().setAngle(0);
        car.setMode(Mode.disabled);
    }

    @POST
    @Path(value = "/mode/{mode}")
    public Response setMode(@PathParam("mode") Mode mode) {
        car.setMode(mode);
        return Response.ok().build();
    }

    @POST
    @Path(value = "/steer/{angle}")
    public Response setAngle(@PathParam("angle") int angle) {
        if (car.getMode() == Mode.manual) {
            car.getSteer().setAngle(angle);
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.PRECONDITION_FAILED).header("Reason","Not in manual mode").build();
        }
    }

    @POST
    @Path(value = "/engine/{throttle}")
    public Response setThrottle(@PathParam("throttle") int throttle) {
        if (car.getMode() == Mode.manual) {
            car.getEngine().setThrottle(throttle);
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.PRECONDITION_FAILED).header("Reason","Not in manual mode").build();
        }
    }

    @POST
    @Path(value = "/engine/throttleLimit/{throttleLimit}")
    public Response setThrottleLimit(@PathParam("throttleLimit") int throttleLimit) {
        car.getEngine().setThrottleLimit(throttleLimit);
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
