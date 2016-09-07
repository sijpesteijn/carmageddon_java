package nl.carmageddon.domain;

import com.google.inject.Inject;

import javax.inject.Singleton;
import java.util.Observable;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
public class Car extends Observable {
    private Steer steer;
    private Engine engine;
    private Camera camera;
    private Mode mode = Mode.disabled;
    private boolean connected;

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

    public Camera getCamera() { return camera; }

    public void setConnected(boolean connected) {
        this.connected = connected;
        this.engine.setConnected(connected);
        this.steer.setConnected(connected);
    }

    public boolean isConnected() {
        return connected;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        setChanged();
        notifyObservers();
    }
}
