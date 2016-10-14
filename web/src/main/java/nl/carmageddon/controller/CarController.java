package nl.carmageddon.controller;

import nl.carmageddon.domain.CarInstuction;
import nl.carmageddon.domain.CarmageddonSettings;
import nl.carmageddon.domain.Mode;
import nl.carmageddon.service.AutonomousService;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
@Path("car")
public class CarController {
    private static Logger logger = LoggerFactory.getLogger(CarController.class);
    private AutonomousService autonomousService;
    private PrintWriter out;
    private Mode mode = Mode.disabled;
    private ObjectMapper mapper = new ObjectMapper();

    @Inject
    public CarController(CarmageddonSettings settings, AutonomousService autonomousService) throws IOException {
        this.autonomousService = autonomousService;
        Socket socket = new Socket(settings.getBeagleBoneSettings().getBeagleBoneIp(),
                                   settings.getBeagleBoneSettings().getCarControlPort());
        out = new PrintWriter(socket.getOutputStream());
    }

    @POST
    @Path(value = "/panic")
    public void stop() throws IOException {
        logger.debug("Panic");
        sendMessage("mode", "disabled");
        autonomousService.stopRace();
    }

    @POST
    @Path(value = "/mode/{mode}")
    public Response setMode(@PathParam("mode") Mode mode) throws IOException {
        this.mode = mode;
        sendMessage("mode", mode);
        return Response.ok().build();
    }

    @POST
    @Path(value = "/steer/{angle}")
    public Response setAngle(@PathParam("angle") int angle) throws IOException {
        if (mode == Mode.manual) {
            sendMessage("angle",angle);
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.PRECONDITION_FAILED).header("Reason","Not in manual mode").build();
        }
    }

    @POST
    @Path(value = "/engine/{throttle}")
    public Response setThrottle(@PathParam("throttle") int throttle) throws IOException {
        if (mode == Mode.manual) {
            sendMessage("throttle", throttle);
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.PRECONDITION_FAILED).header("Reason","Not in manual mode").build();
        }
    }

    @POST
    @Path(value = "/engine/throttleLimit/{throttleLimit}")
    public Response setThrottleLimit(@PathParam("throttleLimit") int throttleLimit) throws IOException {
        sendMessage("throttleLimit",throttleLimit);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    private void sendMessage(String key, Object value) throws IOException {
        CarInstuction ci = new CarInstuction();
        ci.setKey(key);
        ci.setValue(value);
        out.println(mapper.writeValueAsString(ci));
        out.flush();
    }

}
