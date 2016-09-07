package nl.carmageddon.service;

import nl.carmageddon.domain.AutonomousStatus;
import nl.carmageddon.domain.Car;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Observable;

/**
 * @author Gijs Sijpesteijn
 */
@Singleton
public class AutonomousService extends Observable {

    private Car car;
    private boolean racing;
    private String lastMsg = "Ready to race.";

    @Inject
    public AutonomousService(Car car) {
        this.car = car;
    }

    public boolean startRace() {
        this.racing = true;
        this.lastMsg = "Looking for traffic light.";
        notifyClients();
        return true;
    }

    private void notifyClients() {
        setChanged();
        notifyObservers(new AutonomousStatus(car.isConnected(), this.racing, this.lastMsg));
    }

    public AutonomousStatus getStatus() {
        return new AutonomousStatus(car.isConnected(), this.racing, this.lastMsg);
    }
}
