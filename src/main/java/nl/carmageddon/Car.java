package nl.carmageddon;

import nl.carmageddon.domain.Engine;
import nl.carmageddon.domain.Steer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * @author Gijs Sijpesteijn
 */
@Controller
public class Car {
    private Steer steer;
    private Engine engine;

    @Autowired
    public Car(Steer steer, Engine engine) {
        this.steer = steer;
        this.engine = engine;
    }

    public Steer getSteer() {
        return steer;
    }

    public Engine getEngine() {
        return engine;
    }
}
