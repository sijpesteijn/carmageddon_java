package nl.carmageddon;

import com.google.inject.Inject;
import nl.carmageddon.domain.Camera;
import nl.carmageddon.domain.Engine;
import nl.carmageddon.domain.Steer;

/**
 * @author Gijs Sijpesteijn
 */
public class Car {
    private Steer steer;
    private Engine engine;
    private Camera camera;

    @Inject
    public Car(Steer steer, Engine engine, Camera camera) {
        this.steer = steer;
        this.engine = engine;
        this.camera = camera;
    }

    public Steer getSteer() {
        return steer;
    }

    public Engine getEngine() {
        return engine;
    }

    public Camera getCamera() {
        return camera;
    }
}
