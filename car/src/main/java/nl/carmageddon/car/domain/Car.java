package nl.carmageddon.car.domain;

import com.google.inject.Inject;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.inject.Singleton;
import java.util.Observable;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
public class Car extends Observable {
    private Steer steer;
    private Engine engine;
    @JsonIgnore
    private Horn horn;
    private Mode mode = Mode.disabled;
    private boolean connected = false;

    @Inject
    public Car(CarSettings settings, Steer steer, Engine engine, Horn horn) {
        this.steer = steer;
        this.engine = engine;
        this.horn = horn;
        this.engine.setThrottleLimit(settings.getThrotteLimit());
    }

    public Steer getSteer() {
        return steer;
    }

    public Engine getEngine() {
        return engine;
    }

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
        if (this.mode == Mode.disabled) {
            this.engine.setThrottle(0);
            this.steer.setAngle(0);
        }
        setChanged();
        notifyObservers();
    }

    public Horn getHorn() {
        return horn;
    }
}
