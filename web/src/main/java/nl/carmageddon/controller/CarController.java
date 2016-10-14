package nl.carmageddon.controller;

import nl.carmageddon.domain.Mode;
import nl.carmageddon.service.AutonomousService;
import nl.carmageddon.service.CarInstructionSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
@Path("car")
public class CarController {
    private static Logger logger = LoggerFactory.getLogger(CarController.class);
    private AutonomousService autonomousService;
    private CarInstructionSender carInstructionSender;
    private Mode mode = Mode.disabled;

    @Inject
    public CarController(AutonomousService autonomousService,
            CarInstructionSender carInstructionSender) throws IOException {
        this.autonomousService = autonomousService;
        this.carInstructionSender = carInstructionSender;
    }

    @POST
    @Path(value = "/panic")
    public void stop() throws IOException {
        logger.debug("Panic");
        carInstructionSender.sendMessage("mode", "disabled");
        autonomousService.stopRace();
    }

    @POST
    @Path(value = "/mode/{mode}")
    public Response setMode(@PathParam("mode") Mode mode) throws IOException {
        this.mode = mode;
        carInstructionSender.sendMessage("mode", mode);
        return Response.ok().build();
    }

    @POST
    @Path(value = "/steer/{angle}")
    public Response setAngle(@PathParam("angle") int angle) throws IOException {
        if (mode == Mode.manual) {
            carInstructionSender.sendMessage("angle", angle);
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.PRECONDITION_FAILED).header("Reason","Not in manual mode").build();
        }
    }

    @POST
    @Path(value = "/engine/{throttle}")
    public Response setThrottle(@PathParam("throttle") int throttle) throws IOException {
        if (mode == Mode.manual) {
            carInstructionSender.sendMessage("throttle", throttle);
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.PRECONDITION_FAILED).header("Reason","Not in manual mode").build();
        }
    }

    @POST
    @Path(value = "/engine/throttleLimit/{throttleLimit}")
    public Response setThrottleLimit(@PathParam("throttleLimit") int throttleLimit) throws IOException {
        carInstructionSender.sendMessage("throttleLimit", throttleLimit);
        return Response.status(Response.Status.NO_CONTENT).build();
    }


}
