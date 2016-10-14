package nl.carmageddon.controller;

import nl.carmageddon.service.AutonomousService;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
@Path("autonomous")
public class AutonomousController {
    private AutonomousService autonomousService;

    @Inject
    public AutonomousController(AutonomousService autonomousService) {
        this.autonomousService = autonomousService;
    }

    @POST
    @Path("/start")
    public Response startAutonomous() {
        if (autonomousService.startRace()) {
            return Response.status(Response.Status.NO_CONTENT).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("/stop")
    public Response stopAutonomous() {
        autonomousService.stopRace();
        return Response.ok().build();
    }

}
