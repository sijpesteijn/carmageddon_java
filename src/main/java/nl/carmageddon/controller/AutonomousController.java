package nl.carmageddon.controller;

import nl.carmageddon.domain.AutonomousSettings;
import nl.carmageddon.service.AutonomousService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Gijs Sijpesteijn
 */
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
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(value = "/settings")
    public Response saveSettings(AutonomousSettings settings) {
        this.autonomousService.useSettings(settings);
        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "/settings")
    public AutonomousSettings getSettings() {
        return this.autonomousService.getSettings();
    }

}
