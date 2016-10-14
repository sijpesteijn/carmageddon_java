package nl.carmageddon.controller;

import nl.carmageddon.domain.Car;
import nl.carmageddon.domain.CarmageddonSettings;
import nl.carmageddon.service.AutonomousService;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
@Path("settings")
public class SettingsController {

    private CarmageddonSettings carmageddonSettings;

    private Car car;

    private AutonomousService autonomousService;

    @Inject
    public SettingsController(CarmageddonSettings carmageddonSettings, Car car, AutonomousService autonomousService) {
        this.carmageddonSettings = carmageddonSettings;
        this.car = car;
        this.autonomousService = autonomousService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response saveSettings(CarmageddonSettings settings) {
        this.carmageddonSettings = settings;
        this.autonomousService.useSettings(settings);
        this.car.getCamera().setShowVideo(settings.isShowVideo());
        this.car.getEngine().setThrottleLimit(settings.getMaxThrottle());
        return Response.ok().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public CarmageddonSettings getSettings() {
        return this.carmageddonSettings;
    }

}
