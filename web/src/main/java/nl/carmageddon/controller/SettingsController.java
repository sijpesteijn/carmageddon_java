package nl.carmageddon.controller;

import nl.carmageddon.domain.CarmageddonSettings;
import nl.carmageddon.car.AutonomousService;
import nl.carmageddon.car.CarInstructionSender;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
@Path("settings")
public class SettingsController {
    private CarmageddonSettings carmageddonSettings;
    private AutonomousService autonomousService;

    private CarInstructionSender sender;

    @Inject
    public SettingsController(CarmageddonSettings carmageddonSettings,
            AutonomousService autonomousService,
            CarInstructionSender sender) {
        this.carmageddonSettings = carmageddonSettings;
        this.autonomousService = autonomousService;
        this.sender = sender;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response saveSettings(CarmageddonSettings settings) throws IOException {
        this.carmageddonSettings = settings;
        this.autonomousService.useSettings(settings);
        this.sender.sendMessage("throttleLimit", settings.getBeagleBoneSettings().getThrottleLimit());
        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public CarmageddonSettings getSettings() {
        return this.carmageddonSettings;
    }

}
